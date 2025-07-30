package zmq.socket.reqrep.dealer.session;

import zmq.core.Msg;
import zmq.core.Options;
import zmq.core.SocketBase;
import zmq.exception.ZError;
import zmq.io.IOThread;
import zmq.io.SessionBase;
import zmq.io.net.Address;

public class ReqSession extends SessionBase {

    private ReqState state;

    public ReqSession(IOThread ioThread, boolean connect, SocketBase socket, final Options options,
                      final Address addr) {
        super(ioThread, connect, socket, options, addr);

        state = ReqState.BOTTOM;
    }

    @Override
    public boolean pushMsg(Msg msg) {
        //  Ignore commands, they are processed by the engine and should not
        //  affect the state machine.
        if (msg.isCommand()) {
            return true;
        }

        switch (state) {
            case BOTTOM:
                if (msg.hasMore()) {
                    //  In case option ZMQ_CORRELATE is on, allow request_id to be
                    //  transfered as first frame (would be too cumbersome to check
                    //  whether the option is actually on or not).
                    if (msg.size() == 4) {
                        state = ReqState.REQUEST_ID;
                        return super.pushMsg(msg);
                    } else if (msg.size() == 0) {
                        state = ReqState.BODY;
                        return super.pushMsg(msg);
                    }
                }
                break;
            case REQUEST_ID:
                if (msg.hasMore() && msg.size() == 0) {
                    state = ReqState.BODY;
                    return super.pushMsg(msg);
                }
                break;
            case BODY:
                if (msg.hasMore()) {
                    return super.pushMsg(msg);
                }
                if (msg.flags() == 0) {
                    state = ReqState.BOTTOM;
                    return super.pushMsg(msg);
                }
                break;
            default:
                break;
        }
        errno.set(ZError.EFAULT);
        return false;
    }

    @Override
    public void reset() {
        super.reset();
        state = ReqState.BOTTOM;
    }
}
