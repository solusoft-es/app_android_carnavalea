package es.solusoft.santosinocentes.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

/**
 * This is an object that can load images from a URL on a thread.
 * 
 * http://ballardhack.wordpress.com/2010/04/10/loading-images-over-http-on-a-separate-thread-on-android/
 * http://ballardhack.wordpress.com/2010/04/05/loading-remote-images-in-a-listview-on-android/
 * 
 * @author Jeremy Wadsack
 */
public class ImageThreadLoader {

	// Global cache of images.
	// Using SoftReference to allow garbage collector to clean cache if needed
	private ConcurrentHashMap<URL, SoftReference<Bitmap>> cache = new ConcurrentHashMap<URL, SoftReference<Bitmap>>();

	private ConcurrentLinkedQueue<QueueItem> queue = new ConcurrentLinkedQueue<QueueItem>();

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private Handler handler = new Handler(); // Assumes that this is started from the main (UI) thread

	/**
	 * Defines an interface for a callback that will handle responses from the thread loader when an image is done being
	 * loaded.
	 */
	public interface ImageLoadedListener {

		public void imageLoaded(Bitmap imageBitmap);

	}

	private final class QueueItem {

		public URL url;
		public ImageLoadedListener listener;

	}

	private class NotifyImageLoadedTask implements Runnable {

		private ImageLoadedListener listener;
		private Bitmap bitmap;

		public NotifyImageLoadedTask(ImageLoadedListener listener, Bitmap bitmap) {
			this.listener = listener;
			this.bitmap = bitmap;
		}

		@Override
		public void run() {
			listener.imageLoaded(bitmap);
		}

	}

	/**
	 * Provides a Runnable class to handle loading the image from the URL and settings the ImageView on the UI thread.
	 */
	private class QueueRunner implements Runnable {

		@Override
		public void run() {
			QueueItem item = queue.poll();

			SoftReference<Bitmap> ref = cache.get(item.url);

			Bitmap bitmap = null;
			if (ref != null)
				bitmap = ref.get();

			// If in the cache, return that copy and be done
			if (bitmap != null) {
				if (item.listener != null)
					// Use a handler to get back onto the UI thread for the update
					handler.post(new NotifyImageLoadedTask(item.listener, bitmap));
			} else {
				bitmap = readBitmapFromNetwork(item.url);
				if (bitmap != null)
					cache.put(item.url, new SoftReference<Bitmap>(bitmap));
				if (item.listener != null)
					// Use a handler to get back onto the UI thread for the update
					handler.post(new NotifyImageLoadedTask(item.listener, bitmap));
			}
		}
	}

	/**
	 * Queues up a URI to load an image from for a given image view.
	 * 
	 * @param uri
	 *            The URI source of the image
	 * @param callback
	 *            The listener class to call when the image is loaded
	 * @throws MalformedURLException
	 *             If the provided uri cannot be parsed
	 * @return A Bitmap image if the image is in the cache, else null.
	 */
//	public Bitmap loadImage(String uriStr, ImageLoadedListener listener, Post post, Context ctx) throws MalformedURLException {
	public Bitmap loadImage(String uriStr, ImageLoadedListener listener) throws MalformedURLException {
		// If it's in the cache, just get it and quit it

		URL url = new URL(uriStr);
		SoftReference<Bitmap> ref = cache.get(url);
		if (ref != null) {
			Bitmap bitmap = ref.get();
			if (bitmap != null)
				return bitmap;
			else 
			{
				//Fix para que intente utilizar las imagenes desde el disco
				//bitmap = GalleryOps.getCachePhotoThumbnail(ctx, post);
				if (bitmap != null)
				{
					return bitmap;
				}
			}
		}

		QueueItem item = new QueueItem();
		item.url = url;
		item.listener = listener;

		queue.offer(item);

		executor.execute(new QueueRunner());

		return null;
	}

	/**
	 * Convenience method to retrieve a bitmap image from a URL over the network. The built-in methods do not seem to
	 * work, as they return a FileNotFound exception.
	 * 
	 * Note that this does not perform any threading -- it blocks the call while retrieving the data.
	 * 
	 * @param url
	 *            The URL to read the bitmap from.
	 * @return A Bitmap image or null if an error occurs.
	 */
	public static Bitmap readBitmapFromNetwork(URL url) {
		InputStream is = null;
		BufferedInputStream bis = null;
		Bitmap bmp = null;
		try {
			/*URLConnection conn = url.openConnection();
			conn.connect();
			is = conn.getInputStream();
			bis = new BufferedInputStream(is);
			bmp = BitmapFactory.decodeStream(bis);
			*/
			bmp = BitmapFactory.decodeStream(url.openStream());
		} catch (MalformedURLException e) {
			//LogManager.Info(LogManager.Tag.ImageThreadLoader, "Bad URL " + e.getMessage());
		} catch (IOException e) {
			//LogManager.Info(LogManager.Tag.ImageThreadLoader, "Could not get remote ad image " + e.getMessage());
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
			//	LogManager.Info(LogManager.Tag.ImageThreadLoader, "Error closing stream. " + e.getMessage());
			}
		}
		return bmp;
	}

}