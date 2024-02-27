def call(Map config = [:]) {
    // Load JMeter helper class
    def jmeterHome = config.jmeterHome ?: error('JMeter home directory is required')
    def jmxFile = config.jmxFile ?: error('JMeter test plan file is required')
    def reportFile = config.reportFile ?: 'jmeter_report.jtl'

    def JmeterBinfile = new File("${jmeterHome}/bin/jmeter")

    if (! JmeterBinfile.exists()) {
        error("Jmeter binary doesnt exist in path ${jmeterHome}")
    }
    sh "${jmeterHome}/bin/jmeter -n -t ${jmxFile} -l ${reportFile}"
}
