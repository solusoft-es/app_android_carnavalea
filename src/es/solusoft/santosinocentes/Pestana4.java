package es.solusoft.santosinocentes;

import java.util.ArrayList;
import java.util.List;

import topoos.AccessTokenOAuth;
import topoos.Exception.TopoosException;
import topoos.Objects.POI;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import es.solusoft.analytics.AnalyticsListActivity;
import es.solusoft.carnavalea.R;
import es.solusoft.santosinocentes.internal.Config;
import es.solusoft.santosinocentes.internal.Utils;

//Contenido de la pestaña Mis bromas, que incluye tu perfil y el nivel de troll que eres

public class Pestana4 extends AnalyticsListActivity {
	private DatabaseHandler bd;
	private ProgressDialog pDialog;
	private topoos.AccessTokenOAuth token;
	//private POI poi;
	private ArrayList<POI> mList;
	private MiTareaAsincronaDialog tarea2;
	private int nivel;
	private TextView nivelbromista;
	private String nivelbr;
	private TextView marquesina;
	private ListView lv;
	// *******************Metodo onCreate*********************************//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pestana4);
		// Token

		token = new AccessTokenOAuth(Config.APPTOKEN_ADMIN);
		token.save_Token(this);

		// Creamos la base de datos
		bd = new DatabaseHandler(this);
		
		//Definicion del textView
		nivelbromista=(TextView) findViewById(R.id.NivelBromista);
		//Marquesina
				marquesina=(TextView) findViewById(R.id.Title1);
				
		
		// Creamos el pDialog
		pDialog = new ProgressDialog(Pestana4.this);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setTitle(getString(R.string.INFORMACION));
		pDialog.setMessage(getString(R.string.cargandobromas));
		pDialog.setCancelable(true);
		pDialog.setMax(100);

		// Tarea que estamos ejecutando mientras se cargan los POIs cercanos
		tarea2 = new MiTareaAsincronaDialog();
		tarea2.execute();
		
		//Detalle de la lista
		lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
               
				Intent intent= new Intent(Pestana4.this,DetaillJokeActivity.class); 
				intent.putExtra("poi", mList.get(arg2));

                startActivity(intent);
              
			}
		});
		
		//Fuente marquesina
		/*Typeface tf2 = Typeface.createFromAsset(this.getAssets(),
	            "fonts/3Dumb.ttf");
		marquesina.setTypeface(tf2);
		
		//Fuente de nivel troll modificada
		Typeface tf = Typeface.createFromAsset(this.getAssets(),
	            "fonts/Zero.ttf");
	    nivelbromista.setTypeface(tf);						
		 */
	}

	// *******************Clase que se ejecutara en segundo plano****************//
	
	private class MiTareaAsincronaDialog extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			try {

				// Obtenemos los ids y los metemos en la lista ids
				Log.i("ids", "Vamos a obtener los ids");
				
				//Obtenemos los IDS de la base de datos
				ArrayList<Integer> idarr =  bd.getallIds();
				
				//Metemos los IDS en un vector de enteros para obtener posteriormente los POIS 
							
				Integer[] ids = (Integer[])idarr.toArray(new Integer[idarr.size()]);
						
				
				if(ids!=null && ids.length > 0){
					mList = (ArrayList<POI>) topoos.POI.Operations.GetWhere(Pestana4.this, Config.CAT, ids, null, null, null, null,null);
					nivel=GetLevel(mList, 6);
					Log.i("nivel", ""+nivel);
				} else {
					mList = new ArrayList<POI>();
					nivel=GetLevel(mList, 6);
				}
								

			} catch (TopoosException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();

			pDialog.setProgress(progreso);
		}

		@Override
		protected void onPreExecute() {

			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					MiTareaAsincronaDialog.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				pDialog.dismiss();
				setListAdapter(new Adaptador(Pestana4.this, R.layout.lista_item, mList));
				nivelbr=ChangeTittle(nivel);
				Log.i("nivelbr", ""+nivel);
				nivelbromista.setText(nivelbr);
				ChangeIcon(nivel);
				
			}
		}

		@Override
		protected void onCancelled() {
			
		}
	}

	// ******************Clase adaptador para mostrar en la lista la bd de pois***********//
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

			POI p = items.get(position);

			if (p != null) {
				
				final TextView des = (TextView) v.findViewById(R.id.descripcion);
				final ImageView imagen = (ImageView) v.findViewById(R.id.imagen);
				
				final TextView mas1 = (TextView) v.findViewById(R.id.votosmas1);
				final TextView menos1 = (TextView) v.findViewById(R.id.votosmenos1);
				
				if (mas1 != null)
				{
					mas1.setText("+"+p.getWarningcount().getDuplicated().toString());
				}
				
				if (menos1 != null)
				{
					menos1.setText("-"+p.getWarningcount().getClosed().toString());
				}
				
				
				if (des != null) {
					des.setText(p.getDescription());
				}
				
				if(imagen!=null){
					Utils.LoadImageBG(topoos.Images.Operations.GetImageURIThumb(p.getName(), topoos.Images.Operations.SIZE_LARGE), imagen, Pestana4.this);
				}

			}

			return v;

		}

	}

	
	// ****************Metodo para generar el titulo de troll***********************//
	public String ChangeTittle(int level) {
		String title = "";
		
		if (level == 1) {
			title = "MIMO";
			
		} else if (level==2) {
			title = "JUGLAR";
			
		} else if (level ==3) {
			title = "SALTIMBANQUI";
			
		} else if (level==4) {
			title = "ARLEQUIN";
			
		} else if (level==5) {
			title = "TROVADOR";
			
		} else if (level==6) {
			title = "SUPERHEROE";
			
		}	

		
		return title;
	}//END ChangeTittle
	
	//********************Metodo para cambiar el icono troll***********************//
	public void ChangeIcon(int level) {
		ImageView iv= (ImageView) findViewById(R.id.Icono);
		
		if (level==1) {
			iv.setBackgroundResource(R.drawable.perfil_mimo);
			
		} else if (level==2) {
			iv.setBackgroundResource(R.drawable.perfil_juglar);
			
		} else if (level==3) {
			iv.setBackgroundResource(R.drawable.perfil_saltimbanqui);
			
		} else if (level==4) {
			iv.setBackgroundResource(R.drawable.perfil_arlequin);
			
		} else if (level==5) {
			iv.setBackgroundResource(R.drawable.perfil_trovador);
			
		} else if (level==6) {
			iv.setBackgroundResource(R.drawable.perfil_superheroe);
			
		}
		}//END ChangeIcon
	
	//***********************Metodo para obtener el nivel de bromista******************//
	private static int GetLevel(List<POI> pois, int maxLevelAllowed)
	{
		int positivePOIcounter = 0;
		if (pois != null)
		{
			for (POI poi : pois)
			{
				if (IsPOIpositive(poi)) 
					positivePOIcounter++;
			}
		}
		
		int level = 1;
		if (positivePOIcounter > 1)
		{
			level = ((Double)Math.sqrt(positivePOIcounter*3)).intValue();
		}
		if (level > maxLevelAllowed)
		{
			level = maxLevelAllowed;
		}
		
		return level;
	}
	
	
	private static boolean IsPOIpositive(POI p)
	{
		if (p == null)
		{
			return false;
		}else
		{
			return ((p.getWarningcount().getClosed() == 0 && p.getWarningcount().getDuplicated() == 0) 
					|| (p.getWarningcount().getClosed() < p.getWarningcount().getDuplicated()));
		}
	}
	
	
}//END Pestaña4
