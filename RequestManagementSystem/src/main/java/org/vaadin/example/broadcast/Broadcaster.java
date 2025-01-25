package org.vaadin.example.broadcast;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Broadcaster {
    private static final List<BroadcastListener> listeners = new CopyOnWriteArrayList<>();

    public interface BroadcastListener {
        void receiveBroadcast();
    }

    public static void register(BroadcastListener listener) {
        listeners.add(listener);
    }

    public static void unregister(BroadcastListener listener) {
        listeners.remove(listener);
    }

    public static void broadcast() {
        for (BroadcastListener listener : listeners) {
            listener.receiveBroadcast();
        }
    }
}