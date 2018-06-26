package ge.magticom.smpp.api;

public interface IMessageListener
{
	public void registerSequenceID(int aSessionID, int aSubscriberID, String aPhone, int aSequenceID);
	public void registerMessageID(int aSequenceID, String aMessageID);
    public void submitReport(String aMessageID, int aStatusID);
}
