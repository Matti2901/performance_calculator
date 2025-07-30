package zmq.msg;

import zmq.core.Msg;

public interface MsgAllocator
{
    Msg allocate(int size);
}
