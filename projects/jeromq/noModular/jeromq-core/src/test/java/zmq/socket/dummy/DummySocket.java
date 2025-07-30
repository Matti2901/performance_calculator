package zmq.socket.dummy;

import zmq.core.SocketBase;
import zmq.helper.Helper;
import zmq.pipe.Pipe;

public class DummySocket extends SocketBase {
    public DummySocket() {
        super(Helper.ctx, Helper.counter.get(), Helper.counter.get());
        Helper.counter.incrementAndGet();
    }

    @Override
    protected void xattachPipe(Pipe pipe, boolean icanhasall, boolean isLocallyInitiated) {
    }

    @Override
    protected void xpipeTerminated(Pipe pipe) {
    }

}
