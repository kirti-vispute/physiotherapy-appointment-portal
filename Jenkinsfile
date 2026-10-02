pipeline {
    agent { label 'built-in' }
    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }
    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        skipStagesAfterUnstable()
        timestamps()
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
    }
    parameters {
        choice(name: 'APP_ENV', choices: ['demo', 'test'], description: 'Spring profile and separate deployment/database directory')
        choice(name: 'PORT', choices: ['8081', '8082'], description: 'Local application port; Jenkins uses 8080')
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                bat '@git rev-parse HEAD'
            }
        }
        stage('Build') {
            steps { bat '@call mvn -B -ntp clean compile' }
        }
        stage('Unit Test') {
            steps { bat '@call mvn -B -ntp test' }
            post {
                always { junit testResults: 'target/surefire-reports/TEST-*.xml', allowEmptyResults: false }
            }
        }
        stage('Package') {
            steps {
                bat '@call mvn -B -ntp -DskipTests package'
                archiveArtifacts artifacts: 'target/physio-portal-1.0.0.jar', fingerprint: true
            }
        }
        stage('Deploy') {
            steps {
                powershell '& "$env:WORKSPACE/scripts/deploy-local.ps1"'
                archiveArtifacts artifacts: 'target/deployment.json,target/deploy-*.log', fingerprint: true
            }
        }
    }
    post {
        success { echo "Application deployed: http://localhost:${params.PORT}/ (APP_ENV=${params.APP_ENV})" }
        failure { echo 'Pipeline failed. Inspect the stage and console; no successful deployment is claimed.' }
    }
}
