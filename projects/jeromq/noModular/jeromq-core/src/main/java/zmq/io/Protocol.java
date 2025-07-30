package zmq.io;

//  Protocol revisions
enum Protocol
{
    V0(-1),
    V1(0),
    V2(1),
    V3(3);

    final byte revision;

    Protocol(int revision)
    {
        this.revision = (byte) revision;
    }
}
