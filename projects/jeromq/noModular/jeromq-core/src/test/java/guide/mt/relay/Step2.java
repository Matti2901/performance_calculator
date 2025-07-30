package guide.mt.relay;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

class Step2 extends Thread {
    private final ZContext context;

    Step2(ZContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        //  Bind to inproc: endpoint, then start upstream thread
        ZMQ.Socket receiver = context.createSocket(SocketType.PAIR);
        receiver.bind("inproc://step2");

        //  Wait for signal
        receiver.recv(0);
        receiver.close();

        //  Connect to step3 and tell it we're ready
        ZMQ.Socket xmitter = context.createSocket(SocketType.PAIR);
        xmitter.connect("inproc://step3");
        System.out.println("Step 2 ready, signaling step 3");
        xmitter.send("READY", 0);
        xmitter.close();
    }

}
