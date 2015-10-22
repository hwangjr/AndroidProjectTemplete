package co.sihe.apptemplete.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.squareup.leakcanary.RefWatcher;

import java.io.Serializable;

import co.sihe.apptemplete.HMApplication;
import co.sihe.apptemplete.R;
import co.sihe.apptemplete.views.tab.LoadingPage;

public class BaseFragment extends Fragment {
    protected static final int REQUEST_SIZE = 20;

    protected static final int FIRST = 1;
    protected static final int REFRESH = 2;
    protected static final int MORE = 3;

    public static final String TARGET_TAG_KEY = "com.sihe.fragmentname.key";
    public static final String FRAGMENT_STACKNAME = "com.sihe.fragment.stackName";

    private String mLastStackName;

    private LoadingPage mLoadingPage;

    private boolean isLoadingPageShown;

    private int mLoadingPageParentId = View.NO_ID;

    private Serializable mParams;

    public Serializable getParams() {
        return mParams;
    }

    public void setParam(Serializable params) {
        this.mParams = params;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mParams != null) {
            outState.putSerializable("SerializableParams", mParams);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mParams = savedInstanceState.getSerializable("SerializableParams");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isLoadingPageShown) {
            displayLoadingPage(mLoadingPageParentId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fragment fragment = getTargetFragment();
        if (fragment != null && fragment instanceof BaseFragment) {
            BaseFragment targetFragment = (BaseFragment) fragment;
            targetFragment.onFragmentResult(getTargetRequestCode());
        }
        removeLoadingPage();
        if (getView() != null) {
            ((ViewGroup) getView()).removeAllViews();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = HMApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    protected void onFragmentResult(int targetRequestCode) {
    }

    public void onReload(Context context) {
    }

    public void startFragment(Fragment fragment, Bundle args) {
        startFragment(fragment, args, null, null);
    }

    public void startFragment(Fragment fragment, Bundle args, String tag) {
        startFragment(fragment, args, null, tag);
    }

    public void startFragment(Fragment fragment, Bundle args, String stackName,
                              String tag) {
        startFragment(fragment, R.id.fragment_container, args, stackName, tag);
    }

    public void startFragment(Fragment fragment, int layoutId, Bundle args,
                              String stackName, String tag) {
        if (mLastStackName != null) {
            this.finishFragment(mLastStackName);
            mLastStackName = null;
        }
        if (args == null) {
            args = new Bundle();
        }
        if (stackName == null) {
            stackName = "" + System.currentTimeMillis() + fragment.hashCode();
        }
        args.putString(FRAGMENT_STACKNAME, stackName);
        mLastStackName = stackName;
        if (tag == null) {
            tag = stackName;
        }
        args.putString(TARGET_TAG_KEY, tag);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentTransaction transaction = activity
                    .getSupportFragmentManager().beginTransaction();
            fragment.setArguments(args);
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right,
                    R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.add(layoutId, fragment, tag);
            transaction.addToBackStack(stackName);
            transaction.commitAllowingStateLoss();
            activity.getSupportFragmentManager().executePendingTransactions();
        }
    }

    public void finishFragment() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getSupportFragmentManager().popBackStackImmediate();
        }
    }

    public void finishFragment(String name) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getSupportFragmentManager().popBackStackImmediate(name,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void showLoadingPage() {
        showLoadingPage(View.NO_ID);
    }

    public void showLoadingPage(int layoutId) {
        if (!isLoadingPageShown) {
            isLoadingPageShown = true;
            if (mLoadingPage == null) {
                mLoadingPage = createLoadingPage();
            }
            mLoadingPageParentId = layoutId;
            mLoadingPage.loading();
            displayLoadingPage(layoutId);
        }
    }

    public void setLoadFailedMessage(String failedMessage) {
        if (mLoadingPage != null) {
            mLoadingPage.failed(failedMessage);
        }
    }

    public void setLoadFailedMessage(int failedStringId) {
        if (mLoadingPage != null) {
            mLoadingPage.failed(failedStringId);
        }
    }

    public void closeLoadingPage() {
        isLoadingPageShown = false;
        removeLoadingPage();
    }

    private void removeLoadingPage() {
        if (mLoadingPage != null) {
            ViewGroup parent = (ViewGroup) mLoadingPage.getParent();
            if (parent != null) {
                parent.removeView(mLoadingPage);
            }
        }
    }

    private boolean displayLoadingPage(int layoutId) {
        ViewGroup layout = null;
        Class<? extends BaseFragment> class1 = this.getClass();
        System.out.println(class1);
        View fragmentRootView = getView();
        if (fragmentRootView != null) {
            int index = -1;
            if (layoutId != View.NO_ID) {
                layout = (ViewGroup) fragmentRootView.findViewById(layoutId);
                if (layout instanceof LinearLayout) {
                    index = 0;
                }
            } else {
                layout = ((ViewGroup) fragmentRootView);
            }
            if (layout != null) {
                layout.addView(mLoadingPage, index, new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }
        } else {
            System.out.println(">>>>>." + isAdded());
        }
        return layout != null;
    }

    private LoadingPage createLoadingPage() {
        LoadingPage loadingPage = null;
        final Context context = getActivity();
        if (context != null) {
            loadingPage = new LoadingPage(context) {
                @Override
                protected void reload(Context context) {
                    onReload(context);
                }
            };
        }
        return loadingPage;
    }

}
