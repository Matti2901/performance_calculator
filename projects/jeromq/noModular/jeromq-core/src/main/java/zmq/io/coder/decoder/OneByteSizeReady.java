package zmq.io.coder.decoder;

final class OneByteSizeReady implements Step {
    private final Decoder decoder;

    public OneByteSizeReady(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Result apply() {
        return decoder.oneByteSizeReady();
    }
}
