# Instrucciones de ejecución

## 1. Introducción

Este documento describe los requisitos, la configuración del entorno y los procedimientos necesarios para ejecutar 
y probar el proyecto `eft_backendIII`.

La solución utiliza Docker Compose para iniciar los componentes y Oracle Database como sistema de persistencia. 
Las instrucciones contemplan la preparación de las imágenes, la configuración de las variables de entorno, la inicialización 
de la base de datos y la ejecución de los servicios y procesos batch.

## 2. Requisitos previos

Antes de ejecutar el proyecto, se requiere:

- Java 21 para el desarrollo y la compilación local.
- Docker Desktop con el motor de contenedores iniciado.
- Docker Compose.
- Acceso a Oracle Database.
- Wallet de Oracle descargado y configurado.
- Credenciales de acceso a Oracle.
- Credenciales OAuth de GitHub para Auth Server.
- Archivo `https.p12` para las conexiones HTTPS de los BFF (incluido para fines académicos en raíz de proyecto).
- Archivo `docker-compose.yml`, scripts SQL y archivos de configuración incluidos en el proyecto.

Las credenciales y los secretos deben configurarse en el entorno de ejecución y no publicarse en el repositorio.

## 3. Construcción de las imágenes Docker

Las imágenes deben estar construidas localmente, ya que los servicios están configurados con `pull_policy: never`. 
Esta configuración evita que Docker Compose intente descargar las imágenes del registro.

Desde PowerShell, situarse en la raíz del proyecto y ejecutar los siguientes comandos:

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

Una vez finalizada la construcción, configurar las variables de entorno antes de iniciar los servicios.

## 4. Configuración del entorno

### 4.1. Variables de entorno

Las variables deben configurarse en PowerShell antes de ejecutar Docker Compose.

Por ejemplo:

```powershell
$env:DB_URL="valor_de_la_conexion"
```

Reemplazar el valor de ejemplo por el correspondiente al entorno y repetir el procedimiento para las 
demás variables requeridas.

| Variable | Descripción |
|---|---|
| `DB_URL` | URL de conexión a Oracle Database. |
| `DB_USERNAME` | Usuario del esquema de Oracle. |
| `DB_PASSWORD` | Contraseña del usuario de Oracle. |
| `DB_TNS_ADMIN` | Ruta local al directorio del wallet de Oracle. |
| `GITHUB_CLIENT_ID` | Identificador de la aplicación OAuth de GitHub. |
| `GITHUB_CLIENT_SECRET` | Secreto de la aplicación OAuth de GitHub. |

Las variables definidas de esta forma estarán disponibles en la sesión actual de PowerShell. Si se abre una 
sesión nueva, deberán configurarse nuevamente.

### 4.2. Wallet de Oracle

La variable `DB_TNS_ADMIN` debe apuntar al directorio del wallet en el equipo anfitrión.

Docker monta este directorio en los contenedores que necesitan acceder a Oracle, en modo de solo lectura.

Verificar que la ruta configurada corresponda a la ubicación real del wallet y que los archivos necesarios estén disponibles.

### 4.3. Certificado HTTPS

Los BFF utilizan el almacén de certificados `https.p12`, montado en los contenedores en la ruta `/app/certs/https.p12`.

El certificado se utiliza para habilitar HTTPS en los BFF. El navegador puede mostrar una advertencia de confianza 
si el certificado no ha sido emitido por una autoridad certificadora reconocida.

### 4.4. Configuración centralizada

Los archivos de configuración centralizada se encuentran en el directorio `config-repo/` y son utilizados por Config Server.

Verificar que los archivos estén presentes y que sus propiedades sean coherentes con los nombres de los servicios, 
los puertos y las variables configuradas para el entorno.

## 5. Ejecución con Docker Compose

### 5.1. Iniciar los servicios

Desde la raíz del proyecto, ejecutar:

```powershell
docker compose up -d
```

Docker Compose iniciará los componentes definidos en `docker-compose.yml`, respetando las dependencias configuradas.

El proceso de inicialización de la base de datos debe finalizar correctamente antes de que comiencen los servicios 
que dependen de él.

### 5.2. Comprobar el estado

Para consultar el estado de los contenedores:

```powershell
docker compose ps
```

Para revisar los registros de ejecución de los servicios:

```powershell
docker compose logs --tail=100
```

