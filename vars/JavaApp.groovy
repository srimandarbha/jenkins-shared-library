import com.shared.lib.Common

def call(msg="hello") {
    Common common = new Common()
    common.log "${msg}"
}