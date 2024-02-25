//import com.shared.lib.common
import java.util.Date

def call(String msg){
    Date date = new Date()
    println(date.toString() + msg)
}