También se pueden consultar los registros de un componente específico:

```powershell
docker compose logs --tail=100 nombre-del-servicio
```

Comprobar que los servicios hayan iniciado correctamente antes de ejecutar las pruebas funcionales.

El estado de un contenedor por sí solo no garantiza que la aplicación esté disponible. Cuando corresponda, 
verificar también los endpoints de salud o las interfaces de administración expuestas.

### 5.3. Detener los servicios

Para detener y eliminar los contenedores y la red creada por Docker Compose:

```powershell
docker compose down
```

Este comando no elimina los volúmenes persistentes ni las imágenes locales. No utilizar `-v` si se 
desea conservar los volúmenes asociados al proyecto.

## 6. Inicialización de la base de datos

Al iniciar Docker Compose, `database-init` se conecta a Oracle utilizando las credenciales y el wallet configurados. 
Comprueba la existencia de las tablas y secuencias de negocio definidas por los scripts SQL y crea los objetos faltantes.

Los servicios dependientes esperan a que la inicialización finalice correctamente antes de comenzar su ejecución.

Este mecanismo conserva los objetos y registros existentes. No reemplaza las migraciones necesarias cuando 
cambia la estructura de una tabla.

Los scripts SQL se encuentran en el directorio `database/`.

## 7. Puertos y endpoints

Esta sección reúne las direcciones de acceso a la infraestructura, los microservicios y los BFF.

## 7.1. Procesamiento batch

Batch Service implementa tres trabajos independientes que procesan archivos CSV y persisten sus resultados en Oracle Database.

| Trabajo | Archivo de entrada |
|---|---|
| Transacciones | `movimientos_financieros_diarios.csv` |
| Intereses | `intereses_trimestrales.csv` |
| Movimientos anuales | `estados_financieros_anuales.csv` |

Los trabajos se ejecutan manualmente mediante los endpoints de Batch Service y no se ejecutan automáticamente al 
iniciar el contenedor.

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/batch/transacciones` | Ejecuta el trabajo de procesamiento de transacciones |
| POST | `/api/batch/intereses` | Ejecuta el trabajo de procesamiento de intereses |
| POST | `/api/batch/movimientos` | Ejecuta el trabajo de procesamiento de movimientos anuales |
| POST | `/api/batch/todos` | Permite solicitar la ejecución de todos los trabajos |

Se recomienda ejecutar los trabajos por separado para comprobar sus resultados y facilitar la identificación de errores.

Después de cada ejecución, revisar la respuesta del endpoint y los registros del servicio. Cuando corresponda, 
verificar en Oracle que los resultados se hayan persistido correctamente.

Batch Service se ejecuta de forma independiente y no se registra en Eureka.

### 7.2. Infraestructura

| Componente | Dirección | Descripción |
|---|---|---|
| Config Server | `http://localhost:8888` | Configuración centralizada |
| Eureka Dashboard | `http://localhost:8761` | Registro y descubrimiento de servicios |
| Auth Server | `http://localhost:9000` | Inicio del flujo de autenticación |
| ActiveMQ Web Console | `http://localhost:8161` | Administración del broker de mensajería |
| ActiveMQ JMS | `localhost:61616` | Comunicación de las aplicaciones con el broker |

### 7.3. Microservicios y BFF

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

### 7.4. Autenticación

Auth Server inicia el flujo de autenticación OAuth2 mediante GitHub. El canal se especifica en la ruta y se almacena en la sesión antes de redirigir al usuario al proveedor de autenticación.

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/auth/login/WEB` | Inicia la autenticación para el canal web |
| GET | `/api/auth/login/MOBILE` | Inicia la autenticación para el canal móvil |
| GET | `/api/auth/login/ATM` | Inicia la autenticación para el canal de cajeros automáticos |

El endpoint de claves públicas JWT es:

`http://localhost:9000/.well-known/jwks.json`

Estas claves permiten a los componentes que validan los tokens verificar sus firmas.

### 7.5. Endpoints de los servicios y BFF

**Transacciones Service**

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

**Cuentas Service**

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

**Clientes Service**

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

**BFF Web**

