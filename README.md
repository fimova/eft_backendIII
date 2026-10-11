# Backend III — EFT

## 1. Objetivo

Desarrollar una arquitectura de microservicios para la gestión de operaciones bancarias, utilizando Java, 
SpringBoot y Spring Cloud.

El proyecto implementa servicios independientes para la gestión de transacciones, cuentas, clientes y mensajería,
además de procesos batch para el procesamiento de archivos CSV. La solución incorpora configuración centralizada,
descubrimiento de servicios, autenticación mediante OAuth2 y JWT, comunicación asíncrona con JMS, mecanismos de 
resilencia y despliegue mediante Docker Compose.

Los datos de negocio se almacenan en Oracle Database.

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
└── instrucciones.md
└── despliegue.md
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
| Batch Service | Ejecuta trabajos de procesamiento de intereses y estados y movimientos financieros a partir de
archivos CSV |
| Database Init | Comprueba y crea las tablas y secuencias de negocio que faltan en Oracle antes de iniciar los
servicios dependientes |
| BFF Web | Expone operaciones destinadas al canal web |
| BFF Mobile | Expone operaciones destinadas al canal móvil |
| BFF ATM | Expone operaciones destinadas al canal de cajeros automáticos |
| ActiveMQ | Broker de mensajería utilizado para la comunicación asíncrona |
| Oracle Database | Almacena la información de negocio y los eventos procesados |

## 4. Flujo de comunicación

### 4.1. Comunicación síncrona

Los clientes acceden a la solución mediante tres Backend for Frontend (BFF): Web, Mobile y ATM.

Los BFF exponen operaciones específicas para cada canal, validan los tokens JWT mediante las claves públicas del Auth Server 
y se comunican con los microservicios mediante solicitudes REST. Los servicios acceden a Oracle Database para consultar o 
persistir la información.

Config Server centraliza la configuración y Eureka permite descubrir los servicios registrados-.

### 4.2. Comunicación asíncrona mediante JMS

ActiveMQ permite desacoplar la publicación de eventos de su procesamiento. Los servicios de negocio publican eventos asociados 
a operaciones como retiros, depósitos, transferencias, creación de clientes y transacciones.

Mensajería Service consume los eventos y registra su procesamiento en Oracle, utilizando la tabla eventos_procesados. La 
solución incorpora mecanismos de reintento, Circuit Breaker e idempotencia para gestionar determinados fallos y evitar el 
procesamiento duplicado de eventos.

La entrega confiable ante cualquier interrupción requiere mejoras adicionales, como mecanismos de 
recuperación de eventos pendientes.

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

## 6. Consideraciones

- Batch Service se ejecuta de forma independiente y no se registra en Eureka
- Los trabajos batch no se ejecutan automáticamente al iniciar el servicio; se invocan mediante sus endpoints
- `database-init` crea los objetos de negocio que faltan, pero no modifica automáticamente la estructura de las tablas 
existentes.
- Los servicios que utilizan Oracle requieren acceso al wallet y las variables de entorno correspondientes
- Los BFF utilizan HTTPS, mientras que la comunicación interna entre los contenedores utiliza los nombres de servicio
definidos en Docker Compose.
- Los eventos JMS se publican en ActiveMQ y son consumidos por Mensajería Service. La persistencia de una operación 
de negocio no implica, por sí sola, que el evento haya sido procesado correctamente.
- Las credenciales, contraseñas y secretos deben mantenerse fuera del repositorio
- El despliegue implementado y documentado corresponde a un entorno basado en Docker Compose. La implementación en una 
plataforma cloud y el escalamiento horizontal requieren configuración adicional.

## 7. Documentación

- [Instrucciones de ejecución](instrucciones.md)
- [Guía de despliegue](despliegue.md)