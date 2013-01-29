package es.solusoft.santosinocentes;

import java.util.ArrayList;
import java.util.List;
import es.solusoft.santosinocentes.widget.*;
import es.solusoft.santosinocentes.widget.PullToRefreshListView.OnRefreshListener;
import es.solusoft.carnavalea.R;
import es.solusoft.santosinocentes.internal.Config;
import es.solusoft.santosinocentes.internal.Config.SEARCH_TYPE;
import es.solusoft.santosinocentes.internal.ImageThreadLoader;
import es.solusoft.santosinocentes.internal.PostImageLoadedListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import topoos.AccessTokenOAuth;
import topoos.Exception.TopoosException;
import es.solusoft.analytics.AnalyticsListActivity;
import topoos.Objects.POI;
import topoos.Objects.POIDataWarning;

//Contenido de la pestaña "Lista de bromas cercanas a ti

public class Pestana2 extends AnalyticsListActivity {

	// *************Atributos de la clase***************//

	private ImageThreadLoader imageLoader = new ImageThreadLoader();
	private topoos.AccessTokenOAuth token;
	private List<POI> mLista = new ArrayList<POI>();
	//private List<POI> mLista2 = new ArrayList<POI>();
	public final int MENSAJE_ERROR = -1;
	public final int MENSAJE_OK = 1;
	private Handler handler = new Handler(new ResultMessageCallback());
	
	
	// private POI mPoi;

	// **********Localizacion********//
	private Location mlocation;
	private ProgressDialog pDialog = null;

	// *************************Metodo onCreate*****************************//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pestana2);
		// Token

		token = new AccessTokenOAuth(Config.APPTOKEN_ADMIN);
		token.save_Token(this);
		
				
		// Localizacion gps

		configGPS();// Obtenemos mlocation

		// Abrimos la pantalla que bloquea la pantalla mientras dura la tarea
		pDialog = ProgressDialog.show(this, getString(R.string.INFORMACION),
				getString(R.string.cargandobromas));
		
		// Iniciamos la tarea en segundo plano
		Thread thread = new Thread(new GetPois());
		thread.start();
		
		ListView lv=  getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
               
				int fixedpos = arg2 - 1;
				
				Intent intent= new Intent(Pestana2.this,DetaillJokeActivity.class); 
				intent.putExtra("poi", mLista.get(fixedpos));
				
