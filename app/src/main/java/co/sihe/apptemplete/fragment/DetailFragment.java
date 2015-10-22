package co.sihe.apptemplete.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.sihe.apptemplete.R;


public class DetailFragment extends BaseFragment {
    public static final String PARAM_ID = "id";

    private int mID;

    public static DetailFragment newInstance(int id) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(PARAM_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mID = getArguments().getInt(PARAM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_detail, container, false);
        AppBarFragment.setupInDetailFragment(this);

        ((TextView) contentView.findViewById(R.id.fragment_detail_content)).setText("This is Detail View And Id is: " + mID);
        return contentView;
    }
}
