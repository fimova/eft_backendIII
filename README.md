# Backend III — EFT

## 1. Objetivo

Desarrollar una arquitectura de microservicios para la gestión de un banco, utilizando Spring
Boot y Spring Cloud.

El proyecto implementa servicios independientes para la gestión de transacciones, cuentas, clientes y mensajería, además de
procesos batch para el procesamiento de archivos CSV. La arquitectura incorpora configuración centralizada, descubrimiento de
servicios, autenticación mediante OAuth2/JWT, comunicación asíncrona con JMS, tolerancia a fallos y despliegue mediante Docker
Compose.

Los datos se almacenan en Oracle Database.

## 2. Estructura del proyecto

```text
eft_backendIII/
├── config-server/
├── discovery-server/
├── auth-server/
├── transacciones-service/
├── mensajeria-service/
├── cuentas-service/
├── clientes-service/
├── batch-service/
├── database-init/
├── bff-web/
├── bff-mobile/
├── bff-atm/
├── config-repo/
├── database/
│   ├── 01_batch.sql
│   ├── 02_clientes.sql
│   ├── 03_cuentas.sql
│   ├── 04_transacciones.sql
│   └── 05_mensajeria.sql
├── https.p12
├── docker-compose.yml
└── README.md
```

## 3. Responsabilidad de los componentes

| Componente | Responsabilidad |
|---|---|
| Config Server | Centraliza la configuración de los microservicios |
| Discovery Server | Permite registrar y descubrir servicios mediante Eureka |
| Auth Server | Centraliza la autenticación OAuth2 y la emisión de tokens JWT |
| Transacciones Service | Gestiona las transacciones y publica eventos de transacciones creadas |
| Cuentas Service | Gestiona las cuentas y las operaciones asociadas |
| Clientes Service | Gestiona la información de los clientes |
| Mensajería Service | Consume eventos JMS y registra los eventos procesados |
| Batch Service | Ejecuta trabajos de procesamiento de intereses y estados y movimientos financieros a partir de archivos CSV |
| Database Init | Comprueba y crea las tablas y secuencias de negocio que faltan en Oracle antes de iniciar los servicios
dependientes |
| BFF Web | Expone operaciones destinadas al canal web |
| BFF Mobile | Expone operaciones destinadas al canal móvil |
| BFF ATM | Expone operaciones destinadas al canal de cajeros automáticos |
| ActiveMQ | Broker de mensajería utilizado para la comunicación asíncrona |
| Oracle Database | Almacena la información de negocio y los eventos procesados |

## 4. Arquitectura de eventos y flujo de comunicación

### Comunicación síncrona

1. El cliente realiza una petición HTTPS al BFF correspondiente
2. El BFF valida el token JWT mediante las claves públicas expuestas por Auth Server
3. El BFF consulta los microservicios correspondientes mediante solicitudes REST
4. Los microservicios acceden a Oracle para consultar o persistir información
5. El resultado se devuelve al cliente a través del BFF

Config Server proporciona la configuración centralizada y Eureka permite descubrir los servicios registrados.

### Comunicación asíncrona mediante JMS

El proyecto utiliza mensajería asíncrona para desacoplar operaciones entre servicios. Un ejemplo de este 
mecanismo es el flujo de retiros desde `cuenta-service`:

1. Cuentas Service registra un nuevo retiro de dinero
2. El servicio publica un evento `RetiroCuentaEvent` en el destino `cuentas.retiros` de ActiveMQ
3. Mensajería Service consume el evento
4. El servicio registra el evento procesado en Oracle, utilizando la tabla `eventos_procesados`
5. La lógica de resiliencia incorpora mecanismos de reintento, Circuit Breaker e idempotencia para gestionar fallos y evitar
procesar nuevamente eventos duplicados

Esta misma lógica se encuentra implementada para Clientes creados, Operaciones de depósito y transferencia y Transacciones
creadas.

### Procesamiento batch

Batch Service ejecuta tres job independientes:

- **Transacciones:** procesa los registros del archivo `movimientos_financieros_diarios.csv`.
- **Intereses:** procesa la información de `intereses_trimestrales.csv`.
- **Movimientos anuales:** procesa los registros de `estados_financieros_anuales.csv`.

