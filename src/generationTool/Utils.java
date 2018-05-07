package generationTool;

public final class Utils {
    private static final String CLIENT_PROXY_TEMPLATE =
            "package {PACKAGE_NAME};\n" +
            "\n" +
            "import ByteCommunication.Commons.Address;\n" +
            "import ByteCommunication.MessageMarshaller.Message;\n" +
            "import ByteCommunication.MessageMarshaller.MethodInvocationMessage;\n" +
            "import ByteCommunication.MessageMarshaller.MethodMarshaller;\n" +
            "import ByteCommunication.Registry.Entry;\n" +
            "import ByteCommunication.RequestReply.Requestor;\n" +
            "\n" +
            "public class {CLASS_NAME} implements {OBJECT_INTERFACE}{\n" +
            "    private Requestor req = new Requestor(\"clientProxy-to-serverProxy\");\n" +
            "    private Address serverAddress;\n" +
            "    private MethodMarshaller marshaller = new MethodMarshaller();\n" +
            "\n" +
            "    public {CLASS_NAME}(String serverAddress){\n" +
            "        String[] address = serverAddress.split(\":\");\n" +
            "        int port = Integer.parseInt(address[1]);\n" +
            "        Address destAddress = new Entry(address[0], port);\n" +
            "        this.serverAddress = destAddress;\n" +
            "    }\n" +
            "\n" +
            "{ADD_METHODS}" +
            "\n" +
            "    private String invokeServerMethod(String methodName, String params){\n" +
            "\n" +
            "        MethodInvocationMessage msg = new MethodInvocationMessage(\n" +
            "                this.getClass().getSimpleName(), methodName, params);\n" +
            "\n" +
            "        byte[] sentBytes = marshaller.marshal(msg);\n" +
            "        byte[] receivedBytes = req.deliver_and_wait_feedback(serverAddress, sentBytes);\n" +
            "        Message answer = marshaller.unmarshal(receivedBytes);\n" +
            "\n" +
            "        System.out.println(\"Server method invocation returned with: \"\n" +
            "                + answer.sender + \", \" + answer.data + \" \" + ((MethodInvocationMessage) answer).methodName);\n" +
            "        return answer.data;\n" +
            "    }\n" +
            "\n" +
            "}\n";


    private static final String CLIENT_METHOD_TEMPLATE_STRING =
            "    @Override\n" +
            "    public String {METHOD_NAME}({PARAM_TYPE} {PARAM_NAME}{OPTIONAL_TYPE} {OPTIONAL_NAME}) {\n" +
            "        return this.invokeServerMethod(\"{METHOD_NAME}\", {PARAM_NAME} + \"\");\n" +
            "    }\n";

    private static final String CLIENT_METHOD_TEMPLATE_NUMBER =
            "    @Override\n" +
                    "    public {RETURN_TYPE} {METHOD_NAME}({PARAM_TYPE} {PARAM_NAME}{OPTIONAL_TYPE} {OPTIONAL_NAME}) {\n" +
                    "        return {RETURN_TYPE_CLASS}.parse{RETURN_TYPE_WITH_CAPITAL_INITIAL}(this.invokeServerMethod(\"{METHOD_NAME}\", {PARAM_NAME}{OPTIONAL_NAME}));\n" +
                    "    }\n";


