package guide.clone.six;

import guide.kvmsg.kvmsg;
import org.zeromq.ZLoop;
import org.zeromq.ZMQ;

//  .split handling state changes
//  When we switch from passive to active, we apply our pending list so that
//  our kvmap is up-to-date. When we switch to passive, we wipe our kvmap
//  and grab a new snapshot from the active server:
class NewActive implements ZLoop.IZLoopHandler {
    @Override
    public int handle(ZLoop loop, ZMQ.PollItem item, Object arg) {
        clonesrv6 srv = (clonesrv6) arg;

        srv.active = true;
        srv.passive = false;

        //  Stop subscribing to updates
        ZMQ.PollItem poller = new ZMQ.PollItem(srv.subscriber, ZMQ.Poller.POLLIN);
        srv.bStar.zloop().removePoller(poller);

        //  Apply pending list to own hash table
        for (kvmsg msg : srv.pending) {
            msg.setSequence(++srv.sequence);
            msg.send(srv.publisher);
            msg.store(srv.kvmap);
            System.out.printf("I: publishing pending=%d\n", srv.sequence);
        }

        return 0;
    }
}
