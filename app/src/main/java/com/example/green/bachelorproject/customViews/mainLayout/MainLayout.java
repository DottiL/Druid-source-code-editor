package com.example.green.bachelorproject.customViews.mainLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import com.example.green.bachelorproject.R;
import com.example.green.bachelorproject.customViews.codeEditHolder.CodeEditHolder;
import com.example.green.bachelorproject.events.KeyboardEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by Green on 06/03/15.
 */
public class MainLayout extends LinearLayout {
    private CodeEditHolder content;
    private View templatesButton;
    private View keyboardButton;

    private boolean templatesViewLock;

    private int menuHeight;
    private View root;
    private View menu;
    private boolean keyboard;
    private boolean wasOpen;
    private RelativeLayout templatesView;
    private RelativeLayout current;

    private final double menuPercentage = 0.50;

    public MainLayout(Context context) {
        super(context);
        init(context);
    }

    public MainLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = layoutInflater.inflate(R.layout.main_layout, this);
        templatesViewLock = true;

        keyboard = false;
        wasOpen = false;
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                menuHeight = (int) (menuPercentage * getHeight());
            }
        });

        menu = root.findViewById(R.id.main_layout_bottom_bar);
        content = (CodeEditHolder) root.findViewById(R.id.main_layout_content);
        templatesView = (RelativeLayout) root.findViewById(R.id.main_layout_templates_view);
        templatesButton = root.findViewById(R.id.main_layout_templates_button);

        templatesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!templatesViewLock) {
                    if(keyboard) {
                        EventBus.getDefault().post(new KeyboardEvent(false, true));
                        keyboard = false;
                        wasOpen = true;

                    }

                    if(current != null) {
                        if(wasOpen) {
                            EventBus.getDefault().post(new KeyboardEvent(true, true));
                            keyboard = true;
                            wasOpen = false;
                        }
                        current.setVisibility(View.GONE);
                        current = null;

                    } else {
                        current = templatesView;
                        current.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        keyboardButton = root.findViewById(R.id.main_layout_keyboard_button);

        keyboardButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyboard) {
                    EventBus.getDefault().post(new KeyboardEvent(false, true));
                    keyboard = false;
                } else {
                    EventBus.getDefault().post(new KeyboardEvent(true, true));
                    keyboard = true;
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed) {
            if(current != null) {
                current.getLayoutParams().width = getWidth();
                current.getLayoutParams().height = menuHeight;
                current.getChildAt(0).getLayoutParams().width = getWidth();
                current.getChildAt(0).getLayoutParams().height = menuHeight;
            }

            content.getLayoutParams().width = getWidth();
            content.getLayoutParams().height = getHeight()-menu.getHeight();
            menu.getLayoutParams().width = getWidth();
            menu.getLayoutParams().height = menu.getHeight();
        }

        if(current != null) {
            View v = current.getChildAt(0);
            if(v != null) {
                v.getLayoutParams().width = getWidth();
                v.getLayoutParams().height = menuHeight;
            }
        }

        content.layout(l,t+menu.getHeight(),r,b);
        menu.layout(l,t,r,t+menu.getHeight());

        if(current != null) {
            current.layout(l, b - menuHeight, r, b);
        }
    }

    public void setKeyboardState(boolean active) {
        keyboard = active;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(templatesView.getVisibility() == View.VISIBLE) {
                templatesView.setVisibility(View.GONE);
            }
        }

        return false;
    }

    public boolean isTemplatesViewVisible() {
        if(templatesView.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    public void hideTemplatesView() {
        templatesView.setVisibility(View.GONE);
    }

    public void setLockTemplatesView(boolean lock) {
        this.templatesViewLock = lock;
    }
}


enum MenuState {
    NAVIGATOR, TEMPLATES, KEYBOARD;
};