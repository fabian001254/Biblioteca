#!/bin/bash

echo "Deteniendo procesos Java relacionados con Biblioteca..."
pkill -f "java.*biblioteca" || true

echo "Limpiando archivos temporales..."
./gradlew clean

echo "Iniciando la aplicación en el puerto 8083..."
SERVER_PORT=8083 ./gradlew bootRun
