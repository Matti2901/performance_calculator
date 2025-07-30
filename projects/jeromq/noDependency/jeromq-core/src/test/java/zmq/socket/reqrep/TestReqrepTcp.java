package zmq.socket.reqrep;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import zmq.*;

public class TestReqrepTcp
{
    @Test
    public void testReqrepTcp()
    {
        Ctx ctx = ZMQ.init(1);
        assertThat(ctx, notNullValue());

        SocketBase repBind = ZMQ.socket(ctx, ZMQUtilConstant.ZMQ_REP);
        assertThat(repBind, notNullValue());
        boolean rc = ZMQ.bind(repBind, "tcp://127.0.0.1:*");
        assertThat(rc, is(true));

        String host = (String) ZMQ.getSocketOptionExt(repBind, ZMQUtilConstant.ZMQ_LAST_ENDPOINT);
        assertThat(host, notNullValue());

        SocketBase reqConnect = ZMQ.socket(ctx, ZMQUtilConstant.ZMQ_REQ);
        assertThat(reqConnect, notNullValue());

        rc = ZMQ.connect(reqConnect, host);
        assertThat(rc, is(true));

        Helper.bounce(repBind, reqConnect);

        ZMQ.close(reqConnect);
        ZMQ.close(repBind);
        ZMQ.term(ctx);
    }
}
