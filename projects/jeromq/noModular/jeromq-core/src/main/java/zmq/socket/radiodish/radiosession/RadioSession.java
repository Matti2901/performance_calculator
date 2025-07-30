package zmq.socket.radiodish.radiosession;

import zmq.core.Msg;
import zmq.core.Options;
import zmq.core.SocketBase;
import zmq.io.IOThread;
import zmq.io.SessionBase;
import zmq.io.net.Address;

import java.nio.charset.StandardCharsets;

public class RadioSession extends SessionBase {

    private RadioState state;
    private Msg pending;

    public RadioSession(IOThread ioThread, boolean connect, SocketBase socket, final Options options,
                        final Address addr) {
        super(ioThread, connect, socket, options, addr);

        state = RadioState.GROUP;
    }

    @Override
    public boolean pushMsg(Msg msg) {
        if (msg.isCommand()) {
            byte commandNameSize = msg.get(0);

            if (msg.size() < commandNameSize + 1) {
                return super.pushMsg(msg);
            }

            byte[] data = msg.data();

            String commandName = new String(data, 1, commandNameSize, StandardCharsets.US_ASCII);

            int groupLength;
            String group;
            Msg joinLeaveMsg = new Msg();

            // Set the msg type to either JOIN or LEAVE
            if (commandName.equals("JOIN")) {
                groupLength = msg.size() - 5;
                group = new String(data, 5, groupLength, StandardCharsets.US_ASCII);
                joinLeaveMsg.initJoin();
            } else if (commandName.equals("LEAVE")) {
                groupLength = msg.size() - 6;
                group = new String(data, 6, groupLength, StandardCharsets.US_ASCII);
                joinLeaveMsg.initLeave();
            }
            // If it is not a JOIN or LEAVE just push the message
            else {
                return super.pushMsg(msg);
            }

            //  Set the group
            joinLeaveMsg.setGroup(group);

            //  Push the join or leave command
            msg = joinLeaveMsg;
            return super.pushMsg(msg);
        }

        return super.pushMsg(msg);
    }

    @Override
    protected Msg pullMsg() {
        Msg msg;

        switch (state) {
            case GROUP:
                pending = super.pullMsg();
                if (pending == null) {
                    return null;
                }

                //  First frame is the group
                msg = new Msg(pending.getGroup().getBytes(StandardCharsets.US_ASCII));
                msg.setFlags(Msg.MORE);

                //  Next status is the body
                state = RadioState.BODY;
                break;
            case BODY:
                msg = pending;
                state = RadioState.GROUP;
                break;
            default:
                throw new IllegalStateException();
        }

        return msg;
    }

    @Override
    protected void reset() {
        super.reset();
        state = RadioState.GROUP;
    }
}
