package sample;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import sample.FXMLDocumentController;


public class SidePanelContentController implements Initializable {


    @FXML
    private JFXButton b1;
    @FXML
    private JFXButton b2;
    @FXML
    private JFXButton b3;
    @FXML
    private JFXButton exit;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
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
