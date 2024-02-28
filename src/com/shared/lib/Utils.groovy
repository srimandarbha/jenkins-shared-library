package com.shared.lib

class Utils implements Serializable {
    def steps
    Utils(steps){
        this.steps = steps
    }
    def Stdout() {
        println("shout from Utils.groovy ")
    }
}

