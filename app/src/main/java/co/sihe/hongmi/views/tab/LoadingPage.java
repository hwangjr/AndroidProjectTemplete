package co.sihe.hongmi.views.tab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import co.sihe.hongmi.R;

public class LoadingPage extends FrameLayout {

    private ViewGroup mLoadingLayout;
    private ViewGroup mFailedLayout;
    private TextView mFailedText;

    public LoadingPage(Context context) {
        super(context);
        initViews(context);
    }

    public LoadingPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.loading_layout, null);
        this.addView(contentView);
        mLoadingLayout = (ViewGroup) contentView
                .findViewById(R.id.loading_layout);
        mFailedLayout = (ViewGroup) contentView
                .findViewById(R.id.failed_layout);
        mFailedText = (TextView) contentView
                .findViewById(R.id.failed_message_text);
        Button reloadBtn = (Button) contentView.findViewById(R.id.refresh_btn);
        reloadBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loading();
                reload(v.getContext());
            }
        });
    }

    public void loading() {
        this.setVisibility(View.VISIBLE);
        mFailedLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mFailedText.setText("");
    }

    public void failed(String failedMessage) {
        this.setVisibility(View.VISIBLE);
        mLoadingLayout.setVisibility(View.INVISIBLE);
        mFailedLayout.setVisibility(View.VISIBLE);
        if (failedMessage != null) {
            mFailedText.setText(failedMessage);
        }
    }

    public void failed(int failedStringId) {
        this.setVisibility(View.VISIBLE);
        mLoadingLayout.setVisibility(View.INVISIBLE);
        mFailedLayout.setVisibility(View.VISIBLE);
        mFailedText.setText(getContext().getString(failedStringId));
    }

    public boolean isFailed() {
        return mFailedLayout.getVisibility() == View.VISIBLE;
    }

    protected void reload(Context context) {
    }
}
