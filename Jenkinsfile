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
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
    }
    parameters {
        choice(name: 'APP_ENV', choices: ['test', 'demo'], description: 'Spring profile and separate Docker data volume')
        choice(name: 'DOCKER_PORT', choices: ['8087', '8088'], description: 'Local host port mapped to container port 8080')
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
        stage('Start Application') {
            steps { powershell '& "$env:WORKSPACE/scripts/selenium-app.ps1" -Action start' }
        }
        stage('Selenium Tests') {
            steps {
                bat '@call mvn -B -ntp -Pselenium -DskipUnitTests=true -Dselenium.baseUrl=http://127.0.0.1:8091 verify'
            }
            post {
                always {
                    script {
                        if (!fileExists('target/selenium-report/selenium.html') &&
                            fileExists('target/failsafe-reports/TEST-com.kirtivispute.physio.selenium.PortalSeleniumIT.xml')) {
                            def reportExit = bat returnStatus: true, script: '@call mvn -B -ntp -Pselenium surefire-report:failsafe-report-only'
                            echo "Failure report generation exit: ${reportExit}"
                        }
                    }
                    junit testResults: 'target/failsafe-reports/TEST-*.xml', allowEmptyResults: true
                    archiveArtifacts artifacts: 'target/failsafe-reports/TEST-*.xml,target/selenium-evidence/*,target/selenium-app/*.log,target/selenium-app/run.json', allowEmptyArchive: true, fingerprint: true
                    script {
                        if (currentBuild.currentResult != 'SUCCESS') {
                            archiveArtifacts artifacts: 'target/selenium-report/**', allowEmptyArchive: true, fingerprint: true
                        }
                    }
                }
            }
        }
        stage('Publish Test Report') {
            steps {
                archiveArtifacts artifacts: 'target/selenium-report/**', allowEmptyArchive: false, fingerprint: true
                echo 'Published five-journey Selenium HTML and XML results.'
            }
        }
        stage('Docker Build') {
            steps {
                powershell '& "$env:WORKSPACE/scripts/docker-cd.ps1" -Action build'
            }
        }
        stage('Docker Tag') {
            steps { powershell '& "$env:WORKSPACE/scripts/docker-cd.ps1" -Action tag' }
        }
        stage('Docker Push') {
            steps { powershell '& "$env:WORKSPACE/scripts/docker-cd.ps1" -Action push' }
        }
        stage('Stop Previous Container') {
            steps { powershell '& "$env:WORKSPACE/scripts/docker-cd.ps1" -Action stop' }
        }
        stage('Run New Container') {
            steps { powershell '& "$env:WORKSPACE/scripts/docker-cd.ps1" -Action run' }
        }
        stage('Health Check') {
            steps { powershell '& "$env:WORKSPACE/scripts/docker-cd.ps1" -Action health' }
        }
    }
    post {
        always {
            powershell '& "$env:WORKSPACE/scripts/selenium-app.ps1" -Action stop'
            archiveArtifacts artifacts: 'target/docker-deployment.json,target/docker-*.log', allowEmptyArchive: true, fingerprint: true
        }
        success { echo "Container deployed: http://localhost:${params.DOCKER_PORT ?: '8087'}/ (APP_ENV=${params.APP_ENV})" }
        failure { echo 'Pipeline failed. Inspect the stage and console; no successful deployment is claimed.' }
    }
}
