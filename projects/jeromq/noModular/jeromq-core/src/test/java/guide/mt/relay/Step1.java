package guide.mt.relay;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

class Step1 extends Thread {
    private final ZContext context;

    Step1(ZContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        //  Signal downstream to step 2
        ZMQ.Socket xmitter = context.createSocket(SocketType.PAIR);
        xmitter.connect("inproc://step2");
        System.out.println("Step 1 ready, signaling step 2");
        xmitter.send("READY", 0);
        xmitter.close();
    }

}
