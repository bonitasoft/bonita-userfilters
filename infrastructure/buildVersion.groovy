pipeline {
    agent any
    options {
        timestamps()
    }
    stages {
        stage('Build and deploy') {
            steps {
                sh("./mvnw deploy -DaltDeploymentRepository=${env.ALT_DEPLOYMENT_REPOSITORY_TAG}")
            }
        }
    }
}