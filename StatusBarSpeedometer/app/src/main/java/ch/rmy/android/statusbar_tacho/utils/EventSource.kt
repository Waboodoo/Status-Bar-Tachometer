package ch.rmy.android.statusbar_tacho.utils

import java.util.*

class EventSource<T> {

    private val observers = HashSet<Observer<T>>()

    fun add(observer: Observer<T>): Destroyable {
        observers.add(observer)
        return object : Destroyable {
            override fun destroy() {
                observers.remove(observer)
            }
        }
    }

    fun bind(observer: Observer<T>, initialValue: T): Destroyable {
        observer.on(initialValue)
        return add(observer)
    }

    fun notify(item: T) {
        for (observer in observers) {
            observer.on(item)
        }
    }

    interface Observer<T> {

        fun on(item: T)

    }

}
