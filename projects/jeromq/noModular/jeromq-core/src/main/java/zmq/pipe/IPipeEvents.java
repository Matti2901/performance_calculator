package zmq.pipe;

public interface IPipeEvents {
    void readActivated(Pipe pipe);

    void writeActivated(Pipe pipe);

    void hiccuped(Pipe pipe);

    void pipeTerminated(Pipe pipe);
}
