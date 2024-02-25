import com.shared.lib.Common

def call(msg="hello") {
    Common common = new Common()
    pipeline {
        agent none
        stages {
            stage("test"){
                steps {
                    script {
                        echo "${common.MAVEN}"
                    }
                }
            }
        }
    }
}

def log(msg){
    println("INFO: ${msg}")
}