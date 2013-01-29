package es.solusoft.santosinocentes.internal;

public class Config {


	public static final String APPTOKEN_ADMIN = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxx"; //REPLACE THIS
	public static final int Categorias = -1; //REPLACE THIS
	
	
	
	
	
	
	public enum SEARCH_TYPE {
		Where, Near
	}
	
	public static Integer[] CAT = { 
		Categorias 
	};
	
	public static final int RADIUS_SEARCH_POIS_METERS = 600000;
	public static final int total = 50;// Variable que indica el numero de
										// bromas a descargar para op getNear y
										// getWhere
	public static final int totalMap = 150;
	public static final SEARCH_TYPE param_search_type = SEARCH_TYPE.Where;
	public static final SEARCH_TYPE param_search_type_map = SEARCH_TYPE.Near;

}