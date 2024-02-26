package com.shared.lib

import java.util.Date

class Common implements Serializable {
    def steps
    Common(steps) {
        this.steps = steps
    }

    def Shout(){
        Date date = new Date()
        steps.echo "${date.toString()}: shouting from Common.groovy, with maven version ${MAVEN} with build number"
    }
    def log(msg){
        steps.echo "${msg}"
    }

    def getBuild(){
        steps.echo "${env.BUILD_NUMBER}"
    }
}
