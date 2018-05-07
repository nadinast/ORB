package infoApp;

import broker.ToyORB;

public class InfoAppClient {
    public static void main(String[] args) {
        InfoApp info = (InfoApp) ToyORB.getObjectRef("InfoAppServer");
        int temperature = info.get_temp("New York");
        System.out.println(temperature);
    }
}
