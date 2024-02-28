def call(Map args = [:]) {
    def url = args.containsKey('url') ? args.url : error('downloadWithCurl: url parameter is required')
    def output = args.containsKey('output') ? args.output : error('downloadWithCurl: output parameter is required')
    if(isInstalled(tool: 'curl', flag: '--version')) {
        cmd(label: 'download tool', script: "curl -sSLo ${output} --retry 3 --retry-delay 2 --max-time 10 ${url}")
        return true
    } else {
        log(level: 'WARN', text: 'Curl is not available.')
    }
    return false
}