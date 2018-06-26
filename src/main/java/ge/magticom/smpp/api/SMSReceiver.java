package ge.magticom.smpp.api;

import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.SmppObject;
import org.smpp.pdu.PDU;
import org.smpp.util.Queue;


/**
 * Implements simple PDU listener which handles PDUs received from SMSC.
 * It puts the received requests into a queue and discards all received
 * responses. Requests then can be fetched (should be) from the queue by
 * calling to the method <code>getRequestEvent</code>.
 * @see Queue
 * @see ServerPDUEvent
 * @see ServerPDUEventListener
 * @see SmppObject
 */
public class SMSReceiver extends SmppObject implements ServerPDUEventListener
{
    Session session;
    Queue requestEvents;

    public SMSReceiver(Session session)
    {
        this.session = session;
        requestEvents=new Queue();
    }

    public void handleEvent(ServerPDUEvent event)
    {
        PDU pdu = event.getPDU();
        synchronized (requestEvents)
        {
            requestEvents.enqueue(event);
            requestEvents.notify();
        }
        if (pdu.isRequest())
        {
          //  DebugPrinter.PrintLn("async request received, enqueuing " +pdu.debugString());
        } else if (pdu.isResponse())
        {
           // DebugPrinter.PrintLn("async response received " +pdu.debugString());
        } else
        {
            //DebugPrinter.PrintLn("pdu of unknown class (not request nor " + "response) received, discarding " +   pdu.debugString());
        }
    }

    /**
     * Returns received pdu from the queue. If the queue is empty,
     * the method blocks for the specified timeout.
     */
    public ServerPDUEvent getRequestEvent(long timeout)
    {
        ServerPDUEvent pduEvent = null;
        synchronized (requestEvents)
        {
            if (requestEvents.isEmpty())
            {
                try
                {
                    requestEvents.wait(timeout);
                } catch (InterruptedException e)
                {
                    // ignoring, actually this is what we're waiting for
                }
            } else
            {
                pduEvent = (ServerPDUEvent) requestEvents.dequeue();
            }
        }
        return pduEvent;
    }

    public int getWaitingCount()
    {
        return (requestEvents.size());
    }
}
