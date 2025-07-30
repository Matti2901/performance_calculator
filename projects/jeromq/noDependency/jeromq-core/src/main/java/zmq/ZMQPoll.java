package zmq;

import zmq.poll.PollItem;
import zmq.util.Clock;
import zmq.util.Utils;

import java.io.IOException;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ZMQPoll {
    /**
     * Polling on items with given selector
     * CAUTION: This could be affected by jdk epoll bug
     *
     * @param selector Open and reuse this selector and do not forget to close when it is not used.
     * @param items
     * @param timeout
     * @return number of events
     */
    public static int poll(Selector selector, PollItem[] items, long timeout)
    {
        return poll(selector, items, items.length, timeout);
    }

    /**
     * Polling on items with given selector
     * CAUTION: This could be affected by jdk epoll bug
     *
     * @param selector Open and reuse this selector and do not forget to close when it is not used.
     * @param items
     * @param count
     * @param timeout
     * @return number of events
     */
    public static int poll(Selector selector, PollItem[] items, int count, long timeout)
    {
        Utils.checkArgument(items != null, "items have to be supplied for polling");
        if (count == 0) {
            if (timeout <= 0) {
                return 0;
            }
            LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(timeout, TimeUnit.MILLISECONDS));
            return 0;
        }
        long now = 0L;
        long end = 0L;

        HashMap<SelectableChannel, SelectionKey> saved = new HashMap<>();
        for (SelectionKey key : selector.keys()) {
            if (key.isValid()) {
                saved.put(key.channel(), key);
            }
        }

        for (int i = 0; i < count; i++) {
            PollItem item = items[i];
            if (item == null) {
                continue;
            }

            SelectableChannel ch = item.getChannel(); // mailbox channel if ZMQ socket
            SelectionKey key = saved.remove(ch);

            if (key != null) {
                if (key.interestOps() != item.interestOps()) {
                    key.interestOps(item.interestOps());
                }
                key.attach(item);
            }
            else {
                try {
                    ch.register(selector, item.interestOps(), item);
                }
                catch (ClosedSelectorException e) {
                    // context was closed asynchronously, exit gracefully
                    return -1;
                }
                catch (ClosedChannelException e) {
                    throw new ZError.IOException(e);
                }
            }
        }

        if (!saved.isEmpty()) {
            for (SelectionKey deprecated : saved.values()) {
                deprecated.cancel();
            }
        }

        boolean firstPass = true;
        int nevents = 0;
        int ready;

        while (true) {
            //  Compute the timeout for the subsequent poll.
            long waitMillis;
            if (firstPass) {
                waitMillis = 0L;
            }
            else if (timeout < 0L) {
                waitMillis = -1L;
            }
            else {
                waitMillis = TimeUnit.NANOSECONDS.toMillis(end - now);
                if (waitMillis == 0) {
                    waitMillis = 1L;
                }
            }

            //  Wait for events.
            try {
                int rc;
                if (waitMillis < 0) {
                    rc = selector.select(0);
                }
                else if (waitMillis == 0) {
                    rc = selector.selectNow();
                }
                else {
                    rc = selector.select(waitMillis);
                }

                for (SelectionKey key : selector.keys()) {
                    PollItem item = (PollItem) key.attachment();
                    ready = item.readyOps(key, rc);
                    if (ready < 0) {
                        return -1;
                    }

                    if (ready > 0) {
                        nevents++;
                    }
                }
                selector.selectedKeys().clear();

            }
            catch (ClosedSelectorException e) {
                // context was closed asynchronously, exit gracefully
                return -1;
            }
            catch (IOException e) {
                throw new ZError.IOException(e);
            }
            //  If timeout is zero, exit immediately whether there are events or not.
            if (timeout == 0) {
                break;
            }

            if (nevents > 0) {
                break;
            }

            //  At this point we are meant to wait for events but there are none.
            //  If timeout is infinite we can just loop until we get some events.
            if (timeout < 0) {
                if (firstPass) {
                    firstPass = false;
                }
                continue;
            }

            //  The timeout is finite and there are no events. In the first pass
            //  we get a timestamp of when the polling have begun. (We assume that
            //  first pass have taken negligible time). We also compute the time
            //  when the polling should time out.
            if (firstPass) {
                now = Clock.nowNS();
                end = now + TimeUnit.MILLISECONDS.toNanos(timeout);
                if (now == end) {
                    break;
                }
                firstPass = false;
                continue;
            }

            //  Find out whether timeout have expired.
            now = Clock.nowNS();
            if (now >= end) {
                break;
            }
        }
        return nevents;
    }
}
