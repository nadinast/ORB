package mathApp;

import broker.ToyORB;

public class MathAppClient {
    public static void main(String[] args) {
        MathApp mathApp = (MathApp) ToyORB.getObjectRef("MathAppServer");
        float addition = mathApp.do_add(1, 2.5f);
        System.out.println(addition);
    }
}
