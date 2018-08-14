#!/usr/bin/env groovy

def isBaseBranch() {
    def currentBranch = env.BRANCH_NAME
    currentBranch == 'master' || currentBranch == 'dev'
}

timestamps {
    ansiColor('xterm') {
        node {
            stage('Setup') {
                checkout scm
            }

            stage('🔧 Build') {
                try {
                    def goals = 'install'
                    // the -B flag disables download progress logs
                    sh "./mvnw -B install"
                    if (isBaseBranch()) {
                          sh "./mvnw -B -f bonita-userfilter-package/pom.xml -B deploy -DaltDeploymentRepository=${env.ALT_DEPLOYMENT_REPOSITORY_SNAPSHOTS}"
                    }
                } finally {
                    junit '**/target/surefire-reports/*.xml,**/target/failsafe-reports/*.xml'
                    archiveArtifacts '**/target/*.zip'
                }
            }
        }
    }
}