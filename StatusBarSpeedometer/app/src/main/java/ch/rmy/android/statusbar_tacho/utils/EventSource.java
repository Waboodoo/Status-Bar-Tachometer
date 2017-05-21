package ch.rmy.android.statusbar_tacho.utils;

import java.util.HashSet;
import java.util.Set;

public class EventSource<T> {

    private final Set<Observer<T>> observers = new HashSet<>();

    public Destroyable add(final Observer<T> observer) {
        observers.add(observer);
        return new Destroyable() {
            @Override
            public void destroy() {
                observers.remove(observer);
            }
        };
    }

    public Destroyable bind(final Observer<T> observer, T initialValue) {
        observer.on(initialValue);
        return add(observer);
    }

    public void notify(T item) {
        for (Observer observer : observers) {
            observer.on(item);
        }
    }

    public interface Observer<T> {

        void on(T item);

    }

}
