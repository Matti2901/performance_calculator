package guide.ibbroker.three;

import org.zeromq.ZLoop;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

/**
 * In the reactor design, each time a message arrives on a socket, the
 * reactor passes it to a handler function. We have two handlers; one
 * for the frontend, one for the backend:
 */
class FrontendHandler implements ZLoop.IZLoopHandler {

    @Override
    public int handle(ZLoop loop, ZMQ.PollItem item, Object arg_) {
        LBBroker33 arg = (LBBroker33) arg_;
        ZMsg msg = ZMsg.recvMsg(arg.frontend);
        if (msg != null) {
            msg.wrap(arg.workers.poll());
            msg.send(arg.backend);

            //  Cancel reader on frontend if we went from 1 to 0 workers
            if (arg.workers.isEmpty()) {
                loop.removePoller(new ZMQ.PollItem(arg.frontend, 0));
            }
        }
        return 0;
    }

}
