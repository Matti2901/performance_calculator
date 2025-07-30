package zmq;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ZMQUtilConstant {
    public static final int ZMQ_POLLIN  = 1;
    public static final int ZMQ_POLLOUT = 2;
    public static final int ZMQ_POLLERR = 4;
    public static final int ZMQ_EVENTS  = 15;
    // Android compatibility: not using StandardCharsets (API 19+)
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final byte[] PROXY_TERMINATE = "TERMINATE".getBytes(CHARSET);
    public static final byte[] PROXY_RESUME    = "RESUME".getBytes(CHARSET);
    public static final byte[] PROXY_PAUSE     = "PAUSE".getBytes(CHARSET);
    public static final int ZMQ_SNDMORE  = 2;
    public static final int ZMQ_RCVMORE             = 13;

    /******************************************************************************/

    /*  Version macros for compile-time API version detection                     */
    public static final int ZMQ_VERSION_MAJOR = 4;
    public static final int ZMQ_VERSION_MINOR = 1;
    public static final int ZMQ_VERSION_PATCH = 7;
    /*  Context options  */
    public static final int ZMQ_IO_THREADS  = 1;
    public static final int ZMQ_MAX_SOCKETS = 2;
    /*  Default for new contexts                                                  */
    public static final int ZMQ_IO_THREADS_DFLT  = 1;
    public static final int ZMQ_MAX_SOCKETS_DFLT = 1024;
    /******************************************************************************/

    /*  Socket types.                                                             */
    public static final int ZMQ_PAIR    = 0;
    public static final int ZMQ_PUB     = 1;
    public static final int ZMQ_SUB     = 2;
    public static final int ZMQ_REQ     = 3;
    public static final int ZMQ_REP     = 4;
    public static final int ZMQ_DEALER  = 5;
    /*  Deprecated aliases                                                        */
    @Deprecated
    public static final int ZMQ_XREQ = ZMQ_DEALER;
    public static final int ZMQ_ROUTER  = 6;
    @Deprecated
    public static final int ZMQ_XREP = ZMQ_ROUTER;
    public static final int ZMQ_PULL    = 7;
    public static final int ZMQ_PUSH    = 8;
    public static final int ZMQ_XPUB    = 9;
    public static final int ZMQ_XSUB    = 10;
    public static final int ZMQ_STREAM  = 11;
    public static final int ZMQ_SERVER  = 12;
    public static final int ZMQ_CLIENT  = 13;
    public static final int ZMQ_RADIO   = 14;
    public static final int ZMQ_DISH    = 15;
    public static final int ZMQ_CHANNEL = 16;
    public static final int ZMQ_PEER    = 17;
    public static final int ZMQ_RAW     = 18;
    public static final int ZMQ_SCATTER = 19;
    public static final int ZMQ_GATHER  = 20;
    /*  Socket options.                                                           */
    public static final int ZMQ_AFFINITY            = 4;
    public static final int ZMQ_IDENTITY            = 5;
    public static final int ZMQ_SUBSCRIBE           = 6;
    public static final int ZMQ_UNSUBSCRIBE         = 7;
    public static final int ZMQ_RATE                = 8;
    public static final int ZMQ_RECOVERY_IVL        = 9;
    public static final int ZMQ_SNDBUF              = 11;
    public static final int ZMQ_RCVBUF              = 12;
    public static final int ZMQ_FD                  = 14;
    public static final int ZMQ_TYPE                = 16;
    public static final int ZMQ_LINGER              = 17;
    public static final int ZMQ_RECONNECT_IVL       = 18;
    public static final int ZMQ_BACKLOG             = 19;
    public static final int ZMQ_RECONNECT_IVL_MAX   = 21;
    public static final int ZMQ_MAXMSGSIZE          = 22;
    public static final int ZMQ_SNDHWM              = 23;
    public static final int ZMQ_RCVHWM              = 24;
    public static final int ZMQ_MULTICAST_HOPS      = 25;
    public static final int ZMQ_RCVTIMEO            = 27;
    public static final int ZMQ_SNDTIMEO            = 28;
    public static final int ZMQ_LAST_ENDPOINT       = 32;
    public static final int ZMQ_ROUTER_MANDATORY    = 33;
    @Deprecated
    public static final int ZMQ_ROUTER_BEHAVIOR         = ZMQ_ROUTER_MANDATORY;
    @Deprecated
    public static final int ZMQ_FAIL_UNROUTABLE         = ZMQ_ROUTER_MANDATORY;
    public static final int ZMQ_TCP_KEEPALIVE       = 34;
    public static final int ZMQ_TCP_KEEPALIVE_CNT   = 35;
    public static final int ZMQ_TCP_KEEPALIVE_IDLE  = 36;
    public static final int ZMQ_TCP_KEEPALIVE_INTVL = 37;
    public static final int ZMQ_XPUB_VERBOSE        = 40;
    public static final int ZMQ_ROUTER_RAW          = 41;
    public static final int ZMQ_MECHANISM           = 43;
    public static final int ZMQ_PLAIN_SERVER        = 44;
    public static final int ZMQ_PLAIN_USERNAME      = 45;
    public static final int ZMQ_PLAIN_PASSWORD      = 46;
    public static final int ZMQ_CURVE_SERVER        = 47;
    public static final int ZMQ_CURVE_PUBLICKEY     = 48;
    public static final int ZMQ_CURVE_SECRETKEY     = 49;
    public static final int ZMQ_CURVE_SERVERKEY     = 50;
    public static final int ZMQ_PROBE_ROUTER        = 51;
    public static final int ZMQ_REQ_CORRELATE       = 52;
    public static final int ZMQ_REQ_RELAXED         = 53;
    public static final int ZMQ_CONFLATE            = 54;
    public static final int ZMQ_ZAP_DOMAIN          = 55;
    // TODO: more constants
    public static final int ZMQ_ROUTER_HANDOVER          = 56;
    public static final int ZMQ_TOS                      = 57;
    public static final int ZMQ_CONNECT_RID              = 61;
    public static final int ZMQ_GSSAPI_SERVER            = 62;
    public static final int ZMQ_GSSAPI_PRINCIPAL         = 63;
    public static final int ZMQ_GSSAPI_SERVICE_PRINCIPAL = 64;
    public static final int ZMQ_GSSAPI_PLAINTEXT         = 65;
    public static final int ZMQ_HANDSHAKE_IVL            = 66;
    public static final int ZMQ_SOCKS_PROXY              = 67;
    public static final int ZMQ_XPUB_NODROP              = 69;
    public static final int ZMQ_BLOCKY                   = 70;
    public static final int ZMQ_XPUB_MANUAL              = 71;
    public static final int ZMQ_HEARTBEAT_IVL            = 75;
    public static final int ZMQ_HEARTBEAT_TTL            = 76;
    public static final int ZMQ_HEARTBEAT_TIMEOUT        = 77;
    public static final int ZMQ_XPUB_VERBOSER            = 78;
    @Deprecated
    public static final int ZMQ_XPUB_VERBOSE_UNSUBSCRIBE = 78;
    public static final int ZMQ_HELLO_MSG                = 79;
    public static final int ZMQ_AS_TYPE                  = 80;
    public static final int ZMQ_DISCONNECT_MSG           = 81;
    public static final int ZMQ_HICCUP_MSG               = 82;
    public static final int ZMQ_SELFADDR_PROPERTY_NAME   = 83;
    /*  Message options                                                           */
    public static final int ZMQ_MORE = 1;
    /*  Send/recv options.                                                        */
    public static final int ZMQ_DONTWAIT = 1;
    @Deprecated
    public static final int ZMQ_NOBLOCK                 = ZMQ_DONTWAIT;
    /*  Deprecated aliases                                                        */
    @Deprecated
    public static final int ZMQ_TCP_ACCEPT_FILTER       = 38;
    @Deprecated
    public static final int ZMQ_IPV4ONLY                = 31;
    @Deprecated
    public static final int ZMQ_DELAY_ATTACH_ON_CONNECT = 39;
    /******************************************************************************/

    /*  Socket transport events (tcp and ipc only)                                */
    public static final int ZMQ_EVENT_CONNECTED          = 1;
    public static final int ZMQ_EVENT_CONNECT_DELAYED    = 1 << 1;
    public static final int ZMQ_EVENT_CONNECT_RETRIED    = 1 << 2;
    public static final int ZMQ_EVENT_LISTENING          = 1 << 3;
    public static final int ZMQ_EVENT_BIND_FAILED        = 1 << 4;
    public static final int ZMQ_EVENT_ACCEPTED           = 1 << 5;
    public static final int ZMQ_EVENT_ACCEPT_FAILED      = 1 << 6;
    public static final int ZMQ_EVENT_CLOSED             = 1 << 7;
    public static final int ZMQ_EVENT_CLOSE_FAILED       = 1 << 8;
    public static final int ZMQ_IPV6                = 42;
    public static final int ZMQ_EVENT_DISCONNECTED       = 1 << 9;
    public static final int ZMQ_EVENT_MONITOR_STOPPED    = 1 << 10;
    public static final int ZMQ_EVENT_HANDSHAKE_PROTOCOL = 1 << 15;
    public static final int ZMQ_EVENT_ALL                = 0xffff;
    /*  Unspecified system errors during handshake. Event value is an errno.      */
    public static final int ZMQ_EVENT_HANDSHAKE_FAILED_NO_DETAIL   = 1 << 11;
    /*  Handshake complete successfully with successful authentication (if        *
     *  enabled). Event value is unused.                                          */
    public static final int ZMQ_EVENT_HANDSHAKE_SUCCEEDED           = 1 << 12;
    /*  Protocol errors between ZMTP peers or between server and ZAP handler.     *
     *  Event value is one of ZMQ_PROTOCOL_ERROR_*                                */
    public static final int ZMQ_EVENT_HANDSHAKE_FAILED_PROTOCOL     = 1 << 13;
    /*  Failed authentication requests. Event value is the numeric ZAP status     *
     *  code, i.e. 300, 400 or 500.                                               */
    public static final int ZMQ_EVENT_HANDSHAKE_FAILED_AUTH         = 1 << 14;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_UNSPECIFIED                   = 0x10000000;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_UNEXPECTED_COMMAND            = 0x10000001;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_INVALID_SEQUENCE              = 0x10000002;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_KEY_EXCHANGE                  = 0x10000003;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_MALFORMED_COMMAND_UNSPECIFIED = 0x10000011;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_MALFORMED_COMMAND_MESSAGE     = 0x10000012;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_MALFORMED_COMMAND_HELLO       = 0x10000013;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_MALFORMED_COMMAND_INITIATE    = 0x10000014;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_MALFORMED_COMMAND_ERROR       = 0x10000015;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_MALFORMED_COMMAND_READY       = 0x10000016;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_MALFORMED_COMMAND_WELCOME     = 0x10000017;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_INVALID_METADATA              = 0x10000018;
    // the following two may be due to erroneous configuration of a peer
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_CRYPTOGRAPHIC                 = 0x11000001;
    public static final int ZMQ_PROTOCOL_ERROR_ZMTP_MECHANISM_MISMATCH            = 0x11000002;
    public static final int ZMQ_PROTOCOL_ERROR_ZAP_UNSPECIFIED                    = 0x20000000;
    public static final int ZMQ_PROTOCOL_ERROR_ZAP_MALFORMED_REPLY                = 0x20000001;
    public static final int ZMQ_PROTOCOL_ERROR_ZAP_BAD_REQUEST_ID                 = 0x20000002;
    public static final int ZMQ_PROTOCOL_ERROR_ZAP_BAD_VERSION                    = 0x20000003;
    public static final int ZMQ_PROTOCOL_ERROR_ZAP_INVALID_STATUS_CODE            = 0x20000004;
    public static final int ZMQ_PROTOCOL_ERROR_ZAP_INVALID_METADATA               = 0x20000005;
    public static final int ZMQ_PROTOCOL_ERROR_WS_UNSPECIFIED                     = 0x30000000;
    @Deprecated
    public static final int ZMQ_STREAMER  = 1;
    @Deprecated
    public static final int ZMQ_FORWARDER = 2;
    @Deprecated
    public static final int ZMQ_QUEUE     = 3;
    public static final byte[] MESSAGE_SEPARATOR = new byte[0];
    public static final byte[] SUBSCRIPTION_ALL = new byte[0];
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_AFFINITY}
     */
    public static final int DEFAULT_AFFINITY = 0;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_SERVER}
     */
    public static final boolean DEFAULT_AS_SERVER = false;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_AS_TYPE}
     */
    public static final int DEFAULT_AS_TYPE = -1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_BACKLOG}
     */
    public static final int DEFAULT_BACKLOG = 100;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_CONFLATE}
     */
    public static final boolean DEFAULT_CONFLATE = false;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_GSSAPI_PLAINTEXT}
     */
    public static final boolean DEFAULT_GSS_PLAINTEXT = false;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_HANDSHAKE_IVL}
     */
    public static final int DEFAULT_HANDSHAKE_IVL = 30000;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_HEARTBEAT_CONTEXT}
     */
    public static final byte[] DEFAULT_HEARTBEAT_CONTEXT = new byte[0];
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_HEARTBEAT_IVL}
     */
    public static final int DEFAULT_HEARTBEAT_INTERVAL = 0;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_HEARTBEAT_TIMEOUT}
     */
    public static final int DEFAULT_HEARTBEAT_TIMEOUT = -1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_HEARTBEAT_TTL}
     */
    public static final int DEFAULT_HEARTBEAT_TTL = 0;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_IDENTITY}
     */
    public static final byte[] DEFAULT_IDENTITY = new byte[0];
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_IMMEDIATE}
     */
    public static final boolean DEFAULT_IMMEDIATE = true;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_LINGER}
     */
    public static final int DEFAULT_LINGER = -1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_MAXMSGSIZE}
     */
    public static final long DEFAULT_MAX_MSG_SIZE = -1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_RECONNECT_IVL}
     */
    public static final int DEFAULT_RECONNECT_IVL = 100;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_RECONNECT_IVL_MAX}
     */
    public static final int DEFAULT_RECONNECT_IVL_MAX = 0;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_RCVHWM}
     */
    public static final int DEFAULT_RECV_HWM = 1000;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_RCVTIMEO}
     */
    public static final int DEFAULT_RECV_TIMEOUT = -1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_RCVBUF}
     */
    public static final int DEFAULT_RCVBUF = 0;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_RATE}
     */
    public static final int DEFAULT_RATE = 100;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_RECOVERY_IVL}
     */
    public static final int DEFAULT_RECOVERY_IVL = 10000;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_SELFADDR_PROPERTY_NAME}
     */
    public static final String DEFAULT_SELF_ADDRESS_PROPERTY_NAME = null;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_SNDHWM}
     */
    public static final int DEFAULT_SEND_HWM = 1000;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_SNDTIMEO}
     */
    public static final int DEFAULT_SEND_TIMEOUT = -1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_SNDBUF}
     */
    public static final int DEFAULT_SNDBUF = 0;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_SOCKS_PROXY}
     */
    public static final String DEFAULT_SOCKS_PROXY_ADDRESS = null;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_TCP_KEEPALIVE}
     */
    public static final int DEFAULT_TCP_KEEP_ALIVE = -1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_TCP_KEEPALIVE_CNT}
     */
    public static final int DEFAULT_TCP_KEEP_ALIVE_CNT = -1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_TCP_KEEPALIVE_IDLE}
     */
    public static final int DEFAULT_TCP_KEEP_ALIVE_IDLE = -1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_TCP_KEEPALIVE_INTVL}
     */
    public static final int DEFAULT_TCP_KEEP_ALIVE_INTVL = -1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_TOS}
     */
    public static final int DEFAULT_TOS = 0;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_TYPE}
     */
    public static final int DEFAULT_TYPE = -1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_MULTICAST_HOPS}
     */
    public static final int DEFAULT_MULTICAST_HOPS = 1;
    /**
     * Default value for {@link ZMQUtilConstant#ZMQ_ZAP_DOMAIN}
     */
    public static final String DEFAULT_ZAP_DOMAIN = "";
    private static final int ZMQ_CUSTOM_OPTION = 1000;
    public static final int ZMQ_SELECTOR_PROVIDERCHOOSER      = ZMQ_CUSTOM_OPTION + 6;
    public static final int ZMQ_HEARTBEAT_CONTEXT             = ZMQ_CUSTOM_OPTION + 5;
    public static final int ZMQ_MSG_ALLOCATION_HEAP_THRESHOLD = ZMQ_CUSTOM_OPTION + 4;
    public static final int ZMQ_MSG_ALLOCATOR                 = ZMQ_CUSTOM_OPTION + 3;
    @Deprecated
    public static final int ZMQ_DECODER                       = ZMQ_CUSTOM_OPTION + 2;
    /* Custom options */
    @Deprecated
    public static final int ZMQ_ENCODER                       = ZMQ_CUSTOM_OPTION + 1;
    public static final int ZMQ_IMMEDIATE           = 39 + ZMQ_CUSTOM_OPTION; // for compatibility with ZMQ_DELAY_ATTACH_ON_CONNECT
}
