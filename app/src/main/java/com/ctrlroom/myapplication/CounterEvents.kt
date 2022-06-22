package com.ctrlroom.myapplication

class CounterEvents(eventType: String) {

    final val START = "start"
    final val PAUSE = "pause"
    final val RESUME = "resume"
    final val CANCEL = "cancel"
    private var event = eventType

    fun getEventType(): String {
        return event
    }

}
