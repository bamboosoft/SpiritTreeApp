/*
 *
 */

package com.bamboosoft.spirittreeapp.comm;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Extends {@link SwipeRefreshLayout} to support non-direct descendant scrolling views.
 *扩展{ @ link SwipeRefreshLayout }以支持非直接派生滚动视图。
 * <p>
 * {@link SwipeRefreshLayout} works as expected when a scroll view is a direct child: it triggers
 * the refresh only when the view is on top. This class adds a way (@link #setScrollUpChild} to
 * define which view controls this behavior.
 *
 * { @ link SwipeRefreshLayout在滚动视图是一个直接的子元素时，可以像预期的那样工作:它触发
 *只有当视图在顶部时才刷新。这个类增加了一个方法(@ link # setScrollUpChild }
 *定义哪个视图控制该行为。
 */
public class ScrollChildSwipeRefreshLayout extends SwipeRefreshLayout {

    private View mScrollUpChild;

    public ScrollChildSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ScrollChildSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if (mScrollUpChild != null) {
            return ViewCompat.canScrollVertically(mScrollUpChild, -1);
        }
        return super.canChildScrollUp();
    }

    public void setScrollUpChild(View view) {
        mScrollUpChild = view;
    }
}
