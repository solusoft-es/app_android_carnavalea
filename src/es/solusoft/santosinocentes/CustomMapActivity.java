package es.solusoft.santosinocentes;

import android.content.Intent;
import topoos.AccessTokenOAuth;

import es.solusoft.analytics.AnalyticsMapActivity;
import es.solusoft.carnavalea.R;
import es.solusoft.santosinocentes.internal.Config;

public abstract class CustomMapActivity extends AnalyticsMapActivity {

	protected static final int DETAIL_ACTIVITY_RESULT = 1000;
	
	@Override
	public void onStart() {
		super.onStart();
		//EasyTracker.getInstance().activityStart(this); // Add this method.


		topoos.AccessTokenOAuth token = new AccessTokenOAuth(Config.APPTOKEN_ADMIN);
	 	token.save_Token(this);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == DETAIL_ACTIVITY_RESULT)
		{
			onDetailActivityResult();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	protected abstract void onDetailActivityResult();
	
}
