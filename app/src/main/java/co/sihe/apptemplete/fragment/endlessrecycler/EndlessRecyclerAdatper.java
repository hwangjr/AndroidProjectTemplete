package co.sihe.apptemplete.fragment.endlessrecycler;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.sihe.apptemplete.R;

/**
 * Created by hwangjr on 6/26/15.
 */
public class EndlessRecyclerAdatper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> mDataSource = null;
    private OnItemClickListener mOnItemClickListener;

    // Two view types which will be used to determine whether a row should be displaying
    // data or a Progressbar
    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_DATA = 1;

    public EndlessRecyclerAdatper(List<String> dataSource) {
        this.mDataSource = dataSource;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_LOADING) {
            final View loadingView = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_endless_recycler, parent, false);
            return new LoadingViewHolder(loadingView);
        }

        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_endless_recycler, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.OnItemClick(view, (String) itemView.getTag());
                }
            }
        });
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof DataViewHolder) {
            String s = mDataSource.get(position);
            ((DataViewHolder)holder).bindData(s);
            holder.itemView.setTag(s);
        } else if(holder instanceof LoadingViewHolder) {
            // TODO
        }
    }

    @Override
    public int getItemCount() {
        return mDataSource.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position >= mDataSource.size()) ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    @Override
    public long getItemId(int position) {
        return (getItemViewType(position) == VIEW_TYPE_DATA) ? position : -1;
    }

    public void addRefreshItems(List<String> items) {
        if (items != null) {
            this.mDataSource.addAll(0, items);
            this.notifyItemRangeInserted(0, items.size());
        }
    }

    public void addMoreItems(List<String> items) {
        if (items != null) {
            int size = mDataSource.size();
            this.mDataSource.addAll(items);
            this.notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener {
        public void OnItemClick(View view, String data);
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        private TextView mContent;

        public DataViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.item_endless_recycler_tv);
        }

        public void bindData(String s) {
            if (s != null) {
                mContent.setText(s);
            }
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }

        public void bindData(boolean hasMore) {
        }
    }
}