def call(msg) {
    currentBuild.result = 'ABORTED'
    error(msg)
}
