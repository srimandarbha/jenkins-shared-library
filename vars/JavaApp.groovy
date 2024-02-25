import com.shared.lib

def call(msg="hello") {
    Common common = new Common()
    common.log "${msg}"
}