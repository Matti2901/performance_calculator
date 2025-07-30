package zmq.io.coder.decoder;

final class EightByteSizeReady implements Step {
    private final Decoder decoder;

    public EightByteSizeReady(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Result apply() {
        return decoder.eightByteSizeReady();
    }
}
