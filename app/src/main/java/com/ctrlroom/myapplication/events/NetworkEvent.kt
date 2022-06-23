package com.ctrlroom.myapplication.events

class NetworkEvent(status: String) {

    private var conn = status

    fun getStatus(): String {
        return conn
    }


}