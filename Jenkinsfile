pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                bat 'mvn -Dmaven.test.failure.ignore=true clean verify site'
            }
        }
        stage('reports') {
            steps {
                script {
                        allure([
                                includeProperties: false,
                                properties: [],
                                reportBuildPolicy: 'ALWAYS',
                                results: [[path: 'target/allure-results']]
                        ])
                }
            }
        }
    }
    post {
        always {
            deleteDir()
        }
        failure {
            slackSend message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} failed (<${env.BUILD_URL}|Open>)",
                    color: 'danger', teamDomain: 'qameta', channel: 'allure', tokenCredentialId: 'allure-channel'
        }
    }
}