package co.sihe.apptemplete.fragment.endlessrecycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.sihe.apptemplete.R;

/**
 * Created by hwangjr on 6/26/15.l
 * 
 */
public class EndlessRecyclerAdatper extends RecyclerView.Adapter<EndlessRecyclerAdatper.ViewHolder> {
    private List<String> mDataSource = null;
    private OnItemClickListener mOnItemClickListener;

    public EndlessRecyclerAdatper(List<String> dataSource) {
        this.mDataSource = dataSource;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_endless_recycler, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.OnItemClick(view, (String) itemView.getTag());
                }
            }
        });
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String s = mDataSource.get(position);
        holder.bindData(s);
        holder.itemView.setTag(s);
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
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
            this.notifyItemRangeInserted(size, mDataSource.size());
        }
    }

    public interface OnItemClickListener {
        public void OnItemClick(View view, String data);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mContent;

        public ViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.item_endless_recycler_tv);
        }

        public void bindData(String s) {
            if (s != null)
                mContent.setText(s);
        }
    }
}
