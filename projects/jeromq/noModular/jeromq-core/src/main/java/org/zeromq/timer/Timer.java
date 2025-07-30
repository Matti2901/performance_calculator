package org.zeromq.timer;

import zmq.util.Timers;

/**
 * Opaque representation of a timer.
 */
public final class Timer {
    private final Timers.Timer delegate;

    Timer(Timers.Timer delegate) {
        this.delegate = delegate;
    }

    /**
     * Changes the interval of the timer.
     * <p>
     * This method is slow, canceling existing and adding a new timer yield better performance.
     *
     * @param interval the new interval of the time.
     * @return true if set, otherwise false.
     */
    public boolean setInterval(long interval) {
        return delegate.setInterval(interval);
    }

    /**
     * Reset the timer.
     * <p>
     * This method is slow, canceling existing and adding a new timer yield better performance.
     *
     * @return true if reset, otherwise false.
     */
    public boolean reset() {
        return delegate.reset();
    }

    /**
     * Cancels a timer.
     *
     * @return true if cancelled, otherwise false.
     */
    public boolean cancel() {
        return delegate.cancel();
    }
}
