pipeline {
    agent any


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
            // Mostrar estado de los contenedores
            bat 'docker compose ps'
        }
        failure {
            // Puedes agregar notificaciones aquí (ej: email, Slack)
            echo 'El pipeline falló. Revisa los logs.'
        }
    }
}
