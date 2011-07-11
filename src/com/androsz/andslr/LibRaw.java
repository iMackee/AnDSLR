package com.androsz.andslr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.androsz.andslr.R;

/**
 * @author jon
 * @description Singleton used to interface with the native LibRaw (and AnDSLR)
 *              shared libraries. Provides some native JNI methods and
 *              facilitated methods.
 */
public final class LibRaw {

	// this class cannot be instantiated.
	private LibRaw() {
	}

	static {
		System.loadLibrary("raw_r");
		System.loadLibrary("andslr");
	}

	/*
	 * Direct JNI calls Provided to provide flexibility and efficiency boosts
	 */

	private static native byte[] unpackThumbnailBytesToFit(String fileName,
			short height, short width);
	
	private static native short[] getThumbnailDimensions(String fileName);

	/*
	 * Utility methods that rely on JNI calls
	 */

	/**
	 * @param fileName
	 *            The path to the RAW file, ex. /sdcard/raw.CR2
	 * @return A bitmap representing the embedded thumbnail, scaled to fit
	 *         within the desired height and width
	 */
	public static Bitmap unpackThumbnailBitmapToFit(String fileName,
			short height, short width) {
		Bitmap thumbnail = null;
		byte[] thumbnailBytes = unpackThumbnailBytesToFit(fileName, height,
				width);
		/*short[] dimensions = getThumbnailDimensions(fileName);
		if(dimensions[0] > 512)
		{
			
		}*/
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 8;
		// WARNING: this (wrongly?) assumes that Java's short size is the same
		// as native's
		thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes,
				0, thumbnailBytes.length, opts);
		thumbnailBytes = new byte[0];

		// let the system know this would be a decent time to free up the
		// unreferenced thumbnailBytes
		System.gc();

		return thumbnail;
	}
}
