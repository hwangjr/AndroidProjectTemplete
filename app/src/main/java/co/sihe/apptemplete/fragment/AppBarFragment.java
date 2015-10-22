package co.sihe.apptemplete.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.sihe.apptemplete.R;

public class AppBarFragment extends BaseFragment {
    private static final String KEY_TYPE = "key_type";
    private static final String KEY_TITLE = "key_title";

    private static final int TYPE_MAIN_FRAGMENT = 0;
    private static final int TYPE_SECOND_FRAGMENT = 1;

    private Toolbar mToolbar;
    private TextView mTitleView;
    private View.OnClickListener mOnNavigationClickListener;
    private View.OnClickListener mOnTitleClickListener;
    private int mMenuResId;
    private Toolbar.OnMenuItemClickListener mMenuItemClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int type = getArguments().getInt(KEY_TYPE, TYPE_MAIN_FRAGMENT);
        View contentView = inflater.inflate(R.layout.toolbar_actionbar, container, false);
        initView(contentView, type);
        return contentView;
    }

    private void initView(View contentView, int type) {
        mToolbar = (Toolbar) contentView.findViewById(R.id.toolbar_actionbar);
        mTitleView = (TextView) contentView.findViewById(R.id.toolbar_title);
        setTitle(getArguments().getString(KEY_TITLE));
        switch (type) {
            case TYPE_MAIN_FRAGMENT:
                break;
            case TYPE_SECOND_FRAGMENT:
                mToolbar.setNavigationIcon(R.drawable.arrow_left);
                mToolbar.setNavigationOnClickListener(mOnNavigationClickListener);
                break;
            default:
                break;
        }
        if (mMenuResId > 0) {
            mToolbar.inflateMenu(mMenuResId);
            mToolbar.setOnMenuItemClickListener(mMenuItemClickListener);
        }
        if (mOnTitleClickListener != null) {
            contentView.setOnClickListener(mOnTitleClickListener);
        }
    }

    public AppBarFragment setNavigationOnClickListener(View.OnClickListener onClickListener) {
        mOnNavigationClickListener = onClickListener;
        return this;
    }

    public AppBarFragment setTitle(String title) {
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
        return this;
    }

    public AppBarFragment setOnTitleClickListener(View.OnClickListener onClickListener) {
        this.mOnTitleClickListener = onClickListener;
        return this;
    }

    public AppBarFragment setMenuResId(int resId) {
        mMenuResId = resId;
        return this;
    }

    public AppBarFragment setOnMenuClickListener(Toolbar.OnMenuItemClickListener onClickListener) {
        mMenuItemClickListener = onClickListener;
        return this;
    }

    public static AppBarFragment setupInMainFragment(BaseFragment fragment) {
        return setupInFragment(fragment, TYPE_MAIN_FRAGMENT, fragment.getString(R.string.focus));
    }

    public static AppBarFragment setupInDetailFragment(final BaseFragment fragment) {
        return setupInFragment(fragment, TYPE_SECOND_FRAGMENT, fragment.getString(R.string.detail))
                .setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment.finishFragment();
                    }
                });
    }

    private static AppBarFragment setupInFragment(BaseFragment baseFragment, int type, String title) {
        return setupInFragment(baseFragment, type, title, R.id.appbar_container);
    }

    private static AppBarFragment setupInFragment(BaseFragment baseFragment, int type, String title,
                                                  int containId) {
        AppBarFragment fragment = new AppBarFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        args.putString(KEY_TITLE, title);
        fragment.setArguments(args);
        baseFragment.getChildFragmentManager().beginTransaction()
                .replace(containId, fragment).commitAllowingStateLoss();
        return fragment;
    }
}
