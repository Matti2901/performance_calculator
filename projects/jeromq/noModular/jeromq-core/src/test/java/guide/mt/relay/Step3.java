package guide.mt.relay;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

class Step3 extends Thread {
    private final ZContext context;

    Step3(ZContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        //  Bind to inproc: endpoint, then start upstream thread
        ZMQ.Socket receiver = context.createSocket(SocketType.PAIR);
        receiver.bind("inproc://step3");

        //  Wait for signal
        receiver.recv(0);
        receiver.close();

        System.out.println("Step 3 ready");
    }

}
