package generationTool;

import java.io.File;
import java.io.IOException;

public class ProxyFileGenerator {
    private ObjectDetails objDetails;

    public ProxyFileGenerator(Object app){
        objDetails = new ObjectDetails(app);
    }

    public boolean generateServerProxyFile(String fileName){
        fileName = createFilePath(fileName);
        File proxyFile = new File(fileName);
        if(createFile(proxyFile)){

            return true;
        }
        else return false;
    }

    public boolean generateClientProxyFile(String fileName){
        fileName = fileName + ".java";
        File proxyFile = new File(fileName);
        if(createFile(proxyFile)){
            return true;
        }
        else return false;
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
        System.out.println(fileName);
        StringBuffer path = new StringBuffer("src\\");
        String packages = objDetails.getPackage();
        packages = packages.replaceAll("\\.", "\\\\");
        packages = packages.replaceFirst(objDetails.getClassName(), "");
        path.append(packages);
        path.append(fileName);
        path.append(".java");
        System.out.println(path.toString());
        return path.toString();
    }

    public static void main(String[] args) {
        ProxyFileGenerator proxyFileGenerator;
        proxyFileGenerator = new ProxyFileGenerator(new Object());
        proxyFileGenerator.generateServerProxyFile("ServerProxy");
    }
}
