package com.shared.lib

class Utils implements Serializable {
    Utils(env,shell){
        this.shell = shell
        this.env = env
    }
    def Stdout() {
        print ("shout from Utils.groovy ")
    }
}