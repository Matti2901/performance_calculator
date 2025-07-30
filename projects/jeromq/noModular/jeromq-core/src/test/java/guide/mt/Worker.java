package guide.mt;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

class Worker extends Thread {
    private final ZContext context;

    Worker(ZContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        ZMQ.Socket socket = context.createSocket(SocketType.REP);
        socket.connect("inproc://workers");

        while (true) {

            //  Wait for next request from client (C string)
            String request = socket.recvStr(0);
            System.out.println(Thread.currentThread().getName() + " Received request: [" + request + "]");

            //  Do some 'work'
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            //  Send reply back to client (C string)
            socket.send("world", 0);
        }
    }
}
