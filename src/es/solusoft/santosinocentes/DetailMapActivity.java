package es.solusoft.santosinocentes;

import java.util.List;
import java.util.concurrent.Callable;

import topoos.Objects.POI;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import es.solusoft.carnavalea.R;
import es.solusoft.santosinocentes.internal.CircleOverlay;
import es.solusoft.santosinocentes.internal.Config;
import es.solusoft.santosinocentes.internal.JokeOverlay;
import es.solusoft.santosinocentes.internal.JokeOverlayMap;

public class DetailMapActivity extends CustomMapActivity {
	private ImageButton gotoDetail;
	private Activity activity;
	private POI mPOI;
	private MapView mMap=null;
	private com.google.android.maps.MapController mMapController=null;
	private Double mLatitud;
	private Double mLongitud;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		activity=this;
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail_map_layout);
		mPOI = (topoos.Objects.POI)getIntent().getExtras().getSerializable("poi");
		
		mMap=(MapView) findViewById(R.id.mapviewdetail);	
		mMapController=mMap.getController();
		mMap.setBuiltInZoomControls(true);
		
		drawJokes(mPOI);
				
		gotoDetail = (ImageButton) findViewById(R.id.gotoDetail);
		gotoDetail.setOnClickListener(new OnClickListener() {

			
			@Override
			public void onClick(View v) {

				
				/*startActivity(new Intent(getApplicationContext(), DetailMapActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP));
				*/
				
				Intent intent = new Intent(getApplicationContext(),DetaillJokeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("poi",mPOI);
				startActivity(intent);
				overridePendingTransition(R.anim.scale_from_corner, R.anim.scale_to_corner);
				activity.finish();
						
			}
		});

	}
	
	
	private void drawJokes(POI mPOI) {
		mMap.getOverlays().clear();
		
		/*mMapView.getOverlays().add(
				new CircleOverlay(this, centerLat, centerLon,
						Config.RADIUS_SEARCH_POIS_METERS));
		
/*
		mMapView.getOverlays().add(
				new CircleOverlay(this, m_DeviceLocation.getLatitude(),
						m_DeviceLocation.getLongitude(),
						Config.RADIUS_SEARCH_POIS_METERS));
*/
					
				// 1 - create marker
				Drawable drawable = getMarker();

				JokeOverlay itemizedoverlay = new JokeOverlay(drawable, this, mPOI, 
						new Callable<Integer>() {
					   		public Integer call() {
					   			return tapOverlay();
					   		}
						});
				
				// 2 - geolocalize marker
				GeoPoint geoPoint = new GeoPoint(
						(int) (mPOI.getLatitude() * 1000000),
						(int) (mPOI.getLongitude() * 1000000));
				OverlayItem overlayitem = new OverlayItem(
						geoPoint, "", mPOI.getDescription());
				mMapController.animateTo(geoPoint);
				mMapController.setZoom(10);
				// 3 - Add marker to map
				itemizedoverlay.addOverlay(overlayitem);
				mMap.getOverlays().add(itemizedoverlay);
				mMap.postInvalidate();

				
	}

	private int tapOverlay()
	{
		Intent intent = new Intent(this, DetaillJokeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("poi", mPOI);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.scale_from_corner,R.anim.scale_to_corner);
		this.finish();
		
		return 1;
	}
	


	private Drawable getMarker()
	{
		Drawable drawable = null;

		drawable = this.getResources().getDrawable(R.drawable.icono_mapa_yo);

		return drawable;
	}



	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}


	@Override
	protected void onDetailActivityResult() {
		// TODO Auto-generated method stub
		
	}

}
