package es.solusoft.santosinocentes.internal;

import android.graphics.Bitmap;
import android.widget.ImageView;
import es.solusoft.santosinocentes.internal.ImageThreadLoader.ImageLoadedListener;

public class PostImageLoadedListener implements ImageLoadedListener {
	private ImageView m_image = null;

	public PostImageLoadedListener(ImageView image) {
		m_image = image;
	}

	@Override
	public void imageLoaded(Bitmap imageBitmap) {
		m_image.setImageBitmap(imageBitmap);
	}
}
