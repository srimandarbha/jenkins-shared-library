import com.shared.lib.Common
import com.shared.lib.Utils
import java.util.Date

Date date = new Date()
Common common = new Common(env,steps)

def call(String agentType = 'any'){
    //Date date = new Date()
    def output = common.Shout(env)
    logger("${output}, agentType is ${agentType}")
    node {
        stage("start") {
            println(date.toString() + shout())
        }
        stage("process") {
            info("processing ${common.MAVEN}")
        }
        stage("end") {
            err("NOTHING DONE")
        }
    }
}

def err(String msg){
    Date date = new Date()
    timenow = date.toString()
    println("${timenow} ERROR: ${msg}")
}

def info(String msg){
    Date date = new Date()
    timenow = date.toString()
    println("${timenow} INFO: ${msg}")
}

def shout(){
    Date date = new Date()
    timenow = date.toString()
    Utils util = new Utils()
    return util.Shout()
}

def logger(String msg) {
    println(date.toString() + msg)
}