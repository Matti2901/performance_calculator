package guide.clone.base;

import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

public class clone
{
    private final ZContext ctx;  //  Our context wrapper
    private final Socket   pipe; //  Pipe through to clone agent

    //  .split constructor and destructor
    //  Here are the constructor and destructor for the clone class. Note that
    //  we create a context specifically for the pipe that connects our
    //  frontend to the backend agent:
    public clone()
    {
        ctx = new ZContext();
        pipe = ZThread.fork(ctx, new CloneAgent());
    }

    public void destroy()
    {
        ctx.destroy();
    }

    //  .split subtree method
    //  Specify subtree for snapshot and updates, which we must do before
    //  connecting to a server as the subtree specification is sent as the
    //  first command to the server. Sends a [SUBTREE][subtree] command to
    //  the agent:
    public void subtree(String subtree)
    {
        ZMsg msg = new ZMsg();
        msg.add("SUBTREE");
        msg.add(subtree);
        msg.send(pipe);
    }

    //  .split connect method
    //  Connect to a new server endpoint. We can connect to at most two
    //  servers. Sends [CONNECT][endpoint][service] to the agent:
    public void connect(String address, String service)
    {
        ZMsg msg = new ZMsg();
        msg.add("CONNECT");
        msg.add(address);
        msg.add(service);
        msg.send(pipe);
    }

    //  .split set method
    //  Set a new value in the shared hashmap. Sends a [SET][key][value][ttl]
    //  command through to the agent which does the actual work:
    public void set(String key, String value, int ttl)
    {
        ZMsg msg = new ZMsg();
        msg.add("SET");
        msg.add(key);
        msg.add(value);
        msg.add(String.format("%d", ttl));
        msg.send(pipe);
    }

    //  .split get method
    //  Look up value in distributed hash table. Sends [GET][key] to the agent and
    //  waits for a value response. If there is no value available, will eventually
    //  return NULL:
    public String get(String key)
    {
        ZMsg msg = new ZMsg();
        msg.add("GET");
        msg.add(key);
        msg.send(pipe);

        ZMsg reply = ZMsg.recvMsg(pipe);
        if (reply != null) {
            String value = reply.popString();
            reply.destroy();
            return value;
        }
        return null;
    }

    //  .split backend agent class
    //  Here is the implementation of the backend agent itself:

    //  Number of servers to which we will talk to
    final static int SERVER_MAX = 2;

    //  Server considered dead if silent for this long
    final static int SERVER_TTL = 5000; //  msecs

    //  States we can be in
    final static int STATE_INITIAL = 0; //  Before asking server for state
    final static int STATE_SYNCING = 1; //  Getting state from server
    final static int STATE_ACTIVE  = 2; //  Getting new updates from server

}
