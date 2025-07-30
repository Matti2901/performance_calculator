package guide.clone.two;

import guide.kvmsg.kvsimple;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZThread;

import java.util.LinkedHashMap;
import java.util.Map;

public class StateManager2 implements ZThread.IAttachedRunnable {
    private static final Map<String, kvsimple> kvMap = new LinkedHashMap<>();

    @Override
    public void run(Object[] args, ZContext ctx, ZMQ.Socket pipe) {
        pipe.send("READY"); // optional

        ZMQ.Socket snapshot = ctx.createSocket(SocketType.ROUTER);
        snapshot.bind("tcp://*:5556");

        ZMQ.Poller poller = ctx.createPoller(2);
        poller.register(pipe, ZMQ.Poller.POLLIN);
        poller.register(snapshot, ZMQ.Poller.POLLIN);

        long stateSequence = 0;
        while (!Thread.currentThread().isInterrupted()) {
            if (poller.poll() < 0)
                break; //  Context has been shut down

            // apply state updates from main thread
            if (poller.pollin(0)) {
                kvsimple kvMsg = kvsimple.recv(pipe);
                if (kvMsg == null)
                    break;
                StateManager2.kvMap.put(kvMsg.getKey(), kvMsg);
                stateSequence = kvMsg.getSequence();
            }

            // execute state snapshot request
            if (poller.pollin(1)) {
                byte[] identity = snapshot.recv(0);
                if (identity == null)
                    break;
                String request = new String(snapshot.recv(0), ZMQ.CHARSET);

                if (!request.equals("ICANHAZ?")) {
                    System.out.println("E: bad request, aborting");
                    break;
                }

                for (Map.Entry<String, kvsimple> entry : kvMap.entrySet()) {
                    kvsimple msg = entry.getValue();
                    System.out.println("Sending message " + entry.getValue().getSequence());
                    this.sendMessage(msg, identity, snapshot);
                }

                // now send end message with getSequence number
                System.out.println("Sending state snapshot = " + stateSequence);
                snapshot.send(identity, ZMQ.SNDMORE);
                kvsimple message = new kvsimple("KTHXBAI", stateSequence, ZMQ.MESSAGE_SEPARATOR);
                message.send(snapshot);
            }
        }
    }

    private void sendMessage(kvsimple msg, byte[] identity, ZMQ.Socket snapshot) {
        snapshot.send(identity, ZMQ.SNDMORE);
        msg.send(snapshot);
    }
}
