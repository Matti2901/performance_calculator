package guide.ibbroker;

import guide.ZHelper;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * While this example runs in a single process, that is just to make
 * it easier to start and stop the example. Each thread has its own
 * context and conceptually acts as a separate process.
 * This is the worker task, using a REQ socket to do load-balancing.
 */
class WorkerTask extends Thread {
    @Override
    public void run() {
        //  Prepare our context and sockets
        try (ZContext context = new ZContext()) {
            ZMQ.Socket worker = context.createSocket(SocketType.REQ);
            ZHelper.setId(worker); //  Set a printable identity

            worker.connect("ipc://backend.ipc");

            //  Tell backend we're ready for work
            worker.send("READY");

            while (!Thread.currentThread().isInterrupted()) {
                String address = worker.recvStr();
                String empty = worker.recvStr();
                assert (empty.isEmpty());

                //  Get request, send reply
                String request = worker.recvStr();
                System.out.println("Worker: " + request);

                worker.sendMore(address);
                worker.sendMore("");
                worker.send("OK");
            }
        }
    }
}
