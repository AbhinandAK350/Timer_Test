package com.ctrlroom.myapplication.events

class CounterEvents(eventType: String) {

    private var event = eventType

    fun getEventType(): String {
        return event
    }

}
