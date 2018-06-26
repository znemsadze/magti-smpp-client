package ge.magticom.smpp.test;

import ge.magticom.smpp.ListenerRunnable;
import ge.magticom.smpp.SenderRunnable;
import ge.magticom.smpp.model.SmsQueue;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by zviad on 4/30/18.
 * Test Class For Database Methods
 */
public class DbMethodsT {

    Logger logger=Logger.getLogger(DbMethodsT.class);

    private SenderRunnable senderRunnable;

    private ListenerRunnable listenerRunnablel;


    @Before
    public void init(){
        senderRunnable=new SenderRunnable(null,null);
        listenerRunnablel=new ListenerRunnable(null,null);
    }


    @Test
    public  void getAllFroSend(){
        senderRunnable.getMessagesForSend().forEach(logger::info);
    }

    @Test
    public void saveSubmitSm(){
        listenerRunnablel.saveSubmitSm("pialoMessageId",43L);
    }

    @Test
    public void saveDelivery(){
        listenerRunnablel.saveDelivery("pialoMessageId", SmsQueue.STATE_ID_DELIVERRED);
        listenerRunnablel.saveDelivery("pialoMessageId", SmsQueue.STATE_ID_SMS_CENTER_FAIL);
    }


}