Base URL: `https://localhost:8087`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/web/cuentas/{cuentaId}/resumen` | Consulta el resumen de una cuenta |
| GET | `/api/web/cuentas/{cuentaId}/movimientos` | Consulta los movimientos de una cuenta |
| GET | `/api/web/cuentas/{cuentaId}/intereses` | Consulta los intereses de una cuenta |
| GET | `/api/web/transacciones` | Lista las transacciones |
| POST | `/api/web/clientes` | Registra un nuevo cliente |

**BFF Mobile**

Base URL: `https://localhost:8088`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/mobile/cuentas/{cuentaId}/resumen` | Consulta el resumen de una cuenta |
| GET | `/api/mobile/cuentas/{cuentaId}/movimientos` | Consulta los movimientos de una cuenta |

**BFF ATM**

Base URL: `https://localhost:8089`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/atm/cuentas/{cuentaId}/saldo` | Consulta el saldo de una cuenta |
| POST | `/api/atm/cuentas/{cuentaId}/retiros` | Realiza un retiro |

### 7.6. Consideraciones sobre los endpoints

- Los BFF exponen operaciones específicas para cada canal y no replican todas las operaciones disponibles 
en los microservicios.
- Las operaciones de depósito y transferencia están disponibles en Cuentas Service, pero no se encuentran 
expuestas en los controladores BFF documentados.
- El registro directo de transacciones está disponible en Transacciones Service, pero no se expone mediante 
un endpoint de creación en los BFF documentados.
- Mensajería Service consume eventos JMS de ActiveMQ. Su función principal es procesar los eventos recibidos, 
por lo que no se incluyen endpoints REST de negocio en esta sección.
- Los endpoints protegidos requieren un token JWT válido y autorizado para el canal correspondiente.
- Los BFF utilizan HTTPS mediante el certificado configurado en `https.p12`.

## 8. Pruebas funcionales

Una vez iniciados los servicios, se recomienda seguir este orden general para verificar el funcionamiento de la solución:

1. **Infraestructura:** comprobar el estado de los contenedores y la disponibilidad de Config Server, Eureka y ActiveMQ.
2. **Batch:** ejecutar individualmente los tres trabajos y comprobar sus resultados.
3. **Autenticación:** iniciar el flujo OAuth2 mediante GitHub y verificar el acceso con un token JWT válido para el 
canal correspondiente.
4. **Microservicios:** probar las operaciones de consulta y modificación documentadas en las tablas de endpoints.
5. **BFF:** verificar que cada canal pueda acceder a las operaciones autorizadas y que las peticiones sin autorización 
válida sean rechazadas.
6. **Mensajería:** comprobar la publicación y el consumo de eventos JMS, así como los registros de eventos procesados 
en Oracle.
7. **Persistencia:** verificar que las operaciones y los procesos que hayan finalizado correctamente reflejen los 
cambios esperados en Oracle Database.

La respuesta HTTP de una operación no demuestra por sí sola que un evento asíncrono haya sido consumido y registrado. 
Para comprobar ese flujo, revisar también los registros de Mensajería Service y la tabla `eventos_procesados`.

## 10. Consideraciones y solución de problemas

- **Docker no está disponible:** verificar que Docker Desktop esté iniciado y que el motor de contenedores 
funcione correctamente.
- **No se encuentra una imagen:** comprobar que todas las imágenes requeridas se hayan construido localmente con los 
nombres y las etiquetas indicados.
- **Falla la conexión con Oracle:** revisar `DB_URL`, las credenciales, la ruta del wallet y la disponibilidad de la 
base de datos.
- **Falla la inicialización de la base de datos:** revisar los registros de `database-init` y confirmar que tenga acceso 
a Oracle.
- **Falla el inicio de un servicio:** consultar sus registros con `docker compose logs` y comprobar la disponibilidad 
de sus dependencias.
- **Se recibe una respuesta HTTP 401:** comprobar que la petición incluya un token JWT válido.
- **Se recibe una respuesta HTTP 403:** comprobar que el token corresponda al canal autorizado para la operación.
- **Un evento no aparece en `eventos_procesados`:** revisar los registros de publicación y consumo, el estado de ActiveMQ 
y Mensajería Service, y los mecanismos de resiliencia configurados.

Los mecanismos de reintento y Circuit Breaker contribuyen a gestionar determinados fallos, pero no garantizan por sí 
solos la entrega y el procesamiento de todos los eventos ante cualquier interrupción.

Para detener los servicios después de las pruebas, ejecutar `docker compose down`.