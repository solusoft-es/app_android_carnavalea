package es.solusoft.santosinocentes;

import es.solusoft.carnavalea.R;

import es.solusoft.santosinocentes.internal.Constants;
import es.solusoft.santosinocentes.internal.ImageThreadLoader;
import es.solusoft.santosinocentes.internal.PostImageLoadedListener;
import es.solusoft.santosinocentes.internal.Sharing;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetaillJokeActivity extends CustomActivity implements
		OnClickListener {

	private static final int WORKER_MSG_OK = 1;
	private static final int WORKER_MSG_ERROR = -1;

	private ImageThreadLoader imageLoader = new ImageThreadLoader();
	private Handler handler = new Handler(new ResultMessageCallback());
	private ProgressDialog progressDialog;

	private ImageView photo;
	private TextView description;
	private ImageView pointPlus;
	private ImageView pointLess;
	private ImageView share;
	private TextView nVotosMas;
	private TextView nVotosMenos;
	private String urlimg;
	private ImageButton gotoMap;
	private Activity activity;
	private topoos.Objects.POI mPOI;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		activity = this;
		setContentView(R.layout.detaill_joke_layout);
		photo = (ImageView) findViewById(R.id.fotoBroma);
		description = (TextView) findViewById(R.id.descripccionBroma);
		pointPlus = (ImageView) findViewById(R.id.votosMas);
		pointLess = (ImageView) findViewById(R.id.votosMenos);
		pointLess.setOnClickListener(this);
		pointPlus.setOnClickListener(this);

		share = (ImageView) findViewById(R.id.share);
		share.setOnClickListener(this);
		nVotosMas = (TextView) findViewById(R.id.textoMas);
		nVotosMenos = (TextView) findViewById(R.id.textoMenos);

		mPOI = (topoos.Objects.POI) getIntent().getExtras().getSerializable(
				"poi");

		gotoMap = (ImageButton) findViewById(R.id.gotoMap);

		gotoMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * startActivity(new Intent(getApplicationContext(),
				 * DetailMapActivity
				 * .class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
				 * Intent.FLAG_ACTIVITY_SINGLE_TOP));
				 */

				Intent intent = new Intent(getApplicationContext(),
						DetailMapActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("poi", mPOI);
				startActivity(intent);
				overridePendingTransition(R.anim.scale_from_corner,
						R.anim.scale_to_corner);
				activity.finish();

			}
		});

		// Operacion scroll
		description.setMovementMethod(ScrollingMovementMethod.getInstance());

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			/*
			 * POIWarningCount wc = new POIWarningCount(extras.getInt("-1"),//
			 * closed extras.getInt("+1"),// duplicated 0, 0); mPOI = new
			 * POI(extras.getInt("poiid"), extras.getString("idImage"),
			 * extras.getString("description"), extras.getDouble("lat"),
			 * extras.getDouble("long"), null, null, null, null, null, null,
			 * null, null, null, null, null, null, null, wc);
			 */

			// POIWarningCount wc= new
			// POIWarningCount(mPOI.getWarningcount().getClosed(),mPOI.getWarningcount().getDuplicated(),
			// 0, 0);

		}

		if (mPOI != null) {
			nVotosMas.setText(String.valueOf(mPOI.getWarningcount()
					.getDuplicated()));
			nVotosMenos.setText(String.valueOf(mPOI.getWarningcount()
					.getClosed()));
			description.setText(mPOI.getDescription());

			// new getUrl().execute(0);

			photo.setImageBitmap(null);
			Bitmap cachedImage = null;
			try {
				PostImageLoadedListener pill = new PostImageLoadedListener(
						photo);

				cachedImage = imageLoader.loadImage(topoos.Images.Operations
						.GetImageURIThumb(mPOI.getName(),
								topoos.Images.Operations.SIZE_XXXLARGE), pill);

			} catch (Exception e) {
				// SILENT
			}
			if (cachedImage != null) {
				photo.setImageBitmap(cachedImage);
			}

			DatabaseHandler db = new DatabaseHandler(this);
			setVoteButtonsStatus(db.isVoted(mPOI.getId()));
		}
	}

	private void setVoteButtonsStatus(boolean isVoted) {
		if (isVoted) {
			pointLess.setEnabled(false);
			pointPlus.setEnabled(false);

		} else {
			pointLess.setEnabled(true);
			pointPlus.setEnabled(true);

		}
	}

	private class ResultMessageCallback implements Callback {

		public boolean handleMessage(Message arg0) {

			// Cerramos la pantalla de progreso

			switch (arg0.what) {
			case WORKER_MSG_ERROR:
				Toast.makeText(DetaillJokeActivity.this, "Error",
						Toast.LENGTH_LONG).show();
				break;
			case WORKER_MSG_OK:
				setVoteButtonsStatus(true);
				break;
			}

			progressDialog.dismiss();
			return true; // lo marcamos como procesado
		}
	}

	private class AddNegativeVoteBackground implements Runnable {

		public void run() {

			int mensajeDevuelto = WORKER_MSG_OK;
			try {
				topoos.POIWarning.Operations.AddClosed(
						DetaillJokeActivity.this, mPOI.getId());
				DatabaseHandler db = new DatabaseHandler(
						DetaillJokeActivity.this);
				db.addVote(mPOI.getId());

			} catch (Exception e) {
				mensajeDevuelto = WORKER_MSG_ERROR;
			}

			runOnUiThread(new Runnable() {
				public void run() {
					nVotosMenos.setText(String.valueOf(mPOI.getWarningcount()
							.getClosed() + 1));
					// stuff that updates ui

				}
			});

			handler.sendEmptyMessage(mensajeDevuelto);
		}
	}

	private class AddPositiveVoteBackground implements Runnable {

		public void run() {

			int mensajeDevuelto = WORKER_MSG_OK;
			try {
				topoos.POIWarning.Operations.AddDuplicated(
						DetaillJokeActivity.this, mPOI.getId());
				DatabaseHandler db = new DatabaseHandler(
						DetaillJokeActivity.this);
				db.addVote(mPOI.getId());
			} catch (Exception e) {
				mensajeDevuelto = WORKER_MSG_ERROR;
			}

			runOnUiThread(new Runnable() {
				public void run() {
					nVotosMas.setText(String.valueOf(mPOI.getWarningcount()
							.getDuplicated() + 1));
					// stuff that updates ui

				}
			});

			handler.sendEmptyMessage(mensajeDevuelto);
		}
	}

	/*
	 * private class getUrl extends AsyncTask<Integer, Integer, String> {
	 * 
	 * @Override protected String doInBackground(Integer... arg0) { // TODO
	 * Auto-generated method stub String OperationResult = ""; OperationResult =
	 * topoos.Images.Operations.GetImageURIThumb( mPOI.getName(),
	 * topoos.Images.Operations.SIZE_XLARGE);
	 * 
	 * return OperationResult; }
	 * 
	 * protected void onPostExecute(final String result) {
	 * 
	 * runOnUiThread(new Runnable() { public void run() { urlimg = result;
	 * 
	 * try { InputStream is = (InputStream) new URL(urlimg) .getContent();
	 * Drawable d = Drawable.createFromStream(is, "src name");
	 * photo.setImageDrawable(d);
	 * 
	 * } catch (Exception e) {
	 * 
	 * }
	 * 
	 * // Utils.LoadImageBG(urlimg, photo, // DetaillJokeActivity.this); } }); }
	 * 
	 * }
	 */

	@Override
	public void onClick(View v) {
		Thread thread;
		switch (v.getId()) {

		case R.id.votosMas:
			progressDialog = ProgressDialog.show(this,
					this.getString(R.string.espere),
					this.getString(R.string.evotacion));
			thread = new Thread(new AddPositiveVoteBackground());
			thread.start();
			break;
		case R.id.votosMenos:
			progressDialog = ProgressDialog.show(this,
					this.getString(R.string.espere),
					this.getString(R.string.evotacion));
			thread = new Thread(new AddNegativeVoteBackground());
			thread.start();
			break;

		case R.id.share:
			showOneDialog();
		default:
			break;
		}

	}

	private void showOneDialog() {

		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
/*
		String shortText = Sharing.getSharingShortText(mPOI.getDescription(),
				topoos.Images.Operations.GetImageURI(mPOI.getName()),
				Constants.TW_MAXLENGTH);
*/
		String shortText = Sharing
				.getSharingLongText(DetaillJokeActivity.this, topoos.Images.Operations
						.GetImageURI(mPOI.getName()));
		
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shortText);
		startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)));

	}

}