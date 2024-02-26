import com.shared.lib.Common
import com.shared.lib.Utils
import java.util.Date

Date date = new Date()

def call() {
    pipeline {
        agent none
        options
                {
                    timestamps()
                }
        parameters {
            string(name: 'repoUrl', defaultValue: '', description: 'repository URL')
            choice(choices: ['PROD', 'UAT', 'DEV'], name: 'Environment')
        }

        stages {
            stage("PreRequisite Checks") {
                steps {
                    script {
                        echo "Conducting PythonApp deployment prechecks"
                        echo "${params.repoUrl}"
                        ENV_VARS = [changeNo: '###', repoUrl: '', gitOrg: '', gitRepo: '', runTests: true, gitPull: false, notify: true]
                        ARTIFACT = [:]
                        ENV_VARS.repoUrl = params.repoUrl
                        echo "${ENV_VARS.repoUrl} ${params['repoUrl']}"
                        if (ENV_VARS.repoUrl == null ) {
                            error("PLEASE SET REPOSITORY URL TO FURTHER PROCEED")
                        }
                        if (ENV_VARS.repoUrl) {
                            ENV_VARS.gitOrg=''
                            ENV_VARS.gitRepo=''
                            ENV_VARS.gitPull=true
                           // ENV_VARS.gitOrg = env.repoURL.split('uk.hsbc/').last().split('/').first()
                            //ENV_VARS.gitRepo = env.repoURL.split('uk.hsbc/').last().split('/').last().replace('.git', '')
                        }
                    }
                }
            }
            stage("Repo fetch") {
                steps {
                    script {
                        info("${ENV_VARS.gitOrg} ${ENV_VARS.gitRepo}")
                        echo "${ENV_VARS.gitPull}"
                        if (ENV_VARS.gitPull) {
                            Git(repoUrl="${params.repoURL}",repoDir=".")
                        }
                    }
                }
            }
            stage("sonarqube checks") {
                steps {
                    script {
                        info("sonarqube report running")
                    }
                }
            }
            stage("maven upload") {
                steps {
                    script {
                        info("maven push")
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
            stage("Jmeter checks") {
                steps {
                    script {
                        info("Jmeter report")
                    }
                }
            }
        }
        post {
              always
                      {
                            script {
                                info("email compose with jmeter report and build details")
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