package es.solusoft.santosinocentes;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import es.solusoft.carnavalea.R;
import topoos.Objects.POI;

public class DatabaseHandler extends SQLiteOpenHelper {

	// Declaramos el nombre de la base de datos

	private static String DATABASE_NAME = "POIs.db";

	// Services table name
	private static final String TABLE_POIS = "POIs";
	private static final String TABLE_VOTO = "VOTO";

	// Services Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_POI = "KEY_PoI";

	//private static final String BROMA_VALUE = "IS_VOTED";

	private static int DATABASE_VERSION = 1;

	//private final Context myContext;

	// Constructor
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		//this.myContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Se ejecuta la sentencia SQL de creación de la tabla
		String CREATE_POIS_TABLE = "CREATE TABLE " + TABLE_POIS + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_POI + " INTEGER " + ")";
		// Creamos la tabla anterior
		db.execSQL(CREATE_POIS_TABLE);
		String CREATE_VOTO_TABLE = "CREATE TABLE " + TABLE_VOTO + " ("
				+ KEY_ID + " INTEGER PRIMARY KEY)";
		db.execSQL(CREATE_VOTO_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Se elimina la versión anterior de la tabla
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_POIS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOTO);
		// Se crea la nueva versión de la tabla
		this.onCreate(db);
	}

	// Adding new service
	public void addPOIs(POI p) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_POI, p.getId());

		// Inserting Row
		db.insert(TABLE_POIS, null, values);
		// Closing database connection
		db.close();

	}

	// Getting single service
/*
	public POI getPOI(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_POIS, new String[] { KEY_ID, KEY_POI },
				KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null,
				null, null);
		if (cursor != null)
			cursor.moveToFirst();

		POI p = null;
		p.setId((Integer.parseInt(cursor.getString(0))));

		// return POI
		db.close();
		return p;

	}
*/
	

	public boolean isVoted(int id) {

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_VOTO, new String[] { KEY_ID }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				return true;
			} else {
				return false;
			}
		} else
			return false;

	}
	
	public void addVote(int poiId) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, poiId);

		// Inserting Row
		db.insert(TABLE_VOTO, null, values);
		// Closing database connection
		db.close();

	}

	public ArrayList<Integer> getallIds() {
		// Creamos una lista de enteros
		ArrayList<Integer> IDsList = new ArrayList<Integer>();

		// Selcccion de todas las Query
		String selectQuery = "SELECT " + KEY_POI + " FROM " + TABLE_POIS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Integer id = cursor.getInt(0);
				IDsList.add(id);
			} while (cursor.moveToNext());
		}
		db.close();

		return IDsList;

	}

}