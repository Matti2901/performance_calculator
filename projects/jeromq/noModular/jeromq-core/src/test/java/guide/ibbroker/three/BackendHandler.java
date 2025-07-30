package guide.ibbroker.three;

import org.zeromq.ZFrame;
import org.zeromq.ZLoop;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.Arrays;

class BackendHandler implements ZLoop.IZLoopHandler {

    @Override
    public int handle(ZLoop loop, ZMQ.PollItem item, Object arg_) {
        LBBroker33 arg = (LBBroker33) arg_;
        ZMsg msg = ZMsg.recvMsg(arg.backend);
        if (msg != null) {
            ZFrame address = msg.unwrap();
            //  Queue worker address for load-balancing
            arg.workers.add(address);

            //  Enable reader on frontend if we went from 0 to 1 workers
            if (arg.workers.size() == 1) {
                ZMQ.PollItem newItem = new ZMQ.PollItem(arg.frontend, ZMQ.Poller.POLLIN);
                loop.addPoller(newItem, lbbroker3.frontendHandler, arg);
            }

            //  Forward message to client if it's not a READY
            ZFrame frame = msg.getFirst();
            if (Arrays.equals(frame.getData(), lbbroker3.WORKER_READY))
                msg.destroy();
            else msg.send(arg.frontend);
        }
        return 0;
    }
}
