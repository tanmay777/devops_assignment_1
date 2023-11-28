pipeline {
    agent any

    stages {
        stage('Cleanup Workspace') {
            steps {
                cleanWs()
                sh 'echo "Cleaning up workspace"'
            }
        }
        stage('Code Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[url: 'https://github.com/spring-projects/spring-petclinic.git']]
                ])
            }
        }
        stage('Test Cases') {
              steps {
                    sh './mvnw test'
            }
        }
        stage('Code Build') {
              steps {
                    sh './mvnw install -Dmaven.test.skip=true -Dcheckstyle.skip'
            }
        }

    }
  
    post {
            success {
                sh 'JENKINS_NODE_COOKIE=dontkill nohup java -jar target/spring-petclinic-3.2.0-SNAPSHOT.jar --server.port=8085 &'
            }
    }        
}