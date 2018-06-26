package ge.magticom.smpp.api;



public class SMSConfigMgr
{
    private static boolean isInited=false;
    private static String smsServerIP=null;
    private static int smsServerPort=0;
    private static String smsUsername=null;
    private static String smsPassword=null;
    private static String systemType=null;


    public static void Init(String aSMSServerIP, int aSMSServerPort,
                            String aUsername, String aPassword, String aSystemType)
    {
        smsServerIP=aSMSServerIP;
        smsServerPort=aSMSServerPort;
        smsUsername=aUsername;
        smsPassword=aPassword;
        systemType=aSystemType;
        isInited=true;
    }

    public static String getSMSServerIP() throws Exception
    {
        if (!isInited) throw new Exception("Config object not yet inited!"); else
            return smsServerIP;
    }

    public static int getSMSServerPort() throws Exception
    {
        if (!isInited) throw new Exception("Config object not yet inited!"); else
            return smsServerPort;
    }

    public static String getSMSUsername() throws Exception
    {
        if (!isInited) throw new Exception("Config object not yet inited!"); else
            return smsUsername;
    }

    public static String getSMSPassword() throws Exception
    {
        if (!isInited) throw new Exception("Config object not yet inited!"); else
            return smsPassword;
    }

    public static String getSystemType() throws Exception
    {
        if (!isInited) throw new Exception("Config object not yet inited!"); else
            return systemType;
    }
}
