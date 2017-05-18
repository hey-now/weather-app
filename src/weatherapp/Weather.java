/*
 * 	authored by Tom Strudwick ~ trs44 ~ 11.05.2017
 * 	
 * 	Interactive Design ~ group 16
 * 
 */

package weatherapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import json.*;

public class Weather {
	
	public double lat;
	public double lon;
	public String timeframe;
	
	private String url = "http://api.openweathermap.org/data/2.5/";
	private String key = "a355dea360f18429136da6d650a448aa";
	
	public Map<WeatherEnum, String> nowData;
	
	/*
	 *  format for nowData:
	 * 
	 *  {
	 *  
	 *  	icon: < icon file name (no extension) >,
	 *  	temperature: < current temperature in degC >,
	 *  	wind speed: < wind speed in ms-1 >,
	 *  	wind direction: < wind direction in deg clockwise of North >,
	 *  	rain: < mm of rainfall in the past 3 hours >,
	 *  	humidity: < humidity value / 100 >,
	 *  	cloud cover: < cloud cover / 100 >,
	 *  	sunrise: < time of sunrise in s since epoch, must be converted to Long and multiplied by 1000L for use in Date() >,
	 *  	sunset: < time of sunset >,
	 *  	pressure: < air pressure in hpa >,
	 *  	visibility: < visibility in kilometres >
	 *  
	 *  }
	 * 
	 */
	
	public List<HashMap<WeatherEnum, String>> dayData;
	public Map<WeatherEnum, Double[]> dayGraph;
	
	/*
	 *  format for dayData:
	 *  
	 *  [
	 *  
	 *  	{ // 8 time slots
	 *  
	 *  		icon: < icon file name (no extension) >,
	 *  		wind speed: < wind speed in ms-1 >,
	 *  		wind direction: < wind direction in deg clockwise of North >,
	 *  		cloud cover: < cloud cover / 100 >,
	 *  		slot: < day and time formatted as 'DDD TT:TT', intended for use as column/graph axis labels >
	 *  
	 *  	},
	 *  
	 *  	{
	 *  
	 *  		...
	 *  
	 *  	},
	 *  
	 *  	...
	 *  
	 *  ]
	 *  
	 *  format for dayGraph:
	 *  
	 *  {
	 *  
	 *  	rain: [ ... ], // 8 values each for rain (mm) and temperature (degC) corresponding to time slots in dayData
	 *  	temperature: [ ... ]
	 *  
	 *  }
	 * 
	 */
	
	public List<HashMap<WeatherEnum, String>> weekData;
	
	/*
	 *  format for weekData:
	 *  
	 *  [
	 *  
	 *  	{ // 7 days
	 *  
	 *  		day: < full day name >,
	 *  		short day: < day name as DDD >,
	 *  		icon: < icon file name (no extension) >,
	 *  		temperature: < average temperature through day in degC >,
	 *  		min temperature: < minimum temperature of the day in degC>,
	 *  		max temperature: < maximum temperature of the day in degC>,
	 *  		wind speed: < wind speed in ms-1 >,
	 *  		wind direction: < wind direction in deg clockwise of North >,
	 *  		cloud cover: < cloud cover / 100 >,
	 * 			rain: < average mm of rainfall across each 3 hour slot in the day >
	 * 
	 *		},
	 *
	 *		{
	 *
	 *			...
	 *
	 *		},
	 *
	 *		...
	 *
	 * ]
	 * 	
	 */
	
	public Weather(double lattitude, double longitude, String frame) throws TimeFrameException, CoordinateException {
		
		// timeframe is either 'now', 'day', or 'week'
		
		if (lattitude < -90.0 || lattitude > 90.0 || longitude < -180.0 || longitude > 180.0) throw new CoordinateException("The lattitude or longitude was out of bounds : lat : -90 < " + Double.toString(lattitude) + " < 90 : lon : -180 < " + Double.toString(longitude) + " < 180");
		
		lat = lattitude;
		lon = longitude;
		timeframe = frame;
		
		lat = 52.2025;
		lon = 0.1312;
		
		if (timeframe.equals("now")) getNow();
		else if (timeframe.equals("day")) getDay();
		else if (timeframe.equals("week")) getWeek();
		else throw new TimeFrameException("The time frame is not of the correct format - should be 'now', 'day', or 'week'.  Input : " + timeframe);
	
	}
	
