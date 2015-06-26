package co.sihe.apptemplete.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.sihe.apptemplete.R;
import co.sihe.apptemplete.fragment.endlessrecycler.EndlessRecyclerAdatper;
import co.sihe.apptemplete.views.endlessrecycler.EndlessRecyclerScrollListener;

/**
 * Created by hwangjr on 6/26/15.
 */
public class EndlessRecyclerFragment extends BaseFragment {

    private static final String TAG = EndlessRecyclerFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EndlessRecyclerAdatper mAdapter;

    public static EndlessRecyclerFragment newInstance() {
        EndlessRecyclerFragment fragment = new EndlessRecyclerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EndlessRecyclerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new EndlessRecyclerAdatper(DataSource.generate(20));
        mAdapter.setOnItemClickListener(new EndlessRecyclerAdatper.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, String data) {
                Toast.makeText(getActivity(), "onItemClick:" + data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_endless_recycler, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "onRefresh", Toast.LENGTH_SHORT).show();
                new RefreshTask().execute();
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Toast.makeText(getActivity(), "onLoadMore, page=" + page + " totalItemsCount=" + totalItemsCount, Toast.LENGTH_SHORT).show();
                new MoreTask().execute();
            }

            @Override
            public void onFindFirstCompletelyVisibleItemPosition(boolean hasFound) {
                super.onFindFirstCompletelyVisibleItemPosition(hasFound);
                mSwipeRefreshLayout.setEnabled(hasFound);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private class RefreshTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return DataSource.generate(20);
        }

        @Override
        protected void onPostExecute(List<String> data) {
            mAdapter.addRefreshItems(data);
            //通知刷新完毕
            mSwipeRefreshLayout.setRefreshing(false);
            //滚动到列首部--->这是一个很方便的api，可以滑动到指定位置
            mRecyclerView.scrollToPosition(0);
        }
    }

    private class MoreTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return DataSource.generate(20);
        }

        @Override
        protected void onPostExecute(List<String> data) {
            int count = mAdapter.getItemCount();
            mAdapter.addMoreItems(data);
            //通知刷新完毕
            mSwipeRefreshLayout.setRefreshing(false);
            mRecyclerView.scrollToPosition(count);
        }
    }

    static class DataSource {
        static int dp = 0;

        static List<String> generate(int size) {
            List<String> data = new ArrayList<>();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    data.add("Test Data --> " + dp);
                    dp++;
                }
            }
            return data;
        }
    }

}
