package zmq.socket.radiodish;

import zmq.core.Ctx;
import zmq.core.Msg;
import zmq.core.SocketBase;
import zmq.exception.ZError;
import zmq.core.ZMQ;
import zmq.pipe.Pipe;
import zmq.socket.pubsub.Dist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Radio extends SocketBase
{
    private final Map<String, List<Pipe>> subscriptions;
    private final Dist dist;

    public Radio(Ctx parent, int tid, int sid)
    {
        super(parent, tid, sid, true);

        options.type = ZMQ.ZMQ_RADIO;

        subscriptions = new HashMap<>();
        dist = new Dist();
    }

    @Override
    public void xattachPipe(Pipe pipe, boolean subscribe2all, boolean isLocallyInitiated)
    {
        assert (pipe != null);

        pipe.setNoDelay();
        dist.attach(pipe);
        xreadActivated(pipe);
    }

    @Override
    public void xreadActivated(Pipe pipe)
    {
        Msg msg = pipe.read();
        while (msg != null) {
            if (msg.isJoin()) {
                if (! subscriptions.containsKey(msg.getGroup())) {
                    subscriptions.put(msg.getGroup(), new ArrayList<>());
                }
                List<Pipe> pipes = subscriptions.get(msg.getGroup());
                pipes.add(pipe);
            }
            else if (msg.isLeave()) {
                List<Pipe> pipes = subscriptions.get(msg.getGroup());
                if (pipes != null) {
                    pipes.remove(pipe);
                    if (pipes.isEmpty()) {
                        subscriptions.remove(msg.getGroup());
                    }
                }
            }

            msg = pipe.read();
        }
    }

    @Override
    public void xwriteActivated(Pipe pipe)
    {
        dist.activated(pipe);
    }

    @Override
    public void xpipeTerminated(Pipe pipe)
    {
        Iterator<Entry<String, List<Pipe>>> i = subscriptions.entrySet().iterator();
        while (i.hasNext()) {
            Entry<String, List<Pipe>> entry = i.next();
            entry.getValue().remove(pipe);
            if (entry.getValue().isEmpty()) {
                i.remove();
            }
        }

        dist.terminated(pipe);
    }

    @Override
    protected boolean xsend(Msg msg)
    {
        //  SERVER sockets do not allow multipart data (ZMQ_SNDMORE)
        if (msg.hasMore()) {
            errno.set(ZError.EINVAL);
            return false;
        }

        dist.unmatch();

        List<Pipe> range = subscriptions.get(msg.getGroup());
        if (range != null) {
            for (Pipe pipe : range) {
                dist.match(pipe);
            }
        }

        dist.sendToMatching(msg);

        return true;
    }

    @Override
    protected Msg xrecv()
    {
        errno.set(ZError.ENOTSUP);

        //  Messages cannot be received from RADIO socket.
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean xhasOut()
    {
        return dist.hasOut();
    }

}