Los jobs se ejecutan mediante los endpoints del servicio y persisten los resultados en Oracle.

### Inicialización de la base de datos

Al iniciar Docker Compose, `database-init` se conecta a Oracle utilizando las credenciales y el wallet configurados. Revisa los
scripts SQL en orden y crea las tablas y secuencias que no existen.

Los servicios dependientes esperan a que la inicialización termine correctamente. Las tablas y los registros existentes se
conservan.

## 5. Tecnologías utilizadas

- **Lenguaje:** Java 21
- **Framework:** Spring Boot
- **Arquitectura distribuida:** Spring Cloud Config y Spring Cloud Netflix Eureka
- **Seguridad:** Spring Security, OAuth2 y JWT
- **Persistencia:** Oracle Database, JDBC y HikariCP
- **Mensajería:** JMS y ActiveMQ
- **Resiliencia:** Resilience4j, Retry y Circuit Breaker
- **Procesamiento por lotes:** Spring Batch
- **Contenedores:** Docker y Docker Compose
- **Comunicación:** REST/HTTP y HTTPS
- **Certificados:** almacén PKCS12 (`https.p12`)

## 6. Requisitos previos

Antes de ejecutar el proyecto se requiere:

- Java 21 para desarrollo local
- Docker Desktop con el motor de contenedores iniciado
- Docker Compose
- Acceso a Oracle Database
- Wallet de Oracle descargado y configurado
- Credenciales de acceso a Oracle
- Credenciales OAuth de GitHub para Auth Server
- Archivo `https.p12` para las conexiones HTTPS de los BFF (incluido para fines académicos en raíz de proyecto)
- Archivo `docker-compose.yml`, scripts SQL y archivos de configuración del proyecto (incluidos en raíz de proyecto)

Las imágenes Docker utilizadas deben estar construidas localmente, ya que los servicios están configurados con `pull_policy:
never`.

### Construcción de las imágenes Docker

Desde la raíz del proyecto, ejecutar los siguientes comandos para construir las imágenes requeridas:

```powershell
docker build -t config-server:1.0 .\config-server
docker build -t discovery-server:1.0 .\discovery-server
docker build -t auth-server:1.0 .\auth-server
docker build -t transacciones-service:1.0 .\transacciones-service
docker build -t mensajeria-service:1.0 .\mensajeria-service
docker build -t batch-service:1.0 .\batch-service
docker build -t cuentas-service:1.0 .\cuentas-service
docker build -t clientes-service:1.0 .\clientes-service
docker build -t database-init:1.0 .\database-init
docker build -t bff-web:1.0 .\bff-web
docker build -t bff-mobile:1.0 .\bff-mobile
docker build -t bff-atm:1.0 .\bff-atm
```

Una vez construidas las imágenes y configuradas las variables de entorno, se puede iniciar el proyecto con Docker Compose.

## 7. Configuración y variables de entorno

Las variables deben configurarse en PowerShell antes de ejecutar Docker Compose. Por ejemplo:

```powershell
$env:DB_URL="valor_de_la_conexion"
```

Reemplazar el ejemplo por el valor real correspondiente. Repetir el procedimiento para las demás variables de entorno
requeridas. Las variables definidas de esta forma estarán disponibles en la sesión actual de PowerShell.

| Variable | Descripción |
|---|---|
| `DB_URL` | URL de conexión a Oracle Database |
| `DB_USERNAME` | Usuario del esquema de Oracle |
| `DB_PASSWORD` | Contraseña del usuario de Oracle |
| `DB_TNS_ADMIN` | Ruta local al directorio del wallet de Oracle |
| `GITHUB_CLIENT_ID` | Identificador de la aplicación OAuth de GitHub |
| `GITHUB_CLIENT_SECRET` | Secreto de la aplicación OAuth de GitHub |
| `HTTPS_KEYSTORE_PASSWORD` | Contraseña del almacén de certificados HTTPS |

`DB_TNS_ADMIN` apunta al directorio del wallet en el equipo anfitrión. Docker lo monta en modo de solo lectura en `/wallet`.

