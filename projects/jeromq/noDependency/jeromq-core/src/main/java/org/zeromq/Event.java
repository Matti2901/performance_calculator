package org.zeromq;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates types of monitoring events.
 */
public enum Event {
    CONNECTED(ZMQ.EVENT_CONNECTED),
    CONNECT_DELAYED(ZMQ.EVENT_CONNECT_DELAYED),
    CONNECT_RETRIED(ZMQ.EVENT_CONNECT_RETRIED),
    LISTENING(ZMQ.EVENT_LISTENING),
    BIND_FAILED(ZMQ.EVENT_BIND_FAILED),
    ACCEPTED(ZMQ.EVENT_ACCEPTED),
    ACCEPT_FAILED(ZMQ.EVENT_ACCEPT_FAILED),
    CLOSED(ZMQ.EVENT_CLOSED),
    CLOSE_FAILED(ZMQ.EVENT_CLOSE_FAILED),
    DISCONNECTED(ZMQ.EVENT_DISCONNECTED),
    MONITOR_STOPPED(ZMQ.EVENT_MONITOR_STOPPED),
    HANDSHAKE_FAILED_NO_DETAIL(ZMQ.HANDSHAKE_FAILED_NO_DETAIL),
    HANDSHAKE_SUCCEEDED(ZMQ.HANDSHAKE_SUCCEEDED),
    HANDSHAKE_FAILED_PROTOCOL(ZMQ.HANDSHAKE_FAILED_PROTOCOL),
    HANDSHAKE_FAILED_AUTH(ZMQ.HANDSHAKE_FAILED_AUTH),
    HANDSHAKE_PROTOCOL(ZMQ.EVENT_HANDSHAKE_PROTOCOL),
    ALL(ZMQ.EVENT_ALL);

    private static final Map<Integer, Event> MAP = new HashMap<>(Event.values().length);

    static {
        for (Event e : Event.values()) {
            MAP.put(e.code, e);
        }
    }

    final int code;

    Event(int code) {
        this.code = code;
    }

    /**
     * Find the {@link Event} associated with the numerical event code.
     *
     * @param event the numerical event code
     * @return the found {@link Event}
     */
    public static Event findByCode(int event) {
        return MAP.getOrDefault(event, ALL);
    }
}
