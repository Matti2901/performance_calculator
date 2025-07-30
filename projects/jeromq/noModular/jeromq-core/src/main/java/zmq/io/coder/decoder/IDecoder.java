package zmq.io.coder.decoder;

import java.nio.ByteBuffer;

import zmq.core.Msg;
import zmq.util.value.ValueReference;

public interface IDecoder
{

    ByteBuffer getBuffer();

    Result decode(ByteBuffer buffer, int size, ValueReference<Integer> processed);

    Msg msg();

    void destroy();
}
