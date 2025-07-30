package zmq.core;

import java.nio.ByteBuffer;

public class TestMsgDirect extends TestMsg
{
    public TestMsgDirect()
    {
        super(ByteBuffer::allocate);
    }
}
