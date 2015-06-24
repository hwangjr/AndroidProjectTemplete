package co.sihe.apptemplete.views.tab;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public abstract class BaseTabGroup extends RelativeLayout implements
        TabBar.OnViewSwitchedListener {

    public static final int LOCATION_BOTTOM = 0;
    public static final int LOCATION_TOP = 1;
    private static final int WIDGETBAR_ID = 10000;
    protected int mTabBarLocation = LOCATION_BOTTOM;
    protected ViewGroup mContainerLayout;
    protected TabBar mTabBar;
    protected TabChangedListener mTabChangedListener;

    public interface TabChangedListener {
        void onTabChanged(int position);

        void onTabClick(int position);
    }

    public BaseTabGroup(Context context, int layoutId) {
        super(context);
        this.setId(layoutId);
    }

    public BaseTabGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @see #setup(int, ViewGroup)
     */
    public void setup() {
        setup(LOCATION_BOTTOM);
    }

    /**
     * @see #setup(int, ViewGroup)
     */
    public void setup(int tabBarLocation) {
        setup(tabBarLocation, null);
    }

    /**
     * 函数名: setup<br>
     * 功能:初始化TabGroup；必须调用此函数 <br>
     *
     * @param tabBarLocation 导航条的位置<br>
     *                       LOCATION_TOP=0；LOCATION_BOTTOM=1
     * @param tabBarLayout   导航条的layout，优先从布局取id为android.R.id.tabs的布局；若不需要导航条可设置为null
     * @return
     */
    public void setup(int tabBarLocation, ViewGroup tabBarLayout) {
        if (getId() == View.NO_ID) {
            throw new IllegalArgumentException(this.getClass().getName()
                    + " id could not be none...");
        }
        ViewGroup tabBar = (ViewGroup) findViewById(android.R.id.tabs);
        removeAllViews();

        tabBar = (tabBarLayout != null) ? tabBarLayout : tabBar;
        if (tabBar != null) {
            mTabBar = createTabBar(tabBar);
        }
        mContainerLayout = createContainerLayout();

        LayoutParams containerLayoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if (mTabBar != null) {
            LayoutParams tabBarLayoutParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            int widgetBarRule = (tabBarLocation == LOCATION_TOP) ? RelativeLayout.ALIGN_PARENT_TOP
                    : RelativeLayout.ALIGN_PARENT_BOTTOM;
            tabBarLayoutParams.addRule(widgetBarRule);
            int containerRule = (tabBarLocation == LOCATION_TOP) ? RelativeLayout.BELOW
                    : RelativeLayout.ABOVE;
            containerLayoutParams.addRule(containerRule, WIDGETBAR_ID);
            addView(mTabBar, tabBarLayoutParams);
        }
        addView(mContainerLayout, containerLayoutParams);
    }

    public abstract void addTab(Class<?> clazz, Bundle args);

    public abstract int getCurrentPosition();

    protected abstract void onTabSelected(int position);

    @Override
    public void onWidgetSwitched(int position, View selectedView) {
        onTabSelected(position);
        if (mTabChangedListener != null) {
            mTabChangedListener.onTabChanged(position);
        }
    }

    public void setCurrentTab(final int position, final boolean isStartAnimation) {
        if (mTabBar != null) {
            mTabBar.setCurrentView(position, isStartAnimation);
        } else {
            onTabSelected(position);
            if (mTabChangedListener != null) {
                mTabChangedListener.onTabChanged(position);
            }
        }
    }

    public void setCurrentTab(int position) {
        setCurrentTab(position, false);
    }

    public void setOnTabChangeListener(TabChangedListener listener) {
        mTabChangedListener = listener;
    }

    public TabBar getTabWidgetBar() {
        return mTabBar;
    }

    public int getContainerId() {
        return mContainerLayout.getId();
    }

    protected ViewGroup createContainerLayout() {
        FrameLayout containerLayout = new FrameLayout(getContext());
        containerLayout.setId(this.getId());
        setId(View.NO_ID);
        return containerLayout;
    }

    protected TabBar createTabBar(ViewGroup widgetBarLayout) {
        TabBar widgetBar = new TabBar(getContext(), widgetBarLayout);
        widgetBar.setOnViewSwitchedListener(this);
        widgetBar.setId(WIDGETBAR_ID);
        return widgetBar;
    }
}
