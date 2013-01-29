package es.solusoft.santosinocentes.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.EditText;

public class ExtendedEditText extends EditText
{
	private Paint p1;
	private Paint p2;
	
	public ExtendedEditText(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		inicializacion();
	}
	
	public ExtendedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		inicializacion();
	}
	
	public ExtendedEditText(Context context) {
		super(context);
		
		inicializacion();
	}
	
	private void inicializacion()
	{
		p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		p1.setColor(Color.BLACK);
		p1.setStyle(Style.FILL);
		
		p2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		p2.setColor(Color.WHITE);
	}
	
	@Override
	public void onDraw(Canvas canvas) 
	{
		
		//Llamamos al método de la clase base (EditText)
		super.onDraw(canvas);
	
		//Dibujamos el fondo negro del contador
		canvas.drawRect(this.getWidth()-40, 5, 
				this.getWidth()-5, 30, p1);
		
		int numerocaractereswrite=this.getText().toString().length();//Caracteres que obtenemos
		int numerocaracteres=140;//Max longitud
		int caracteresrestantes=numerocaracteres-numerocaractereswrite;//Caracteres restantes
		//Dibujamos el número de caracteres sobre el contador
		canvas.drawText("" + caracteresrestantes, 
				this.getWidth()-38, 27, p2);
	}
}
