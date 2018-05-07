package generationTool;

import infoApp.InfoAppImpl;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ObjectDetails {
    private Object object;
    private String className;
    private String packageName;
    private String objectInterface;
    private String[] methodNames;
    private HashMap<String, String[]> paramTypes =  new HashMap<>();
    private HashMap<String, String> returnTypes =  new HashMap<>();

    public ObjectDetails(Object object) {
        this.object = object;
        getObjectDetails();
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getObjectInterface() {
        return objectInterface;
    }

    public String[] getMethodNames() {
        return methodNames;
    }

    public HashMap<String, String[]> getParamTypes() {
        return paramTypes;
    }

    public HashMap<String, String> getReturnTypes() {
        return returnTypes;
    }

    public String getReturnTypeAsClass(String returnType){
        switch(returnType){
            case "float": return "Float";
            case "int": return "Integer";
        }
        return "";
    }

    public String capitalizeInitial(String returnType){
        return returnType.substring(0, 1).toUpperCase()
                + returnType.substring(1, returnType.length());
    }

    public String getParamTypeClass(String paramType){
        switch(paramType){
            case "String" : return "String";
            case "float" : return "Float";
            case "int" : return "Integer";
        }
        return "";
    }

    private void getObjectDetails() {
        Class objClass = object.getClass();

        this.className = objClass.getSimpleName();

        this.packageName = objClass.getName().replaceAll("\\." + className, "");

        Class[] interfaces = objClass.getInterfaces();
        for(Class _interface : interfaces)
            if(className.contains(_interface.getSimpleName())) {
                objectInterface = _interface.getSimpleName();
                break;
            }

        Method[] methods = objClass.getDeclaredMethods();
        this.methodNames = new String[methods.length];
        for(int i = 0; i < methods.length; i++){
            methodNames[i] = methods[i].getName();

            this.returnTypes.put(methodNames[i], methods[i].getReturnType().getSimpleName());

            Class[] paramsT = methods[i].getParameterTypes();
            String[] paramTypes = new String[paramsT.length];
            for(int j = 0; j < paramTypes.length; j++)
                paramTypes[j] = paramsT[j].getSimpleName();
            this.paramTypes.put(methodNames[i], paramTypes);
        }
    }

    public static void main(String[] args) {
        InfoAppImpl app = new InfoAppImpl();
        ObjectDetails od = new ObjectDetails(app);
    }

}
