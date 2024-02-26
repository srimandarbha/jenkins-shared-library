def call(repo) {
    pipeline
         {
          agent any
            }
        stages {
            stage("testing") {
                script {
                    shell "${repo}"
                }
            }
        }
}