package infoApp;

import java.util.HashMap;

public class InfoAppImpl implements InfoApp {

    HashMap<Integer, String> roadInfo =  new HashMap<>();
    HashMap<String, Integer> tempInfo =  new HashMap<>();

    public InfoAppImpl(){
        roadInfo.put(1, "Congested");
        roadInfo.put(2, "Free");
        tempInfo.put("Paris", 23);
        tempInfo.put("New York", 30);
    }

    @Override
    public String get_road_info(int road_ID) {
        return roadInfo.get(road_ID);
    }

    @Override
    public int get_temp(String city) {
        return tempInfo.get(city);
    }
}
