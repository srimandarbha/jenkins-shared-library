import com.shared.lib.Common

def call(msg="hello") {
    //Common common = new Common()
    pipeline {
        agent any
        stages {
            stage("test"){
                steps {
                    script {
                        echo "hello"
                    }
                }
            }
        }
    }
}

def log(msg){
    println("INFO: ${msg}")
}