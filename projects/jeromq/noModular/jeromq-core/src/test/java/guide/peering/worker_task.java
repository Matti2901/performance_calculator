package guide.peering;

import org.zeromq.*;

import java.util.Random;

class worker_task extends Thread {
    @Override
    public void run() {
        Random rand = new Random(System.nanoTime());
        try (ZContext ctx = new ZContext()) {
            ZMQ.Socket worker = ctx.createSocket(SocketType.REQ);
            worker.connect(String.format("ipc://%s-localbe.ipc", peering3.self));

            //  Tell broker we're ready for work
            ZFrame frame = new ZFrame(peering3.WORKER_READY);
            frame.send(worker, 0);

            while (true) {
                //  Send request, get reply
                ZMsg msg = ZMsg.recvMsg(worker, 0);
                if (msg == null)
                    break; //  Interrupted

                //  Workers are busy for 0/1 seconds
                try {
                    Thread.sleep(rand.nextInt(2) * 1000);
                } catch (InterruptedException e) {
                }

                msg.send(worker);
            }
        }
    }
}
