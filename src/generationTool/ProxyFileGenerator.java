package generationTool;

import infoApp.InfoAppImpl;

import javax.tools.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class ProxyFileGenerator {
    private ObjectDetails objDetails;

    public ProxyFileGenerator(Object app){
        objDetails = new ObjectDetails(app);
    }

    public boolean generateServerProxyFile(String fileName){
        String filePath = createFilePath(fileName);
        File proxyFile = new File(filePath);

        if(createFile(proxyFile)){
            String fileTemplate = Utils.getServerProxyTemplate();

            fileTemplate = fileTemplate.replaceAll("\\{PACKAGE_NAME}", objDetails.getPackageName());
            fileTemplate = fileTemplate.replaceAll("\\{CLASS_NAME}", objDetails.getClassName());
            fileTemplate = fileTemplate.replaceAll("\\{FILE_NAME}", fileName);
            fileTemplate = fileTemplate.replaceAll("\\{OBJECT_INTERFACE}", objDetails.getObjectInterface());

            String[] methodNames = objDetails.getMethodNames();
            for(String methodName : methodNames) {
                String invocationTemplate = Utils.getServerInvocationTemplate();
                invocationTemplate = invocationTemplate.replaceAll("\\{METHOD_NAME}", methodName);
                invocationTemplate = invocationTemplate.replaceAll("\\{OBJECT_INTERFACE}", objDetails.getObjectInterface());

                String[] paramTypes = objDetails.getParamTypes().get(methodName);
                invocationTemplate = invocationTemplate.replaceAll("\\{PARAM_TYPE_CLASS}", objDetails.getParamTypeClass(paramTypes[0]));
                invocationTemplate = invocationTemplate.replaceAll("\\{INDEX}", "0");
                int paramIndex = 1;

                for(int i = 1; i < paramTypes.length; i++) {
                    invocationTemplate = invocationTemplate.replaceAll("\\{OPTIONAL}", ", " +
                            objDetails.getParamTypeClass(paramTypes[i]) + ".class.cast(receivedParams[" + paramIndex + "]){OPTIONAL}");

                    paramIndex++;
                }

                invocationTemplate = invocationTemplate.replaceAll("\\{OPTIONAL}", "");

                fileTemplate = fileTemplate.replaceAll("\\{SERVER_INVOCATION_TEMPLATE}", invocationTemplate + "{SERVER_INVOCATION_TEMPLATE}\n");
            }

            fileTemplate = fileTemplate.replaceAll("\\{SERVER_INVOCATION_TEMPLATE}", "");

            writeToFile(fileTemplate, proxyFile);

            return true;
        }
        else return false;
    }

    public boolean generateClientProxyFile(String fileName){
        String filePath = createFilePath(fileName);
        File proxyFile = new File(filePath);

        if(createFile(proxyFile)){
            String fileTemplate = Utils.getClientProxyTemplate();
            fileTemplate = fileTemplate.replaceAll("\\{PACKAGE_NAME}", objDetails.getPackageName());
            fileTemplate = fileTemplate.replaceAll("\\{CLASS_NAME}", objDetails.getClassName());
            fileTemplate = fileTemplate.replaceAll("\\{FILE_NAME}", fileName);
            fileTemplate = fileTemplate.replaceAll("\\{OBJECT_INTERFACE}", objDetails.getObjectInterface());

            String[] methodNames = objDetails.getMethodNames();
            for(String methodName : methodNames) {
                String returnType = objDetails.getReturnTypes().get(methodName);
                String methodTemplate;

                if(returnType.equals("String")) {
                    methodTemplate = Utils.getClientMethodTemplateString();
                    methodTemplate = replaceMethodSimilarities(methodName, methodTemplate);
                }
                else {
                    methodTemplate = Utils.getClientMethodTemplateNumber();
                    methodTemplate = replaceMethodSimilarities(methodName, methodTemplate);
                    methodTemplate = methodTemplate.replaceAll("\\{RETURN_TYPE}", returnType);
                    methodTemplate = methodTemplate.replaceAll("\\{RETURN_TYPE_CLASS}", objDetails.getReturnTypeAsClass(returnType));
                    methodTemplate = methodTemplate.replaceAll("\\{RETURN_TYPE_WITH_CAPITAL_INITIAL}", objDetails.capitalizeInitial(returnType));
                }

                fileTemplate = fileTemplate.replaceAll("\\{ADD_METHODS}", methodTemplate + "{ADD_METHODS}\n");
            }

            fileTemplate = fileTemplate.replaceAll("\\{ADD_METHODS}", "");

            writeToFile(fileTemplate, proxyFile);

            return true;
        }
        else return false;
    }

    private String replaceMethodSimilarities(String methodName, String methodTemplate) {
        methodTemplate = methodTemplate.replaceAll("\\{METHOD_NAME}", methodName);

        String[] paramTypes = objDetails.getParamTypes().get(methodName);
        methodTemplate = methodTemplate.replaceAll("\\{PARAM_TYPE}", paramTypes[0]);
        int paramIndex = 2;

        if(paramTypes.length == 1) {
            methodTemplate = methodTemplate.replaceAll("\\{OPTIONAL_TYPE} \\{OPTIONAL_NAME}", "");
            methodTemplate = methodTemplate.replaceAll("\\{OPTIONAL_NAME2}", "");
        }
        else
            for(int i = 1; i < paramTypes.length; i++) {
                methodTemplate = methodTemplate.replaceAll("\\{OPTIONAL_TYPE}", ", " + paramTypes[i]);
                if (i + 1 < paramTypes.length) {
                    methodTemplate = methodTemplate.replaceAll("\\{OPTIONAL_NAME}", "param" + paramIndex + "{OPTIONAL_TYPE} {OPTIONAL_NAME} ");
                    methodTemplate = methodTemplate.replaceAll("\\{OPTIONAL_NAME2}", " + \", \"" + " + param" + paramIndex + "{OPTIONAL_NAME2} ");
                    paramIndex++;
                } else {
                    methodTemplate = methodTemplate.replaceAll("\\{OPTIONAL_NAME}", "param" + paramIndex);
                    methodTemplate = methodTemplate.replaceAll("\\{OPTIONAL_NAME2}", " + \", \"" + " + param" + paramIndex);
                }
            }
        return methodTemplate;
    }

    private boolean createFile(File proxyFile){
        try {
            if (proxyFile.createNewFile()){
                return true;
            }else{
                return false;
            }

        } catch (IOException e) {
            return false;
        }
    }

    private String createFilePath(String fileName){
        StringBuffer path = new StringBuffer("src\\");
        String packages = objDetails.getPackageName();
        packages = packages.replaceAll("\\.", "\\\\");
        packages = packages.replaceFirst(objDetails.getClassName(), "");
        path.append(packages);
        path.append("\\");
        path.append(fileName);
        path.append(".java");
        System.out.println(path.toString());
        return path.toString();
    }

    private void writeToFile(String fileContents, File file) {
        try {

            BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(file.getCanonicalPath()));
            byte[] contentBytes = fileContents.getBytes();
            System.out.println(file.getCanonicalPath());
            buf.write(contentBytes);
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ProxyFileGenerator proxyFileGenerator;
        proxyFileGenerator = new ProxyFileGenerator(new InfoAppImpl());
        proxyFileGenerator.generateServerProxyFile("ServerProxy");
        proxyFileGenerator.generateClientProxyFile("ClientProxy");
    }
}
