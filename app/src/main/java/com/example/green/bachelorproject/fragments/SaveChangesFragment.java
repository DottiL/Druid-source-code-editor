package com.example.green.bachelorproject.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.green.bachelorproject.R;
import com.example.green.bachelorproject.internalFileSystem.InternalFile;

/**
 * Created by Green on 17/04/15.
 */
public class SaveChangesFragment extends DialogFragment {
    public interface SaveChangesDialogListener {
        public void onChangesDialogSaveClick(DialogFragment dialog);
        public void onChangesDialogCancelClick(DialogFragment dialog);
        public void onChangesDialogNoSaveClick(DialogFragment dialog);
    }

    private SaveChangesDialogListener listener;
    private InternalFile file;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.save_changes_fragment_layout, (ViewGroup) getActivity().findViewById(R.id.save_changes_fragment_root));

        Button noSave = (Button) v.findViewById(R.id.save_changes_nosave_button);
        Button save = (Button) v.findViewById(R.id.save_changes_save_button);
        Button cancel = (Button) v.findViewById(R.id.save_changes_cancel_button);

        noSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChangesDialogNoSaveClick(SaveChangesFragment.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChangesDialogSaveClick(SaveChangesFragment.this);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChangesDialogCancelClick(SaveChangesFragment.this);
            }
        });

        builder.setView(v);

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (SaveChangesDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public void setFile(InternalFile file) {
        this.file = file;
    }

    public InternalFile getFile() {
        return file;
    }
}
