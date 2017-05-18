package sample;

import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import weatherapp.*;

import java.util.HashMap;

/**
 * Created by treba on 18.05.2017.
 */
public class DataController {
    private Parent root;
    private Weather day_data = null;

    DataController (Parent r){
        root=r;
    }

    public void fillData(WeatherStructure structure, String data){
        Text node = (Text)root.lookup("#"+structure.getXml_id());
        if(node != null) {
            node.setText(data + " " + structure.getSuffix());
        }
    }

    public void loadDayData() throws CoordinateException, TimeFrameException {
        day_data = new Weather(52.207148,0.122047,"day");

        //Filling the data in day screen
        for(int i=0;i<8;i++){

            System.out.println(day_data.dayData.get(i));

            HashMap<WeatherEnum, String> weatherHourlyData = day_data.dayData.get(i);

            String slot = weatherHourlyData.getOrDefault(WeatherEnum.SLOT,"err").substring(3);
            Text slot_text = (Text)root.lookup("#day-hour-text"+i);
            slot_text.setText(slot);

            String temperature = weatherHourlyData.getOrDefault(WeatherEnum.TEMPERATURE,"err");
            fillData(new WeatherStructure("day-temp-text"+i, "°C"),temperature);


            ImageView day_icon = (ImageView)root.lookup("#day-icon"+i);
            String icon = weatherHourlyData.getOrDefault(WeatherEnum.ICON,"err");
            day_icon.setImage(new Image("/sample/icons/main_icons/"+icon+".png"));


            String wind_speed = weatherHourlyData.getOrDefault(WeatherEnum.WIND_SPEED,"err");
            fillData(new WeatherStructure("day-wind-text"+i, "m/s"),wind_speed);

            ImageView wind_direction_icon = (ImageView)root.lookup("#day-wind-icon"+i);
            String direction  = weatherHourlyData.get(WeatherEnum.WIND_DIRECTION);
            wind_direction_icon.setImage(new Image("/sample/icons/wind/"+direction+".png"));


            ImageView day_bike_icon = (ImageView)root.lookup("#day-bike-icon"+i);
            String bike_colour;

            //Very pro choice of bike colour, did tremendous research on cycling conditions
            if (Double.parseDouble(wind_speed)>12 || Double.parseDouble(temperature)<0) bike_colour="r";
            else if (Double.parseDouble(wind_speed)>6 || Double.parseDouble(temperature)<5 || Integer.parseInt(icon.substring(0,2))>5) bike_colour="y";
            else bike_colour  = "g";

            day_bike_icon.setImage(new Image("/sample/icons/bike/"+bike_colour+".png"));
        }
    }


    public void loadRainGraph(){

        Double[] rainData = day_data.dayGraph.get(WeatherEnum.TEMPERATURE);

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Time");
        yAxis.setLabel("Rainfall");

        final AreaChart<String,Number> areaChart = new AreaChart<String,Number>(xAxis,yAxis);

        XYChart.Series series = new XYChart.Series();

        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return (object.intValue()) + "mm";
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });

        for(int i=0;i<8;i++){
            HashMap<WeatherEnum, String> weatherHourlyData = day_data.dayData.get(i);
            String slot = weatherHourlyData.getOrDefault(WeatherEnum.SLOT,"err").substring(3);

            series.getData().add(new XYChart.Data(slot, rainData[i]));


        }

        areaChart.getData().add(series);

        AnchorPane pane = (AnchorPane)root.lookup("#chartPane");


        AnchorPane.setTopAnchor(areaChart, 10.0);
        AnchorPane.setLeftAnchor(areaChart, -30.0);
        AnchorPane.setRightAnchor(areaChart, -10.0);
        AnchorPane.setBottomAnchor(areaChart, -50.0);


        pane.getChildren().add(areaChart);

    }
}
