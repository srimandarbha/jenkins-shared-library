import com.shared.lib.Common
import com.shared.lib.Utils
import java.util.Date

Date date = new Date()

def call() {
    pipeline {
        agent any
        options
                {
                    timestamps()
                }
        parameters {
            string(name: 'repoUrl', defaultValue: 'https://github.com/srimandarbha/django_todo', description: 'repository URL')
            choice(choices: ['PROD', 'UAT', 'DEV'], name: 'Environment')
        }

        stages {
            stage("Check if GitHub repo exists") {
                steps {
                    script {
                        echo "Conducting PythonApp deployment prechecks"
                        ENV_VARS = [changeNo: '###', repoUrl: '', gitOrg: '', gitRepo: '', runTests: true, gitPull: false, notify: true]
                        ARTIFACT = [:]
                        ENV_VARS.repoUrl = params.repoUrl
                        if (ENV_VARS.repoUrl == null) {
                            error("PLEASE SET REPOSITORY URL TO FURTHER PROCEED")
                        } else {
                            ENV_VARS.gitPull = true
                        }
                    }
                }
            }
            stage('proceed with Python app github repo pull') {
                steps {
                    script {
                        if ( ENV_VARS.gitPull) {
                            checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: "${ENV_VARS.repoUrl}"]])
                            echo "Checks for jenkins_config.yaml"
                            data = readYaml file: "jenkins_config.yaml"
                            echo "${data.config}"
                        }
                    }
                }
            }
            stage('verifying jenkins pipeline template if defined') {
                when {
                    expression {
                        return fileExists('jenkins_config.yaml') || abortJob("Please define jenkins_config.yaml file under your repository")
                    }
                }

                steps {
                    script{
                        if (ENV_VARS.repoUrl) {
                            ENV_VARS.app_name = data.config.app_name
                            ENV_VARS.app_version = '1.0'
                            ENV_VARS.nexus_user = data.config.nexus.nexus_user
                            ENV_VARS.nexus_pass = data.config.nexus.nexus_pass
                            ENV_VARS.nexus_server = data.config.nexus.nexus_server
                            ENV_VARS.nexus_server_repo = data.config.nexus.nexus_apps
                            ENV_VARS.jenkins_sonar_tool_client = data.config.sonarqube.jenkins_sonar_tool_client
                            ENV_VARS.jenkins_sonar_tool_server = data.config.sonarqube.jenkins_sonar_tool_server
                            ENV_VARS.sonarqube_projectKey = data.config.sonarqube.projectKey
                            ENV_VARS.jmeter_install_base = data.config.jmeter.jmeter_install_base ?: '/var/jenkins_home/apache-jmeter-5.6.3'

                        }
                        else {
                            ENV_VARS.app_name = params.repoUrl.contains('.git') ? params.repoUrl.split('/')[-1][0..-5] : params.repoUrl.split('/')[-1]
                            ENV_VARS.app_version = '1.0'
                            ENV_VARS.nexus_user = 'deploy'
                            ENV_VARS.nexus_pass = 'deploy'
                            ENV_VARS.nexus_server = 'http://172.17.0.2:8081'
                            ENV_VARS.nexus_server_repo = 'apps'
                            ENV_VARS.jenkins_sonar_toolname = 'sonar-scanner'
                        }
                    }
                }
            }
            stage("sonarqube checks") {
                steps {
                    script {
                        info("sonarqube report running")
                        def scannerHome = tool "${ENV_VARS.jenkins_sonar_tool_client}";
                        withSonarQubeEnv(credentialsId: 'sonar', installationName: "${ENV_VARS.jenkins_sonar_tool_client}") {
                            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${ENV_VARS.sonarqube_projectKey}"
                        }

                        /*
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            echo "failure: ${qg.status}"
                            def getURL = readProperties file: '.scannerwork/report-task.txt'

                            echo "SonarQube report tasks url: ${getURL['dashboardUrl']}"

                        }
                         */
                    }
                }
            }
            stage("maven upload") {
                steps {
                    script {
                        info("creating archive of stable python code")
                        sh "git archive --format=tar main > ${ENV_VARS.app_name}-${ENV_VARS.app_version}.tar"
                        sh "curl -u ${ENV_VARS.nexus_user}:${ENV_VARS.nexus_pass} --upload-file ${ENV_VARS.app_name}-${ENV_VARS.app_version}.tar ${ENV_VARS.nexus_server}/repository/${ENV_VARS.nexus_server_repo}/${ENV_VARS.app_name}/${ENV_VARS.app_name}-${ENV_VARS.app_version}.tar"
                        info("pushing artifact to nexus repository ${ENV_VARS.nexus_server}/${ENV_VARS.nexus_server_repo}")
                    }
                }
            }
            stage("Deployment") {
                steps {
                    script {
                        info("Deploy code changes using ansible playbooks")
                    }
                }
            }
            stage("running pytests") {
                steps {
                    script {
                        echo 'python pytest'
                        sh 'python3 -m pytest --junit-xml results.xml'
                    }
                }
            }
            stage("Jmeter checks") {
                steps {
                    script {
                        sh "${ENV_VARS.jmeter_install_base}/bin/jmeter -j jmeter.save.saveservice.output_format=xml -n -t ${ENV_VARS.jmeter_testfile} -l jenkins.io.report.jtl"
                    }
                }
            }
        }
        post {
            always
                    {
                        script {
                            info("email compose with jmeter report and build details")
                            perfReport 'jenkins.io.report.jtl'
                            junit skipMarkingBuildUnstable: true, testResults: 'results.xml'
                        }
                    }
        }
    }
}

def info(msg){
    Date date = new Date()
    timenow = date.toString()
    println("${timenow} INFO: ${msg}")
}
