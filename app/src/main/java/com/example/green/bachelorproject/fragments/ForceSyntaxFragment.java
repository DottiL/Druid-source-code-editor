package com.example.green.bachelorproject.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.green.bachelorproject.R;

import java.util.ArrayList;
import java.util.List;

import utils.Syntax;

/**
 * Created by Green on 12/05/15.
 */
public class ForceSyntaxFragment extends DialogFragment {
    ForceSyntaxDialogListener listener;

    public interface ForceSyntaxDialogListener {
        public void onForceSyntaxDialogClick(DialogFragment dialog);
    }

    private List<Syntax> syntaxList;
    private int selectedSyntax;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.force_syntax_fragment_layout, (ViewGroup) getActivity().findViewById(R.id.force_syntax_fragment_root));
        ListView list = (ListView) v.findViewById(R.id.force_syntax_list);
        List<String> syntaxName = new ArrayList<>();
        for(Syntax s: syntaxList) {
            syntaxName.add(s.getLanguageName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, syntaxName);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSyntax = position;
                listener.onForceSyntaxDialogClick(ForceSyntaxFragment.this);
            }
        });

        builder.setView(v);
        return builder.create();
    }

    public Syntax getSelectedSyntax() {
        return syntaxList.get(selectedSyntax);
    }

    public void setSyntaxList(List<Syntax> syntaxList) {
        this.syntaxList = syntaxList;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ForceSyntaxDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
