pipeline {
    agent {
            docker {
                image 'maven:3.8.7-openjdk-11' // Adjust the version as needed
                args '-v $HOME/.m2:/root/.m2' // Optional: Cache Maven dependencies
            }
        }
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