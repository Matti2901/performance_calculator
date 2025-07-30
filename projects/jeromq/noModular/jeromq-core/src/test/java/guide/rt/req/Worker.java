package guide.rt.req;

import guide.ZHelper;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

class Worker extends Thread {
    @Override
    public void run() {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket worker = context.createSocket(SocketType.REQ);
            ZHelper.setId(worker); //  Set a printable identity

            worker.connect("tcp://localhost:5671");

            int total = 0;
            while (true) {
                //  Tell the broker we're ready for work
                worker.send("Hi Boss");

                //  Get workload from broker, until finished
                String workload = worker.recvStr();
                boolean finished = workload.equals("Fired!");
                if (finished) {
                    System.out.printf("Completed: %d tasks\n", total);
                    break;
                }
                total++;

                //  Do some random work
                try {
                    Thread.sleep(rtreq.rand.nextInt(500) + 1);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
