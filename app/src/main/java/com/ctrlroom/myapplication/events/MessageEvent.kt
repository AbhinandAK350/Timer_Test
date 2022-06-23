package com.ctrlroom.myapplication.events

class MessageEvent(s: String) {

    private var mMessage: String? = s

    fun getMessage(): String? {
        return mMessage
    }

}
