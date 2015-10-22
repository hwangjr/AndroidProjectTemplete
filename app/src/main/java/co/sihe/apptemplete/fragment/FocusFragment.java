package co.sihe.apptemplete.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import co.sihe.apptemplete.R;

public class FocusFragment extends BaseFragment implements AbsListView.OnItemClickListener {

    private static ArrayList<String> ITEMS = new ArrayList<>();

    static {
        ITEMS.add("John");
        ITEMS.add("Sam");
        ITEMS.add("Aragorn");
        ITEMS.add("Josef");
        ITEMS.add("Staff");
    }

    private AbsListView mListView;
    private ListAdapter mAdapter;

    public static FocusFragment newInstance() {
        FocusFragment fragment = new FocusFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FocusFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position % 2 == 0) {
            FragmentStarter.startDetailFragment(this, (int) id);
        } else {
            FragmentStarter.startDetailSlidingFragment(this, (int) id);
        }
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }
}
