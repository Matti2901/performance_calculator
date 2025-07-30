package guide.clone.five;

import java.util.HashMap;
import java.util.Map;

import guide.kvmsg.kvmsg;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZLoop;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Socket;

//  Clone server - Model Five
public class clonesrv5
{
    private final ZContext           ctx;       //  Context wrapper
    final Map<String, kvmsg> kvmap;     //  Key-value store
    private final ZLoop              loop;      //  zloop reactor
    long               sequence;  //  How many updates we're at
    private final Socket             snapshot;  //  Handle snapshot requests
    final Socket             publisher; //  Publish updates to clients
    private final Socket             collector; //  Collect updates from clients

    public clonesrv5()
    {
        //  Main port we're working on
        int port = 5556;
        ctx = new ZContext();
        kvmap = new HashMap<>();
        loop = new ZLoop(ctx);
        loop.verbose(false);

        //  Set up our clone server sockets
        snapshot = ctx.createSocket(SocketType.ROUTER);
        snapshot.bind(String.format("tcp://*:%d", port));
        publisher = ctx.createSocket(SocketType.PUB);
        publisher.bind(String.format("tcp://*:%d", port + 1));
        collector = ctx.createSocket(SocketType.PULL);
        collector.bind(String.format("tcp://*:%d", port + 2));
    }

    public void run()
    {
        //  Register our handlers with reactor
        PollItem poller = new PollItem(snapshot, ZMQ.Poller.POLLIN);
        loop.addPoller(poller, new Snapshots5(), this);
        poller = new PollItem(collector, ZMQ.Poller.POLLIN);
        loop.addPoller(poller, new Collector5(), this);
        loop.addTimer(1000, 0, new FlushTTL5(), this);

        loop.start();
        loop.destroy();
        ctx.destroy();
    }

    //  We call this function for each getKey-value pair in our hash table
    static void sendSingle(kvmsg msg, byte[] identity, String subtree, Socket socket)
    {
        if (msg.getKey().startsWith(subtree)) {
            socket.send(identity, //  Choose recipient
                    ZMQ.SNDMORE);
            msg.send(socket);
        }
    }

    //  .split flush ephemeral values
    //  At regular intervals, we flush ephemeral values that have expired. This
    //  could be slow on very large data sets:

    //  If getKey-value pair has expired, delete it and publish the
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

    public static void main(String[] args)
    {
        clonesrv5 srv = new clonesrv5();
        srv.run();
    }
}
