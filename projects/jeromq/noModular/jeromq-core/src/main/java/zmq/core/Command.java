package zmq.core;

import zmq.core.type.CommandType;

//  This structure defines the commands that can be sent between threads.
public class Command
{
    //  Object to process the command.
    final ZObject destination;
    final CommandType type;
    final Object  arg;

    Command(ZObject destination, CommandType type)
    {
        this(destination, type, null);
    }

    Command(ZObject destination, CommandType type, Object arg)
    {
        this.destination = destination;
        this.type = type;
        this.arg = arg;
    }

    public final void process()
    {
        destination.processCommand(this);
    }

    @Override
    public String toString()
    {
        return "Cmd" + "[" + destination + ", " + (destination == null ? "Reaper" : destination.getTid() + ", ") + type
                + (arg == null ? "" : ", " + arg) + "]";
    }

}