Los BFF utilizan el certificado `https.p12`, montado en `/app/certs/https.p12`.

Las configuraciones centralizadas se encuentran en `config-repo/`.

## 8. Ejecución con Docker Compose

### 8.1. Configurar las variables de entorno

Es necesario las variables anteriores en PowerShell antes de iniciar los contenedores. Por seguridad, las credenciales
no son incorporadas directamente al repositorio.

### 8.2. Iniciar los servicios

```powershell
docker compose up -d
```

Docker Compose inicia la infraestructura, ejecuta `database-init` y permite que los servicios dependientes arranquen cuando la
inicialización de la base de datos termina correctamente.

### 8.3. Comprobar el estado

```powershell
docker compose ps -a
```

Para consultar los registros de un componente:

```powershell
docker compose logs --tail=100 batch-service
```

También pueden consultarse los registros de cualquier otro servicio sustituyendo su nombre.

### 8.4. Detener el proyecto

```powershell
docker compose down
```

Este comando elimina los contenedores y la red del proyecto, pero conserva las imágenes y los datos almacenados en Oracle.

## 9. Puertos y endpoints

### 9.1. Infraestructura

| Componente | Dirección | Descripción |
|---|---|---|
| Config Server | `http://localhost:8888` | Configuración centralizada |
| Eureka Dashboard | `http://localhost:8761` | Registro y descubrimiento de servicios |
| Auth Server | `http://localhost:9000` | Inicio del flujo de autenticación |
| ActiveMQ Web Console | `http://localhost:8161` | Administración del broker de mensajería |
| ActiveMQ JMS | `localhost:61616` | Comunicación de las aplicaciones con el broker |

### 9.2. Puertos de los microservicios y BFF

| Componente | Puerto | Protocolo |
|---|---:|---|
| Transacciones Service | 8081 | HTTP |
| Mensajería Service | 8082 | HTTP |
| Batch Service | 8084 | HTTP |
| Cuentas Service | 8085 | HTTP |
| Clientes Service | 8086 | HTTP |
| BFF Web | 8087 | HTTPS |
| BFF Mobile | 8088 | HTTPS |
| BFF ATM | 8089 | HTTPS |

### 9.3. Autenticación

Auth Server inicia el flujo de autenticación OAuth2 mediante GitHub. El canal se especifica en la ruta y se almacena en la
sesión antes de redirigir al usuario al proveedor de autenticación.

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/auth/login/WEB` | Inicia la autenticación para el canal web |
| GET | `/api/auth/login/MOBILE` | Inicia la autenticación para el canal móvil |
| GET | `/api/auth/login/ATM` | Inicia la autenticación para el canal de cajeros automáticos |

Base URL: `http://localhost:9000`

El endpoint de claves públicas JWT es:

`http://localhost:9000/.well-known/jwks.json`

Las solicitudes protegidas deben utilizar un token JWT válido y correspondiente al canal autorizado.

### 9.4. Batch Service

Base URL: `http://localhost:8084/api/batch`

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/batch/transacciones` | Ejecuta el trabajo de procesamiento de transacciones |
| POST | `/api/batch/intereses` | Ejecuta el trabajo de procesamiento de intereses |
| POST | `/api/batch/movimientos` | Ejecuta el trabajo de procesamiento de movimientos anuales |
| POST | `/api/batch/todos` | Permite solicitar la ejecución de todos los trabajos |

Se recomienda ejecutar los trabajos individualmente para controlar el consumo de recursos durante el procesamiento.

### 9.5. Transacciones Service

Base URL: `http://localhost:8081`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/transacciones` | Lista las transacciones registradas |
| GET | `/api/transacciones/{id}` | Consulta una transacción por su identificador |
| POST | `/api/transacciones` | Registra una nueva transacción |

Ejemplo de solicitud para POST:
```json
{
    "fecha": "2026-10-10",
    "monto": 5000,
    "tipo": "CREDITO"
}
```

### 9.6. Cuentas Service

