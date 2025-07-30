package zmq;

import java.net.SocketException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;

import org.zeromq.UncheckedZMQException;

public class ZError
{
    private ZError()
    {
    }

    /**
     * Resolve code from errornumber.
     * <p>
     * Messages are taken from https://pubs.opengroup.org/onlinepubs/9699919799/basedefs/errno.h.html
     */
    public enum Error
    {
        NOERROR(0, "No error"),
        ENOTSUP(ZError.ENOTSUP, "Not supported"),
        EPROTONOSUPPORT(ZError.EPROTONOSUPPORT, "Protocol not supported"),
        ENOBUFS(ZError.ENOBUFS, "No buffer space available"),
        ENETDOWN(ZError.ENETDOWN, "Network is down"),
        EADDRINUSE(ZError.EADDRINUSE, "Address already in use"),
        EADDRNOTAVAIL(ZError.EADDRNOTAVAIL, "Address not available"),
        ECONNREFUSED(ZError.ECONNREFUSED, "Connection refused"),
        EINPROGRESS(ZError.EINPROGRESS, "Operation in progress"),
        EHOSTUNREACH(ZError.EHOSTUNREACH, "Host unreachable"),
        EMTHREAD(ZError.EMTHREAD, "No thread available"),
        EFSM(ZError.EFSM, "Operation cannot be accomplished in current state"),
        ENOCOMPATPROTO(ZError.ENOCOMPATPROTO, "The protocol is not compatible with the socket type"),
        ETERM(ZError.ETERM, "Context was terminated"),
        ENOTSOCK(ZError.ENOTSOCK, "Not a socket"),
        EAGAIN(ZError.EAGAIN, "Resource unavailable, try again"),
        ENOENT(ZError.ENOENT, "No such file or directory"),
        EINTR(ZError.EINTR, "Interrupted function"),
        EACCESS(ZError.EACCESS, "Permission denied"),
        EFAULT(ZError.EFAULT, "Bad address"),
        EINVAL(ZError.EINVAL, "Invalid argument"),
        EISCONN(ZError.EISCONN, "Socket is connected"),
        ENOTCONN(ZError.ENOTCONN, "The socket is not connected"),
        EMSGSIZE(ZError.EMSGSIZE, "Message too large"),
        EAFNOSUPPORT(ZError.EAFNOSUPPORT, "Address family not supported"),
        ENETUNREACH(ZError.ENETUNREACH, "Network unreachable"),
        ECONNABORTED(ZError.ECONNABORTED, "Connection aborted"),
        ECONNRESET(ZError.ECONNRESET, "Connection reset"),
        ETIMEDOUT(ZError.ETIMEDOUT, "Connection timed out"),
        ENETRESET(ZError.ENETRESET, "Connection aborted by network"),
        EIOEXC(ZError.EIOEXC),
        ESOCKET(ZError.ESOCKET),
        EMFILE(ZError.EMFILE, "File descriptor value too large"),
        EPROTO(ZError.EPROTO, "Protocol error");

        private static final Map<Integer, Error> map = new HashMap<>(Error.values().length);
        static {
            for (Error e : Error.values()) {
                map.put(e.code, e);
            }
        }
        private final int code;
        private final String message;

        Error(int code)
        {
            this.code = code;
            this.message = "errno " + code;
        }

        Error(int code, String message)
        {
            this.code = code;
            this.message = message;
        }

        public static Error findByCode(int code)
        {
            if (code <= 0) {
                return NOERROR;
            }
            else if (map.containsKey(code)) {
                return map.get(code);
            }
            else {
                throw new IllegalArgumentException("Unknown " + Error.class.getName() + " enum code: " + code);
            }
        }

        public int getCode()
        {
            return code;
        }

        public String getMessage()
        {
            return message;
        }
    }

    public static class CtxTerminatedException extends UncheckedZMQException
    {
        private static final long serialVersionUID = -4404921838608052956L;

        public CtxTerminatedException()
        {
            super();
        }
    }

    public static class InstantiationException extends UncheckedZMQException
    {
        private static final long serialVersionUID = -4404921838608052955L;

        public InstantiationException(Throwable cause)
        {
            super(cause);
        }

        public InstantiationException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public InstantiationException(String message)
        {
            super(message);
        }
    }

    public static class IOException extends UncheckedZMQException
    {
        private static final long serialVersionUID = 9202470691157986262L;

        public IOException(java.io.IOException e)
        {
            super(e);
        }
    }

    public static final int ENOENT          = 2;
    public static final int EINTR           = 4;
    public static final int EACCESS         = 13;
    public static final int EFAULT          = 14;
    public static final int EINVAL          = 22;
    public static final int EAGAIN          = 35;
    public static final int EINPROGRESS     = 36;
    public static final int EPROTONOSUPPORT = 43;
    public static final int ENOTSUP         = 45;
    public static final int EADDRINUSE      = 48;
    public static final int EADDRNOTAVAIL   = 49;
    public static final int ENETDOWN        = 50;
    public static final int ENOBUFS         = 55;
    public static final int EISCONN         = 56;
    public static final int ENOTCONN        = 57;
    public static final int ECONNREFUSED    = 61;
    public static final int EHOSTUNREACH    = 65;
    public static final int ECANCELED       = 125;

    private static final int ZMQ_HAUSNUMERO = 156384712;

    public static final int ENOTSOCK     = ZMQ_HAUSNUMERO + 5;
    public static final int EMSGSIZE     = ZMQ_HAUSNUMERO + 10;
    public static final int EAFNOSUPPORT = ZMQ_HAUSNUMERO + 11;
    public static final int ENETUNREACH  = ZMQ_HAUSNUMERO + 12;

    public static final int ECONNABORTED = ZMQ_HAUSNUMERO + 13;
    public static final int ECONNRESET   = ZMQ_HAUSNUMERO + 14;
    public static final int ETIMEDOUT    = ZMQ_HAUSNUMERO + 16;
    public static final int ENETRESET    = ZMQ_HAUSNUMERO + 18;

    public static final int EFSM           = ZMQ_HAUSNUMERO + 51;
    public static final int ENOCOMPATPROTO = ZMQ_HAUSNUMERO + 52;
    public static final int ETERM          = ZMQ_HAUSNUMERO + 53;
    public static final int EMTHREAD       = ZMQ_HAUSNUMERO + 54;

    public static final int EIOEXC  = ZMQ_HAUSNUMERO + 105;
    public static final int ESOCKET = ZMQ_HAUSNUMERO + 106;
    public static final int EMFILE  = ZMQ_HAUSNUMERO + 107;

    public static final int EPROTO = ZMQ_HAUSNUMERO + 108;

    public static int exccode(java.io.IOException e)
    {
        if (e instanceof SocketException) {
            return ESOCKET;
        }
        else if (e instanceof ClosedByInterruptException) {
            return EINTR;
        }
        else if (e instanceof ClosedChannelException) {
            return ENOTCONN;
        }
        else {
            return EIOEXC;
        }
    }

    public static String toString(int code)
    {
        return Error.findByCode(code).getMessage();
    }
}
