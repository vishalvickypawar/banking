pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/vishalvickypawar/banking.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Publish to Nexus') {
            steps {
                sh 'mvn deploy'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t banking .'
            }
        }
        stage('Run Docker Container') {
            steps {
                sh 'docker run -d -p 8080:8080 banking'
            }
        }
    }
}