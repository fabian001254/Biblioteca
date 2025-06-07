pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Preparar Gradle Cache') {
            steps {
                script {
                    // Ruta correcta encontrada en el debug
                    def gradleZip = 'C:\\.gradle\\wrapper\\dists\\gradle-8.14.2-bin\\manual\\gradle-8.14.2-bin.zip'
                    
                    if (fileExists(gradleZip)) {
                        echo "[INFO] Gradle ZIP encontrado en: ${gradleZip}"
                        echo "[INFO] Preparando directorio docker-cache..."
                        
                        // Crear directorio docker-cache
                        bat 'if not exist docker-cache mkdir docker-cache'
                        
                        // Copiar el archivo ZIP al directorio docker-cache
                        bat "copy \"${gradleZip}\" docker-cache\\gradle-8.14.2-bin.zip"
                        
                        // Verificar que se copió correctamente
                        bat 'dir docker-cache'
                        
                        echo "[INFO] Gradle ZIP copiado exitosamente a docker-cache"
                    } else {
                        echo "[ADVERTENCIA] Gradle ZIP NO encontrado en: ${gradleZip}"
                        echo "[INFO] Creando directorio docker-cache vacío"
                        bat 'if not exist docker-cache mkdir docker-cache'
                    }
                }
            }
        }
        
        stage('Test') {
            steps {
                bat 'gradlew.bat test --no-daemon'
            }
        }
        
        stage('Build Docker Images') {
            steps {
                bat 'docker compose build'
            }
        }
        
        stage('Deploy (Docker Compose Up)') {
            steps {
                bat 'docker compose up -d'
            }
        }
    }
    
    post {
        always {
            bat 'docker compose ps'
        }
        failure {
            echo 'El pipeline falló. Revisa los logs.'
        }
    }
}