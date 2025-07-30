package zmq.io.coder.decoder;

final class FlagsReady implements Step {
    private final Decoder decoder;

    public FlagsReady(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Result apply() {
        return decoder.flagsReady();
    }
}
