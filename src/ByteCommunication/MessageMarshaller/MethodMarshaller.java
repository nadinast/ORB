package ByteCommunication.MessageMarshaller;

public class MethodMarshaller {
    public byte[] marshal(MethodInvocationMessage theMsg)
    {
        String m = " " + theMsg.sender + "-" + theMsg.data + "-" + theMsg.methodName;
        byte b[] = m.getBytes();
        b[0] = (byte)m.length();
        return b;
    }
    public MethodInvocationMessage unmarshal(byte[] byteArray)
    {
        String msg = new String(byteArray);
        System.out.println(msg);
        String[] msgDetails = msg.split("-");
        String sender = msgDetails[0];
        String params = msgDetails[1];
        String methodName = msgDetails[2];
        System.out.println(sender + " " + params + " " + methodName);
        return new MethodInvocationMessage(sender, methodName, params);
    }
}
