package zmq.core.ctxdef;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.channels.SelectableChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class that holds the informations needed to forward channel in monitor sockets.
 * Of course, it only works with inproc sockets.
 * <p>
 * It uses WeakReference to avoid holding references to channel if the monitor event is
 * lost.
 * <p>
 * A class is used as a lock in lazy allocation of the needed objects.
 */
public class ChannelForwardHolder {
    public final AtomicInteger handleSource = new AtomicInteger(0);
    public final Map<Integer, WeakReference<SelectableChannel>> map = new ConcurrentHashMap<>();
    // The WeakReference is empty when the reference is empty, so keep a reverse empty to clean the direct map.
    public final Map<WeakReference<SelectableChannel>, Integer> reversemap = new ConcurrentHashMap<>();
    public final ReferenceQueue<SelectableChannel> queue = new ReferenceQueue<>();
}
