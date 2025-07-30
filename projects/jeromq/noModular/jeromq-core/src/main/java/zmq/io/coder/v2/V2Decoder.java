package zmq.io.coder.v2;

import java.nio.ByteBuffer;

import zmq.core.Msg;
import zmq.exception.ZError;
import zmq.io.coder.decoder.Decoder;
import zmq.io.coder.decoder.Result;
import zmq.msg.MsgAllocator;
import zmq.util.erno.Errno;
import zmq.util.Wire;

public class V2Decoder extends Decoder
{
    private final ByteBuffer tmpbuf;
    private int msgFlags;

    public V2Decoder(Errno errno, int bufsize, long maxmsgsize, MsgAllocator allocator)
    {
        super(errno, bufsize, maxmsgsize, allocator);

        tmpbuf = ByteBuffer.allocate(8);
        tmpbuf.limit(1);

        //  At the beginning, read one byte and go to ONE_BYTE_SIZE_READY state.
        nextStep(tmpbuf, flagsReady);
    }

    @Override
    protected Msg allocate(int size)
    {
        Msg msg = super.allocate(size);
        msg.setFlags(msgFlags);
        return msg;
    }

    @Override
    protected Result oneByteSizeReady()
    {
        int size = tmpbuf.get(0) & 0xff;
        Result rc = sizeReady(size);
        if (rc != Result.ERROR) {
            nextStep(inProgress, messageReady);
        }
        return rc;
    }

    @Override
    protected Result eightByteSizeReady()
    {
        //  The payload size is encoded as 64-bit unsigned integer.
        //  The most significant byte comes first.
        tmpbuf.position(0);
        tmpbuf.limit(8);
        final long size = Wire.getUInt64(tmpbuf, 0);
        if ( size <= 0 ) {
            errno(ZError.EPROTO);
            return Result.ERROR;
        }

        Result rc = sizeReady(size);
        if (rc != Result.ERROR) {
            nextStep(inProgress, messageReady);
        }
        return rc;
    }

    @Override
    protected Result flagsReady()
    {
        //  Store the flags from the wire into the message structure.
        this.msgFlags = 0;
        int first = tmpbuf.get(0) & 0xff;
        if ((first & V2Protocol.MORE_FLAG) > 0) {
            this.msgFlags |= Msg.MORE;
        }
        if ((first & V2Protocol.COMMAND_FLAG) > 0) {
            this.msgFlags |= Msg.COMMAND;
        }

        //  The payload length is either one or eight bytes,
        //  depending on whether the 'large' bit is set.
        tmpbuf.position(0);
        if ((first & V2Protocol.LARGE_FLAG) > 0) {
            tmpbuf.limit(8);
            nextStep(tmpbuf, eightByteSizeReady);
        }
        else {
            tmpbuf.limit(1);
            nextStep(tmpbuf, oneByteSizeReady);
        }

        return Result.MORE_DATA;
    }

    @Override
    protected Result messageReady()
    {
        //  Message is completely read. Signal this to the caller
        //  and prepare to decode next message.
        tmpbuf.position(0);
        tmpbuf.limit(1);
        nextStep(tmpbuf, flagsReady);

        return Result.DECODED;
    }
}
