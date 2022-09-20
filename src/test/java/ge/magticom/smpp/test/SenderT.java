package ge.magticom.smpp.test;

import ge.magticom.smpp.api.SMSLogica;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by zviad on 4/30/18.
 * Test Class For submit simple message
 */
public class SenderT {

    private SMSLogica smsLogica;

    @Before
    public   void init(){
        smsLogica = new SMSLogica(1, false);
        try {
            smsLogica.reBind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Simple Message Submiting tests
     * */
    @Test
    public void sendSms() throws Exception {
        /*Send Simple Message
        * */
        smsLogica.submitMessageSimple(-22334455,"599443000","pitalo Message","Pitalo",0,1,false);
        smsLogica.submitMessageSimple(-22334452,"599443000","pitalo Message","Pitalo",0,1,false);
        smsLogica.submitMessageSimple(-22334451,"599443000","pitalo Message","Pitalo",0,1,false);
        /*Send Unicode Message
        * */
//        smsLogica.submitMessageSimple(-22334456,"599443000","pitalo მესიჯი","Pitalo",1,1,false);
//        /*send flash message
//        * */
//        smsLogica.submitMessageSimple(-22334458,"599443000","pitalo flash message","Pitalo",1,1,true);
//        /*Send unicode flash Message
//        * */
//        smsLogica.submitMessageSimple(-22334457,"599443000","pitalo flash მესიჯი","Pitalo",1,1,true);
    }










}
