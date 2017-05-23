package sample;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import json.JSONArray;
import json.JSONObject;
import sample.FXMLDocumentController;
import weatherapp.CoordinateException;
import weatherapp.GeneralPOSTException;
import weatherapp.TimeFrameException;
import weatherapp.Weather;


public class SidePanelContentController implements Initializable {


    @FXML
    private JFXButton b1;
    @FXML
    private JFXButton b2;
    @FXML
    private JFXButton b3;
    @FXML
    private JFXButton exit;

    @FXML
    private ImageView search;

    @FXML
    private JFXTextField locationf;

    @FXML
    private VBox root;

    private String gkey = "AIzaSyBeh6I5z5h0XT36uHx5NZmw4cOcPU6RbHM";


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Weather wdata = null;
        try {
            wdata = new Weather(52.207148,0.122047,"now");
        } catch (TimeFrameException e) {
            e.printStackTrace();
        } catch (CoordinateException e) {
            e.printStackTrace();
        }

        Main main = Main.getInstance();

        Weather finalWdata = wdata;

        search.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("Clicked!");
               // Text node = (Text)root.lookup("#location-text");
                search.setVisible(false);
               // node.setVisible(false);
                //JFXTextField text = (JFXTextField)root.lookup("#location-field");
                locationf.setDisable(false);
                locationf.setVisible(true);

                locationf.setPromptText("Enter the location name or postcode (e.g. 'Cambridge, UK' or 'CB2 8PH')");

                locationf.setOnKeyPressed(new EventHandler<KeyEvent>(){

                    @Override
                    public void handle(KeyEvent keyEvent){
                        if(keyEvent.getCode() == KeyCode.ENTER){
                            String input = locationf.getText().replaceAll(" ", "+");
                            String query = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+input+"&key="+gkey;
                            JSONObject data = null;
                            try {
                                data = finalWdata.generalPOST(query);
                            }catch(GeneralPOSTException e){
                                System.out.println(e.getMessage());
                            }
                            JSONArray predictions = data.getJSONArray("predictions");
                            if(predictions.length() > 0) {
                                String name = predictions.getJSONObject(0).getString("description");
                                String id = predictions.getJSONObject(0).getString("place_id");
                                System.out.println("Name: " + name + " id: " + id);

                                String placeq = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + id + "&key=" + gkey;
                                JSONObject pdata = null;
                                try {
                                    pdata = finalWdata.generalPOST(placeq);
                                } catch (GeneralPOSTException e) {
                                    System.out.println(e.getMessage());
                                }
                                double lat = pdata.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                double lng = pdata.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                System.out.println("Lat: " + lat + " Long:" + lng);
                                try {
                                    main.changeLocation(name, lat, lng);
                                } catch (Exception e) {
                                    //...
                                }
                                locationf.setText("");
                                locationf.setVisible(false);
                                locationf.setDisable(true);
                              //  node.setVisible(true);
                                search.setVisible(true);
                            }else{
                                //TODO: what to do if no results found?
                            }
                        }
                    }
                });
            }
        });
    }


    @FXML
    private void buttonHandler(ActionEvent event) throws Exception{
        JFXButton btn = (JFXButton) event.getSource();
        Main main = Main.getInstance();

        String location = "";

        System.out.println(btn.getText());
        location=btn.getText();
        main.changeLocation(location);

    }

    @FXML
    private void exit(ActionEvent event) {
        System.exit(0);
    }
    
}
