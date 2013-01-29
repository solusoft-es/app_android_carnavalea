package es.solusoft.santosinocentes.internal;

import es.solusoft.carnavalea.R;
import android.content.Context;

public class Sharing {
	
		
	public static String getSharingShortText(String msg, String imgURI, int msgMaxChar)
	{
		String fullText = "";
		if (msg != null)
		{
			fullText = Constants.TW_HASHTAG + " ";
			int caracteresReservados = fullText.length() + Constants.TW_URL_IMG_URI_LENGTH + 3;
			
			if (msg != null )
			{
				if (msg.length() > caracteresReservados)
				{
					fullText = fullText + msg.substring(0, caracteresReservados) + "... ";
				}else
				{
					fullText = fullText + msg + " ";
				}
			}
			
			fullText = fullText + imgURI;
		}
		
		return fullText;
	}
	
	public static String getSharingLongText(Context ctx, String imgUri){
		String  fullText="";
		
		//fullText=Constants.TW_TEXT +Constants.Space+Constants.TW_HASHTAG+Constants.Space+imgUri+Constants.Space+"Via"+Constants.Space+Constants.TW_HASHTAGAPP +Constants.Space+ Constants.URLAPP;
		fullText = ctx.getString(R.string.sharing).replace("{uri}", imgUri);
		
		return fullText;
	}
	
	
}
