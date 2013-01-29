package es.solusoft.santosinocentes;

import es.solusoft.analytics.AnalyticsActivity;
import es.solusoft.carnavalea.R;
import android.os.Bundle;
import android.view.Window;

public class AboutActivity extends AnalyticsActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.splash);

	}

}
