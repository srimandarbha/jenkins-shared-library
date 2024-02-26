def call(repo) {
    pipeline {
        agent any
        stages {
            stage("testing") {
                steps{
                    script {
                        shell "${repo}"
                    }
                }
            }
        }
    }
}
