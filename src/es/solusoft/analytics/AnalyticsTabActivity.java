package es.solusoft.analytics;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.ListActivity;
import android.app.TabActivity;

public class AnalyticsTabActivity extends TabActivity {

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
}