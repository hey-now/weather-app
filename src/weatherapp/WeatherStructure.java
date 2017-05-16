package weatherapp;

/**
 * Created by pighe on 16/05/2017.
 */
public class WeatherStructure {
    private String xml_id;
    private String suffix;

    public WeatherStructure(String xml_id, String suffix){
        this.xml_id = xml_id;
        this.suffix = suffix;
    }

    public String getXml_id() {
        return xml_id;
    }

    public String getSuffix() {
        return suffix;
    }
}
