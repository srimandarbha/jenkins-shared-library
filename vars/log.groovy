//import com.shared.lib.common
import java.util.Date

def call(String msg){
    Date date = new Date()
    println(date.toString() + msg)
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