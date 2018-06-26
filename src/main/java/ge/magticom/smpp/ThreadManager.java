package ge.magticom.smpp;

import ge.magticom.smpp.api.SMSLogica;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zviad on 4/30/18.
 * Main class
 */
public class ThreadManager {


    private static Properties props;
    private static Integer runThreads;
    private static Integer  sendersCnt;
    private static String actioncCmd;

    static {
        props = new Properties();
        try {
            runThreads = 0;
            props.load(SenderRunnable.class.getClassLoader().getResourceAsStream("config.properties"));
            actioncCmd = props.getProperty("Action");
            sendersCnt = Integer.parseInt(props.getProperty("SenderCount"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static ExecutorService pool = Executors.newFixedThreadPool(14, runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName(String.format("sms-broadcast %s", thread.getId()));
        return thread;
    });

    public static void main(String[] argv) throws Exception {
        List<SenderRunnable> senders = new ArrayList<>();
        List<ListenerRunnable> listeners = new ArrayList<>();
        String cmd = actioncCmd;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        runThreads++;
        while (runThreads > 0) {
            if ( "StartSending".equals(cmd)) {
                for (int i = 0; i < sendersCnt; i++) {
                    SMSLogica smsLogica = new SMSLogica(i + 1, false);
                    smsLogica.reBind();
                    SenderRunnable sender = new SenderRunnable(null, smsLogica);
                    senders.add(sender);
                    ListenerRunnable listener = new ListenerRunnable(smsLogica, null);
                    listeners.add(listener);
                    pool.submit(sender);
                    runThreads++;
                    pool.submit(listener);
                    runThreads++;
                }
                cmd = in.readLine();
            }else if("exit".equals(cmd)){
                senders.forEach(item->{
                     item.stop();
                    runThreads--;
                });
                listeners.forEach(item->{
                    item.stop();
                    runThreads--;
                });
            }
        }
        pool.shutdown();
    }

}
