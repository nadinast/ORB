package infoApp;

import broker.ToyORB;

public class InfoAppServer {
    public static void main(String[] args) {
        InfoAppImpl infoApp = new InfoAppImpl();
        ToyORB.register("InfoAppServer", infoApp);
    }
}
