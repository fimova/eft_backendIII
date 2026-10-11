# Guía de despliegue

## 1. Objetivo

Este documento describe la arquitectura y el procedimiento de despliegue del proyecto `eft_backendIII` mediante 
Docker Compose.

La solución se ejecuta en contenedores independientes, con configuración centralizada, descubrimiento de 
servicios, autenticación, comunicación asíncrona y acceso a Oracle Database.

El despliegue implementado corresponde a un entorno local basado en Docker Compose. La implementación en una 
plataforma cloud o en una infraestructura productiva requiere configuraciones adicionales.

## 2. Arquitectura de despliegue

La solución se compone de servicios independientes que se ejecutan en contenedores Docker y se comunican mediante 
la red definida en `docker-compose.yml`.

Los principales componentes son:

- **Infraestructura:** Config Server, Discovery Server, Auth Server y ActiveMQ.
- **Microservicios de negocio:** Transacciones Service, Cuentas Service y Clientes Service.
- **Mensajería:** Mensajería Service, encargada de consumir eventos JMS.
- **Procesamiento batch:** Batch Service, que ejecuta trabajos sobre archivos CSV.
- **Canales de acceso:** BFF Web, BFF Mobile y BFF ATM.
- **Inicialización de datos:** Database Init, que comprueba y crea los objetos de negocio faltantes en Oracle.

Oracle Database se utiliza como sistema de persistencia y se accede mediante las credenciales y el wallet configurados 
en el entorno.

Docker Compose define las relaciones de dependencia entre los contenedores, de modo que los servicios que requieren 
la base de datos esperan a que Database Init finalice correctamente.

## 3. Requisitos de despliegue

Para desplegar la solución se requiere:

- Docker Desktop con el motor de contenedores disponible, o un entorno compatible con Docker Engine y Docker Compose.
- Las imágenes Docker del proyecto construidas localmente.
- Acceso a Oracle Database.
- El wallet de Oracle y las credenciales correspondientes.
- Las credenciales OAuth de GitHub utilizadas por Auth Server.
- El certificado HTTPS `https.p12` utilizado por los BFF.
- Los archivos de configuración y el archivo `docker-compose.yml` del proyecto.

Las variables de entorno necesarias deben estar configuradas antes de iniciar los contenedores.

Los detalles de configuración, las variables requeridas y los comandos de construcción de las imágenes se encuentran en `instrucciones.md`.

## 4. Despliegue con Docker Compose

### 4.1. Iniciar los servicios

Desde la raíz del proyecto, ejecutar:

```powershell
docker compose up -d
```

Docker Compose crea e inicia los contenedores definidos en el archivo de configuración y establece la red 
de comunicación entre ellos.

Los servicios dependientes de la inicialización de la base de datos esperan a que Database Init finalice 
correctamente antes de comenzar su ejecución.

### 4.2. Configuración y coordinación de los contenedores

El archivo `docker-compose.yml` define la ejecución integrada de los componentes, sus dependencias, la configuración 
de red y la exposición de puertos.

**Red compartida**

Los servicios se conectan a la red `backend3-eft`, de tipo `bridge`, definida explícitamente en Docker Compose. 
Esto permite la comunicación entre contenedores mediante los nombres de servicio, evitando depender de direcciones 
IP internas fijas.

**Comprobación de salud**

Config Server dispone de un `healthcheck` que consulta el endpoint `/actuator/health`. La comprobación se 
realiza cada 10 segundos, con un tiempo de espera de 5 segundos, hasta 12 reintentos y un período inicial de 120 segundos.

Esta configuración permite que los servicios que dependen de Config Server mediante la condición `service_healthy` 
esperen a que supere la comprobación de salud antes de iniciar.

**Dependencias entre servicios**

La propiedad `depends_on` establece las condiciones de inicio entre los componentes:

- `service_healthy`: espera a que Config Server supere su comprobación de salud.
- `service_started`: espera a que el contenedor dependiente haya comenzado a ejecutarse, sin garantizar que la 
aplicación esté lista para recibir solicitudes.
- `service_completed_successfully`: espera a que `database-init` finalice correctamente antes de iniciar los servicios 
que dependen de la inicialización de la base de datos.

