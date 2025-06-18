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

# Dar permisos a gradlew
RUN chmod +x ./gradlew

# Crear directorio docker-cache vacío para evitar errores
RUN mkdir -p docker-cache

# Copiar cache si existe (ahora seguro porque el directorio existe)
COPY docker-cache* docker-cache/

# Configurar Gradle cache (solo si existe el archivo)
RUN mkdir -p /root/.gradle/wrapper/dists/gradle-8.14.2-bin/ && \
    if [ -f docker-cache/gradle-8.14.2-bin.zip ]; then \
      echo '[INFO] Gradle ZIP encontrado en docker-cache, copiando...' && \
      cp docker-cache/gradle-8.14.2-bin.zip /root/.gradle/wrapper/dists/gradle-8.14.2-bin/gradle-8.14.2-bin.zip && \
      echo '[INFO] Gradle ZIP copiado al cache local del contenedor.' && \
      ls -la /root/.gradle/wrapper/dists/gradle-8.14.2-bin/; \
    else \
      echo '[INFO] No hay cache de Gradle disponible. Descargando desde internet...'; \
    fi

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