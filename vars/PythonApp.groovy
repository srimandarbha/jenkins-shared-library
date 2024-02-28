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
            string(name: 'repoUrl', defaultValue: '', description: 'repository URL')
            choice(choices: ['PROD', 'UAT', 'DEV'], name: 'Environment')
        }

        stages {
            stage("PreRequisite Checks") {
                steps {
                    script {
                        echo "Conducting PythonApp deployment prechecks"
                        ENV_VARS = [changeNo: '###', repoUrl: '', gitOrg: '', gitRepo: '', runTests: true, gitPull: false, notify: true]
                        ARTIFACT = [:]
                        ENV_VARS.repoUrl = params.repoUrl
                        if (ENV_VARS.repoUrl == null) {
                            error("PLEASE SET REPOSITORY URL TO FURTHER PROCEED")
                        }
                        echo "testing.yaml reading"
                        data = readYaml file: "testing.yaml"
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
                        def scannerHome = tool 'sonar-scanner';
                        withSonarQubeEnv(credentialsId: 'sonar', installationName: 'sonar-scanner') {
                            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=toDo"
                        }

                    }
                }
            }
            stage("maven upload") {
                steps {
                    script {
                        info("maven push")
                        sh "tar -cvzf django_todo_master-1.0.tgz ."
                        sh "curl -v -u deploy:deploy --upload-file django_todo_master-1.0.tgz http://172.17.0.2:8081/repository/apps/django_todo/1.0/django_todo-1.tgz"
                    }
                }
            }
            stage("Deployment") {
                steps {
                    script {
                        info("Deployment to Ansible")
                        echo "currentBuild.result ${currentBuild.result}"
                    }
                }
            }
            stage("running pytests") {
                steps {
                    script {
                        sh 'python -m pytest -W ignore::UserWarning'
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