Estas condiciones coordinan el orden de inicio, pero no sustituyen las comprobaciones de disponibilidad ni garantizan 
que todos los servicios permanezcan operativos ante fallos posteriores.

**Configuración y recursos externos**

Las credenciales y los parámetros de conexión se suministran mediante variables de entorno. El wallet de Oracle y 
el certificado HTTPS se montan en los contenedores en modo de solo lectura.

Las imágenes se construyen localmente y utilizan `pull_policy: never`, por lo que deben estar disponibles antes de 
iniciar el despliegue.

**Exposición de puertos**

Los puertos definidos en el archivo permiten acceder desde el equipo anfitrión a los componentes que los exponen. 
La comunicación interna entre contenedores utiliza los puertos y nombres de servicio definidos en la configuración.

### 4.3. Verificar el despliegue

Para consultar el estado de los contenedores:

```powershell
docker compose ps -a
```

Para revisar los registros de un servicio:

```powershell
docker compose logs --tail=100 nombre-del-servicio
```

Se recomienda comprobar que los componentes hayan iniciado correctamente y verificar la disponibilidad de 
los servicios mediante sus endpoints o interfaces de administración correspondientes.

### 4.4. Detener el proyecto

Para detener y eliminar los contenedores y la red del proyecto:

```powershell
docker compose down
```

Este comando conserva las imágenes locales y los datos almacenados en Oracle Database. No elimina los 
volúmenes persistentes si no se especifica la opción `-v`.

## 5. Configuración y dependencias

La configuración de los servicios se centraliza mediante Config Server, utilizando los archivos disponibles en `config-repo/`.

Eureka permite el registro y descubrimiento de los servicios configurados para utilizarlo. Batch Service se 
ejecuta de manera independiente y no se registra en Eureka.

Los BFF utilizan HTTPS para las conexiones de entrada. La comunicación interna entre los contenedores 
utiliza los nombres de servicio definidos en Docker Compose y los protocolos configurados para cada componente.

La comunicación asíncrona se implementa mediante JMS y ActiveMQ. Mensajería Service consume los eventos publicados 
por los servicios de negocio y registra los eventos procesados en Oracle Database.

Database Init comprueba y crea las tablas y secuencias de negocio que faltan. Este proceso no reemplaza las 
migraciones necesarias cuando se modifica la estructura de objetos existentes.

## 6. Persistencia y ciclo de vida

Oracle Database se encuentra fuera del ciclo de vida de los contenedores definidos por Docker Compose.

Por lo tanto, detener o recrear los contenedores no elimina los registros de negocio almacenados en Oracle.

La inicialización comprueba la existencia de los objetos requeridos y crea los que faltan, conservando los 
objetos y registros existentes.

Nuevamente importante recalcar que las imágenes Docker se construyen localmente y el archivo `docker-compose.yml` 
utiliza `pull_policy: never`. Por este motivo, cuando se modifica el código de un componente, es necesario reconstruir 
su imagen antes de desplegar nuevamente los contenedores.

## 7. Consideraciones para un despliegue productivo

El despliegue mediante Docker Compose permite ejecutar e integrar los componentes del proyecto en un entorno local. 
Para trasladar la solución a una infraestructura productiva o cloud, sería necesario evaluar, entre otros, 
los siguientes aspectos:

- **Gestión de secretos:** utilizar un mecanismo seguro para administrar credenciales, certificados y otros datos sensibles.
- **Imágenes y distribución:** publicar las imágenes en un registro privado y establecer un proceso controlado 
de construcción y actualización.
- **Disponibilidad y escalamiento:** definir estrategias de alta disponibilidad y escalamiento horizontal 
según los requisitos de cada servicio.
- **Observabilidad:** centralizar registros y métricas, y establecer mecanismos de monitorización y alertas.
- **Seguridad de las comunicaciones:** gestionar certificados, accesos de red y cifrado de las conexiones 
según las necesidades del entorno.
- **Confiabilidad de los eventos:** incorporar mecanismos adicionales, cpara mejorar la entrega confiable 
y la recuperación de eventos ante fallos.
- **Persistencia y recuperación:** definir políticas de respaldo, recuperación ante desastres y 
administración de cambios de esquema.

Estos aspectos representan consideraciones para una evolución futura de la solución y no deben interpretarse 
como funcionalidades ya desplegadas o verificadas en un entorno cloud.