package org.zeromq;

public interface IZContext {
    ZMQ.Socket createSocket(SocketType socketType);

    IZContext shadow();

    void destroy();

    Thread.UncaughtExceptionHandler getUncaughtExceptionHandler();
    ZMQ.Poller createPoller(int size);
}
