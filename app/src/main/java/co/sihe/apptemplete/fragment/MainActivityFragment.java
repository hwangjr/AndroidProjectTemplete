package co.sihe.apptemplete.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.sihe.apptemplete.R;
import co.sihe.apptemplete.views.tab.BaseTabGroup;
import co.sihe.apptemplete.views.tab.FragmentPagerTabGroup;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends BaseFragment {

    private FragmentPagerTabGroup mFragmentPagerTabGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_main, container,
                false);
        initTitleBar(contentView);
        initTabGroup(contentView);
        return contentView;
    }

    private void initTabGroup(View contentView) {
        mFragmentPagerTabGroup = (FragmentPagerTabGroup) contentView
                .findViewById(R.id.tabgroup_main);
        mFragmentPagerTabGroup.setupInFragment(this, BaseTabGroup.LOCATION_BOTTOM);
        Bundle args = new Bundle();
        args.putString(TARGET_TAG_KEY, getTag());
        mFragmentPagerTabGroup.addTab(FocusFragment.class, args);
        mFragmentPagerTabGroup.addTab(EndlessRecyclerFragment.class, args);
        mFragmentPagerTabGroup.addTab(FocusFragment.class, args);
        mFragmentPagerTabGroup.addTab(FocusFragment.class, args);
        mFragmentPagerTabGroup.setCurrentTab(0);
    }

    private void initTitleBar(View rootView) {
    }
}