				/*   	intent.putExtra("poiid", (Integer)mLista.get(fixedpos).getId());
                intent.putExtra("idImage",(String) mLista.get(fixedpos).getName());
                intent.putExtra("description",(String)mLista.get(fixedpos).getDescription());
                intent.putExtra("+1", (Integer)mLista.get(fixedpos).getWarningcount().getDuplicated());
                intent.putExtra("-1", (Integer)mLista.get(fixedpos).getWarningcount().getClosed());
                intent.putExtra("long", (Double) mLista.get(fixedpos).getLongitude());
                intent.putExtra("lat",(Double)mLista.get(fixedpos).getLatitude());
              */ 
                startActivity(intent);
                               
			}
		});
		
		
		/*Cambio de fuente de la marquesina
		 * Typeface tf = Typeface.createFromAsset(this.getAssets(),
	            "fonts/3Dumb.ttf");
		marquesina.setTypeface(tf);
		 */ 

	}// END onCreate

	//*************************Metodo onStart*****************************//
	public void onStart(){
		super.onStart();
		((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });	
	}
	
	// ************Metodo que obtiene los puntos cercanos a tu localizacion***********//
	public List<POI> GetNearPOI(AccessTokenOAuth apptokenAdmin, double lat,
			double lng, int radius, Integer[] categories) throws Exception,
			TopoosException {

		List<POI> POIsCercanos = topoos.POI.Operations.GetNear(apptokenAdmin,
				lat, lng, radius, categories,Config.total);
		return POIsCercanos;

	}// END GetNearPOI

	// ***************Tarea que se ejecuta en segundo plano run********************//
	private class GetPois implements Runnable {

		@Override
		public void run() {
			int mensajeDevuelto = MENSAJE_OK;
			try {
				while (mlocation == null) {
					Thread.sleep(100);
				}
				
				if (Config.param_search_type == SEARCH_TYPE.Near){
								
				mLista = GetNearPOI(token, mlocation.getLatitude(),
						mlocation.getLongitude(),
						Config.RADIUS_SEARCH_POIS_METERS, Config.CAT);
				}else
					mLista=topoos.POI.Operations.GetWhere(token, Config.CAT, null, null, null, null, null, Config.total);	
								

			} catch (TopoosException e) {
				e.printStackTrace();
			} catch (Exception e) {
				mensajeDevuelto = MENSAJE_ERROR;
				e.printStackTrace();
			}
			handler.sendEmptyMessage(mensajeDevuelto);
		}

	}

	// ***************Metodo resultCalback GetPois**************************//
	private class ResultMessageCallback implements Callback {

		public boolean handleMessage(Message arg0) {

			pDialog.dismiss();

			switch (arg0.what) {
			case MENSAJE_ERROR:
				Toast.makeText(Pestana2.this, getString(R.string.ErrorCarga),
						Toast.LENGTH_LONG).show();
				break;
			case MENSAJE_OK:
				setListAdapter(new Adaptador(Pestana2.this,
						R.layout.lista_item, mLista));
				
				break;
			}

			return true;
		}
	}// END ResultMessageCallback

	// ***************Clase Adaptador para mostrar los items como deseamos**********//
	private class Adaptador extends ArrayAdapter<POI> {

		private ArrayList<POI> items;

		public Adaptador(Context context, int textViewResourceId,
				List<POI> items) {
			super(context, textViewResourceId, items);
			this.items = (ArrayList<POI>) items;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			 View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.lista_item, null);
			}

			final POI p = items.get(position);

			if (p != null) {

				final TextView des = (TextView) v
						.findViewById(R.id.descripcion);
				final ImageView imagen = (ImageView) v
						.findViewById(R.id.imagen);

				final TextView mas1 = (TextView) v.findViewById(R.id.votosmas1);
				final TextView menos1 = (TextView) v
						.findViewById(R.id.votosmenos1);

				if (mas1 != null) {
					mas1.setText("+"
							+ p.getWarningcount().getDuplicated().toString());
				}

				if (menos1 != null) {
					menos1.setText("-"
							+ p.getWarningcount().getClosed().toString());
				}

				if (des != null) {
					des.setText(p.getDescription());
				}

				if (imagen != null) {

						imagen.setImageBitmap(null);
						Bitmap cachedImage = null;
						try {

							PostImageLoadedListener pill = new PostImageLoadedListener(
									imagen);

							cachedImage = imageLoader
									.loadImage(
											topoos.Images.Operations
													.GetImageURIThumb(
															p.getName(),
															topoos.Images.Operations.SIZE_SMALL),
											pill);

							// LogManager.Info(LogManager.Tag.Gallery, "getted"
							// +
							// Long.toString(o.id));

						} catch (Exception e) {
							// SILENT
						}
						if (cachedImage != null) {
							imagen.setImageBitmap(cachedImage);
						}
					

					/*
					 * new AsyncTask<Integer, Integer, String>() {
					 * 
					 * @Override protected String doInBackground(Integer...
					 * arg0) { // TODO Auto-generated method stub String
					 * OperationResult = ""; OperationResult =
					 * topoos.Images.Operations.GetImageURIThumb( p.getName(),
					 * topoos.Images.Operations.SIZE_XXSMALL);
					 * 
					 * return OperationResult; }
					 * 
					 * protected void onPostExecute(final String result) {
					 * 
					 * runOnUiThread(new Runnable() { public void run() {
					 * 
					 * try { InputStream is = (InputStream) new URL(result)
					 * .getContent(); Drawable d = Drawable.createFromStream(is,
					 * "src name"); imagen.setImageDrawable(d);
					 * 
					 * } catch (Exception e) {
					 * 
					 * }
					 * 
					 * // Utils.LoadImageBG(urlimg, photo, //
					 * DetaillJokeActivity.this); } }); }
					 * 
					 * }.execute(0);
					 */

				}

			}

			return v;

		}

	}

	// *********************Metodo para obtener la geolocalizacion****************//
	private void configGPS() {

		LocationManager mlocationManager;

		LocationListener mlocationListener;

		mlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		String locationProvider = LocationManager.GPS_PROVIDER;
		mlocation = mlocationManager.getLastKnownLocation(locationProvider);
		if (mlocation == null) {
			locationProvider = LocationManager.NETWORK_PROVIDER;
			mlocation = mlocationManager.getLastKnownLocation(locationProvider);
		}
		if (mlocation != null) {

		}

		mlocationListener = new MyLocationListener();

		if (mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mlocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, mlocationListener);

		} else {
			mlocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, mlocationListener);

		}

	}// END configGPS()

	// **********************Clase MyLocationListener************************//
	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			mlocation = location;

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}//END MyLocationListener

	// **********************Clase GetDataTask*****************************//
	private class GetDataTask extends AsyncTask<Void, Void, List<POI>> {

	        @Override
	        protected List<POI> doInBackground(Void...params) {
	       //REALIZAR TAREA EN SEGUNDO PLANO
	        	
	        	mLista.clear();//Eliminamos los elementos de la lista
	        		        		        	
	        	try {
					
	        		//Llamada 
	        		if (Config.param_search_type == SEARCH_TYPE.Near){
						
	    				mLista= GetNearPOI(token, mlocation.getLatitude(),
	    						mlocation.getLongitude(),
	    						Config.RADIUS_SEARCH_POIS_METERS, Config.CAT);
	    				}else
	    					mLista=topoos.POI.Operations.GetWhere(token, Config.CAT, null, null, null, null, null, Config.total);	
	        			
	        		
				} catch (TopoosException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

	        	return mLista;
	        	
	        }

	        @Override
	        protected void onPostExecute(List<POI> result) {
	        	
	        	Log.i("lista antes del adaptador", ""+mLista);
	        	
	        	setListAdapter(new Adaptador(Pestana2.this, //Modificamos la lista mediante el adaptador
    					R.layout.lista_item, mLista));
	        	
	        	Log.i("lista despues del adaptador", ""+mLista);    	 
	        	   
	        	// Call onRefreshComplete when the list has been refreshed.
	        	((PullToRefreshListView) getListView()).onRefreshComplete();
	        		        	
	        	 super.onPostExecute(result);
	        }
	    }

}// END Pestana2
