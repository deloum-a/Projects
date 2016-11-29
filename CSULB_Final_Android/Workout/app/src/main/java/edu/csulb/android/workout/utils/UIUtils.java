package edu.csulb.android.workout.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class UIUtils {

	static float widthPixels = 0;
	static float scaleDensity = 1.0F;
	static float density = 0;
	static int heightPadding = -1;

	public static void initDensity(Context ctx) {
		final DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		scaleDensity = dm.scaledDensity;
		widthPixels = dm.widthPixels;
		density = dm.density;
	}

	public static List<String> getBestImageList() {
		List<String> best_image_list;

		if (scaleDensity >= 1.5) {
			best_image_list = Arrays.asList("45", "63", "43");
		} else if (scaleDensity == 1) {
			best_image_list = Arrays.asList("45", "55", "51", "43", "54");
		} else {
			best_image_list = Arrays.asList("45", "55", "51", "43", "54");
		}

		return best_image_list;
	}

	public static boolean isHD() {
		return (scaleDensity > 1.0);
	}

	public static int getHeightPadding() {
		if (heightPadding < 0) {
			heightPadding = 13;
			if (scaleDensity != 1) {
				heightPadding = dipToPixel(11);
			}
		}
		return heightPadding;
	}

	public static int dipToPixel(int dip) {
		return (int) ((float) dip * scaleDensity);
	}

	public static String getPictoPlay() {
		String pictoPlay = null;
		if (scaleDensity >= 1.5) {
			pictoPlay = "picto_play";
		} else if (scaleDensity == 1) {
			pictoPlay = "picto_play_mdpi";
		} else {
			pictoPlay = "picto_play_ldpi";
		}

		return pictoPlay;
	}

	public static float getWidthPixels() {
		return widthPixels;
	}

	public static float getDensity() {
		return density;
	}

	/**
	 * WARNING this method must be called in the UI Thread
	 *
	 * @param fgt         fragment that calls the toast
	 * @param msgResource resource message to display
	 * @param toastDelay  time to display toast
	 */
	public static void showToastError(Fragment fgt, int msgResource, int toastDelay) {

		if (fgt != null && !fgt.isDetached() && fgt.getActivity() != null) {
			Toast toast = Toast.makeText(fgt.getActivity(), msgResource, toastDelay);
			toast.show();
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenWidth(Context ctx) {
		int width = 0;

		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
		} else {
			width = display.getWidth();
		}

		return width;
	}

	public static View createTabView(final Context ctx, final String text) {
		// View view =
		// LayoutInflater.from(ctx).inflate(R.layout.tab_indicator_holo, null);
		// TextView tv = (TextView) view.findViewById(android.R.id.title);
		// tv.setText(text);
		// return view;
		return null;
	}

}