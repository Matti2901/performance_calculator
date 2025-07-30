package zmq.io.coder.raw;

import java.nio.ByteBuffer;

import zmq.core.Msg;
import zmq.io.coder.decoder.IDecoder;
import zmq.io.coder.decoder.Result;
import zmq.util.value.ValueReference;

public class RawDecoder implements IDecoder
{
    //  The buffer for data to decode.
    private final ByteBuffer buffer;

    protected Msg inProgress;

    public RawDecoder(int bufsize)
    {
        buffer = ByteBuffer.allocateDirect(bufsize);
        inProgress = new Msg();
    }

    @Override
    public ByteBuffer getBuffer()
    {
        buffer.clear();
        return buffer;
    }

    @Override
    public Result decode(ByteBuffer buffer, int size, ValueReference<Integer> processed)
    {
        processed.set(size);
        inProgress = new Msg(size);
        inProgress.put(buffer);

        return Result.DECODED;
    }

    @Override
    public Msg msg()
    {
        return inProgress;
    }

    @Override
    public void destroy()
    {
    }
}
