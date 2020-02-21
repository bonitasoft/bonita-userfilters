#!/usr/bin/env groovy


def getBranchName() {
    env.BRANCH_NAME ? env.BRANCH_NAME : env.branch
}

def isBaseBranch() {
    def currentBranch = branchName
    currentBranch == 'master' || currentBranch == 'dev' || currentBranch?.startsWith('release-')
}

timestamps {
    ansiColor('xterm') {
        node {
            stage('Setup') {
                checkout scm
            }

            stage('🔧 Build & Test') {
                try {
                    def goals = isBaseBranch() ? 
                        "clean deploy -DaltDeploymentRepository=${env.ALT_DEPLOYMENT_REPOSITORY_SNAPSHOTS}" 
                        : "clean install"
                    // the -B flag disables download progress logs
                    sh "./mvnw -B -Djvm=${env.JAVA_HOME_11}/bin/java $goals"
                } finally {
                    junit '**/target/surefire-reports/*.xml'
                    archiveArtifacts '**/target/*.zip'
                }
            }
        }
    }
}