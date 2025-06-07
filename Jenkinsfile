pipeline {
    agent any

    environment {
        // Puedes agregar variables de entorno aquí si es necesario
    }

    options {
        // Mantén solo los últimos 5 builds y muestra timestamps
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test --no-daemon'
            }
        }
        stage('Build Docker Images') {
            steps {
                sh 'docker compose build'
            }
        }
        stage('Deploy (Docker Compose Up)') {
            steps {
                sh 'docker compose up -d'
            }
        }
    }
    post {
        always {
            // Mostrar estado de los contenedores
            sh 'docker compose ps'
        }
        failure {
            // Puedes agregar notificaciones aquí (ej: email, Slack)
            echo 'El pipeline falló. Revisa los logs.'
        }
    }
}
