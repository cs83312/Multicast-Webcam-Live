package threadMethod;

import java.util.LinkedList;

import signalPacket.SIPPacket;

public class RequestQueue {
	private final LinkedList queue = new LinkedList();
	
	public synchronized SIPPacket getRequest() {
        while (queue.size() <= 0) {
            try {
                System.out.println(Thread.currentThread().getName() + ": wait() begins, queue = " + queue);
                wait();
                System.out.println(Thread.currentThread().getName() + ": wait() ends,   queue = " + queue);
            } catch (InterruptedException e) {      
            }                                       
        }                                           
        return (SIPPacket)queue.removeFirst();
    }
    public synchronized void putRequest(SIPPacket request) {
        queue.addLast(request);
        System.out.println(Thread.currentThread().getName() + ": notifyAll() begins, queue = " + queue);
        notifyAll();
       System.out.println(Thread.currentThread().getName() + ": notifyAll() ends, queue = " + queue);
    }

}
