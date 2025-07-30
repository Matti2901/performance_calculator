package zmq.io.mechanism.curve;

enum State {
    SEND_HELLO,
    EXPECT_WELCOME,
    SEND_INITIATE,
    EXPECT_READY,
    ERROR_RECEIVED,
    CONNECTED
}
