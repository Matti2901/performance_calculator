package guide.ibbroker.three;

import org.zeromq.ZFrame;
import org.zeromq.ZMQ;

import java.util.Queue;

//Our load-balancer structure, passed to reactor handlers
class LBBroker33 {
    ZMQ.Socket frontend; //  Listen to clients
    ZMQ.Socket backend;  //  Listen to workers
    Queue<ZFrame> workers;  //  List of ready workers
}
