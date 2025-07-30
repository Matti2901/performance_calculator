package zmq;

import java.nio.channels.Selector;

public interface ICtx {
    Selector createSelector();

   boolean closeSelector(Selector selector);
}
