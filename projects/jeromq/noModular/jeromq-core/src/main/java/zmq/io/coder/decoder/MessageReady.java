package zmq.io.coder.decoder;

final class MessageReady implements Step {
    private final Decoder decoder;

    public MessageReady(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Result apply() {
        return decoder.messageReady();
    }
}
