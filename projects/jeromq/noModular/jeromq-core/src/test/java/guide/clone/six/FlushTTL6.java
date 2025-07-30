package guide.clone.six;

import guide.kvmsg.kvmsg;
import org.zeromq.ZLoop;
import org.zeromq.ZMQ;

import java.util.ArrayList;

class FlushTTL6 implements ZLoop.IZLoopHandler {
    @Override
    public int handle(ZLoop loop, ZMQ.PollItem item, Object arg) {
        clonesrv6 srv = (clonesrv6) arg;
        if (srv.kvmap != null) {
            for (kvmsg msg : new ArrayList<>(srv.kvmap.values())) {
                srv.flushSingle(msg);
            }
        }
        return 0;
    }
}
