package zmq.core.event;

/**
 * An interface used to consume events in monitor
 */
public interface EventConsummer {
    void consume(Event ev);

    /**
     * An optional method to close the monitor if needed
     */
    default void close() {
        // Default do nothing
    }
}
