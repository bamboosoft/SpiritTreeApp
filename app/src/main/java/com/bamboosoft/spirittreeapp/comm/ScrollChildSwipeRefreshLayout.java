/*
 *
 */

package com.example.android.architecture.blueprints.todoapp;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Extends {@link SwipeRefreshLayout} to support non-direct descendant scrolling views.
 *��չ{ @ link SwipeRefreshLayout }��֧�ַ�ֱ������������ͼ��
 * <p>
 * {@link SwipeRefreshLayout} works as expected when a scroll view is a direct child: it triggers
 * the refresh only when the view is on top. This class adds a way (@link #setScrollUpChild} to
 * define which view controls this behavior.
 *
 * { @ link SwipeRefreshLayout�ڹ�����ͼ��һ��ֱ�ӵ���Ԫ��ʱ��������Ԥ�ڵ���������:������
 *ֻ�е���ͼ�ڶ���ʱ��ˢ�¡������������һ������(@ link # setScrollUpChild }
 *�����ĸ���ͼ���Ƹ���Ϊ��
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
