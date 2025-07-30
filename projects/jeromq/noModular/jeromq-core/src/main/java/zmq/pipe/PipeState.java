package zmq.pipe;

//  States of the pipe endpoint:
//  active: common state before any termination begins,
//  delimiter_received: delimiter was read from pipe before
//      term command was received,
//  waiting_fo_delimiter: term command was already received
//      from the peer but there are still pending messages to read,
//  term_ack_sent: all pending messages were already read and
//      all we are waiting for is ack from the peer,
//  term_req_sent1: 'terminate' was explicitly called by the user,
//  term_req_sent2: user called 'terminate' and then we've got
//      term command from the peer as well.
enum PipeState {
    ACTIVE,
    DELIMITER_RECEIVED,
    WAITING_FOR_DELIMITER,
    TERM_ACK_SENT,
    TERM_REQ_SENT_1,
    TERM_REQ_SENT_2
}
