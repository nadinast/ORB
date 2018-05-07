package generationTool;

public class ObjectDetails {
    private Object object;

    public ObjectDetails(Object object) {
        this.object = object;
    }

    public String getPackage() {
        return "this.is.another.package.AClassName";
    }

    public String getClassName() {
        return "AClassName";
    }
}
