package sample;

import com.jfoenix.controls.JFXHamburger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
        HashMap<String, Image> weatherImages = new HashMap<>();
        weatherImages.put("01d", new Image("/sample/icons/main_icons/01d.png")); //clear sky
        weatherImages.put("01n", new Image("/sample/icons/main_icons/01n.png")); //night variant

        weatherImages.put("02d", new Image("/sample/icons/main_icons/02d.png")); //few clouds
        weatherImages.put("02n", new Image("/sample/icons/main_icons/02n.png"));

        weatherImages.put("03d", new Image("/sample/icons/main_icons/03d.png")); //scattered clouds
        weatherImages.put("03n", new Image("/sample/icons/main_icons/03n.png"));

        weatherImages.put("04d", new Image("/sample/icons/main_icons/04d.png")); //broken clouds
        weatherImages.put("04n", new Image("/sample/icons/main_icons/04n.png"));

        weatherImages.put("09d", new Image("/sample/icons/main_icons/09d.png")); //shower rain
        weatherImages.put("09n", new Image("/sample/icons/main_icons/09n.png"));

        weatherImages.put("10d", new Image("/sample/icons/main_icons/10d.png")); //rain
        weatherImages.put("10n", new Image("/sample/icons/main_icons/10n.png"));

        weatherImages.put("11d", new Image("/sample/icons/main_icons/11d.png")); //thunderstorm
        weatherImages.put("11n", new Image("/sample/icons/main_icons/11n.png"));

        weatherImages.put("13d", new Image("/sample/icons/main_icons/13d.png")); //snow
        weatherImages.put("13n", new Image("/sample/icons/main_icons/13n.png"));

        weatherImages.put("50d", new Image("/sample/icons/main_icons/50d.png")); //mist
        weatherImages.put("50n", new Image("/sample/icons/main_icons/50n.png"));



        HashMap<WeatherEnum, WeatherStructure> dataMap = new HashMap<>();
        dataMap.put(WeatherEnum.TEMPERATURE, new WeatherStructure("temperature-text", "°C"));
        dataMap.put(WeatherEnum.DESCRIPTION, new WeatherStructure("description-text", ""));
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

        ImageView node = (ImageView)root.lookup("#main-icon1");
        node.setImage(weatherImages.get(wdata.nowData.get(WeatherEnum.ICON)));

        DataController dataController = new DataController(root);
        dataController.loadDayData();
        dataController.loadRainGraph();



    }


    public static void main(String[] args) {
        launch(args);
    }
}
