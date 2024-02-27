package com.shared.lib

import java.util.Date

class Common implements Serializable {
    def steps
    Common(steps) {
        this.steps = steps
    }

    def log(msg){
        steps.echo "${msg}"
    }
}
