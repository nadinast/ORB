package namingService;

import ByteCommunication.Registry.Entry;

public class ServerDetails {
    private String className;
    private Entry serverAddress;

    public ServerDetails(String className, Entry serverAddress) {
        this.className = className;
        this.serverAddress = serverAddress;
    }

    public String getClassName() {
        return className;
    }

    public String getServerAddress() {
        return serverAddress.dest() + ":" + serverAddress.port();
    }

}
