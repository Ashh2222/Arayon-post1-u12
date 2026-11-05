# Post-contenido — Unidad 12: Despliegue y CI/CD

Laboratorio de Programación Web (Séptimo Semestre). Aplicación Spring Boot (catálogo de
productos de la Unidad 10) dockerizada con un Dockerfile multi-stage y perfiles dev/prod.
La Parte 2 la extiende con un pipeline de GitHub Actions y monitoreo con Actuator.

## Parte 1: Dockerización con perfiles y variables de entorno

El Dockerfile compila con Maven (imagen `maven:3.9-eclipse-temurin-17-alpine`) y ejecuta el
JAR en `eclipse-temurin:17-jre-alpine` con un usuario no root. La configuración sensible se
externaliza con placeholders `${VAR:default}`: sin variables de entorno arranca con el perfil
`dev` (H2 en memoria); con `SPRING_PROFILES_ACTIVE=prod` y las variables `DATASOURCE_URL`,
`DATASOURCE_USER` y `DATASOURCE_PASSWORD` usa la configuración de producción (PostgreSQL).

![Imagen construida](capturas/docker-images.png)
Imagen construida. `DISK USAGE` (~352 MB) incluye capas descomprimidas; el tamaño de
contenido, que es lo que se publicaría, es ~113 MB.

![Perfil dev](capturas/perfil-dev.png)
Sin variables de entorno: perfil `dev`.

![Perfil prod](capturas/perfil-prod.png)
Con `SPRING_PROFILES_ACTIVE=prod`: perfil `prod` (sin PostgreSQL local, la conexión falla; se
verifica solo el perfil activo).

## Decisiones de diseño
1. **Placeholders con valor por defecto** en vez de estrictos: la app arranca sin fricción en
   local y en CI; los valores reales se inyectan en producción. La contraseña de producción no
   tiene default utilizable.
2. **`application-dev.properties` y `application-prod.properties` separados:** hace explícito
   qué configuración corresponde a cada ambiente y evita editar producción por error.
3. **JRE Alpine y no distroless:** imagen pequeña sin perder `docker exec` para depurar.

## Decisiones propias (ajustes al material base)
- **Etapa de build con imagen de Maven:** `eclipse-temurin:*-jdk-alpine` no incluye `mvn`.
- **Java 17** en ambas etapas, igual que `java.version` del `pom.xml`.
- **Driver de PostgreSQL** agregado como `runtime`, necesario para el perfil `prod`.
- **`ddl-auto=update` en prod:** `validate` falla con una base vacía y no hay Flyway en el alcance.
- **Paquete de logs** ajustado a `com.universidad.catalogo`.

## Cómo compilar y ejecutar
1. `docker build -t mi-spring-app:local .`
2. Perfil dev: `docker run -p 8080:8080 mi-spring-app:local`
3. Perfil prod: `docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod -e DATASOURCE_URL=... -e DATASOURCE_USER=... -e DATASOURCE_PASSWORD=... mi-spring-app:local`