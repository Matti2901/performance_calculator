package guide.clone.base;

import guide.kvmsg.kvmsg;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class Agent {
    private final ZContext ctx;        //  Context wrapper
    private final ZMQ.Socket pipe;       //  Pipe back to application
    private final Map<String, String> kvmap;      //  Actual key/value table
    String subtree;    //  Subtree specification, if any
    final Server[] server;
    int nbrServers; //  0 to SERVER_MAX
    int state;      //  Current state
    int curServer;  //  If active, server 0 or 1
    long sequence;   //  Last kvmsg processed
    private final ZMQ.Socket publisher;  //  Outgoing updates

    protected Agent(ZContext ctx, ZMQ.Socket pipe) {
        this.ctx = ctx;
        this.pipe = pipe;
        kvmap = new HashMap<>();
        subtree = "";
        state = clone.STATE_INITIAL;
        publisher = ctx.createSocket(SocketType.PUB);

        server = new Server[clone.SERVER_MAX];
    }

    protected void destroy() {
        for (int serverNbr = 0; serverNbr < nbrServers; serverNbr++)
            server[serverNbr].destroy();
    }

    //  .split handling a control message
    //  Here we handle the different control messages from the frontend;
    //  SUBTREE, CONNECT, SET, and GET:
    boolean controlMessage() {
        ZMsg msg = ZMsg.recvMsg(pipe);
        String command = msg.popString();
        if (command == null)
            return false; //  Interrupted

        if (command.equals("SUBTREE")) {
            subtree = msg.popString();
        } else if (command.equals("CONNECT")) {
            String address = msg.popString();
            String service = msg.popString();
            if (nbrServers < clone.SERVER_MAX) {
                server[nbrServers++] = new Server(ctx, address, Integer.parseInt(service), subtree);
                //  We broadcast updates to all known servers
                publisher.connect(String.format("%s:%d", address, Integer.parseInt(service) + 2));
            } else System.out.printf("E: too many servers (max. %d)\n", clone.SERVER_MAX);
        } else
            //  .split set and get commands
            //  When we set a property, we push the new key-value pair onto
            //  all our connected servers:
            if (command.equals("SET")) {
                String key = msg.popString();
                String value = msg.popString();
                String ttl = msg.popString();
                kvmap.put(key, value);

                //  Send key-value pair on to server
                kvmsg kvmsg = new kvmsg(0);
                kvmsg.setKey(key);
                kvmsg.setUUID();
                kvmsg.fmtBody("%s", value);
                kvmsg.setProp("ttl", ttl);
                kvmsg.send(publisher);
                kvmsg.destroy();
            } else if (command.equals("GET")) {
                String key = msg.popString();
                String value = kvmap.get(key);
                pipe.send(Objects.requireNonNullElse(value, ""));
            }
        msg.destroy();

        return true;
    }
}
