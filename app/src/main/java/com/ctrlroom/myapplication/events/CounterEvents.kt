package com.ctrlroom.myapplication.events

class CounterEvents(eventType: String) {

    val START = "start"
    val PAUSE = "pause"
    val RESUME = "resume"
    val CANCEL = "cancel"
    private var event = eventType

    fun getEventType(): String {
        return event
    }

}
