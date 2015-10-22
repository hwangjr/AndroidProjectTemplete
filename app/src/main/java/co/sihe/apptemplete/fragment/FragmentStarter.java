package co.sihe.apptemplete.fragment;

import android.os.Bundle;

public class FragmentStarter {

    public static void startDetailFragment(BaseFragment baseFragment,
                                           int id) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(DetailFragment.PARAM_ID, id);
        baseFragment.startFragment(fragment, args);
    }

    public static void startDetailSlidingFragment(BaseFragment baseFragment,
                                                  int id) {
        DetailSlidingFragment fragment = new DetailSlidingFragment();
        Bundle args = new Bundle();
        args.putInt(DetailFragment.PARAM_ID, id);
        baseFragment.startFragment(fragment, args);
    }
}
