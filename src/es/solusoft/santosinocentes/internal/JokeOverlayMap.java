package es.solusoft.santosinocentes.internal;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import es.solusoft.carnavalea.R;
import es.solusoft.santosinocentes.DetaillJokeActivity;


@SuppressWarnings("rawtypes")
public class JokeOverlayMap extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private topoos.Objects.POI mPOI;
	private Activity mActivity;
		
	public JokeOverlayMap(Drawable defaultMarker, Activity activity, topoos.Objects.POI poi) {
		super(boundCenterBottom(defaultMarker));
		mPOI = poi;
		mActivity= activity;
	}


	public void addOverlay(OverlayItem item) {
		mOverlays.add(item);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {

		// Overlay for Map

		Intent intent = new Intent(mActivity, DetaillJokeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("poi", mPOI);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.scale_from_corner,R.anim.scale_to_corner);
		mActivity.finish();

		return true;

	}

}
