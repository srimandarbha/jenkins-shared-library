package com.shared.lib

def analyze(Map config = [:]) {
    Common common = new Common()
    def projectKey = config.projectKey ?: error('Project key is required for SonarQube analysis')
    def sonarqubeScannerHome = config.sonarqubeScannerHome ?: error('SonarQube Scanner home directory is required')
    common.sh "${sonarqubeScannerHome}/bin/sonar-scanner -Dsonar.projectKey=${projectKey} ..."
}

def call(Map config = [:]) {
    def projectKey = config.projectKey ?: error('Project key is required for SonarQube analysis')
    def sonarqubeScannerHome = config.sonarqubeScannerHome ?: error('SonarQube Scanner home directory is required')
    def sonarqubeToken = config.sonarqubeToken ?: error('Sonarqube Token missing')
    def sonarqubeUrl = config.sonarqubeUrl ?: error('Sonarqube URL missing')
    sh "${sonarqubeScannerHome}/bin/sonar-scanner -Dsonar.projectKey=${projectKey}  -Dsonar.sources=${sources}   -Dsonar.host.url=${sonarqubeUrl}   -Dsonar.token=${sonarqubeToken}"
}