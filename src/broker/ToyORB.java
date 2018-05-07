package broker;

import ByteCommunication.Commons.Address;
import ByteCommunication.MessageMarshaller.Marshaller;
import ByteCommunication.MessageMarshaller.Message;
import ByteCommunication.Registry.Entry;
import ByteCommunication.RequestReply.Requestor;
import generationTool.ProxyFileGenerator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ToyORB {
    private static final int NAMING_SERVICE_PORT = 1000;
    private static final String NAMING_SERVICE_DEST = "localhost";
    private static Address namingServiceAddress = new Entry(NAMING_SERVICE_DEST, NAMING_SERVICE_PORT);
    private static Requestor req = new Requestor("broker-to-namingService");
    private static Marshaller marshaller = new Marshaller();
    private static ProxyFileGenerator proxyFileGenerator;

    public static void register(String name, Object obj){

        Message msg = new Message(name, obj.getClass().getName());

        byte[] sentBytes = marshaller.marshal(msg);
        byte[] receivedBytes;
        receivedBytes = req.deliver_and_wait_feedback(namingServiceAddress, sentBytes);
        Message answer = marshaller.unmarshal(receivedBytes);

        System.out.println(answer.sender + " registered the server " + name + " at " + answer.data);

        startServerProxy(obj.getClass().getName(), answer.data, obj);
    }

    public static Object getObjectRef(String serverName){

        Message msg = new Message("ToyORB", serverName);

        byte[] sentBytes = marshaller.marshal(msg);
        byte[] receivedBytes;
        receivedBytes = req.deliver_and_wait_feedback(namingServiceAddress, sentBytes);
        Message answer = marshaller.unmarshal(receivedBytes);

        String serverAddress = answer.data;
        String className  = answer.sender;
        System.out.println("ToyOrb received address " + serverAddress
                            + " registered for " + className);

        return clientProxyFactory(className, serverAddress);
    }

    /**
     * @param className - name of the class that the implements the common interface
     * @return will return null in case of failure to load proxy, otherwise returns the client proxy
     */
    private static Object clientProxyFactory(String className, String serverAddress){
        /*ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class<?> proxyClass = Class.forName(className, true, loader);*/
        String proxyName = className.replaceFirst("Impl", "ClientSideProxy");
        System.out.println(proxyName);
        try {
            Class proxyClass = Class.forName(proxyName);
            Constructor constructor = proxyClass.getConstructor(String.class);
            Object obj = constructor.newInstance(serverAddress);
            return obj;
        } catch (ClassNotFoundException |
                IllegalAccessException |
                NoSuchMethodException |
                InstantiationException |
                InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }



    private static void startServerProxy(String serverName, String address, Object appObj){
        String serverProxyName = serverName.replaceFirst("Impl", "ServerSideProxy");
        String clientProxyName = serverProxyName.replaceFirst("Server", "Client");

        proxyFileGenerator = new ProxyFileGenerator(appObj);
        if(!proxyFileGenerator.generateServerProxyFile(serverProxyName))
            System.out.println("Server Proxy not generated correctly");
        if(!proxyFileGenerator.generateClientProxyFile(clientProxyName))
        System.out.println("Server Proxy not generated correctly");

        String[] addr = address.split(":");
        Address serverAddress = new Entry(addr[0], Integer.parseInt(addr[1]));
        try {
            Class proxyClass = Class.forName(serverProxyName);
            Constructor constructor = proxyClass.getConstructor(Address.class, appObj.getClass());
            Object obj = constructor.newInstance(serverAddress, appObj);
            for(Method m : Class.forName(serverProxyName).cast(obj).getClass().getDeclaredMethods())
                if(m.getName().equals("start"))
                    m.invoke(Class.forName(serverProxyName).cast(obj));
        } catch (ClassNotFoundException |
                IllegalAccessException |
                InstantiationException |
                NoSuchMethodException |
                InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
