package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.text.Text;
import weatherapp.Weather;
import weatherapp.WeatherEnum;
import weatherapp.WeatherStructure;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    private Parent root;

    public void fillData(WeatherStructure structure, String data){
        Text node = (Text)root.lookup("#"+structure.getXml_id());
        if(node != null) {
            node.setText(data + " " + structure.getSuffix());
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        HashMap<WeatherEnum, WeatherStructure> dataMap = new HashMap<>();
        dataMap.put(WeatherEnum.TEMPERATURE, new WeatherStructure("temperature-text", "°C"));
        dataMap.put(WeatherEnum.WIND_SPEED, new WeatherStructure("wind-speed-text", "m/s"));
        dataMap.put(WeatherEnum.WIND_DIRECTION, new WeatherStructure("wind-direction-text", ""));
        dataMap.put(WeatherEnum.RAIN, new WeatherStructure("rain-text", "mm"));
        dataMap.put(WeatherEnum.HUMIDITY, new WeatherStructure("humidity-text", ""));
        dataMap.put(WeatherEnum.CLOUD_COVER, new WeatherStructure("cloud-cover-text", ""));
        dataMap.put(WeatherEnum.SUNRISE, new WeatherStructure("sunrise-text", ""));
        dataMap.put(WeatherEnum.SUNSET, new WeatherStructure("sunset-text", ""));
        dataMap.put(WeatherEnum.PRESSURE, new WeatherStructure("pressure-text", "hPa"));
        dataMap.put(WeatherEnum.VISIBILITY, new WeatherStructure("visibility-text", "km"));
        dataMap.put(WeatherEnum.DAY, new WeatherStructure("day-text", ""));
        dataMap.put(WeatherEnum.SHORT_DAY, new WeatherStructure("short-day-text", ""));
        dataMap.put(WeatherEnum.MIN_TEMPERATURE, new WeatherStructure("min-temperature-text", "°C"));
        dataMap.put(WeatherEnum.MAX_TEMPERATURE, new WeatherStructure("max-temperature-text", "°C"));


        this.root = FXMLLoader.load(getClass().getResource("app_layout.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 450, 800));
        primaryStage.show();

        Weather wdata = new Weather(52.207148,0.122047,"now");

        for(Map.Entry<WeatherEnum, WeatherStructure> entry : dataMap.entrySet()){
            WeatherEnum k = entry.getKey();
            if(wdata.nowData.containsKey(k)){
                fillData(entry.getValue(), wdata.nowData.get(k));
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
