package zmq.socket.pubsub;

import zmq.*;
import zmq.pipe.Pipe;

public class Pub extends XPub
{
    public Pub(Ctx parent, int tid, int sid)
    {
        super(parent, tid, sid);
        options.type = ZMQUtilConstant.ZMQ_PUB;
    }

    @Override
    protected void xattachPipe(Pipe pipe, boolean subscribeToAll, boolean isLocallyInitiated)
    {
        assert (pipe != null);

        //  Don't delay pipe termination as there is no one
        //  to receive the delimiter.
        pipe.setNoDelay();

        super.xattachPipe(pipe, subscribeToAll, isLocallyInitiated);
    }

    @Override
    protected Msg xrecv()
    {
        errno.set(ZError.ENOTSUP);
        //  Messages cannot be received from PUB socket.
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean xhasIn()
    {
        return false;
    }
}
