package zmq.io.mechanism.curve;

enum StateServer {
    EXPECT_HELLO,
    SEND_WELCOME,
    EXPECT_INITIATE,
    EXPECT_ZAP_REPLY,
    SEND_READY,
    SEND_ERROR,
    ERROR_SENT,
    CONNECTED
}
