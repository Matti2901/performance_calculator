package guide.clone.six;

import guide.kvmsg.kvmsg;
import org.zeromq.ZLoop;
import org.zeromq.ZMQ;

//  .split heartbeating
//  We send a HUGZ message once a second to all subscribers so that they
//  can detect if our server dies. They'll then switch over to the backup
//  server, which will become active:
class SendHugz6 implements ZLoop.IZLoopHandler {
    @Override
    public int handle(ZLoop loop, ZMQ.PollItem item, Object arg) {
        clonesrv6 srv = (clonesrv6) arg;

        kvmsg msg = new kvmsg(srv.sequence);
        msg.setKey("HUGZ");
        msg.setBody(ZMQ.MESSAGE_SEPARATOR);
        msg.send(srv.publisher);
        msg.destroy();

        return 0;
    }
}
