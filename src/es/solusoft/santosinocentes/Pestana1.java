package es.solusoft.santosinocentes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import topoos.Exception.TopoosException;
import topoos.Objects.Image;
import topoos.Objects.POI;
import topoos.Objects.POICategory;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import es.solusoft.carnavalea.R;
import es.solusoft.santosinocentes.esri.EsriComunication;
import es.solusoft.santosinocentes.internal.Config;
import es.solusoft.santosinocentes.internal.Constants;
import es.solusoft.santosinocentes.internal.Sharing;

//Contenido de la pestaña "Haz tu broma"

public class Pestana1 extends CustomActivity implements
		android.view.View.OnClickListener {

	// ************************Atributos de la clase*************************//
	// Marquesina
	private TextView marquesina;

	// ****Botones****//

	private Button boton1;// Boton publicar

	// ****Camara****//

	private ImageView ImageView1;// ImagenView camara
	private static int TAKE_PICTURE = 1;// Tomar foto
	private static final int SELECT_PICTURE = 2;// Seleccionarla de la galeria
	private String nombreCap = "";// Nombre para el archivo donde almacenamos la
									// foto

	private topoos.Objects.POI mRegisteredPOI = null;

	private EditText e;// Contenedor de la descripcion

	// ****Base de datos****//

	private DatabaseHandler bd;// Base de datos

	// ****Topoos****////Token actualizado dia 13/12/12//

	// *****Posicion GPS*****//

	private Location mlocation;
	private ProgressDialog pd = null;

	// *****Imagen********//
	private byte[] bitmapdata;
	private ByteArrayOutputStream stream;

	public final int MENSAJE_ERROR = -1;
	public final int MENSAJE_OK = 1;
	private Handler handler = new Handler(new ResultMessageCallback());

	// ******Token*********//
	// private AccessTokenOAuth token;

	// *************************Metodo onCreate***************************//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pestana1);
		
		// GENERATE APP CATEGORIES
		// Get your own topoos api key and set it in Config.java class.
		// Then uncomment and execute this lines only one time
		// Read the Logcat to get the Category identifier and set it on Config.java class.
		// Finally comment this lines again :)
		
		
		/*
		Runnable categorizer = new GenerateCategories();
		Thread thread = new Thread(categorizer);
		thread.start();
		 */
		
		
		
		
		
		nombreCap = Environment.getExternalStorageDirectory() + "/test.jpg";
		bd = new DatabaseHandler(this);
		boton1 = (Button) findViewById(R.id.Publicar);// Definicion del objeto
		marquesina = (TextView) findViewById(R.id.ListadeBromas);
		boton1.setEnabled(false);// Deshabilitamos el boton de publicar al
									// iniciar

		boton1.setOnClickListener(this);// Invocacion a la accion onClick
		ImageView1 = (ImageView) findViewById(R.id.ImageView1);// Definicion del
																// iv
		e = (EditText) findViewById(R.id.Descripcion);

		/*
		 * Cambio de fuente de la marquesina Typeface tf =
		 * Typeface.createFromAsset(this.getAssets(), "fonts/3Dumb.ttf");
		 * marquesina.setTypeface(tf);
		 */
		e.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				checkPublishButtonStatus();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

		});

		configGPS();// llamada de geolocalizacion

		// Creacion stream imagen capture

		stream = new ByteArrayOutputStream();

		// *******************Metodo onclick de
		// ImageView**********************//

		ImageView1.setOnClickListener(new View.OnClickListener() {// Invocacion
																	// onClick()

					@Override
					public void onClick(View v) {

						GalleryCamSelection();

					}// END onClick ImageView
				});// END onClickListener()

	}// END onCreate

	// ************************Metodo onActivityResult
	// camara********************//

	private void checkPublishButtonStatus() {
		boton1.setEnabled(bitmapdata != null && e.getText() != null
				&& e.getText().toString().length() > 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == TAKE_PICTURE) {
			/*
			 * Si se reciben datos en el intent tenemos una vista previa
			 * (thumbnail)
			 */
			if (data != null) {
				/*
				 * En el caso de una vista previa, obtenemos el extra data del
				 * intent y lo mostramos en el ImageView
				 */
				if (data.hasExtra("data")) {

					Bitmap bitmap = ((Bitmap) data.getParcelableExtra("data"));

					// String path = getRealPathFromURI(selectedImage);

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					/*
					BitmapFactory.Options options = new
					BitmapFactory.Options();
					options.inSampleSize = (calculateInSampleSize(bitmap, 480,
					480));
					options.inDensity = 72;
					*/
					bitmap = Bitmap.createScaledBitmap(bitmap, 480, 480, false);
					
					bitmap.compress(CompressFormat.JPEG, 75, bos);
					bitmapdata = bos.toByteArray();
					ImageView iv = (ImageView) findViewById(R.id.ImageView1);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
					iv.setImageBitmap(bitmap);

				}
				/*
				 * De lo contrario es una imagen completa
				 */
			} else {
				/*
				 * A partir del nombre del archivo ya definido lo buscamos y
				 * creamos el bitmap para el ImageView
				 */
				ImageView iv = (ImageView) findViewById(R.id.ImageView1);
				iv.setImageBitmap(BitmapFactory.decodeFile(nombreCap));
				// Guardar imagen en la galeria mediante mediascanner
				new MediaScannerConnectionClient() {
					private MediaScannerConnection msc = null;
					{
						msc = new MediaScannerConnection(
								getApplicationContext(), this);
						msc.connect();
					}

					public void onMediaScannerConnected() {
						msc.scanFile(nombreCap, null);
					}

					public void onScanCompleted(String path, Uri uri) {
						msc.disconnect();
					}
				};
			}
		} else if (requestCode == SELECT_PICTURE) {
			Uri selectedImage = data.getData();

			String path = getRealPathFromURI(selectedImage);

			try {

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = (calculateInSampleSize(path, 480, 480));
				options.inDensity = 72;
				Bitmap bitmap = BitmapFactory.decodeFile(path, options);
				bitmap.compress(CompressFormat.JPEG, 75, bos);
				bitmapdata = bos.toByteArray();
				ImageView iv = (ImageView) findViewById(R.id.ImageView1);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
				iv.setImageBitmap(bitmap);

				// bitmapdata=stream.toByteArray();
			} catch (Exception e) {

			}
		}

		checkPublishButtonStatus();

	}// END onActivityResult()

	public String getRealPathFromURI(Uri contentUri) {
		// canPostImage
		String[] proj = { MediaStore.Images.Media.DATA };
		android.database.Cursor cursor = managedQuery(contentUri, proj, // columns
																		// to
																		// return
				null, // clause rows
				null, // clause selection
				null); // order
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);

	}

	public static int calculateInSampleSize(String photoURI, int reqWidth,
			int reqHeight) {

		// lee las dimensiones sin ocupar espacio en memoria gracias a
		// inJustDecodeBounds
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoURI, options);

		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}
	
	public static int calculateInSampleSize(Bitmap bitmap, int reqWidth,
			int reqHeight) {


		// Raw height and width of image
		final int height = bitmap.getHeight();
		final int width = bitmap.getWidth();
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

	// ****Clase privada que implementa la interfaz runnable para lanzar
	// añadirPOI****//
	
	private class GenerateCategories implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			  try {
					GenerarCategoria();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (TopoosException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
		
	}

	private class AddPOIWorker implements Runnable {

		@Override
		public void run() {
			int mensajeDevuelto = MENSAJE_OK;
			try {
				while (mlocation == null) {
					Thread.sleep(100);
				}

				AñadirPOI(Pestana1.this, nombreCap, mlocation.getLatitude(),
						mlocation.getLongitude(), e.getText().toString(),
						Config.CAT);
			} catch (Exception e) {
				mensajeDevuelto = MENSAJE_ERROR;
				e.printStackTrace();
			}
			handler.sendEmptyMessage(mensajeDevuelto);
		}

	}// END AddPOIWorker

	// **************************onClick botonPublicar************************//

	@Override
	public void onClick(View v) {

		// Si tenemos localizacion añadimos el punto a la bd y
		// compartimos/Iniciamos el thread
		// Creamos el hilo secundario para añadir el poi con la broma

		pd = ProgressDialog.show(this, getString(R.string.INFORMACION),
				getString(R.string.PublicarBroma));
		Runnable poiWorker = new AddPOIWorker();
		Thread thread = new Thread(poiWorker);
		thread.start();

	}// END boton publicar

	// ******************* Metodo para lanzar la camara***********************//
	public void CaptureFoto() {

		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, nombreCap);
		Uri mCapturedImageURI = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		Intent camaraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		startActivityForResult(camaraIntent, TAKE_PICTURE);
	}// END CaptureFoto

	public void GallerySelection() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		startActivityForResult(intent, SELECT_PICTURE);
	}// END GallerySelection

	// *******************Metodo para limpiar la vista del
	// usuario****************//

	public void clean_view() {

		e.setText("");// Limpiamos la descripcion

		ImageView1.setImageBitmap(null);// Cambiar por imagen predefinida (ahora
		// null)

	}// END clean_view

	// **************Metodo que nos muestra las opciones del
	// Dialog**************//

	private void showOneDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.sharing_title))
				.setTitle("")
				.setCancelable(false)
				.setNegativeButton(getString(R.string.sharing_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								clean_view();
							}
						})
				.setPositiveButton(getString(R.string.sharing_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// EditText e = (EditText)
								// findViewById(R.id.Descripcion);

								String shortText = Sharing
										.getSharingLongText(Pestana1.this, topoos.Images.Operations
												.GetImageURI(mRegisteredPOI
														.getName()));

								Intent sharingIntent = new Intent(
										Intent.ACTION_SEND);
								sharingIntent.setType("text/plain");

								sharingIntent.putExtra(
										android.content.Intent.EXTRA_TEXT,
										shortText);
								startActivity(Intent.createChooser(
										sharingIntent, "Compartir via"));
								clean_view();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}// END showOneDialog

	// ****************Metodo para seleccionar la imagen de la
	// galeria/cam***********//

	private void GalleryCamSelection() {

		final String[] items = { "Camara", "Galeria" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Selección");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {// Camara
					CaptureFoto();
				} else if (item == 1) {// Galeria
					GallerySelection();
				}

			}
		});

		builder.create();
		builder.show();

	}// END GalleryCamSelection

	// ***************Metodo para habilitar posicion
	// GPS**************************//

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

	// ************************Class
	// MyLocationListener************************//
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

	}// END MyLocationListener

	// ************************Metodo para añadir un POI**********************//

	public void AñadirPOI(Context c, String name, double latitud,
			double longitud, String descripcion, Integer[] cat)
			throws IOException, TopoosException {

		Image image = null;
		// Añadimos el POI a topoos
		Log.i("STOS", "POI " + "upload img");

		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		randomUUIDString = randomUUIDString.replace("-", "");
		if (randomUUIDString.length() > 10) randomUUIDString = randomUUIDString.substring(0,9); 
		randomUUIDString = randomUUIDString + ".jpg";
		

		image = topoos.Images.Operations.ImageUpload(c, bitmapdata,
				randomUUIDString);

		Log.i("STOS", "POI " + "img anadida");
		Log.i("STOS", "POI " + "img id " + image.getFilename_unique());

		/* topoos.Images.Operations.GetImageURIThumb(filename_unique, size) */
		POI p = topoos.POI.Operations.Add(c, image.getFilename_unique(),
				latitud, longitud, cat, null, null, null, descripcion, null,
				null, null, null, null, null, null);

		mRegisteredPOI = p;

		Log.i("STOS", "POI " + "antes de añadir POI " + " ");

		Log.i("STOS", "POI " + "POI añadido" + " ");
		Log.i("STOS", "POI " + "nombre " + " " + p.getName());
		Log.i("STOS", "POI " + "long " + " " + p.getLongitude());
		Log.i("STOS", "POI " + "lat " + " " + p.getLatitude());
		Log.i("STOS", "POI " + "cat " + " " + p.getCategories());
		Log.i("STOS", "POI " + "desc " + " " + p.getDescription());

		// Añadimos el POI la base de datos

		Log.i("STOS", "POI " + "antes bd " + " ");

		bd.addPOIs(p);

		EsriComunication eC = new EsriComunication();
		eC.addPOI(p);

		Log.i("STOS", "POI " + " bd add " + " ");

		// Añadimos la imagen

		// Log.i("STOS", "POI " +
		// "IMAGEN"+topoos.Images.Operations.GetImageURI(image.getFilename_unique()));

	}// END AñadirPOI()

	// ***************Metodo resultCalback **************************//
	private class ResultMessageCallback implements Callback {

		public boolean handleMessage(Message arg0) {

			pd.dismiss();

			switch (arg0.what) {
			case MENSAJE_ERROR:
				Toast.makeText(Pestana1.this, getString(R.string.NoPublicada),
						Toast.LENGTH_LONG).show();
				break;
			case MENSAJE_OK:
				Log.i("Adapter", "PRE");

				// Llamamos a showondialog
				showOneDialog();

				break;
			}

			return true;
		}
	}// END ResultMessageCallback

	// ********************Metodo para generar una
	// categoria*******************//

	public void GenerarCategoria() throws IOException, TopoosException {
		POICategory pc = topoos.POICategories.Operations.Add(this, "jokes");
		pc.getId();
		Log.i("STOS", "cat " + "jokes categoria: " + " " + pc.getId());
	}
	// / END GenerarCategoria

}// END Pestana1
