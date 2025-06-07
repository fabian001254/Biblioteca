pipeline {
    agent any


    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Verifica Gradle Cache') {
            steps {
                script {
                    def gradleZip = 'C:/\.gradle/wrapper/dists/gradle-8.14.2-bin/manual/gradle-8.14.2-bin.zip'
                    if (fileExists(gradleZip)) {
                        echo "[INFO] Gradle ZIP encontrado en cache local: ${gradleZip}"
                    } else {
                        echo "[ADVERTENCIA] Gradle ZIP NO encontrado en cache local: ${gradleZip}. Se intentará descargar desde internet."
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
