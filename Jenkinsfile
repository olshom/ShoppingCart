pipeline {
    agent any
     environment {
            JAVA_HOME = '/usr/lib/jvm/java-21-openjdk'
            DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
            DOCKERHUB_REPO = 'olgasho/temperatureconverter'
            DOCKER_IMAGE_TAG = 'latest'
         }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                url: 'https://github.com/olshom/TemperatureConverter.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install' // sh for linux and ios
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Code Coverage') {
            steps {
                sh 'mvn jacoco:report'
            }
        }
        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
        stage('Publish Coverage Report') {
            steps {
                recordCoverage(tools: [[parser: 'JACOCO']])
            }
        }
        stage('Build Docker Image') {
                  steps {
                      script {
                          docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
                      }
                  }
              }

              stage('Push Docker Image to Docker Hub') {
                  steps {
                      script {
                          docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
                              docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                          }
                      }
                  }
              }
    }
}