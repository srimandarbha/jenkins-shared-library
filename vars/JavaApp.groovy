import com.shared.lib.Common

def call(msg="hello") {
    Common common = new Common()
    pipeline {
        agent none
        stages {
            stage("test"){
                steps {
                    step {
                        script {
                            sh "${common.MAVEN}"
                        }
                    }
                }
            }
        }
    }
}

def log(msg){
    println("INFO: ${msg}")
}