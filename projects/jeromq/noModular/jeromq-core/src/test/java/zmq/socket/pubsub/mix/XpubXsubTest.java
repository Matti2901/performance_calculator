package zmq.socket.pubsub.mix;

import org.junit.Test;
import zmq.core.Ctx;
import zmq.core.Msg;
import zmq.core.SocketBase;
import zmq.core.ZMQ;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XpubXsubTest
{
    @Test(timeout = 5000)
    public void testXPubSub()
    {
        System.out.println("XPub - Sub");
        final Ctx ctx = zmq.core.ZMQ.createContext();
        assertThat(ctx, notNullValue());

        boolean rc;

        SocketBase sub = ZMQ.socket(ctx, zmq.core.ZMQ.ZMQ_SUB);
        rc = zmq.core.ZMQ.setSocketOption(sub, zmq.core.ZMQ.ZMQ_SUBSCRIBE, "topic");
        assertThat(rc, is(true));
        rc = zmq.core.ZMQ.setSocketOption(sub, ZMQ.ZMQ_SUBSCRIBE, "topix");
        assertThat(rc, is(true));

        SocketBase pub = zmq.core.ZMQ.socket(ctx, zmq.core.ZMQ.ZMQ_XPUB);
        rc = zmq.core.ZMQ.bind(pub, "inproc://1");
        assertThat(rc, is(true));

        String endpoint = (String) ZMQ.getSocketOptionExt(pub, ZMQ.ZMQ_LAST_ENDPOINT);
        assertThat(endpoint, notNullValue());

        rc = ZMQ.connect(sub, endpoint);
        assertThat(rc, is(true));

        System.out.print("Send.");

        rc = pub.send(new Msg("topic".getBytes(ZMQ.CHARSET)), ZMQ.ZMQ_SNDMORE);
        assertThat(rc, is(true));

        rc = pub.send(new Msg("hop".getBytes(ZMQ.CHARSET)), 0);
        assertThat(rc, is(true));

        System.out.print("Recv.");

        Msg msg = sub.recv(0);
        assertThat(msg, notNullValue());
        assertThat(msg.size(), is(5));

        msg = sub.recv(0);
        assertThat(msg, notNullValue());
        assertThat(msg.size(), is(3));

        rc = zmq.core.ZMQ.setSocketOption(sub, zmq.core.ZMQ.ZMQ_UNSUBSCRIBE, "topix");
        assertThat(rc, is(true));

        rc = pub.send(new Msg("topix".getBytes(ZMQ.CHARSET)), ZMQ.ZMQ_SNDMORE);
        assertThat(rc, is(true));

        rc = pub.send(new Msg("hop".getBytes(ZMQ.CHARSET)), 0);
        assertThat(rc, is(true));

        rc = zmq.core.ZMQ.setSocketOption(sub, zmq.core.ZMQ.ZMQ_RCVTIMEO, 500);
        assertThat(rc, is(true));

        msg = sub.recv(0);
        assertThat(msg, nullValue());

        System.out.print("End.");

        zmq.core.ZMQ.close(sub);

        for (int idx = 0; idx < 2; ++idx) {
            rc = pub.send(new Msg("topic abc".getBytes(ZMQ.CHARSET)), 0);
            assertThat(rc, is(true));
            ZMQ.msleep(10);
        }
        zmq.core.ZMQ.close(pub);

        zmq.core.ZMQ.term(ctx);
        System.out.println("Done.");
    }

    @Test(timeout = 5000)
    public void testXPubXSub()
    {
        System.out.println("XPub - XSub");
        final Ctx ctx = ZMQ.createContext();
        assertThat(ctx, notNullValue());

        boolean rc;

        SocketBase pub = ZMQ.socket(ctx, zmq.core.ZMQ.ZMQ_XPUB);
        rc = ZMQ.bind(pub, "inproc://1");
        assertThat(rc, is(true));

        String endpoint = (String) ZMQ.getSocketOptionExt(pub, ZMQ.ZMQ_LAST_ENDPOINT);
        assertThat(endpoint, notNullValue());

        SocketBase sub = zmq.core.ZMQ.socket(ctx, zmq.core.ZMQ.ZMQ_XSUB);
        rc = zmq.core.ZMQ.connect(sub, endpoint);
        assertThat(rc, is(true));

        System.out.print("Send.");

        rc = sub.send(new Msg("\1topic".getBytes(ZMQ.CHARSET)), 0);
        assertThat(rc, is(true));

        rc = pub.send(new Msg("topic".getBytes(ZMQ.CHARSET)), 0);
        assertThat(rc, is(true));

        System.out.print("Recv.");

        rc = sub.send(new Msg("\0topic".getBytes(ZMQ.CHARSET)), 0);
        assertThat(rc, is(true));

//        rc = pub.send(new Msg("topix".getBytes(ZMQ.CHARSET)), ZMQ.ZMQ_SNDMORE);
//        assertThat(rc, is(true));
//
//        rc = pub.send(new Msg("hop".getBytes(ZMQ.CHARSET)), 0);
//        assertThat(rc, is(true));
//
//        rc = zmq.core.ZMQ.setSocketOption(sub, zmq.core.ZMQ.ZMQ_RCVTIMEO, 500);
//        assertThat(rc, is(true));
//
//        msg = sub.recv(0);
//        assertThat(msg, nullValue());

        ZMQ.close(sub);
        zmq.core.ZMQ.close(pub);
        ZMQ.term(ctx);
        System.out.println("Done.");
    }

    @Test(timeout = 5000)
    public void testIssue476() throws InterruptedException, ExecutionException
    {
        System.out.println("Issue 476");

        final Ctx ctx = ZMQ.createContext();
        assertThat(ctx, notNullValue());

        boolean rc;
        final SocketBase proxyPub = zmq.core.ZMQ.socket(ctx, zmq.core.ZMQ.ZMQ_XPUB);
        rc = proxyPub.bind("inproc://1");
        assertThat(rc, is(true));
        final SocketBase proxySub = zmq.core.ZMQ.socket(ctx, zmq.core.ZMQ.ZMQ_XSUB);
        rc = proxySub.bind("inproc://2");
        assertThat(rc, is(true));

        final SocketBase ctrl = zmq.core.ZMQ.socket(ctx, ZMQ.ZMQ_PAIR);
        rc = ctrl.bind("inproc://ctrl-proxy");
        assertThat(rc, is(true));

        ExecutorService service = Executors.newFixedThreadPool(1);

        Future<?> proxy = service.submit(() -> {
            ZMQ.proxy(proxySub, proxyPub, null, ctrl);
        });
        SocketBase sub = zmq.core.ZMQ.socket(ctx, ZMQ.ZMQ_SUB);
        rc = zmq.core.ZMQ.setSocketOption(sub, zmq.core.ZMQ.ZMQ_SUBSCRIBE, "topic");
        assertThat(rc, is(true));

        rc = zmq.core.ZMQ.connect(sub, "inproc://1");
        assertThat(rc, is(true));

        SocketBase pub = zmq.core.ZMQ.socket(ctx, zmq.core.ZMQ.ZMQ_XPUB);

        rc = zmq.core.ZMQ.connect(pub, "inproc://2");
        assertThat(rc, is(true));

        rc = ZMQ.setSocketOption(sub, ZMQ.ZMQ_RCVTIMEO, 100);
        assertThat(rc, is(true));
        sub.recv(0);

        System.out.print("Send.");

        rc = pub.send(new Msg("topic".getBytes(ZMQ.CHARSET)), ZMQ.ZMQ_SNDMORE);
        assertThat(rc, is(true));

        rc = pub.send(new Msg("hop".getBytes(ZMQ.CHARSET)), 0);
        assertThat(rc, is(true));

        System.out.print("Recv.");

        Msg msg = sub.recv(0);
        assertThat(msg, notNullValue());
        assertThat(msg.size(), is(5));

        msg = sub.recv(0);
        assertThat(msg, notNullValue());
        assertThat(msg.size(), is(3));

        System.out.print("End.");

        zmq.core.ZMQ.close(sub);

        for (int idx = 0; idx < 2; ++idx) {
            rc = pub.send(new Msg("topic abc".getBytes(ZMQ.CHARSET)), 0);
            assertThat(rc, is(true));
            ZMQ.msleep(10);
        }
        zmq.core.ZMQ.close(pub);

        final SocketBase command = zmq.core.ZMQ.socket(ctx, zmq.core.ZMQ.ZMQ_PAIR);
        rc = command.connect("inproc://ctrl-proxy");
        assertThat(rc, is(true));

        command.send(new Msg(ZMQ.PROXY_TERMINATE), 0);

        proxy.get();
        ZMQ.close(command);
        ZMQ.close(proxyPub);
        zmq.core.ZMQ.close(proxySub);
        zmq.core.ZMQ.close(ctrl);

        zmq.core.ZMQ.term(ctx);
        System.out.println("Done.");
    }
}
