#!/usr/bin/env groovy

def isBaseBranch() {
    def currentBranch = env.BRANCH_NAME
    currentBranch == 'master' || currentBranch == 'dev' || currentBranch == '7.7.x'
}

timestamps {
    ansiColor('xterm') {
        node {
            stage('Setup') {
                checkout scm
            }

            stage('🔧 Build') {
                try {
                    def goals = isBaseBranch() ? 
                        "clean deploy -DaltDeploymentRepository=${env.ALT_DEPLOYMENT_REPOSITORY_SNAPSHOTS}" 
                        : "clean install"
                    // the -B flag disables download progress logs
                    sh "./mvnw -B $goals"
                } finally {
                    try{
                        junit '**/target/surefire-reports/*.xml,**/target/failsafe-reports/*.xml'
                        archiveArtifacts '**/target/*.zip'
                    }catch (Exception e){
                        
                    }
                }
            }
        }
    }
}
