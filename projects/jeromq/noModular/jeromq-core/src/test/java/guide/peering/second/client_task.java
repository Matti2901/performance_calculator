package guide.peering.second;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

//  The client task does a request-reply dialog using a standard
//  synchronous REQ socket:
class client_task extends Thread {
    @Override
    public void run() {
        try (ZContext ctx = new ZContext()) {
            ZMQ.Socket client = ctx.createSocket(SocketType.REQ);
            client.connect(String.format("ipc://%s-localfe.ipc", peering2.self));

            while (true) {
                //  Send request, get reply
                client.send("HELLO", 0);
                String reply = client.recvStr(0);
                if (reply == null)
                    break; //  Interrupted
                System.out.printf("Client: %s\n", reply);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
