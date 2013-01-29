package es.solusoft.santosinocentes;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import es.solusoft.analytics.AnalyticsTabActivity;
import es.solusoft.carnavalea.R;

public class MainActivity extends AnalyticsTabActivity {
	private static final int MENU_ACERCA_DE = Menu.FIRST;
	private static final int MENU_QUIT = Menu.FIRST + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);


		// Tablas de pestañas

		final TabHost tabHost = getTabHost();// Creamos el tabhost de la actividad
		TabHost.TabSpec spec;// Creamos un recurso para las propiedades de la
								// pestaña
		
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

	        @Override
	        public void onTabChanged(String tabId) {
	          //  setTabColor(tabHost);
	        }

		});

		Intent intent;// Intent que se utiliza para abrir cada pestaña
		Resources res = getResources();

		// Pestaña1
		intent = new Intent().setClass(this, Pestana1.class);// Se crea el
																// intent para
																// abrir la
																// actividad(clases)

		spec = tabHost
				.newTabSpec("Pestana1")
				.setIndicator("",
						res.getDrawable(R.drawable.pestana1_style))
				.setContent(intent);
	
				
		tabHost.addTab(spec);

		// Pestaña2
		intent = new Intent().setClass(this, Pestana2.class).addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);// Se crea el
																// intent para
																// abrir la
																// actividad(clases)

		spec = tabHost
				.newTabSpec("Pestana2")
				.setIndicator("",
						res.getDrawable(R.drawable.pestana2_style))
				.setContent(intent);
		
		tabHost.addTab(spec);
		
		// Pestaña3
		intent = new Intent().setClass(this, Pestana3.class).addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);// Se crea el
																// intent para
																// abrir la
																// actividad(clases)

		spec = tabHost
				.newTabSpec("Pestana3")
				.setIndicator("",
						res.getDrawable(R.drawable.pestana3_style))
				.setContent(intent);
		tabHost.addTab(spec);
		

		// Pestaña4
		intent = new Intent().setClass(this, Pestana4.class).addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);// Se crea el
																// intent para
																// abrir la
																// actividad(clases)

		spec = tabHost
				.newTabSpec("Pestana4")
				.setIndicator("",
						res.getDrawable(R.drawable.pestana4_style))
				.setContent(intent);
		tabHost.addTab(spec);
		

		//setTabColor(tabHost);
		
		//background color tabHost
		 getTabHost().getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.pestana1_drawable_shape);//Pestaña1
		 getTabHost().getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.pestana2_drawable_shape);//Pestaña2
		 getTabHost().getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.pestana3_drawable_shape);//Pestaña3
		 getTabHost().getTabWidget().getChildAt(3).setBackgroundResource(R.drawable.pestana4_drawable_shape);//Pestaña4
		 

		
	}
	

	
	
	
	
/*
	private void setTabColor(TabHost tabHost) {
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) { 
			tabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.grey)); 
	        } 
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.red));
	}
	*/

	//Metodo quit del menu contextual->salir de la aplicacion
	public void quit(){
		
		setResult(RESULT_OK);
		finish();
		
	}
	//Metodo AcercaDe del menu contextual->Mostrar pantalla Acerca de
	public void AcercaDe(){
		Intent intent = new Intent(this, AboutActivity.class);
		this.startActivity(intent);						
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ACERCA_DE, 0, getString(R.string.about));
		menu.add(0, MENU_QUIT, 0, getString(R.string.exit));
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_ACERCA_DE:
				AcercaDe();
				return true;
			case MENU_QUIT:
				quit();
				return true;
		}
		return false;
	}

}