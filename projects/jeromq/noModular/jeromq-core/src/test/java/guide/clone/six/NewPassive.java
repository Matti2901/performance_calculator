package guide.clone.six;

import guide.kvmsg.kvmsg;
import org.zeromq.ZLoop;
import org.zeromq.ZMQ;

class NewPassive implements ZLoop.IZLoopHandler {
    @Override
    public int handle(ZLoop loop, ZMQ.PollItem item, Object arg) {
        clonesrv6 srv = (clonesrv6) arg;

        if (srv.kvmap != null) {
            for (kvmsg msg : srv.kvmap.values())
                msg.destroy();
        }
        srv.active = false;
        srv.passive = true;

        //  Start subscribing to updates
        ZMQ.PollItem poller = new ZMQ.PollItem(srv.subscriber, ZMQ.Poller.POLLIN);
        srv.bStar.zloop().addPoller(poller, new Subscriber(), srv);
        return 0;
    }
}
