package guide.bstar;

//  States we can be in at any point in time
enum State {
    STATE_PRIMARY, //  Primary, waiting for peer to connect
    STATE_BACKUP, //  Backup, waiting for peer to connect
    STATE_ACTIVE, //  Active - accepting connections
    STATE_PASSIVE //  Passive - not accepting connections
}
