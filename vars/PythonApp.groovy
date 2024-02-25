import com.shared.lib.Common
import com.shared.lib.Utils
import java.util.Date


def call(String agentType = 'any') {
    Date date = new Date()
    Common common = new Common(this)
    println "hello"
    common.Shout()
    logger("agentType is ${agentType}")
    pipeline {
        agent none
        stages {
            stage("start") {
                steps {
                    script {
                        err("START")
                    }
                }
            }
            stage("process") {
                steps {
                    script {
                        err("processing ${common.MAVEN}")
                    }
                }
            }
            stage("end") {
                steps {
                    script {
                        err("NOTHING DONE")
                    }
                }
            }
        }
    }
}

def err(String msg){
    Date date = new Date()
    timenow = date.toString()
    common.log("${timenow} ERROR: ${msg}")
}

/*
def info(String msg){
    Date date = new Date()
    timenow = date.toString()
    println("${timenow} INFO: ${msg}")
}
*/
def shout(){
    Date date = new Date()
    timenow = date.toString()
    Utils util = new Utils()
    return util.Shout()
}

def logger(String msg) {
    println(date.toString() + msg)
}