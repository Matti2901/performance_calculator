package zmq.core.socket;

import zmq.core.SocketBase;
import zmq.core.event.Event;
import zmq.core.event.EventConsummer;

/**
 * The old consumer that forward events through a socket
 */
public class SocketEventHandler implements EventConsummer {
    private final SocketBase monitorSocket;

    public SocketEventHandler(SocketBase monitorSocket) {
        this.monitorSocket = monitorSocket;
    }

    public void consume(Event ev) {
        ev.write(monitorSocket);
    }

    @Override
    public void close() {
        monitorSocket.close();
    }
}
