package ByteCommunication.MessageMarshaller;

public class MethodInvocationMessage extends Message {
    public String methodName;
    public MethodInvocationMessage(String theSender, String methodName, String paramsAsString) {
        super(theSender, paramsAsString);
        this.methodName = methodName;
    }
}
