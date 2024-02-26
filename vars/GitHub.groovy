def call(String repoUrl, String repoDir) {
    node {
        git url: repoUrl, branch: 'master', credentialsId: 'your-credentials-id', directory: repoDir
    }
}