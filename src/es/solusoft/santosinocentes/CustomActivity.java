package es.solusoft.santosinocentes;

import topoos.AccessTokenOAuth;
import android.os.Bundle;
import es.solusoft.analytics.AnalyticsActivity;
import es.solusoft.carnavalea.R;
import es.solusoft.santosinocentes.internal.Config;

public class CustomActivity extends AnalyticsActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
			topoos.AccessTokenOAuth token = new AccessTokenOAuth(Config.APPTOKEN_ADMIN);
		 	token.save_Token(this);
	
	}

}
