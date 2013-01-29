package es.solusoft.santosinocentes.internal;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import es.solusoft.santosinocentes.DetaillJokeActivity;


/**
 * Marcador de mapa personalizado
 * 
 * @author findemor
 * 
 */
@SuppressWarnings("rawtypes")
public class JokeOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private topoos.Objects.POI mPOI;
	
	private Callable<Integer> mTapFunc;

	public JokeOverlay(Drawable defaultMarker, topoos.Objects.POI poi, Callable<Integer> func) {
		super(boundCenterBottom(defaultMarker));
		mPOI = poi;
		mTapFunc = func;
	}

	public JokeOverlay(Drawable defaultMarker, Context context, topoos.Objects.POI poi, Callable<Integer> func) {
		this(defaultMarker, poi, func);
		mContext = context;
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

		try {
			mTapFunc.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Intent intent= new Intent(mContext,DetaillJokeActivity.class); 
		//intent.putExtra("poi", mPOI);
        //mContext.startActivity(intent);

        /*	intent.putExtra("poiid", (Integer)mPOI.getId());
        intent.putExtra("idImage",(String) mPOI.getName());
        intent.putExtra("description",(String)mPOI.getDescription());
        intent.putExtra("+1", (Integer)mPOI.getWarningcount().getDuplicated());
        intent.putExtra("-1", (Integer)mPOI.getWarningcount().getClosed());
        */
	
		return true;

	}
}
