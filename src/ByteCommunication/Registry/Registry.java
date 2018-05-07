package ByteCommunication.Registry;

import namingService.ServerDetails;

import java.util.Hashtable;

public class Registry
{
	private Hashtable<String, ServerDetails> hTable = new Hashtable();
	private static Registry _instance = null;

	private Registry() { }

	public static Registry instance()
	{
		if (_instance == null)
			_instance = new Registry();
		return _instance;
	}

	public void put(String theKey, ServerDetails serverDetails)
	{
		hTable.put(theKey, serverDetails);
	}
	public ServerDetails get(String aKey)
	{
		return (ServerDetails) hTable.get(aKey);
	}
}




