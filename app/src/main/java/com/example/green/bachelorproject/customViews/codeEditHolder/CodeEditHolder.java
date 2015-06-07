package com.example.green.bachelorproject.customViews.codeEditHolder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.green.bachelorproject.R;
import com.example.green.bachelorproject.customViews.codeEditView.CodeEditView;

/**
 * This view holds several CodeEditView objects.
 */
public class CodeEditHolder extends RelativeLayout {
    //the container of the CodeEditViews in the CodeEditHolder's layout
    private RelativeLayout codeEditViewContainer;

    public CodeEditHolder(Context context) {
        super(context);
        init(null, context);
    }

    public CodeEditHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public CodeEditHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    private void init(AttributeSet attrs, Context context) {
        //inflate the layout for the CodeEditHolder
        if(attrs != null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            codeEditViewContainer = (RelativeLayout) layoutInflater.inflate(R.layout.code_edit_holder_layout, this);
        }
    }

    /**
     * This method remove the current CodeEditView displayed and add the provided one.
     *
     * @param codeEdiView the CodeEditView to display
     */
    public void setCodeEdiView(CodeEditView codeEdiView) {
        codeEditViewContainer.removeAllViews();
        codeEditViewContainer.addView(codeEdiView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
    }

    /**
     * This method clear the current CodeEditView displayed
     */
    public void clearContent() {
        codeEditViewContainer.removeAllViews();
    }
}
