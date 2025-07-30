package guide.clone.five;

import guide.kvmsg.kvmsg;
import org.zeromq.ZLoop;
import org.zeromq.ZMQ;

//  .split collect updates
//  We store each update with a new getSequence number, and if necessary, a
//  time-to-live. We publish updates immediately on our publisher socket:
class Collector5 implements ZLoop.IZLoopHandler {
    @Override
    public int handle(ZLoop loop, ZMQ.PollItem item, Object arg) {
        clonesrv5 srv = (clonesrv5) arg;
        ZMQ.Socket socket = item.getSocket();

        kvmsg msg = kvmsg.recv(socket);
        if (msg != null) {
            msg.setSequence(++srv.sequence);
            msg.send(srv.publisher);
            int ttl = Integer.parseInt(msg.getProp("ttl"));
            if (ttl > 0)
                msg.setProp("ttl", "%d", System.currentTimeMillis() + ttl * 1000);
            msg.store(srv.kvmap);
            System.out.printf("I: publishing update=%d\n", srv.sequence);
        }

        return 0;
    }
}
