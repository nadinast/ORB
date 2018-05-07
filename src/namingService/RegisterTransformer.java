package namingService;

import ByteCommunication.MessageMarshaller.Marshaller;
import ByteCommunication.MessageMarshaller.Message;
import ByteCommunication.RequestReply.ByteStreamTransformer;

public class RegisterTransformer implements ByteStreamTransformer {

    public byte[] transform(byte[] in)
    {
        Message msg;
        Marshaller m = new Marshaller();
        msg = m.unmarshal(in);

        Message answer = this.getAnswer(msg);

        byte[] bytes = m.marshal(answer);
        return bytes;

    }

    public Message getAnswer(Message msg)
    {
        Message answer;
        System.out.println("NamingService received " + msg.data + " from " + msg.sender);
        if(msg.sender.equals("ToyORB")) {
            ServerDetails serverDetails = NamingService.getServerAddress(msg.data);
            answer = new Message(serverDetails.getClassName(), serverDetails.getServerAddress());
        }
        else {
            NamingService.registerServer(msg);
            answer = new Message("NamingService", NamingService.getServerAddress(msg.sender).getServerAddress());
            System.out.println(NamingService.getServerAddress(msg.sender).getServerAddress());
        }
        return answer;
    }
}
