package es.solusoft.analytics;

import android.content.Context;
import android.os.AsyncTask;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;

public class EasyAnalytics {
	
	private static Tracker tracker=null;

	/**
	 * Se encarga de obtener el tracker para el uso de las google analytics
	 * 
	 * @param cxt
	 * @return
	 */
	public static Tracker GetTracker(Context context) {
		if (tracker != null)
			return tracker;
		else {
			EasyTracker.getInstance().setContext(context); 
			tracker = EasyTracker.getTracker();
			return tracker;
		}
	}
	
	/**
	 * Inserción de paginación.
	 * 
	 * @param cxt
	 *            Context
	 * @param page
	 *            Página a traquear
	 */
	public static void TrackPageGoogle(final Context cxt, final String page) {
		new AsyncTask<Integer, Integer, Integer> (){

			@Override
			protected Integer doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				Tracker tracker = GetTracker(cxt);
				tracker.trackView(page);
				EasyTracker.getInstance().dispatch();
				return null;
			}}.execute(0);

	}
	
	/**
	 * Inserción de eventos.
	 * 
	 * @param cxt
	 *            Context
	 * @param category
	 *            Categoría en la que se quiere insertar el evento
	 * @param action
	 *            Acción que provoca el evento.
	 * @param label
	 *            Etiqueta del evento
	 * @param value
	 *            Valor (Entero)
	 */
	public static void trackEventGoogle(final Context cxt, final String category,
			final String action, final String label, final long value) {
		new AsyncTask<Integer, Integer, Integer> (){
			@Override
			protected Integer doInBackground(Integer... params) {
				Tracker tracker = GetTracker(cxt);
				tracker.trackEvent(category, // Category
						action, // Action
						label, // Label
						value); // Value
				EasyTracker.getInstance().dispatch();
				return null;
			}
		}.execute(0);
	}

	
}
