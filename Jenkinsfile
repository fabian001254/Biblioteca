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
        
        stage('Debug Gradle Location') {
            steps {
                script {
                    echo "=== Buscando Gradle en el sistema ==="
                    // Buscar dónde está realmente Gradle
                    bat 'dir C:\\.gradle\\wrapper\\dists\\ /s /b 2>nul || echo "Directorio .gradle no encontrado"'
                    // Buscar archivos zip de gradle en todo el sistema
                    bat 'dir C:\\*gradle*.zip /s /b 2>nul || echo "No se encontraron archivos gradle.zip"'
                    // Buscar en directorio de usuario
                    bat 'dir %USERPROFILE%\\.gradle\\wrapper\\dists\\ /s /b 2>nul || echo "Directorio .gradle de usuario no encontrado"'
                    // Ver variables de entorno relacionadas con Gradle
                    bat 'echo GRADLE_HOME: %GRADLE_HOME%'
                    bat 'echo GRADLE_USER_HOME: %GRADLE_USER_HOME%'
                    bat 'echo USERPROFILE: %USERPROFILE%'
                    // Verificar si gradle está en PATH
                    bat 'where gradle 2>nul || echo "Gradle no encontrado en PATH"'
                    // Verificar directorio de trabajo actual
                    bat 'echo Directorio actual: %CD%'
                    bat 'dir gradle\\wrapper 2>nul || echo "No hay directorio gradle/wrapper en proyecto"'
                }
            }
        }
        
        stage('Preparar Gradle Cache') {
            steps {
                script {
                    def gradleZip = 'C:\\.gradle\\wrapper\\dists\\gradle-8.14.2-bin\\manual\\gradle-8.14.2-bin.zip'
                    if (fileExists(gradleZip)) {
                        echo "[INFO] Gradle ZIP encontrado, copiando al directorio docker-cache"
                        bat 'if not exist docker-cache mkdir docker-cache'
                        bat "copy \"${gradleZip}\" docker-cache\\gradle-8.14.2-bin.zip"
                        echo "[INFO] Gradle ZIP copiado exitosamente"
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