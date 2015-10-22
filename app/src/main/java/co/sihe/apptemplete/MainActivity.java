package co.sihe.apptemplete;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import co.sihe.apptemplete.fragment.MainActivityFragment;
import co.sihe.apptemplete.utils.SoftInputUtil;
import co.sihe.apptemplete.views.ToastHelper;


public class MainActivity extends AppCompatActivity {
    private ToastHelper mToastHelper;
    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initMainFragment();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FragmentManager manager = getSupportFragmentManager();
            int backStackCount = manager.getBackStackEntryCount();
            if (backStackCount == 0) {
                if ((System.currentTimeMillis() - mExitTime) > 3000) {
                    getToastHelper().toast(R.string.exit_app);
                    mExitTime = System.currentTimeMillis();
                    return false;
                } else {
                    finish();
                    System.exit(0);
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            supportFinishAfterTransition();
        }
    }

    private void initMainFragment() {
        MainActivityFragment fragment = new MainActivityFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();
    }

    protected ToastHelper getToastHelper() {
        if (mToastHelper == null) {
            mToastHelper = new ToastHelper(this);
        }
        return mToastHelper;
    }

    // hide soft input when touch outside the edit text
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            final View view = getCurrentFocus();
            if (view != null) {
                final boolean result = super.dispatchTouchEvent(motionEvent);
                final View viewTmp = getCurrentFocus();
                final View viewNew = (viewTmp != null ? viewTmp : view);
                if (viewNew.equals(view)) {
                    final Rect rect = new Rect();
                    final int[] coordinates = new int[2];
                    view.getLocationOnScreen(coordinates);
                    rect.set(coordinates[0], coordinates[1], coordinates[0] + view.getWidth(), coordinates[1] + view.getHeight());
                    final int x = (int) motionEvent.getX();
                    final int y = (int) motionEvent.getY();

                    if (rect.contains(x, y)) {
                        return result;
                    }
                } else if (viewNew instanceof EditText) {
                    return result;
                }
                SoftInputUtil.hideSoftInput(viewNew);
                viewNew.clearFocus();
                return result;
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }
}
