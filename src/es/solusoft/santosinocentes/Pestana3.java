package es.solusoft.santosinocentes;

import java.util.List;
import java.util.concurrent.Callable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import topoos.Objects.POI;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import es.solusoft.carnavalea.R;
import es.solusoft.santosinocentes.internal.CircleOverlay;
import es.solusoft.santosinocentes.internal.Config;
import es.solusoft.santosinocentes.internal.JokeOverlay;
import es.solusoft.santosinocentes.internal.MapUtils;
import es.solusoft.santosinocentes.internal.Config.SEARCH_TYPE;

//Contenido de la pestaña "Bromas cercanas a ti en el Mapa"

public class Pestana3 extends CustomMapActivity {

	private static final int WORKER_MSG_OK = 1;
	private static final int WORKER_MSG_ERROR = -1;

	private LocationManager mLocationManager;
	
	private MapView mMapView;
	private MapController mMapController;

	private Handler m_Handler = new Handler(new ResultMessageCallback());

	private double m_SearchCenterLocation_lat;
	private double m_SearchCenterLocation_lng;
	
	private float mMapSavedY;
	private float mMapSavedX;
	private int mMapSavedZoom;
	
	
	private List<POI> m_pois = null;
	
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// hide titlebar of application must be before setting the layout
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.pestana3);			
		
		// Get view references
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapController = mMapView.getController();
		/*
		mMapView.setOnTouchListener(new View.OnTouchListener() {

	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            // TODO Auto-generated method stub
	            GeoPoint p = null;

	            if (event.getAction() == MotionEvent.ACTION_UP) {
	                p = mMapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
	                
	                GeoPoint mapCenter = mMapView.getProjection().fromPixels(
	                		mMapView.getWidth()/2,
	                        mMapView.getHeight()/2);
	                
	                

	                m_SearchCenterLocation_lat = mapCenter.getLatitudeE6() / (1000000f);
	                m_SearchCenterLocation_lng = mapCenter.getLongitudeE6() / (1000000f);
	                	                
	                centerLocationAndLoadJokes(mapCenter.getLatitudeE6(), mapCenter.getLongitudeE6());
	                
	                Log.e("MAPA","UP");
	            }
	            return false;
	        }

	    });
*/
		//Modificamos la fuente de la marquesina
		/*Typeface tf = Typeface.createFromAsset(this.getAssets(),
	            "fonts/3Dumb.ttf");
		marquesina.setTypeface(tf);
		*/
		
		
		// Get Location Manager references
		mLocationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Get last cached device location
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		Location lastKnownLocation = mLocationManager
				.getLastKnownLocation(locationProvider);

		if (lastKnownLocation != null) {
			// Center map
			mMapController.setZoom(20); // Fixed Zoom Level
			centerLocationAndLoadJokes(lastKnownLocation);
			m_SearchCenterLocation_lat = lastKnownLocation.getLatitude();
			m_SearchCenterLocation_lng = lastKnownLocation.getLongitude();
		} else {
			// Comenzamos a atender actualizaciones en las posiciones
			CustomLocationListener customLocationListener = new CustomLocationListener();

			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 2000, // minTime
					0, // minDistance
					customLocationListener);
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	public void onRefresh(View v)
	{
		GeoPoint mapCenter = mMapView.getProjection().fromPixels(
        		mMapView.getWidth()/2,
                mMapView.getHeight()/2);
        
        m_SearchCenterLocation_lat = mapCenter.getLatitudeE6() / (1000000f);
        m_SearchCenterLocation_lng = mapCenter.getLongitudeE6() / (1000000f);
        	                
        centerLocationAndLoadJokes(mapCenter.getLatitudeE6(), mapCenter.getLongitudeE6());
	}
	
	/**
	 * Center map on location
	 * 
	 * @param loc
	 */
	private void centerLocationAndLoadJokes(Location loc) {
		centerLocationAndLoadJokes((int) (loc.getLatitude() * 1000000),
				(int) (loc.getLongitude() * 1000000));
	}
	
	private void centerLocationAndLoadJokes(int latitude, int longitude) {
		GeoPoint geoPoint = new GeoPoint(latitude, longitude);

		MapUtils.BoundingCoordinates(geoPoint, Config.RADIUS_SEARCH_POIS_METERS, mMapView);

		mMapController.animateTo(geoPoint);

		Thread thread = new Thread(new LoadJokesWorker());
		thread.start();
	}

	/**
	 * Draw Jokes before loading
	 */
	private void drawJokes(double centerLat, double centerLon) {
		mMapView.getOverlays().clear();
		
		mMapView.getOverlays().add(
				new CircleOverlay(this, centerLat, centerLon,
						Config.RADIUS_SEARCH_POIS_METERS));
		
/*
		mMapView.getOverlays().add(
				new CircleOverlay(this, m_DeviceLocation.getLatitude(),
						m_DeviceLocation.getLongitude(),
						Config.RADIUS_SEARCH_POIS_METERS));
*/
		if (m_pois != null) {
			for (final POI p : m_pois) {
				// 1 - create marker
				Drawable drawable = getMarker();
				JokeOverlay itemizedoverlay = new JokeOverlay(drawable, this, p, 
						new Callable<Integer>() {
					   		public Integer call() {
					   			return tapOverlay(p);
					   		}
						});
				// 2 - geolocalize marker
				GeoPoint geoPoint = new GeoPoint(
						(int) (p.getLatitude() * 1000000),
						(int) (p.getLongitude() * 1000000));
				OverlayItem overlayitem = new OverlayItem(
						geoPoint, "", p.getDescription());
				// 3 - Add marker to map
				itemizedoverlay.addOverlay(overlayitem);
				mMapView.getOverlays().add(itemizedoverlay);
			}
		}

		mMapView.postInvalidate();
	}
	

	private Drawable getMarker()
	{
		Drawable drawable = null;

		drawable = this.getResources().getDrawable(R.drawable.icono_mapa_yo);

		return drawable;
	}

	private class ResultMessageCallback implements Callback {

		public boolean handleMessage(Message arg0) {

			switch (arg0.what) {
			case WORKER_MSG_ERROR:
				Toast.makeText(Pestana3.this,
						getString(R.string.ErrorCarga), Toast.LENGTH_LONG)
						.show();
				break;
			case WORKER_MSG_OK:
				drawJokes(	m_SearchCenterLocation_lat,
							m_SearchCenterLocation_lng);
				break;
			}

			return true; // lo marcamos como procesado
		}
	}

	/**
	 * Custom Location Listener implementation
	 * 
	 */
	private class CustomLocationListener implements LocationListener {

		public void onLocationChanged(Location argLocation) {
			m_SearchCenterLocation_lat = argLocation.getLatitude();
			m_SearchCenterLocation_lng = argLocation.getLongitude();
			centerLocationAndLoadJokes(argLocation);
			mLocationManager.removeUpdates(this); // We need only one aproximate
													// location
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	/**
	 * Load Jokes in background
	 * 
	 */
	private class LoadJokesWorker implements Runnable {

		public void run() {

			int mensajeDevuelto = WORKER_MSG_OK;

			try {
				if (Config.param_search_type_map == SEARCH_TYPE.Near){
					
					m_pois = topoos.POI.Operations.GetNear(Pestana3.this, m_SearchCenterLocation_lat,
							m_SearchCenterLocation_lng,
							Config.RADIUS_SEARCH_POIS_METERS, Config.CAT, Config.totalMap);
					}else
						m_pois=topoos.POI.Operations.GetWhere(Pestana3.this, Config.CAT, null, null, null, null, null, 10);	
					
				
			} catch (Exception e) {
				mensajeDevuelto = WORKER_MSG_ERROR;
				Log.e("Upload", e.getMessage());
			}

			m_Handler.sendEmptyMessage(mensajeDevuelto);
		}
	}

	

	private int tapOverlay(POI poi)
	{
		Intent intent= new Intent(this,DetaillJokeActivity.class); 
		intent.putExtra("poi", poi);
		this.startActivityForResult(intent, DETAIL_ACTIVITY_RESULT);
		
		mMapSavedZoom = mMapView.getZoomLevel();
		
		GeoPoint mapCenter = mMapView.getProjection().fromPixels(
        		mMapView.getWidth()/2,
                mMapView.getHeight()/2);
        
        m_SearchCenterLocation_lat = mapCenter.getLatitudeE6() / (1000000f);
        m_SearchCenterLocation_lng = mapCenter.getLongitudeE6() / (1000000f);
        
		//mMapSavedX=mMapView.getX();
	///	mMapSavedY=mMapView.getY();
		
		return 1;
	}

	
	@Override
	protected void onDetailActivityResult() {
		
		Log.i("Zoom", ""+mMapSavedZoom);
		mMapController.setZoom(mMapSavedZoom);
	//	mMapView.setX(mMapSavedX);
	//	mMapView.setY(mMapSavedY);
		
		GeoPoint geoPoint = new GeoPoint(
				(int) (m_SearchCenterLocation_lat * 1000000), 
				(int) (m_SearchCenterLocation_lng * 1000000));
		mMapController.animateTo(geoPoint);
	}

	

}//END Pestana3


