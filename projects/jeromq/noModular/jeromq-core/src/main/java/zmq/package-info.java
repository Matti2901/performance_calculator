/**
 * <p>Provides low-level bindings for ØMQ.</p>
 *
 * <p>This is the java equivalent of <a href="https://github.com/zeromq/libzmq">libzmq project</a>.</p>
 *
 * <p>The ZeroMQ lightweight messaging kernel is a library which extends the standard socket interfaces
 * with features traditionally provided by specialised messaging middleware products.
 * ZeroMQ sockets provide an abstraction of asynchronous message queues,
 * multiple messaging patterns, message filtering (subscriptions),
 * seamless access to multiple transport protocols and more.</p>
 *
 * <p>All subpackages should be considered internal, with the exception of {@link zmq.msg}.</p>
 *
 * <p>Within this package, only {@link zmq.core.ZMQ}, {@link zmq.core.Ctx}, {@link zmq.core.SocketBase} and {@link zmq.core.Msg} should be used.</p>
 */
package zmq;
