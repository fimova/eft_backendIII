
package com.duoc.database_init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class DatabaseInitApplication implements CommandLineRunner {

	private static final Path SQL_DIRECTORY = Path.of("/database");

	private static final List<String> SCRIPTS = List.of(
			"01_batch.sql",
			"02_clientes.sql",
			"03_cuentas.sql",
			"04_transacciones.sql",
			"05_mensajeria.sql");

	private static final Pattern CREATE_OBJECT = Pattern.compile(
			"(?is)^\\s*CREATE\\s+(TABLE|SEQUENCE)\\s+([A-Z0-9_$#]+)");

	public static void main(String[] args) {
		SpringApplication.run(DatabaseInitApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String wallet = System.getenv().getOrDefault(
				"DB_TNS_ADMIN", "/wallet");

		System.setProperty("oracle.net.tns_admin", wallet);

		String url = requiredEnv("DB_URL");
		String username = requiredEnv("DB_USERNAME");
		String password = requiredEnv("DB_PASSWORD");

		System.out.println("Iniciando preparación del esquema Oracle...");

		try (Connection connection = DriverManager.getConnection(url, username, password)) {

			for (String script : SCRIPTS) {
				executeScript(connection, SQL_DIRECTORY.resolve(script));
			}
		}

		System.out.println("Inicialización completada correctamente.");
	}

	private void executeScript(Connection connection, Path path)
			throws Exception {

		if (!Files.isRegularFile(path)) {
			throw new IllegalStateException(
					"No se encontró el script: " + path);
		}

		System.out.println("Revisando " + path.getFileName());

		String content = Files.readString(path);

		// Elimina comentarios SQL de una línea.
		content = content.replaceAll("(?m)^\\s*--.*$", "");

		String[] statements = content.split(";");

		for (String rawSql : statements) {
			String sql = rawSql.trim();

			if (sql.isEmpty()) {
				continue;
			}

			Matcher matcher = CREATE_OBJECT.matcher(sql);

			if (!matcher.find()) {
				throw new IllegalArgumentException(
						"Instrucción no soportada en "
								+ path.getFileName() + ": " + sql);
			}

			String type = matcher.group(1).toUpperCase(Locale.ROOT);
			String name = matcher.group(2).toUpperCase(Locale.ROOT);

			if (objectExists(connection, type, name)) {
				System.out.println(
						"Ya existe " + type + " " + name + "; se conserva.");
				continue;
			}

			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
			}

			System.out.println("Creado " + type + " " + name);
		}
	}

	private boolean objectExists(
			Connection connection, String type, String name)
			throws SQLException {

		String catalog;

		if ("TABLE".equals(type)) {
			catalog = "USER_TABLES";
		} else if ("SEQUENCE".equals(type)) {
			catalog = "USER_SEQUENCES";
		} else {
			throw new IllegalArgumentException(
					"Tipo de objeto no soportado: " + type);
		}

		String column = "TABLE".equals(type)
				? "TABLE_NAME"
				: "SEQUENCE_NAME";

		String sql = "SELECT 1 FROM " + catalog
				+ " WHERE " + column + " = ?";

		try (var statement = connection.prepareStatement(sql)) {
			statement.setString(1, name);

			try (ResultSet result = statement.executeQuery()) {
				return result.next();
			}
		}
	}

	private String requiredEnv(String name) {
		String value = System.getenv(name);

		if (value == null || value.isBlank()) {
			throw new IllegalStateException(
					"Falta la variable de entorno " + name);
		}

		return value;
	}
}