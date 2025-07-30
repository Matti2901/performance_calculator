package guide.peering;

import java.util.ArrayList;
import java.util.Random;

import org.zeromq.*;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

//  Broker peering simulation (part 3)
//  Prototypes the full flow of status and tasks

public class peering3
{

    private static final int    NBR_CLIENTS  = 10;
    private static final int    NBR_WORKERS  = 5;
    // Signals worker is ready
    static final String WORKER_READY = "\001";

    //  Our own name; in practice this would be configured per node
    static String self;

    //  This is the worker task, which uses a REQ socket to plug into the LRU
    //  router. It's the same stub worker task you've seen in other examples:

    //  The main task begins by setting-up all its sockets. The local frontend
    //  talks to clients, and our local backend talks to workers. The cloud
    //  frontend talks to peer brokers as if they were clients, and the cloud
    //  backend talks to peer brokers as if they were workers. The state
    //  backend publishes regular state messages, and the state frontend
    //  subscribes to all state backends to collect these messages. Finally,
    //  we use a PULL monitor socket to collect printable messages from tasks:
    public static void main(String[] argv)
    {
        //  First argument is this broker's name
        //  Other arguments are our peers' names
        //
        if (argv.length < 1) {
            System.out.println("syntax: peering3 me {you}");
            System.exit(-1);
        }
        self = argv[0];
        System.out.printf("I: preparing broker at %s\n", self);
        Random rand = new Random(System.nanoTime());

        try (ZContext ctx = new ZContext()) {
            //  Prepare local frontend and backend
            Socket localfe = ctx.createSocket(SocketType.ROUTER);
            localfe.bind(String.format("ipc://%s-localfe.ipc", self));
            Socket localbe = ctx.createSocket(SocketType.ROUTER);
            localbe.bind(String.format("ipc://%s-localbe.ipc", self));

            //  Bind cloud frontend to endpoint
            Socket cloudfe = ctx.createSocket(SocketType.ROUTER);
            cloudfe.setIdentity(self.getBytes(ZMQ.CHARSET));
            cloudfe.bind(String.format("ipc://%s-cloud.ipc", self));

            //  Connect cloud backend to all peers
            Socket cloudbe = ctx.createSocket(SocketType.ROUTER);
            cloudbe.setIdentity(self.getBytes(ZMQ.CHARSET));
            int argn;
            for (argn = 1; argn < argv.length; argn++) {
                String peer = argv[argn];
                System.out.printf(
                    "I: connecting to cloud forintend at '%s'\n", peer
                );
                cloudbe.connect(String.format("ipc://%s-cloud.ipc", peer));
            }

            //  Bind state backend to endpoint
            Socket statebe = ctx.createSocket(SocketType.PUB);
            statebe.bind(String.format("ipc://%s-state.ipc", self));

            //  Connect statefe to all peers
            Socket statefe = ctx.createSocket(SocketType.SUB);
            statefe.subscribe(ZMQ.SUBSCRIPTION_ALL);
            for (argn = 1; argn < argv.length; argn++) {
                String peer = argv[argn];
                System.out.printf(
                    "I: connecting to state backend at '%s'\n", peer
                );
                statefe.connect(String.format("ipc://%s-state.ipc", peer));
            }

            //  Prepare monitor socket
            Socket monitor = ctx.createSocket(SocketType.PULL);
            monitor.bind(String.format("ipc://%s-monitor.ipc", self));

            //  Start local workers
            int worker_nbr;
            for (worker_nbr = 0; worker_nbr < NBR_WORKERS; worker_nbr++)
                new worker_task().start();

            //  Start local clients
            int client_nbr;
            for (client_nbr = 0; client_nbr < NBR_CLIENTS; client_nbr++)
                new client_task().start();

            //  Queue of available workers
            int localCapacity = 0;
            int cloudCapacity = 0;
            ArrayList<ZFrame> workers = new ArrayList<>();

            //  The main loop has two parts. First we poll workers and our two
            //  service sockets (statefe and monitor), in any case. If we have
            //  no ready workers, there's no point in looking at incoming
            //  requests. These can remain on their internal 0MQ queues:
            Poller primary = ctx.createPoller(4);
            primary.register(localbe, Poller.POLLIN);
            primary.register(cloudbe, Poller.POLLIN);
            primary.register(statefe, Poller.POLLIN);
            primary.register(monitor, Poller.POLLIN);

            Poller secondary = ctx.createPoller(2);
            secondary.register(localfe, Poller.POLLIN);
            secondary.register(cloudfe, Poller.POLLIN);

            while (true) {
                //  First, route any waiting replies from workers

                //  If we have no workers anyhow, wait indefinitely
                int rc = primary.poll(localCapacity > 0 ? 1000 : -1);
                if (rc == -1)
                    break; //  Interrupted

                //  Track if capacity changes during this iteration
                int previous = localCapacity;

                //  Handle reply from local worker
                ZMsg msg = null;
                if (primary.pollin(0)) {
                    msg = ZMsg.recvMsg(localbe);
                    if (msg == null)
                        break; //  Interrupted
                    ZFrame address = msg.unwrap();
                    workers.add(address);
                    localCapacity++;

                    //  If it's READY, don't route the message any further
                    ZFrame frame = msg.getFirst();
                    String frameData = new String(frame.getData(), ZMQ.CHARSET);
                    if (frameData.equals(WORKER_READY)) {
                        msg.destroy();
                        msg = null;
                    }
                }
                //  Or handle reply from peer broker
                else if (primary.pollin(1)) {
                    msg = ZMsg.recvMsg(cloudbe);
                    if (msg == null)
                        break; //  Interrupted
                    //  We don't use peer broker address for anything
                    ZFrame address = msg.unwrap();
                    address.destroy();
                }
                //  Route reply to cloud if it's addressed to a broker
                for (argn = 1; msg != null && argn < argv.length; argn++) {
                    byte[] data = msg.getFirst().getData();
                    if (argv[argn].equals(new String(data, ZMQ.CHARSET))) {
                        msg.send(cloudfe);
                        msg = null;
                    }
                }
                //  Route reply to client if we still need to
                if (msg != null)
                    msg.send(localfe);

                //  If we have input messages on our statefe or monitor sockets
                //  we can process these immediately:

                if (primary.pollin(2)) {
                    String peer = statefe.recvStr();
                    String status = statefe.recvStr();
                    cloudCapacity = Integer.parseInt(status);
                }
                if (primary.pollin(3)) {
                    String status = monitor.recvStr();
                    System.out.println(status);
                }

                //  Now we route as many client requests as we have worker
                //  capacity for. We may reroute requests from our local
                //  frontend, but not from the cloud frontend. We reroute
                //  randomly now, just to test things out. In the next version
                //  we'll do this properly by calculating cloud capacity.

                while (localCapacity + cloudCapacity > 0) {
                    rc = secondary.poll(0);

                    assert (rc >= 0);

                    if (secondary.pollin(0)) {
                        msg = ZMsg.recvMsg(localfe);
                    }
                    else if (localCapacity > 0 && secondary.pollin(1)) {
                        msg = ZMsg.recvMsg(cloudfe);
                    }
                    else break; //  No work, go back to backends

                    if (localCapacity > 0) {
                        ZFrame frame = workers.remove(0);
                        msg.wrap(frame);
                        msg.send(localbe);
                        localCapacity--;

                    }
                    else {
                        //  Route to random broker peer
                        int random_peer = rand.nextInt(argv.length - 1) + 1;
                        msg.push(argv[random_peer]);
                        msg.send(cloudbe);
                    }
                }

                //  We broadcast capacity messages to other peers; to reduce
                //  chatter we do this only if our capacity changed.
                if (localCapacity != previous) {
                    //  We stick our own address onto the envelope
                    statebe.sendMore(self);
                    //  Broadcast new capacity
                    statebe.send(String.format("%d", localCapacity), 0);
                }
            }
            //  When we're done, clean up properly
            while (!workers.isEmpty()) {
                ZFrame frame = workers.remove(0);
                frame.destroy();
            }
        }
    }
}
