package zmq.socket.radiodish.dish;

import zmq.core.Msg;
import zmq.core.Options;
import zmq.core.SocketBase;
import zmq.exception.ZError;
import zmq.io.IOThread;
import zmq.io.SessionBase;
import zmq.io.net.Address;

import java.nio.charset.StandardCharsets;

public class DishSession extends SessionBase {
    static final byte[] JOIN_BYTES = "\4JOIN".getBytes(StandardCharsets.US_ASCII);
    static final byte[] LEAVE_BYTES = "\5LEAVE".getBytes(StandardCharsets.US_ASCII);

    private DishState state;
    private String group;

    public DishSession(IOThread ioThread, boolean connect, SocketBase socket, final Options options,
                       final Address addr) {
        super(ioThread, connect, socket, options, addr);

        state = DishState.GROUP;
        group = "";
    }

    @Override
    public boolean pushMsg(Msg msg) {
        switch (state) {
            case GROUP:
                if (!msg.hasMore()) {
                    errno.set(ZError.EFAULT);
                    return false;
                }

                if (msg.size() > Msg.MAX_GROUP_LENGTH) {
                    errno.set(ZError.EFAULT);
                    return false;
                }

                group = new String(msg.data(), StandardCharsets.US_ASCII);
                state = DishState.BODY;

                return true;
            case BODY:
                //  Set the message group
                msg.setGroup(group);

                //  Thread safe socket doesn't support multipart messages
                if (msg.hasMore()) {
                    errno.set(ZError.EFAULT);
                    return false;
                }

                //  Push message to dish socket
                boolean rc = super.pushMsg(msg);
                if (rc) {
                    state = DishState.GROUP;
                }

                return rc;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    protected Msg pullMsg() {
        Msg msg = super.pullMsg();
        if (msg == null) {
            return null;
        }

        if (!msg.isJoin() && !msg.isLeave()) {
            return msg;
        }

        Msg command;

        byte[] groupBytes = msg.getGroup().getBytes(StandardCharsets.US_ASCII);

        if (msg.isJoin()) {
            command = new Msg(groupBytes.length + 5);
            command.put(JOIN_BYTES);
        } else {
            command = new Msg(groupBytes.length + 6);
            command.put(LEAVE_BYTES);
        }

        command.setFlags(Msg.COMMAND);

        //  Copy the group
        command.put(groupBytes);

        return command;
    }

    @Override
    protected void reset() {
        super.reset();
        state = DishState.GROUP;
    }
}
