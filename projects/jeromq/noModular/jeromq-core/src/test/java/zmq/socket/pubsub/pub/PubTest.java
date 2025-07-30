package zmq.socket.pubsub.pub;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Assert;
import org.junit.Test;

import zmq.core.Ctx;
import zmq.core.SocketBase;
import zmq.exception.ZError;
import zmq.core.ZMQ;

public class PubTest
{
    @Test
    public void testHasIn()
    {
        Ctx ctx = ZMQ.createContext();
        SocketBase pub = null;
        try {
            pub = ctx.createSocket(ZMQ.ZMQ_PUB);

            int events = pub.getSocketOpt(ZMQ.ZMQ_EVENTS);
            assertThat(events, is(2));
        }
        finally {
            ZMQ.close(pub);
            ZMQ.term(ctx);
        }
    }

    @Test
    public void testRecv()
    {
        Ctx ctx = ZMQ.createContext();
        SocketBase pub = null;
        try {
            pub = ctx.createSocket(ZMQ.ZMQ_PUB);

            pub.recv(ZMQ.ZMQ_DONTWAIT);
            Assert.fail("Pub cannot receive message");
        }
        catch (UnsupportedOperationException e) {
            assertThat(ctx.errno().get(), is(ZError.ENOTSUP));
        }
        finally {
            ZMQ.close(pub);
            ZMQ.term(ctx);
        }
    }
}
