# Dockerfile para la app Spring Boot
FROM openjdk:17-jdk-slim AS build
WORKDIR /app

# Copiar solo los archivos de Gradle primero (para mejor cache de Docker)
COPY gradle/ gradle/
COPY gradlew .
COPY gradlew.bat .
COPY gradle.properties .
COPY build.gradle .
COPY settings.gradle .

# Copiar el cache de Docker si existe (opcional)
COPY docker-cache/ docker-cache/ 2>/dev/null || true

# Configurar Gradle cache (solo si existe)
RUN mkdir -p /root/.gradle/wrapper/dists/gradle-8.14.2-bin/ && \
    if [ -f docker-cache/gradle-8.14.2-bin.zip ]; then \
      echo '[INFO] Gradle ZIP encontrado en docker-cache, copiando...' && \
      cp docker-cache/gradle-8.14.2-bin.zip /root/.gradle/wrapper/dists/gradle-8.14.2-bin/gradle-8.14.2-bin.zip && \
      echo '[INFO] Gradle ZIP copiado al cache local del contenedor.' && \
      ls -la /root/.gradle/wrapper/dists/gradle-8.14.2-bin/; \
    else \
      echo '[INFO] No hay cache de Gradle disponible. Descargando desde internet...'; \
    fi

# Dar permisos a gradlew
RUN chmod +x ./gradlew

# Descargar dependencias (se cachea si no cambian los archivos de gradle)
RUN ./gradlew dependencies --no-daemon || true

# Copiar el resto del código fuente
COPY src/ src/

# Build final
RUN ./gradlew build --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]