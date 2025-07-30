package zmq;

import java.io.IOException;

interface IoOperation<O> {
    O call() throws IOException;
}
