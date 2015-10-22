package co.sihe.apptemplete.utils;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftInputUtil {
	public static void hideSoftInput(View view) {
		if (view != null) {
			Context context = view.getContext();
			IBinder windowToken = view.getWindowToken();
			InputMethodManager inputMethodManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
		}
	}

	public static void showSoftInput(View view) {
		if (view != null) {
			Context context = view.getContext();
			IBinder windowToken = view.getWindowToken();
			InputMethodManager inputMethodManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.showSoftInputFromInputMethod(windowToken, 0);
		}
	}
}
