package guide.clone.six;

import guide.kvmsg.kvmsg;
import org.zeromq.ZLoop;
import org.zeromq.ZMQ;

class Collector6 implements ZLoop.IZLoopHandler {
    @Override
    public int handle(ZLoop loop, ZMQ.PollItem item, Object arg) {
        clonesrv6 srv = (clonesrv6) arg;
        ZMQ.Socket socket = item.getSocket();

        kvmsg msg = kvmsg.recv(socket);
        if (msg != null) {
            if (srv.active) {
                msg.setSequence(++srv.sequence);
                msg.send(srv.publisher);
                int ttl = Integer.parseInt(msg.getProp("ttl"));
                if (ttl > 0)
                    msg.setProp("ttl", "%d", System.currentTimeMillis() + ttl * 1000);
                msg.store(srv.kvmap);
                System.out.printf("I: publishing update=%d\n", srv.sequence);
            } else {
                //  If we already got message from active, drop it, else
                //  hold on pending list
                if (srv.wasPending(msg))
                    msg.destroy();
                else srv.pending.add(msg);
            }
        }

        return 0;
    }
}
