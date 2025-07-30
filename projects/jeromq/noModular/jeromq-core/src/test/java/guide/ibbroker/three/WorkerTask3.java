package guide.ibbroker.three;

import guide.ZHelper;
import org.zeromq.*;

/**
 * Worker using REQ socket to do load-balancing
 */
class WorkerTask3 implements ZThread.IDetachedRunnable {
    @Override
    public void run(Object[] args) {
        //  Prepare our context and sockets
        try (ZContext context = new ZContext()) {
            ZMQ.Socket worker = context.createSocket(SocketType.REQ);
            ZHelper.setId(worker); //  Set a printable identity

            worker.connect("ipc://backend.ipc");

            //  Tell backend we're ready for work
            ZFrame frame = new ZFrame(lbbroker3.WORKER_READY);
            frame.send(worker, 0);

            while (true) {
                ZMsg msg = ZMsg.recvMsg(worker);
                if (msg == null)
                    break;

                msg.getLast().reset("OK");
                msg.send(worker);
            }
        }
    }
}
