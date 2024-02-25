package com.shared.lib

/*
common class
 */
class Common implements Serializable {
    def steps
    def env
    Common(steps,env) {
        this.env = env
        this.steps = steps
    }
    String MAVEN = "Apache Maven 3.3.9"
    def Shout(){
        print("shouting from Common.groovy, with maven version ${MAVEN}")
    }
}
