package guide.clone.two;

import java.nio.ByteBuffer;
import java.util.Random;

import guide.kvmsg.kvsimple;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZThread;

/**
 * Clone server Model Two
 *
 * @author Danish Shrestha &lt;dshrestha06@gmail.com&gt;
 *
 */
public class clonesrv2
{

    public void run()
    {
        try (ZContext ctx = new ZContext()) {
            Socket publisher = ctx.createSocket(SocketType.PUB);
            publisher.bind("tcp://*:5557");

            Socket updates = ZThread.fork(ctx, new StateManager2());

            Random random = new Random();
            long sequence = 0;
            while (!Thread.currentThread().isInterrupted()) {
                long currentSequenceNumber = ++sequence;
                int key = random.nextInt(10000);
                int body = random.nextInt(1000000);

                ByteBuffer b = ByteBuffer.allocate(4);
                b.asIntBuffer().put(body);

                kvsimple kvMsg = new kvsimple(String.valueOf(key), currentSequenceNumber, b.array()
                );
                kvMsg.send(publisher);
                kvMsg.send(updates); // send a message to State Manager thread.

                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                }
            }

            System.out.printf(" Interrupted\n%d messages out\n", sequence);
        }
    }

    public static void main(String[] args)
    {
        new clonesrv2().run();
    }
}
