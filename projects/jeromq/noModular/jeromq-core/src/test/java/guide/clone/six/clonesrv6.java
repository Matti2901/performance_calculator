package guide.clone.six;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import guide.bstar.base.bstar;
import guide.kvmsg.kvmsg;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZLoop;
import org.zeromq.ZLoop.IZLoopHandler;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Socket;

//  Clone server - Model Six
public class clonesrv6
{
    final ZContext           ctx;        //  Context wrapper
    Map<String, kvmsg> kvmap;      //  Key-value store
    final bstar bStar;      //  Bstar reactor core
    long               sequence;   //  How many updates we're at
    final int                peer;       //  Main port of our peer
    final Socket             publisher;  //  Publish updates and hugz
    private final Socket             collector;  //  Collect updates from clients
    final Socket             subscriber; //  Get updates from peer
    final List<kvmsg>        pending;    //  Pending updates from clients
    boolean            active;     //  TRUE if we're active
    boolean            passive;    //  TRUE if we're passive

    public clonesrv6(boolean primary)
    {
        //  TRUE if we're primary
        boolean primary1;
        //  Main port we're working on
        int port;
        if (primary) {
            bStar = new bstar(true, "tcp://*:5003", "tcp://localhost:5004");
            bStar.voter("tcp://*:5556", SocketType.ROUTER, new Snapshots6(), this);

            port = 5556;
            peer = 5566;
        }
        else {
            bStar = new bstar(false, "tcp://*:5004", "tcp://localhost:5003");
            bStar.voter("tcp://*:5566", SocketType.ROUTER, new Snapshots6(), this);

            port = 5566;
            peer = 5556;
        }

        //  Primary server will become first active
        if (primary)
            kvmap = new HashMap<>();

        ctx = new ZContext();
        pending = new ArrayList<>();
        bStar.setVerbose(true);

        //  Set up our clone server sockets
        publisher = ctx.createSocket(SocketType.PUB);
        collector = ctx.createSocket(SocketType.SUB);
        collector.subscribe(ZMQ.SUBSCRIPTION_ALL);
        publisher.bind(String.format("tcp://*:%d", port + 1));
        collector.bind(String.format("tcp://*:%d", port + 2));

        //  Set up our own clone client interface to peer
        subscriber = ctx.createSocket(SocketType.SUB);
        subscriber.subscribe(ZMQ.SUBSCRIPTION_ALL);
        subscriber.connect(String.format("tcp://localhost:%d", peer + 1));
    }

    //  .split main task body
    //  After we've setup our sockets, we register our binary star
    //  event handlers, and then start the bstar reactor. This finishes
    //  when the user presses Ctrl-C or when the process receives a SIGINT
    //  interrupt:
    public void run()
    {
        //  Register state change handlers
        bStar.newActive(new NewActive(), this);
        bStar.newPassive(new NewPassive(), this);

        //  Register our other handlers with the bstar reactor
        PollItem poller = new PollItem(collector, ZMQ.Poller.POLLIN);

        bStar.zloop().addPoller(poller, new Collector6(), this);
        bStar.zloop().addTimer(1000, 0, new FlushTTL6(), this);
        bStar.zloop().addTimer(1000, 0, new SendHugz6(), this);

        //  Start the bstar reactor
        bStar.start();

        //  Interrupted, so shut down
        for (kvmsg value : pending)
            value.destroy();

        bStar.destroy();
        for (kvmsg value : kvmap.values())
            value.destroy();

        ctx.destroy();
    }

    //  Send one state snapshot key-value pair to a socket
    //  Hash item data is our kvmsg object, ready to send
    static void sendSingle(kvmsg msg, byte[] identity, String subtree, Socket socket)
    {
        if (msg.getKey().startsWith(subtree)) {
            socket.send(identity, //  Choose recipient
                    ZMQ.SNDMORE);
            msg.send(socket);
        }
    }

    //  The collector is more complex than in the clonesrv5 example because the
    //  way it processes updates depends on whether we're active or passive.
    //  The active applies them immediately to its kvmap, whereas the passive
    //  queues them as pending:

    //  If message was already on pending list, remove it and return TRUE,
    //  else return FALSE.
    boolean wasPending(kvmsg msg)
    {
        Iterator<kvmsg> it = pending.iterator();
        while (it.hasNext()) {
            if (java.util.Arrays.equals(msg.UUID(), it.next().UUID())) {
                it.remove();
                return true;
            }

        }
        return false;
    }

    //  We purge ephemeral values using exactly the same code as in
    //  the previous clonesrv5 example.
    //  .skip
    //  If key-value pair has expired, delete it and publish the
    //  fact to listening clients.
    void flushSingle(kvmsg msg)
    {
        long ttl = Long.parseLong(msg.getProp("ttl"));
        if (ttl > 0 && System.currentTimeMillis() >= ttl) {
            msg.setSequence(++sequence);
            msg.setBody(ZMQ.MESSAGE_SEPARATOR);
            msg.send(publisher);
            msg.store(kvmap);
            System.out.printf("I: publishing delete=%d\n", sequence);
        }
    }

    //  .split main task setup
    //  The main task parses the command line to decide whether to start
    //  as a primary or backup server. We're using the Binary Star pattern
    //  for reliability. This interconnects the two servers so they can
    //  agree on which one is primary and which one is backup. To allow the
    //  two servers to run on the same box, we use different ports for
    //  primary and backup. Ports 5003/5004 are used to interconnect the
    //  servers. Ports 5556/5566 are used to receive voting events (snapshot
    //  requests in the clone pattern). Ports 5557/5567 are used by the
    //  publisher, and ports 5558/5568 are used by the collector:
    public static void main(String[] args)
    {
        clonesrv6 srv = null;

        if (args.length == 1 && "-p".equals(args[0])) {
            srv = new clonesrv6(true);
        }
        else if (args.length == 1 && "-b".equals(args[0])) {
            srv = new clonesrv6(false);
        }
        else {
            System.out.print("Usage: clonesrv4 { -p | -b }\n");
            System.exit(0);
        }
        srv.run();
    }
}
