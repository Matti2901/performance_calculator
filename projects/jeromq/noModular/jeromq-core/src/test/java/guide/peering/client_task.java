package guide.peering;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

//  This is the client task. It issues a burst of requests and then sleeps
//  for a few seconds. This simulates sporadic activity; when a number of
//  clients are active at once, the local workers should be overloaded. The
//  client uses a REQ socket for requests and also pushes statistics to the
//  monitor socket:
class client_task extends Thread {
    @Override
    public void run() {
        try (ZContext ctx = new ZContext()) {
            ZMQ.Socket client = ctx.createSocket(SocketType.REQ);
            client.connect(String.format("ipc://%s-localfe.ipc", peering3.self));
            ZMQ.Socket monitor = ctx.createSocket(SocketType.PUSH);
            monitor.connect(String.format("ipc://%s-monitor.ipc", peering3.self));
            Random rand = new Random(System.nanoTime());

            ZMQ.Poller poller = ctx.createPoller(1);
            poller.register(client, ZMQ.Poller.POLLIN);

            boolean done = false;
            while (!done) {
                try {
                    Thread.sleep(rand.nextInt(5) * 1000);
                } catch (InterruptedException e1) {
                }
                int burst = rand.nextInt(15);

                while (burst > 0) {
                    String taskId = String.format(
                            "%04X", rand.nextInt(10000)
                    );
                    //  Send request, get reply
                    client.send(taskId, 0);

                    //  Wait max ten seconds for a reply, then complain
                    int rc = poller.poll(10 * 1000);
                    if (rc == -1)
                        break; //  Interrupted

                    if (poller.pollin(0)) {
                        String reply = client.recvStr(0);
                        if (reply == null)
                            break; //  Interrupted
                        //  Worker is supposed to answer us with our task id
                        assert (reply.equals(taskId));
                        monitor.send(String.format("%s", reply), 0);
                    } else {
                        monitor.send(
                                String.format(
                                        "E: CLIENT EXIT - lost task %s", taskId
                                ),
                                0);
                        done = true;
                        break;
                    }
                    burst--;
                }
            }
        }
    }
}
