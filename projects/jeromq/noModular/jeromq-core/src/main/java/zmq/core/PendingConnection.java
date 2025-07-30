package zmq.core;

import zmq.pipe.Pipe;

public class PendingConnection {
    public final Ctx.Endpoint endpoint;
    public final Pipe connectPipe;
    public final Pipe bindPipe;

    public PendingConnection(Ctx.Endpoint endpoint, Pipe connectPipe, Pipe bindPipe) {
        super();
        this.endpoint = endpoint;
        this.connectPipe = connectPipe;
        this.bindPipe = bindPipe;
    }
}
