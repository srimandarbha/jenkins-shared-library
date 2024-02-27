def call(Map config=[:]){
    Map CrRet = [:]
    if (config.isEmpty()) {
        CrRet.number="CHG000123"
        CrRet.short_description="Dummy CR test"
        CrRet.state="approved"
        CrRet.assigned_to="dummy"
        return CrRet
    }

    def changeRequestId = config.changeRequestId ?: error('PLEASE PROVIDE VALID CR DETAILS')
    def serviceNowBaseUrl = config.serviceNowBaseUrl ?: error('PLEASE PROVIDE VALID CR DETAILS')
    def username = config.username ?: error('PLEASE PROVIDE VALID CR DETAILS')
    def password = config.password ?: error('PLEASE PROVIDE VALID CR DETAILS')

    def response = httpRequest(
            authentication: 'Basic',
            username: username,
            password: password,
            url: "${serviceNowBaseUrl}/api/now/table/change_request/${changeRequestId}",
            contentType: 'APPLICATION_JSON'
    )

    def changeRequest = readJSON(text: response.content)

    if (changeRequest.result == null) {
        error("Change request ${changeRequestId} not found.")
    }

    if (changeRequest.result.state == 'open') {
        CrRet.number="${changeRequest.result.number}"
        CrRet.short_description="${changeRequest.result.short_description}"
        CrRet.state="approved"
        CrRet.assigned_to="${changeRequest.assigned_to}"
    }
    return CrRet
}
