package zmq.io;

import zmq.core.Msg;
import zmq.util.function.Supplier;

final class ProducePongMessage implements Supplier<Msg>
{
    private final StreamEngine streamEngine;
    private final byte[] pingContext;

    public ProducePongMessage(StreamEngine streamEngine, byte[] pingContext)
    {
        this.streamEngine = streamEngine;
        assert (pingContext != null);
        this.pingContext = pingContext;
    }

    @Override
    public Msg get()
    {
        return streamEngine.producePongMessage(pingContext);
    }
}