	private void getNow() {
		
		try {
			
			JSONObject data = generalPOST(url + "weather?lat=" + lat + "&lon=" + lon + "&appid=" + key);
			System.out.println(data);
			
			// for some reason these requests do not work despite the URLs they request working in a browser
			
			//JSONObject uviData = generalPOST("http://api.openweathermap.org/v3/uvi/" + Double.toString(round(lat, 0) + 0.25) + "," + Double.toString(round(lon, 0) + 0.25) + "/current.json?appid=" + key);
			//System.out.println(uviData);
			
			//JSONObject coData = generalPOST("http://api.openweathermap.org/pollution/v1/co/" + Integer.toString((int) lat) + "," + Integer.toString((int) lon) + "/current.json?appid=" + "cec097ef8d171ffd4358bcf96990eb9f");
			//System.out.println(coData);
			
			nowData = new HashMap<>();
			
			nowData.put(WeatherEnum.ICON, data.getJSONArray("weather").getJSONObject(0).getString("icon"));
			nowData.put(WeatherEnum.TEMPERATURE, Double.toString(round(data.getJSONObject("main").getDouble("temp") - 273.15, 0)));
			nowData.put(WeatherEnum.WIND_SPEED, Double.toString(data.getJSONObject("wind").getDouble("speed")));
			nowData.put(WeatherEnum.WIND_DIRECTION, direction(data.getJSONObject("wind").getDouble("deg")));
			try {
				
				nowData.put(WeatherEnum.RAIN, Double.toString(data.getJSONObject("rain").getDouble("3h")));
			
			} catch (JSONException e) {
				
				System.out.println(e.getMessage());
				nowData.put(WeatherEnum.RAIN, "0.0");
				
			}
			nowData.put(WeatherEnum.HUMIDITY, Integer.toString(data.getJSONObject("main").getInt("humidity")));
			nowData.put(WeatherEnum.CLOUD_COVER, Integer.toString(data.getJSONObject("clouds").getInt("all")));
			nowData.put(WeatherEnum.SUNRISE, new SimpleDateFormat("h:mm a").format((long)data.getJSONObject("sys").getInt("sunrise")*1000l));
			nowData.put(WeatherEnum.SUNSET, new SimpleDateFormat("h:mm a").format((long)data.getJSONObject("sys").getInt("sunset")*1000l));
			nowData.put(WeatherEnum.PRESSURE, Double.toString(data.getJSONObject("main").getDouble("pressure")));
			try {
				
				nowData.put(WeatherEnum.VISIBILITY, Integer.toString(data.getInt("visibility") / 1000));
			
			} catch (JSONException e) {
				
				System.out.println("Error with getting visibility: " + e.getMessage());
				nowData.put(WeatherEnum.VISIBILITY, "1");
				
			}
			
			System.out.println(nowData.toString());
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	private void getDay() {
		
		try {
			
			JSONObject data = generalPOST(url + "forecast?lat=" + lat + "&lon=" + lon + "&cnt=8&appid=" + key);
			System.out.println(data);
			
			dayData = new ArrayList<HashMap<WeatherEnum, String>>();
			dayGraph = new HashMap<WeatherEnum, Double[]>();
			Double[] graphRain = new Double[8];
			Double[] graphTemp = new Double[8];
			
			for (int i = 0; i < 8; i ++) {
				
				JSONObject r = data.getJSONArray("list").getJSONObject(i);
				HashMap<WeatherEnum, String> slot = new HashMap<>();
				
				Date d = new Date(Integer.toUnsignedLong(r.getInt("dt")) * 1000L + 1L);
				String name = (new SimpleDateFormat("E")).format(d);
				String time = name + " " + r.getString("dt_txt").substring(11, 16);
				
				slot.put(WeatherEnum.ICON, r.getJSONArray("weather").getJSONObject(0).getString("icon"));
				slot.put(WeatherEnum.WIND_SPEED, Double.toString(r.getJSONObject("wind").getDouble("speed")));
				slot.put(WeatherEnum.WIND_DIRECTION, direction(r.getJSONObject("wind").getDouble("deg")));
				slot.put(WeatherEnum.CLOUD_COVER, Integer.toString(r.getJSONObject("clouds").getInt("all")));
				slot.put(WeatherEnum.SLOT, time);
				
				dayData.add(slot);
				
				try {
					
					graphRain[i] = r.getJSONObject("rain").getDouble("3h");
					
				} catch (JSONException e) {
					
					System.out.println(e.getMessage());
					graphRain[i] = 0.0;
					
				}
				graphTemp[i] = round(r.getJSONObject("main").getDouble("temp") - 273.15, 2);
				
			}
			
			dayGraph.put(WeatherEnum.RAIN, graphRain);
			dayGraph.put(WeatherEnum.TEMPERATURE, graphTemp);
			
			System.out.println(dayData.toString());
			System.out.println(dayGraph.toString());
			System.out.println(Arrays.toString(graphRain));
			System.out.println(Arrays.toString(graphTemp));
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	private void getWeek() {
		
		try {
			
			JSONObject data = generalPOST(url + "forecast/daily?lat=" + lat + "&lon=" + lon + "&cnt=7&appid=" + key);
			System.out.println(data);
			
			weekData = new ArrayList<HashMap<WeatherEnum, String>>();
			
			for (int i = 0; i < 7; i ++) {
				
				JSONObject r = data.getJSONArray("list").getJSONObject(i);
				HashMap<WeatherEnum, String> day = new HashMap<>();
				
				Date d = new Date(Integer.toUnsignedLong(r.getInt("dt")) * 1000L);
				String sname = (new SimpleDateFormat("E")).format(d);
				String name = (new SimpleDateFormat("EEEE")).format(d);
				
				day.put(WeatherEnum.DAY, name);
				day.put(WeatherEnum.SHORT_DAY, sname);
				day.put(WeatherEnum.ICON, r.getJSONArray("weather").getJSONObject(0).getString("icon"));
				day.put(WeatherEnum.TEMPERATURE, Double.toString(round(r.getJSONObject("temp").getDouble("day") - 273.15, 0)));
				day.put(WeatherEnum.MIN_TEMPERATURE, Double.toString(round(r.getJSONObject("temp").getDouble("min") - 273.15, 0)));
				day.put(WeatherEnum.MAX_TEMPERATURE, Double.toString(round(r.getJSONObject("temp").getDouble("max") - 273.15, 0)));
				day.put(WeatherEnum.WIND_SPEED, Double.toString(r.getDouble("speed")));
				day.put(WeatherEnum.WIND_DIRECTION, direction(r.getDouble("deg")));
				day.put(WeatherEnum.CLOUD_COVER, Integer.toString(r.getInt("clouds")));
				try {
					
					day.put(WeatherEnum.RAIN, Double.toString(r.getDouble("rain")));
					
				} catch (JSONException e) {
					
					System.out.println(e.getMessage());
					day.put(WeatherEnum.RAIN, "0.0");
					
				}
				
				weekData.add(day);
				
			}
			
			System.out.println(weekData.toString());
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	private JSONObject generalPOST(String requesturl) throws GeneralPOSTException {
		
		try {
			
			System.out.println(requesturl);
			
			URL request = new URL(requesturl);
		    HttpURLConnection connection = (HttpURLConnection) request.openConnection();
	
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	
		    int responseCode = connection.getResponseCode();
	
		    if (responseCode == HttpURLConnection.HTTP_OK) {
		    	
		        // success, get result from server
		    	
		        BufferedReader result = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		        StringBuffer response = new StringBuffer();
		        String resultLine;
	
		        while ((resultLine = result.readLine()) != null) {response.append(resultLine);}
		        
		        result.close();
		        
		        return new JSONObject(response.toString());
		        
		    } else {
		    	
		        // error
		    	
		        System.out.println(connection.getResponseMessage());
		        
		        throw new GeneralPOSTException("Result could not be fetched from server.");
		        
		    }
		    
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			e.printStackTrace();
			
			throw new GeneralPOSTException("Error in establishing connection to server.  URL may be incorrect.");
			
		}
		
	}
	
	public static double round(double value, int places) {

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	    
	}
	
	public String direction(Double d) {
		
		// takes degrees from North as input and outputs direction on 8-point compass
		
		if (d > 337.5 || d < 22.5) return "N";
		if (d < 67.5) return "NE";
		if (d < 112.5) return "E";
		if (d < 157.5) return "SE";
		if (d < 202.5) return "S";
		if (d < 247.5) return "SW";
		if (d < 292.5) return "W";
		return "NW";
		
	}
	
	public static void main(String[] args) {
		
		try {
			
			Weather w = new Weather(0.0, 0.0, "week");
			
		} catch (Exception e) {

			e.printStackTrace();
			
		}
		
	}
	
}