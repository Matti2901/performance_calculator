package guide.rt;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Worker implements Runnable {
    private final byte[] END = "END".getBytes(ZMQ.CHARSET);

    public void run() {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket worker = context.createSocket(SocketType.REQ);
            // worker.setIdentity(); will set a random id automatically
            worker.connect("ipc://routing.ipc");

            int total = 0;
            while (true) {
                worker.send("ready", 0);
                byte[] workerload = worker.recv(0);
                if (new String(workerload, ZMQ.CHARSET).equals("END")) {
                    System.out.printf("Processs %d tasks.%n", total);
                    break;
                }
                total += 1;
            }
        }
    }
}
