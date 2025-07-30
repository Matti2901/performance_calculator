package guide.ibbroker.three;

import java.util.LinkedList;

import guide.ibbroker.ClientTask3;
import org.zeromq.*;
import org.zeromq.ZMQ.PollItem;

/**
 * Load-balancing broker
 * Demonstrates use of the ZLoop API and reactor style
 * <p>
 * The client and worker tasks are identical from the previous example.
 */
public class lbbroker3
{
    private static final int NBR_CLIENTS  = 10;
    private static final int NBR_WORKERS  = 3;
    static final byte[]    WORKER_READY = { '\001' };

    final static FrontendHandler frontendHandler = new FrontendHandler();
    private final static BackendHandler  backendHandler  = new BackendHandler();

    /**
     * And the main task now sets-up child tasks, then starts its reactor.
     * If you press Ctrl-C, the reactor exits and the main task shuts down.
     */
    public static void main(String[] args)
    {
        //  Prepare our context and sockets
        try (ZContext context = new ZContext()) {
            LBBroker33 arg = new LBBroker33();
            arg.frontend = context.createSocket(SocketType.ROUTER);
            arg.backend = context.createSocket(SocketType.ROUTER);
            arg.frontend.bind("ipc://frontend.ipc");
            arg.backend.bind("ipc://backend.ipc");

            int clientNbr;
            for (clientNbr = 0; clientNbr < NBR_CLIENTS; clientNbr++)
                ZThread.start(new ClientTask3());

            for (int workerNbr = 0; workerNbr < NBR_WORKERS; workerNbr++)
                ZThread.start(new WorkerTask3());

            //  Queue of available workers
            arg.workers = new LinkedList<>();

            //  Prepare reactor and fire it up
            ZLoop reactor = new ZLoop(context);
            PollItem item = new PollItem(arg.backend, ZMQ.Poller.POLLIN);
            reactor.addPoller(item, backendHandler, arg);
            reactor.start();
        }
    }
}
