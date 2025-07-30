package zmq.helper;

import zmq.io.IOThread;

public class DummyIOThread extends IOThread {
    public DummyIOThread() {
        super(Helper.ctx, 2);
    }
}
