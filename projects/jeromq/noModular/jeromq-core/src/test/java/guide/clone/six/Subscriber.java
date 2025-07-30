package guide.clone.six;

import guide.kvmsg.kvmsg;
import org.zeromq.SocketType;
import org.zeromq.ZLoop;
import org.zeromq.ZMQ;

import java.util.HashMap;

//  .split subscriber handler
//  When we get an update, we create a new kvmap if necessary, and then
//  add our update to our kvmap. We're always passive in this case:
class Subscriber implements ZLoop.IZLoopHandler {
    @Override
    public int handle(ZLoop loop, ZMQ.PollItem item, Object arg) {
        clonesrv6 srv = (clonesrv6) arg;
        ZMQ.Socket socket = item.getSocket();

        //  Get state snapshot if necessary
        if (srv.kvmap == null) {
            srv.kvmap = new HashMap<>();
            ZMQ.Socket snapshot = srv.ctx.createSocket(SocketType.DEALER);
            snapshot.connect(String.format("tcp://localhost:%d", srv.peer));

            System.out.printf("I: asking for snapshot from: tcp://localhost:%d\n", srv.peer);
            snapshot.sendMore("ICANHAZ?");
            snapshot.send(""); // blank subtree to get all

            while (true) {
                kvmsg msg = kvmsg.recv(snapshot);
                if (msg == null)
                    break; //  Interrupted
                if (msg.getKey().equals("KTHXBAI")) {
                    srv.sequence = msg.getSequence();
                    msg.destroy();
                    break; //  Done
                }
                msg.store(srv.kvmap);
            }
            System.out.printf("I: received snapshot=%d\n", srv.sequence);
            snapshot.close();

        }

        //  Find and remove update off pending list
        kvmsg msg = kvmsg.recv(item.getSocket());
        if (msg == null)
            return 0;

        if (!msg.getKey().equals("HUGZ")) {
            if (!srv.wasPending(msg)) {
                //  If active update came before client update, flip it
                //  around, store active update (with sequence) on pending
                //  list and use to clear client update when it comes later
                srv.pending.add(msg.dup());
            }
            //  If update is more recent than our kvmap, apply it
            if (msg.getSequence() > srv.sequence) {
                srv.sequence = msg.getSequence();
                msg.store(srv.kvmap);
                System.out.printf("I: received update=%d\n", srv.sequence);
            }
        }
        msg.destroy();

        return 0;
    }
}