Base URL: `http://localhost:8085`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/cuentas` | Lista las cuentas |
| GET | `/api/cuentas/{id}` | Consulta una cuenta por su identificador |
| POST | `/api/cuentas` | Crea una cuenta |
| GET | `/api/cuentas/{id}/movimientos` | Consulta los movimientos de una cuenta |
| GET | `/api/cuentas/{id}/intereses` | Consulta los intereses de una cuenta |
| POST | `/api/cuentas/{id}/retiros` | Realiza un retiro |
| POST | `/api/cuentas/{id}/depositos` | Realiza un depósito |
| POST | `/api/cuentas/{id}/transferencias` | Realiza una transferencia |

Ejemplo de solicitud para POST crear cuenta:
```json
{
    "cuentaId": 101,
    "estado": "ACTIVA"
}
```
Ejemplo de solicitud para POST depósitos:
```json
{
  "monto": 1000
}
```

Ejemplo de solicitud para POST transferencias:
```json
{
  "cuentaDestinoId": 102,
  "monto": 500
}
```

Ejemplo de solicitud para POST retiros:
```json
{
  "monto": 1000
}
```

Utilizar los mismos desde BFF cuando corresponda.

### 9.7. Clientes Service

Base URL: `http://localhost:8086`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/clientes` | Lista los clientes |
| GET | `/api/clientes/{id}` | Consulta un cliente por su identificador |
| POST | `/api/clientes` | Registra un nuevo cliente |

Ejemplo de solicitud para POST:
```json
{
    "clienteId": 1,
    "nombre": "Prueba",
    "email": "Pruebaz@email.com",
    "estado": "ACTIVO"
}
```

Utilizar el mismo desde BFF cuando corresponda.

### 9.8. BFF Web

Base URL: `https://localhost:8087`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/web/cuentas/{cuentaId}/resumen` | Consulta el resumen de una cuenta |
| GET | `/api/web/cuentas/{cuentaId}/movimientos` | Consulta los movimientos de una cuenta |
| GET | `/api/web/cuentas/{cuentaId}/intereses` | Consulta los intereses de una cuenta |
| GET | `/api/web/transacciones` | Lista las transacciones |
| POST | `/api/web/clientes` | Registra un nuevo cliente |

### 9.9. BFF Mobile

Base URL: `https://localhost:8088`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/mobile/cuentas/{cuentaId}/resumen` | Consulta el resumen de una cuenta |
| GET | `/api/mobile/cuentas/{cuentaId}/movimientos` | Consulta los movimientos de una cuenta |

### 9.10. BFF ATM

Base URL: `https://localhost:8089`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/atm/cuentas/{cuentaId}/saldo` | Consulta el saldo de una cuenta |
| POST | `/api/atm/cuentas/{cuentaId}/retiros` | Realiza un retiro |

### 9.11. Consideraciones sobre los endpoints

- Los BFF exponen operaciones específicas para cada canal y no replican todas las operaciones disponibles en los microservicios.
- Las operaciones de depósito y transferencia están disponibles en Cuentas Service, pero no se encuentran expuestas en los
controladores BFF compartidos.
- El registro directo de transacciones está disponible en Transacciones Service, pero no se expone mediante un endpoint de
creación en los BFF compartidos.
- Mensajería Service consume eventos JMS de ActiveMQ. No se documentan endpoints REST específicos de este servicio en esta
sección.
- Los endpoints protegidos requieren las credenciales de autenticación correspondientes y un token JWT válido para el canal
autorizado.
- Los BFF utilizan HTTPS mediante el certificado configurado en `https.p12`.

## 10. Consideraciones adicionales

- Batch Service se ejecuta de forma independiente y no se registra en Eureka
- Los trabajos batch no se ejecutan automáticamente al iniciar el servicio; se invocan mediante sus endpoints
- `database-init` solo crea objetos de negocio que no existen. No sustituye las migraciones necesarias cuando cambia la
estructura de una tabla existente
- Los servicios que utilizan Oracle requieren acceso al wallet y las variables de entorno correspondientes
- Los BFF utilizan HTTPS, mientras que la comunicación interna entre los contenedores utiliza los nombres de servicio definidos
en Docker Compose.
- Los eventos JMS se publican en ActiveMQ y son consumidos por Mensajería Service
- Las credenciales, contraseñas y secretos deben mantenerse fuera del repositorio