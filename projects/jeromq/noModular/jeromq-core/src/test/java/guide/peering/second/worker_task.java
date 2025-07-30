package guide.peering.second;

import org.zeromq.*;

class worker_task extends Thread {
    @Override
    public void run() {
        try (ZContext ctx = new ZContext()) {
            ZMQ.Socket worker = ctx.createSocket(SocketType.REQ);
            worker.connect(String.format("ipc://%s-localbe.ipc", peering2.self));

            //  Tell broker we're ready for work
            ZFrame frame = new ZFrame(peering2.WORKER_READY);
            frame.send(worker, 0);

            while (true) {
                //  Send request, get reply
                ZMsg msg = ZMsg.recvMsg(worker, 0);
                if (msg == null)
                    break; //  Interrupted
                msg.getLast().print("Worker: ");
                msg.getLast().reset("OK");
                msg.send(worker);

            }
        }
    }
}
