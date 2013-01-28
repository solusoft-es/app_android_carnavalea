app_android_carnavalea
======================

Full android app example running on topoos.com platform

You can download the app from http://bit.ly/carnavalea for free

App developed by www.solusoft.es


This app was built using the www.topoos.com platform (platform for developing context aware apps).

## Building

### Eclipse
 * This project has to be build with Google APIs Level 8 or higher!

### topoos APPTOKEN_ADMIN for Access
 * You must write your own app APPTOKEN_ADMIN (which you got when you registered your app) for get it working (Replace XXXXXXXXXXX)
  
```java
public class Config {
  
  ////Must get your tokens from topoos developer panel https://developers.topoos.com
	public static final String APPTOKEN_ADMIN = "xxxxxxxxxxxxxxxx";
```

### Libs
 * You must include topoos SDK in your project libs folder
 * http://docs.topoos.com/tools/sdks/android/download-and-changes-log/

## First RUN

 You must create POI Categories ( http://docs.topoos.com/reference/poi-categories/ ) the first time (AND ONLY THE FIRST time that you run the app).
 For doing that, uncomment the this lines in the first run (after set APPTOKEN_ADMIN in Config.java):
 
 ```java
  	Runnable categorizer = new GenerateCategories();
		Thread thread = new Thread(categorizer);
		thread.start();
```

and then read the LogCat and use the "cat category" specified for set it on Config.java class, in the Categorias member id:

Example:
 
 ```java
  //Replace XX for the integer identifier that you got in the logCat
    public static final int Categorias=XX;
```

Now you must comment again those lines:

 ```java
    Runnable categorizer = new GenerateCategories();
		Thread thread = new Thread(categorizer);
		thread.start();
```

## Map and ArcGIS

You can configure your own ArcGIS feature service if you want to use it:


 ```java
 
public static  String URL = "http://services.arcgis.com/XXXXXXXXXXXXXX/arcgis/rest/services/carnavalea_poi/FeatureServer/0/addFeatures";
  
```

You can configure your own Google Maps signature in this classes:

* layout/pestana3.xml
* layout/detail_map_layout.xml


## Related documentation

 * Register your topoos app http://docs.topoos.com/api-guides/registering-your-app/
 * topoos docs http://docs.topoos.com/
