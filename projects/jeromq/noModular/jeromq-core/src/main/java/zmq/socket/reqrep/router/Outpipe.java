package zmq.socket.reqrep.router;

import zmq.pipe.Pipe;

class Outpipe {
    final Pipe pipe;
    boolean active;

    public Outpipe(Pipe pipe, boolean active) {
        this.pipe = pipe;
        this.active = active;
    }
}
