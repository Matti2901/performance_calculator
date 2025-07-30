package zmq.socket.reqrep;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import zmq.*;

public class TestReqrepIpc
{
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    @Ignore
    public void testReqrepIpc()
    {
        String socketUri = String.format("ipc:///%s/tester2", tempFolder.getRoot().toString());
        Ctx ctx = ZMQ.init(1);
        assertThat(ctx, notNullValue());
        SocketBase sb = ZMQ.socket(ctx, ZMQUtilConstant.ZMQ_REP);
        assertThat(sb, notNullValue());
        boolean brc = ZMQ.bind(sb, socketUri);
        assertThat(brc, is(true));

        SocketBase sc = ZMQ.socket(ctx, ZMQUtilConstant.ZMQ_REQ);
        assertThat(sc, notNullValue());
        brc = ZMQ.connect(sc, socketUri);
        assertThat(brc, is(true));

        Helper.bounce(sb, sc);

        //  Tear down the wiring.
        ZMQ.close(sb);
        ZMQ.close(sc);
        ZMQ.term(ctx);
    }
}
