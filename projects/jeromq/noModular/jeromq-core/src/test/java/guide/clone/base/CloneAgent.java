package guide.clone.base;

import guide.kvmsg.kvmsg;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZThread;

class CloneAgent implements ZThread.IAttachedRunnable {

    @Override
    public void run(Object[] args, ZContext ctx, ZMQ.Socket pipe) {
        Agent self = new Agent(ctx, pipe);

        ZMQ.Poller poller = ctx.createPoller(1);
        poller.register(pipe, ZMQ.Poller.POLLIN);

        while (!Thread.currentThread().isInterrupted()) {
            long pollTimer = -1;
            int pollSize = 2;
            Server server = self.server[self.curServer];
            switch (self.state) {
                case clone.STATE_INITIAL:
                    //  In this state we ask the server for a snapshot,
                    //  if we have a server to talk to...
                    if (self.nbrServers > 0) {
                        System.out.printf("I: waiting for server at %s:%d...\n", server.address, server.port);
                        if (server.requests < 2) {
                            server.snapshot.sendMore("ICANHAZ?");
                            server.snapshot.send(self.subtree);
                            server.requests++;
                        }
                        server.expiry = System.currentTimeMillis() + clone.SERVER_TTL;
                        self.state = clone.STATE_SYNCING;

                        poller.close();
                        poller = ctx.createPoller(2);
                        poller.register(pipe, ZMQ.Poller.POLLIN);
                        poller.register(server.snapshot, ZMQ.Poller.POLLIN);
                    } else pollSize = 1;
                    break;

                case clone.STATE_SYNCING:
                    //  In this state we read from snapshot and we expect
                    //  the server to respond, else we fail over.
                    poller.close();
                    poller = ctx.createPoller(2);
                    poller.register(pipe, ZMQ.Poller.POLLIN);
                    poller.register(server.snapshot, ZMQ.Poller.POLLIN);
                    break;

                case clone.STATE_ACTIVE:
                    //  In this state we read from subscriber and we expect
                    //  the server to give hugz, else we fail over.
                    poller.close();
                    poller = ctx.createPoller(2);
                    poller.register(pipe, ZMQ.Poller.POLLIN);
                    poller.register(server.subscriber, ZMQ.Poller.POLLIN);
                    break;
            }
            if (server != null) {
                pollTimer = server.expiry - System.currentTimeMillis();
                if (pollTimer < 0)
                    pollTimer = 0;
            }
            //  .split client poll loop
            //  We're ready to process incoming messages; if nothing at all
            //  comes from our server within the timeout, that means the
            //  server is dead:
            int rc = poller.poll(pollTimer);
            if (rc == -1)
                break; //  Context has been shut down

            if (poller.pollin(0)) {
                if (!self.controlMessage())
                    break; //  Interrupted
            } else if (pollSize == 2 && poller.pollin(1)) {
                kvmsg msg = kvmsg.recv(poller.getSocket(1));
                if (msg == null)
                    break; //  Interrupted

                //  Anything from server resets its expiry time
                server.expiry = System.currentTimeMillis() + clone.SERVER_TTL;
                if (self.state == clone.STATE_SYNCING) {
                    //  Store in snapshot until we're finished
                    server.requests = 0;
                    if (msg.getKey().equals("KTHXBAI")) {
                        self.sequence = msg.getSequence();
                        self.state = clone.STATE_ACTIVE;
                        System.out.printf("I: received from %s:%d snapshot=%d\n", server.address, server.port,
                                self.sequence);
                        msg.destroy();
                    }
                } else if (self.state == clone.STATE_ACTIVE) {
                    //  Discard out-of-sequence updates, incl. hugz
                    if (msg.getSequence() > self.sequence) {
                        self.sequence = msg.getSequence();
                        System.out.printf("I: received from %s:%d update=%d\n", server.address, server.port,
                                self.sequence);
                    } else msg.destroy();
                }
            } else {
                //  Server has died, failover to next
                System.out.printf("I: server at %s:%d didn't give hugz\n", server.address, server.port);
                self.curServer = (self.curServer + 1) % self.nbrServers;
                self.state = clone.STATE_INITIAL;
            }
        }
        self.destroy();
    }
}
