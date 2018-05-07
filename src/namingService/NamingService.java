package namingService;

import ByteCommunication.MessageMarshaller.Message;
import ByteCommunication.Registry.*;
import ByteCommunication.RequestReply.ByteStreamTransformer;
import ByteCommunication.RequestReply.Replyer;

public class NamingService {
    public static int port = 2000;
    private static ByteStreamTransformer nsTransformer = new RegisterTransformer();
    private static Entry namingServiceAddress = new Entry("localhost", 1000);
    private static Replyer rep = new Replyer("NamingService", namingServiceAddress);

    public static void registerServer(Message msg){
        String serverName = msg.sender;
        String className = msg.data;
        ServerDetails serverDetails = new ServerDetails(className, new Entry("localhost", port));
        Registry.instance().put(serverName, serverDetails);
        port++;
        System.out.println("The server " + serverName + " was registered at address: "
                + Registry.instance().get(serverName).getServerAddress()
                + " with obj of type "
                + Registry.instance().get(serverName).getClassName());
    }

    public static ServerDetails getServerAddress(String serverName){
        return Registry.instance().get(serverName);
    }

    public static void main(String[] args) {
        while(true){
            rep.receive_transform_and_send_feedback(nsTransformer);
        }
    }
}
