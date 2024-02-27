def call(String repoUrl) {
    //node {
        //git url: repoUrl, branch: 'master', credentialsId: 'your-credentials-id', directory: repoDir
        git "${repoUrl}"
    //}
}