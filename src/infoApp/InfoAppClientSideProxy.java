package infoApp;

import ByteCommunication.Commons.Address;
import ByteCommunication.MessageMarshaller.Message;
import ByteCommunication.MessageMarshaller.MethodInvocationMessage;
import ByteCommunication.MessageMarshaller.MethodMarshaller;
import ByteCommunication.Registry.Entry;
import ByteCommunication.RequestReply.Requestor;

public class InfoAppClientSideProxy implements InfoApp{
    private Requestor req = new Requestor("clientProxy-to-serverProxy");
    private Address serverAddress;
    private MethodMarshaller marshaller = new MethodMarshaller();

    public InfoAppClientSideProxy(String serverAddress){
        String[] address = serverAddress.split(":");
        int port = Integer.parseInt(address[1]);
        Address destAddress = new Entry(address[0], port);
        this.serverAddress = destAddress;
    }

    @Override
    public String get_road_info(int road_ID) {

        return this.invokeServerMethod("get_road_info", road_ID + "");
    }

    @Override
    public int get_temp(String city) {
        return Integer.parseInt(this.invokeServerMethod("get_temp", city));
    }

    private String invokeServerMethod(String methodName, String params){

        MethodInvocationMessage msg = new MethodInvocationMessage(
                this.getClass().getSimpleName(), methodName, params);

        byte[] sentBytes = marshaller.marshal(msg);
        byte[] receivedBytes = req.deliver_and_wait_feedback(serverAddress, sentBytes);
        Message answer = marshaller.unmarshal(receivedBytes);

        System.out.println("Server method invocation returned with: "
                + answer.sender + ", " + answer.data + " " + ((MethodInvocationMessage) answer).methodName);
        return answer.data;
    }

}
