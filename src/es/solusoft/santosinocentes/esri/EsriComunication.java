package es.solusoft.santosinocentes.esri;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import topoos.Objects.POI;

public class EsriComunication {
	
	//REPLACE URL WITH YOUR OWN ARCGIS FEATURE SERVICE
	
	public static  String URL = "http://services.arcgis.com/XXXXXXXXXXXX/arcgis/rest/services/carnavalea_poi/FeatureServer/0/addFeatures";
	
	public JSONArray createJsonObject(POI poi) {

		JSONObject jsonObjct = new JSONObject();
		JSONObject geometryJson = new JSONObject();
		JSONObject attributesJson = new JSONObject();
		JSONObject spatialReferenceJson = new JSONObject();

		try {
			spatialReferenceJson.put("wkid", 4326);

			geometryJson.put("x", poi.getLongitude());
			geometryJson.put("y", poi.getLatitude());
			geometryJson.put("spatialReference", spatialReferenceJson);

			attributesJson.put("OBJECTID", "");
			attributesJson.put("CreationDate", System.currentTimeMillis());
			attributesJson.put("PhotoID", poi.getName());
			attributesJson.put("Description", poi.getDescription());

			jsonObjct.put("geometry", geometryJson);
			jsonObjct.put("attributes", attributesJson);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.println(0, "error Json Object", e.getMessage());
			e.printStackTrace();

		}
		JSONArray jsArray = new JSONArray();
		jsArray.put(jsonObjct);
		return jsArray;

	}

	public void addPOI(final POI poi) {

		Thread addThread = new Thread() {

			public void run() {
				HttpClient httpClient = new DefaultHttpClient();

				int TIMEOUT_MILLISEC = 10000; // = 10 seconds
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(
						httpClient.getParams(), TIMEOUT_MILLISEC);
				HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);

				HttpPost httpPostRequest = new HttpPost(URL);

				JSONArray jsonObct = createJsonObject(poi);

				try {

					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("f", "json"));
					nameValuePairs.add(new BasicNameValuePair("features",
							jsonObct.toString()));
					nameValuePairs.add(new BasicNameValuePair(
							"rollbackOnFailure", "false"));
					httpPostRequest.setHeader("Content-Type",
							"application/x-www-form-urlencoded");
					httpPostRequest.setEntity(new UrlEncodedFormEntity(
							nameValuePairs));

					HttpResponse response = httpClient.execute(httpPostRequest);
					Log.i("respuesta del server ",
							EntityUtils.toString(response.getEntity()));

				} catch (UnsupportedEncodingException e) {
					Log.println(0, "error enviando Poi", e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					Log.println(0, "error enviando Poi", e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					Log.println(0, "error enviando Poi", e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		addThread.start();
	}
}
