package com.shared.lib

class Utils implements Serializable {
    Utils(shell){
        this.shell = shell
    }
    def Stdout() {
        print ("shout from Utils.groovy ")
    }
}