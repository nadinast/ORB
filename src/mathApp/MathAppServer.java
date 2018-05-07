package mathApp;

import broker.ToyORB;

public class MathAppServer {
    public static void main(String[] args) {
        MathAppImpl mathApp = new MathAppImpl();
        ToyORB.register("MathAppServer", mathApp);
    }
}
