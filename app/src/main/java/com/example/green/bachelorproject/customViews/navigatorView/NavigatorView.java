package com.example.green.bachelorproject.customViews.navigatorView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.example.green.bachelorproject.R;

/**
 * Created by Green on 30/03/15.
 */
public class NavigatorView extends LinearLayout {

    public NavigatorView(Context context) {
        super(context);
        init(null, context);
    }

    public NavigatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public NavigatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    private void init(AttributeSet attrs, Context context) {
        if(attrs != null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutInflater.inflate(R.layout.navigator_layout, this);
        }
    }
}
