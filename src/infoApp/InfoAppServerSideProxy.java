package infoApp;

import ByteCommunication.Commons.Address;
import ByteCommunication.MessageMarshaller.Marshaller;
import ByteCommunication.MessageMarshaller.Message;
import ByteCommunication.MessageMarshaller.MethodInvocationMessage;
import ByteCommunication.MessageMarshaller.MethodMarshaller;
import ByteCommunication.RequestReply.ByteStreamTransformer;
import ByteCommunication.RequestReply.Replyer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InfoAppServerSideProxy{
    private final Address serverAddress;
    private Replyer rep;
    private InfoAppResponseTransformer infoTransformer = new InfoAppResponseTransformer();
    private InfoApp infoApp;

    public InfoAppServerSideProxy(Address serverAddress, InfoAppImpl infoApp){
        this.serverAddress = serverAddress;
        this.infoApp = infoApp;
        rep = new Replyer(this.getClass().getSimpleName(), serverAddress);
    }
    public void start(){
        System.out.println("ServerSideProxy started");
        while(true){
            rep.receive_transform_and_send_feedback(infoTransformer);
        }
    }

    @SuppressWarnings("ALL")
    class InfoAppResponseTransformer implements ByteStreamTransformer {
        @Override
        public byte[] transform(byte[] in) {
            MethodInvocationMessage msg;
            MethodMarshaller m = new MethodMarshaller();
            msg = m.unmarshal(in);

            MethodInvocationMessage answer = this.getAnswer(msg);

            byte[] bytes = m.marshal(answer);
            return bytes;
        }

        @Override
        public MethodInvocationMessage getAnswer(Message msg) {
            String methodName = new String(((MethodInvocationMessage)msg).methodName);
            System.out.println("ServerSideProxy received " + msg.data + " and " + methodName + " from " + msg.sender);
            Object[] receivedParams = parseRawData(msg.data);

            System.out.println(methodName);
            if(methodName.equals("get_road_info\u0000"))
                if (receivedParams.length >= 1) {
                    return new MethodInvocationMessage("InfoServerApp",
                            methodName,
                            infoApp.get_road_info(Integer.class.cast(receivedParams[0])));
                }
            if(methodName.equals("get_temp\u0000")){
                if(receivedParams.length >= 1) {
                    return new MethodInvocationMessage("InfoServerApp",
                            methodName,
                            infoApp.get_temp(String.class.cast(receivedParams[0])) + "");
                    }
            }
            /*try {
                method = infoApp.getClass().getMethod(methodName, null);
                Class[] paramTypes = method.getParameterTypes();
                System.out.println(methodName);
                //Class[] paramTypes = method.getParameterTypes();
                //if(paramTypes.length == receivedParams.length) {
                   *//* for (int i = 0; i < paramTypes.length; i++) {
                        if(!paramTypes[i].getSimpleName().equals(receivedParams[i].getClass().getSimpleName()))

                    }*//*
                    response = method.invoke(infoApp, receivedParams);
                //}

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return new MethodInvocationMessage("InfoAppServer", methodName, "Can't find the method");
            } catch (IllegalAccessException | InvocationTargetException e ) {
                e.printStackTrace();
                return new MethodInvocationMessage("InfoAppServer", methodName, "Can't invoke the method");
            }*/
            return new MethodInvocationMessage("InfoAppServer", methodName, "Can't find the method");
        }

        private Object[] parseRawData(String rawData) {
            Object[] params;
            String[] rawParams;
            if (!rawData.contains(",")){
                params = new Object[1];
                rawParams = new String[1];
                rawParams[0] = rawData;
            }
            else {
                rawParams = rawData.split(",");
                for (int i = 0; i < rawParams.length; i++)
                    rawParams[i] = rawParams[i].trim();
                params = new Object[rawParams.length];
            }
            for(int i = 0; i < params.length; i++) {
                try {
                    int value = Integer.parseInt(rawParams[i]);
                    params[i] = value;
                }
                catch(NumberFormatException e){
                    try{
                        float value = Float.parseFloat(rawParams[i]);
                        params[i] = value;
                    }
                    catch (NumberFormatException e2){
                        params[i] = rawParams[i];
                    }
                }
            }
            return params;
        }
    }
}