    private static final String SERVER_PROXY_TEMPLATE =
            "package {PACKAGE_NAME};\n" +
            "\n" +
            "import ByteCommunication.Commons.Address;\n" +
            "import ByteCommunication.MessageMarshaller.Marshaller;\n" +
            "import ByteCommunication.MessageMarshaller.Message;\n" +
            "import ByteCommunication.MessageMarshaller.MethodInvocationMessage;\n" +
            "import ByteCommunication.MessageMarshaller.MethodMarshaller;\n" +
            "import ByteCommunication.RequestReply.ByteStreamTransformer;\n" +
            "import ByteCommunication.RequestReply.Replyer;\n" +
            "\n" +
            "import java.lang.reflect.InvocationTargetException;\n" +
            "import java.lang.reflect.Method;\n" +
            "\n" +
            "public class {CLASS_NAME}{\n" +
            "    private final Address serverAddress;\n" +
            "    private Replyer rep;\n" +
            "    private {OBJECT_INTERFACE}ResponseTransformer appTransformer = new {OBJECT_INTERFACE}ResponseTransformer();\n" +
            "    private InfoApp infoApp;\n" +
            "\n" +
            "    public {CLASS_NAME}(Address serverAddress, {OBJECT_INTERFACE}Impl app){\n" +
            "        this.serverAddress = serverAddress;\n" +
            "        this.app = app;\n" +
            "        rep = new Replyer(this.getClass().getSimpleName(), serverAddress);\n" +
            "    }\n" +
            "    public void start(){\n" +
            "        System.out.println(\"ServerSideProxy started\");\n" +
            "        while(true){\n" +
            "            rep.receive_transform_and_send_feedback(appTransformer);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    @SuppressWarnings(\"ALL\")\n" +
            "    class {OBJECT_INTERFACE}ResponseTransformer implements ByteStreamTransformer {\n" +
            "        @Override\n" +
            "        public byte[] transform(byte[] in) {\n" +
            "            MethodInvocationMessage msg;\n" +
            "            MethodMarshaller m = new MethodMarshaller();\n" +
            "            msg = m.unmarshal(in);\n" +
            "\n" +
            "            MethodInvocationMessage answer = this.getAnswer(msg);\n" +
            "\n" +
            "            byte[] bytes = m.marshal(answer);\n" +
            "            return bytes;\n" +
            "        }\n" +
            "\n" +
            "        @Override\n" +
            "        public MethodInvocationMessage getAnswer(Message msg) {\n" +
            "            String methodName = new String(((MethodInvocationMessage)msg).methodName);\n" +
            "            System.out.println(\"ServerSideProxy received \" + msg.data + \" and \" + methodName + \" from \" + msg.sender);\n" +
            "            Object[] receivedParams = parseRawData(msg.data);\n" +
            "\n" +
            "            if(methodName.equals(\"get_road_info\"))\n" +
            "                if (receivedParams.length >= 1) {\n" +
            "                    System.out.println(\"in here\");\n" +
            "                    System.out.println(Integer.class.cast(receivedParams[0]));\n" +
            "                    return new MethodInvocationMessage(\"InfoServerApp\",\n" +
            "                            methodName,\n" +
            "                            infoApp.get_road_info(Integer.class.cast(receivedParams[0])));\n" +
            "                }\n" +
            "            if(methodName.equals(\"get_temp\\u0000\")){\n" +
            "                if(receivedParams.length >= 1) {\n" +
            "                    System.out.println(\"in here\");\n" +
            "                    System.out.println(String.class.cast(receivedParams[0]));\n" +
            "                    return new MethodInvocationMessage(\"InfoServerApp\",\n" +
            "                            methodName,\n" +
            "                            infoApp.get_temp(String.class.cast(receivedParams[0])) + \"\");\n" +
            "                    }\n" +
            "            }\n" +
            "\n" +
            "            return new MethodInvocationMessage(\"InfoAppServer\", methodName, \"Can't find the method\");\n" +
            "        }\n" +
            "\n" +
            "        private Object[] parseRawData(String rawData) {\n" +
            "            Object[] params;\n" +
            "            String[] rawParams;\n" +
            "            if (!rawData.contains(\",\")){\n" +
            "                params = new Object[1];\n" +
            "                rawParams = new String[1];\n" +
            "                rawParams[0] = rawData;\n" +
            "            }\n" +
            "            else {\n" +
            "                rawParams = rawData.split(\",\");\n" +
            "                for (int i = 0; i < rawParams.length; i++)\n" +
            "                    rawParams[i] = rawParams[i].trim();\n" +
            "                params = new Object[rawParams.length];\n" +
            "            }\n" +
            "            for(int i = 0; i < params.length; i++) {\n" +
            "                try {\n" +
            "                    int value = Integer.parseInt(rawParams[i]);\n" +
            "                    params[i] = value;\n" +
            "                }\n" +
            "                catch(NumberFormatException e){\n" +
            "                    try{\n" +
            "                        float value = Float.parseFloat(rawParams[i]);\n" +
            "                        params[i] = value;\n" +
            "                    }\n" +
            "                    catch (NumberFormatException e2){\n" +
            "                        params[i] = rawParams[i];\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "            return params;\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "\n";

    private static final String SERVER_METHOD_INVOCATION_TEMPLATE =
            "            if(methodName.equals(\"{METHOD_NAME}\u0000\"))\n" +
                    "                if (receivedParams.length >= 1) {\n" +
                    "                    return new MethodInvocationMessage(\"{OBJECT_INTERFACE}Server\",\n" +
                    "                            methodName,\n" +
                    "                            app.{METHOD_NAME}({PARAM_TYPE_CLASS}.class.cast(receivedParams[{INDEX}]){OPTIONAL}));\n" +
                    "                }\n";

    public static String getServerProxyTemplate() {
        return SERVER_PROXY_TEMPLATE;
    }


    public static String getClientProxyTemplate(){
        return CLIENT_PROXY_TEMPLATE;
    }


    public static String getClientMethodTemplateString() {
        return CLIENT_METHOD_TEMPLATE_STRING;
    }


    public static String getClientMethodTemplateNumber() {
        return CLIENT_METHOD_TEMPLATE_NUMBER;
    }
}
