package guide.clone.five;

import guide.kvmsg.kvmsg;
import org.zeromq.ZLoop;
import org.zeromq.ZMQ;

import java.util.Map;

//  .split snapshot handler
//  This is the reactor handler for the snapshot socket; it accepts
//  just the ICANHAZ? request and replies with a state snapshot ending
//  with a KTHXBAI message:
class Snapshots5 implements ZLoop.IZLoopHandler {
    @Override
    public int handle(ZLoop loop, ZMQ.PollItem item, Object arg) {
        clonesrv5 srv = (clonesrv5) arg;
        ZMQ.Socket socket = item.getSocket();

        byte[] identity = socket.recv();
        if (identity != null) {
            //  Request is in second frame of message
            String request = socket.recvStr();
            String subtree = null;
            if (request.equals("ICANHAZ?")) {
                subtree = socket.recvStr();
            } else System.out.print("E: bad request, aborting\n");

            if (subtree != null) {
                //  Send state socket to client
                for (Map.Entry<String, kvmsg> entry : srv.kvmap.entrySet()) {
                    clonesrv5.sendSingle(entry.getValue(), identity, subtree, socket);
                }

                //  Now send END message with getSequence number
                System.out.printf("I: sending shapshot=%d\n", srv.sequence);
                socket.send(identity, ZMQ.SNDMORE);
                kvmsg kvmsg = new kvmsg(srv.sequence);
                kvmsg.setKey("KTHXBAI");
                kvmsg.setBody(subtree.getBytes(ZMQ.CHARSET));
                kvmsg.send(socket);
                kvmsg.destroy();
            }
        }
        return 0;
    }
}
