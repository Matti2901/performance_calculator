package zmq.io.coder.decoder.custom;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.ByteBuffer;

import org.junit.Test;

import zmq.core.Ctx;
import zmq.core.Msg;
import zmq.core.SocketBase;
import zmq.exception.ZError;
import zmq.core.ZMQ;
import zmq.io.coder.decoder.Decoder;
import zmq.io.coder.decoder.Result;
import zmq.io.coder.decoder.Step;
import zmq.msg.MsgAllocatorThreshold;
import zmq.util.erno.Errno;
import zmq.util.value.ValueReference;

public class CustomDecoderTest
{
    static class CustomDecoder extends Decoder
    {
        private final Step readHeader = this::readHeader;
        private final Step readBody   = this::readBody;

        final ByteBuffer header = ByteBuffer.allocate(10);
        Msg        msg;
        int        size   = -1;

        public CustomDecoder(int bufsize, long maxmsgsize)
        {
            super(new Errno(), bufsize, maxmsgsize, new MsgAllocatorThreshold());
            nextStep(header, readHeader);
        }

        private Result readHeader()
        {
            byte[] headerBuff = new byte[6];
            header.position(0);
            header.get(headerBuff);
            assertThat(new String(headerBuff, 0, 6, ZMQ.CHARSET), is("HEADER"));
            ByteBuffer b = header.duplicate();
            b.position(6);
            size = b.getInt();

            msg = allocate(size);
            nextStep(msg, readBody);

            return Result.MORE_DATA;
        }

        private Result readBody()
        {
            nextStep(header, readHeader);
            return Result.DECODED;
        }
    }

    @Test
    public void testCustomDecoder()
    {
        CustomDecoder cdecoder = new CustomDecoder(32, 64);

        ByteBuffer in = cdecoder.getBuffer();
        int insize = readHeader(in);
        assertThat(insize, is(10));
        readBody(in);

        in.flip();
        ValueReference<Integer> processed = new ValueReference<>(0);
        Result result = cdecoder.decode(in, 30, processed);
        assertThat(processed.get(), is(30));
        assertThat(cdecoder.size, is(20));
        assertThat(result, is(Result.DECODED));
    }

    private void readBody(ByteBuffer in)
    {
        in.put("1234567890".getBytes(ZMQ.CHARSET));
        in.put("1234567890".getBytes(ZMQ.CHARSET));
    }

    private int readHeader(ByteBuffer in)
    {
        in.put("HEADER".getBytes(ZMQ.CHARSET));
        in.putInt(20);
        return in.position();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testAssignCustomDecoder()
    {
        Ctx ctx = ZMQ.createContext();

        SocketBase socket = ctx.createSocket(ZMQ.ZMQ_PAIR);

        boolean rc = socket.setSocketOpt(ZMQ.ZMQ_DECODER, CustomDecoder.class);
        assertThat(rc, is(true));

        ZMQ.close(socket);
        ZMQ.term(ctx);
    }

    private static class WrongDecoder extends CustomDecoder
    {
        public WrongDecoder(int bufsize)
        {
            super(bufsize, 0);
        }
    }

    @SuppressWarnings("deprecation")
    @Test(expected = ZError.InstantiationException.class)
    public void testAssignWrongCustomDecoder()
    {
        Ctx ctx = ZMQ.createContext();
        SocketBase socket = ctx.createSocket(ZMQ.ZMQ_PAIR);

        try {
            socket.setSocketOpt(ZMQ.ZMQ_DECODER, WrongDecoder.class);
        }
        finally {
            ZMQ.close(socket);
            ZMQ.term(ctx);
        }
    }
}
