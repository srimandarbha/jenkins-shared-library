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
            stage("PreRequisite Checks") {
                steps {
                    script {
                        echo "Conducting PythonApp deployment prechecks"
                        ENV_VARS = [ changeNo: '###', repoUrl: '', gitOrg: '', gitRepo: '', runTests: true, gitPull: false, notify: true ]
                        ARTIFACT = [:]
                        ENV_VARS.repoUrl = params.repoUrl
                        if (ENV_VARS.repoUrl == null) {
                            error("PLEASE SET REPOSITORY URL TO FURTHER PROCEED")
                        }
                        echo "Checks for jenkins_config.yaml"
                        data = readYaml file: "jenkins_config.yaml"
                        echo "${data}"
                        /*
                        if (ENV_VARS.repoUrl) {
                            if ( ENV_VARS.repoUrl.contains("http") ) {
                                ENV_VARS.gitOrg = env.repoURL.split('https://').last().split('/').first()
                                ENV_VARS.gitRepo = env.repoURL.split('http://').last().split('/').last().replace('.git', '')
                            } else if ( ENV_VARS.repoUrl.contains("git@") ) {
                                ENV_VARS.gitOrg = env.repoURL.split('git@').last().split('/').first()
                                ENV_VARS.gitRepo = env.repoURL.split('git@').last().split('/').last().replace('.git', '')
                            } else {
                                ENV_VARS.gitOrg = env.repoURL.split('uk.hsbc/').last().split('/').first()
                                ENV_VARS.gitRepo = env.repoURL.split('uk.hsbc/').last().split('/').last().replace('.git', '')
                            }
                            ENV_VARS.gitOrg=''
                            ENV_VARS.gitRepo=''
                            ENV_VARS.gitPull=true

                        } */
                        if (ENV_VARS.repoUrl) {
                            ENV_VARS.gitPull = true
                            ENV_VARS.app_name = 'django_todo'
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
            stage("Repo fetch") {
                steps {
                    script {
                        info("${ENV_VARS.gitOrg} ${ENV_VARS.gitRepo}")
                        if (ENV_VARS.gitPull) {
                            //Git(repoUrl="${params.repoUrl}")
                            checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: "${ENV_VARS.repoUrl}"]])
                        }
                    }
                }
            }
            stage("sonarqube checks") {
                steps {
                    script {
                        info("sonarqube report running")
                        def scannerHome = tool "${ENV_VARS.jenkins_sonar_toolname}";
                        withSonarQubeEnv(credentialsId: 'sonar', installationName: 'sonar-scanner') {
                            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=toDo"
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
                        info("maven push")
                        sh "git archive --format=tar main > ${ENV_VARS.app_name}-${ENV_VARS.app_version}.tar"
                        sh "curl -u ${ENV_VARS.nexus_user}:${ENV_VARS.nexus_pass} --upload-file ${ENV_VARS.app_name}-${ENV_VARS.app_version}.tar ${ENV_VARS.nexus_server}/repository/${ENV_VARS.nexus_server_repo}/${ENV_VARS.app_name}/${ENV_VARS.app_version}/${ENV_VARS.app_name}-${ENV_VARS.app_version}.tar"
                    }
                }
            }
            stage("Deployment") {
                steps {
                    script {
                        info("Deployment to Ansible")
                    }
                }
            }
            stage("running pytests") {
                steps {
                    script {
                        echo 'python pytest'
                        //sh 'python -m pytest -W ignore::UserWarning'
                    }
                }
            }
            stage("Jmeter checks") {
                steps {
                    script {
                        sh "/var/jenkins_home/apache-jmeter-5.6.3/bin/jmeter -j jmeter.save.saveservice.output_format=xml -n -t basic_test.jmx -l jenkins.io.report.jtl"
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