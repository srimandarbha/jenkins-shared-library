import com.shared.lib.Common
import com.shared.lib.Utils
import java.util.Date

def call(String msg, String agentType = 'any'){
    Date date = new Date()
    Common common = new Common(env,steps)
    def output = common.Shout(env)
    println(date.toString() + " " + msg + " ${output}")
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