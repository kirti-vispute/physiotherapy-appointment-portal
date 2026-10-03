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
        stage('Deploy') {
            steps {
                powershell '& "$env:WORKSPACE/scripts/deploy-local.ps1"'
                archiveArtifacts artifacts: 'target/deployment.json,target/deploy-*.log', fingerprint: true
            }
        }
    }
    post {
        always { powershell '& "$env:WORKSPACE/scripts/selenium-app.ps1" -Action stop' }
        success { echo "Application deployed: http://localhost:${params.PORT}/ (APP_ENV=${params.APP_ENV})" }
        failure { echo 'Pipeline failed. Inspect the stage and console; no successful deployment is claimed.' }
    }
}
