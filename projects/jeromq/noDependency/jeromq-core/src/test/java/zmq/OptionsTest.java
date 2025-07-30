package zmq;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.SelectorProviderTest.DefaultSelectorProviderChooser;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import zmq.io.mechanism.Mechanisms;
import zmq.io.net.SelectorProviderChooser;
import zmq.msg.MsgAllocatorDirect;
import zmq.msg.MsgAllocatorThreshold;

public class OptionsTest
{
    private Options options;

    @Before
    public void setUp()
    {
        options = new Options();
    }

    @Test
    public void testDefaultValues()
    {
        assertThat(options.affinity, is(0L));
        assertThat(options.allocator, notNullValue());
        assertThat(options.allocator, is(instanceOf(MsgAllocatorThreshold.class)));
        assertThat(options.backlog, is(100));
        assertThat(options.conflate, is(false));
    }

    @Test
    public void testAffinity()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_AFFINITY, 1000L);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_AFFINITY), is(1000L));
    }

    @Test
    public void testAllocator()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_MSG_ALLOCATOR, new MsgAllocatorDirect());
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_MSG_ALLOCATOR), is(options.allocator));
    }

    @Test
    public void testBacklog()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_BACKLOG, 2000);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_BACKLOG), is(2000));
    }

    @Test
    public void testConflate()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_CONFLATE, true);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_CONFLATE), is(true));
    }

    @Test
    public void testRate()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_RATE, 10);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_RATE), is(10));
    }

    @Test
    public void testRecoveryIvl()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_RECOVERY_IVL, 11);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_RECOVERY_IVL), is(11));
    }

    @Test
    public void testMulticastHops()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_MULTICAST_HOPS, 12);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_MULTICAST_HOPS), is(12));
    }

    @Test
    public void testPlainUsername()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_CURVE_SERVER, true);
        String username = "username";

        options.setSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_USERNAME, username);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_USERNAME), is(username));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_SERVER), is(false));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_MECHANISM), is(Mechanisms.PLAIN));
    }

    @Test
    public void testPlainPassword()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_CURVE_SERVER, true);
        String password = "password";

        options.setSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_PASSWORD, password);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_PASSWORD), is(password));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_SERVER), is(false));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_MECHANISM), is(Mechanisms.PLAIN));
    }

    @Test
    public void testPlainUsernameNull()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_CURVE_SERVER, true);

        options.setSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_USERNAME, null);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_USERNAME), nullValue());
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_SERVER), is(false));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_MECHANISM), is(Mechanisms.NULL));
    }

    @Test
    public void testPlainPasswordNull()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_CURVE_SERVER, true);

        options.setSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_PASSWORD, null);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_PASSWORD), nullValue());
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_SERVER), is(false));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_MECHANISM), is(Mechanisms.NULL));
    }

    @Test
    public void testCurvePublicKey()
    {
        byte[] key = new byte[32];
        Arrays.fill(key, (byte) 11);
        options.setSocketOpt(ZMQUtilConstant.ZMQ_CURVE_PUBLICKEY, key);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_CURVE_PUBLICKEY), is(key));
    }

    @Test
    public void testCurveSecretKey()
    {
        byte[] key = new byte[32];
        Arrays.fill(key, (byte) 12);
        options.setSocketOpt(ZMQUtilConstant.ZMQ_CURVE_SECRETKEY, key);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_CURVE_SECRETKEY), is(key));
    }

    @Test
    public void testCurveServerKey()
    {
        byte[] key = new byte[32];
        Arrays.fill(key, (byte) 14);
        options.setSocketOpt(ZMQUtilConstant.ZMQ_CURVE_SERVERKEY, key);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_CURVE_SERVERKEY), is(key));
    }

    @Test
    public void testGssPlaintext()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_GSSAPI_PLAINTEXT, true);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_GSSAPI_PLAINTEXT), is(true));
    }

    @Test
    public void testHeartbeatInterval()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_IVL, 1000);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_IVL), is(1000));
    }

    @Test
    public void testHeartbeatTimeout()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TIMEOUT, 1001);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TIMEOUT), is(1001));
    }

    @Test
    public void testHeartbeatTtlRounded()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TTL, 2020);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TTL), is(2000));
    }

    @Test
    public void testHeartbeatTtlMin()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TTL, -99);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TTL), is(0));
    }

    @Test
    public void testHeartbeatTtlRoundedMin()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TTL, 99);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TTL), is(0));
    }

    @Test
    public void testHeartbeatTtlMax()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TTL, 655399);
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TTL), is(655300));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHeartbeatTtlOverflow()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TTL, 655400);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHeartbeatTtlUnderflow()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TTL, -100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHeartbeatIvlUnderflow()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_IVL, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHeartbeatTimeoutUnderflow()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TIMEOUT, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandshakeIvlUnderflow()
    {
        options.setSocketOpt(ZMQUtilConstant.ZMQ_HANDSHAKE_IVL, -1);
    }

    @Test(timeout = 5000)
    public void testSelectorObject()
    {
        try (ZContext ctx = new ZContext();
             Socket socket = ctx.createSocket(SocketType.PUB)) {
            SelectorProviderChooser chooser = new DefaultSelectorProviderChooser();
            socket.setSelectorChooser(chooser);
            Assert.assertEquals(chooser, socket.getSelectorProviderChooser());
        }
    }

    @Test
    public void testSelectorClass()
    {
        Options opt = new Options();
        Class<DefaultSelectorProviderChooser> chooser = DefaultSelectorProviderChooser.class;
        opt.setSocketOpt(ZMQUtilConstant.ZMQ_SELECTOR_PROVIDERCHOOSER, chooser);
        Assert.assertTrue(opt.getSocketOpt(ZMQUtilConstant.ZMQ_SELECTOR_PROVIDERCHOOSER) instanceof SelectorProviderChooser);
    }

    @Test
    public void testSelectorClassName()
    {
        Options opt = new Options();
        Class<DefaultSelectorProviderChooser> chooser = DefaultSelectorProviderChooser.class;
        opt.setSocketOpt(ZMQUtilConstant.ZMQ_SELECTOR_PROVIDERCHOOSER, chooser.getName());
        Assert.assertTrue(opt.getSocketOpt(ZMQUtilConstant.ZMQ_SELECTOR_PROVIDERCHOOSER) instanceof SelectorProviderChooser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectorClassNameFailed()
    {
        Options opt = new Options();
        opt.setSocketOpt(ZMQUtilConstant.ZMQ_SELECTOR_PROVIDERCHOOSER, String.class.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectorFailed()
    {
        Options opt = new Options();
        Assert.assertFalse(opt.setSocketOpt(ZMQUtilConstant.ZMQ_SELECTOR_PROVIDERCHOOSER, ""));
    }

    @Test
    public void testIdentityOk()
    {
        Options opt = new Options();
        // Try with a big identity
        Assert.assertTrue(opt.setSocketOpt(ZMQUtilConstant.ZMQ_IDENTITY, new byte[255]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIdentityFails()
    {
        Options opt = new Options();
        // Try with a big identity
        Assert.assertTrue(opt.setSocketOpt(ZMQUtilConstant.ZMQ_IDENTITY, new byte[256]));
    }

    @Test
    public void testDefaultValue()
    {
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_GSSAPI_PRINCIPAL), is(options.gssPrincipal));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_GSSAPI_SERVICE_PRINCIPAL), is(options.gssServicePrincipal));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_HANDSHAKE_IVL), is(options.handshakeIvl));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_IDENTITY), is(options.identity));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_IMMEDIATE), is(options.immediate));
        //        assertThat(options.getSocketOpt(ZMQ.ZMQ_TCP_ACCEPT_FILTER), is((Object)options.ipcAcceptFilters));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_IPV6), is(options.ipv6));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_LAST_ENDPOINT), is(options.lastEndpoint));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_LINGER), is(options.linger));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_MAXMSGSIZE), is(options.maxMsgSize));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_MECHANISM), is(options.mechanism));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_MULTICAST_HOPS), is(options.multicastHops));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_PASSWORD), is(options.plainPassword));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_PLAIN_USERNAME), is(options.plainUsername));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_RATE), is(options.rate));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_RCVBUF), is(options.rcvbuf));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_RECONNECT_IVL), is(options.reconnectIvl));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_RECONNECT_IVL_MAX), is(options.reconnectIvlMax));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_RECOVERY_IVL), is(options.recoveryIvl));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_RCVHWM), is(options.recvHwm));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_RCVTIMEO), is(options.recvTimeout));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_SNDHWM), is(options.sendHwm));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_SNDTIMEO), is(options.sendTimeout));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_SNDBUF), is(options.sndbuf));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_SOCKS_PROXY), is(options.socksProxyAddress));
        //        assertThat(options.getSocketOpt(ZMQ.ZMQ_TCP_ACCEPT_FILTER), is((Object)options.tcpAcceptFilters));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_TCP_KEEPALIVE), is(options.tcpKeepAlive));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_TOS), is(options.tos));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_TYPE), is(options.type));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_ZAP_DOMAIN), is(options.zapDomain));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_HANDSHAKE_IVL), is(options.handshakeIvl));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_IVL), is(options.heartbeatInterval));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TIMEOUT), is(options.heartbeatTimeout));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_HEARTBEAT_TTL), is(options.heartbeatTtl));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_SELECTOR_PROVIDERCHOOSER), nullValue());
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_IDENTITY), is(new byte[0]));
        assertThat(options.getSocketOpt(ZMQUtilConstant.ZMQ_SELFADDR_PROPERTY_NAME), nullValue());
    }
}
