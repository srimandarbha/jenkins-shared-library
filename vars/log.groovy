import com.shared.lib.Common
import java.util.Date

def call(String msg){
    Date date = new Date()
    Common common = new Common(env,steps)
    def output = common.Shout(env)
    println(date.toString() + " " + msg + " ${output}")
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