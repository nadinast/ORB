package ByteCommunication.RequestReply;

import ByteCommunication.MessageMarshaller.Message;

public interface ByteStreamTransformer
{
	byte[] transform(byte[] in);
	Message getAnswer(Message msg);
}