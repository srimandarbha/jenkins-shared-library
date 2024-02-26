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
            string name: 'ProjectUrl', trim: true
            choice choices: ['PROD', 'UAT', 'DEV'], name: 'Environment'
        }

        environment
                {
                    ENV_VARS = ""
                    ARTIFACT = ""
                }
        stages {
            stage("PreRequisite Checks") {
                steps {
                    script {
                        echo "Conducting PythonApp deployment prechecks"

                        ENV_VARS = [changeNo:'###', repoUrl: '', gitOrg: '', gitRepo: '', runTests: true, gitCollect: false, notify: true]
                        ARTIFACT = [:]

                        if (env.GIT_URL)
                        {
                            ENV_VARS.gitOrg = env.GIT_URL.split('uk.hsbc/').last().split('/').first();
                            ENV_VARS.gitRepo = env.GIT_URL.split('uk.hsbc/').last().split('/').last().replace('.git','')
                        }
                    }
                }
            }
            stage("Repo fetch") {
                steps {
                    script {
                        info("${env.BUILD_NUMBER}")
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
}

def info(msg){
    Date date = new Date()
    timenow = date.toString()
    println("${timenow} INFO: ${msg}")
}