package guide.bstar;

//  Events, which start with the states our peer can be in
enum Event {
    PEER_PRIMARY, //  HA peer is pending primary
    PEER_BACKUP, //  HA peer is pending backup
    PEER_ACTIVE, //  HA peer is active
    PEER_PASSIVE, //  HA peer is passive
    CLIENT_REQUEST //  Client makes request
}
