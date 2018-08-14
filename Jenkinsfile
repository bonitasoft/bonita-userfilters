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
                    def goals = 'clean install'
                     if (isBaseBranch()) {
                         goals = 'clean deploy -DaltDeploymentRepository=${env.ALT_DEPLOYMENT_REPOSITORY_SNAPSHOTS}'
                     }
                    // the -B flag disables download progress logs
                    sh "./mvnw -B $goal"
                } finally {
                    junit '**/target/surefire-reports/*.xml,**/target/failsafe-reports/*.xml'
                    archiveArtifacts '**/target/*.zip'
                }
            }
        }
    }
}