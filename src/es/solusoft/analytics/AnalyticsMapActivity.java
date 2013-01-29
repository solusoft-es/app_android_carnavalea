package es.solusoft.analytics;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;

public class AnalyticsMapActivity extends com.google.android.maps.MapActivity {

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this); 
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this); 
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}