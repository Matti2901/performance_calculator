package guide.mt;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

/**
 * Multi threaded Hello World server
 */
public class mtserver
{

    public static void main(String[] args)
    {
        try (ZContext context = new ZContext()) {
            Socket clients = context.createSocket(SocketType.ROUTER);
            clients.bind("tcp://*:5555");

            Socket workers = context.createSocket(SocketType.DEALER);
            workers.bind("inproc://workers");

            for (int thread_nbr = 0; thread_nbr < 5; thread_nbr++) {
                Thread worker = new Worker(context);
                worker.start();
            }

            //  Connect work threads to client threads via a queue
            ZMQ.proxy(clients, workers, null);
        }
    }
}
