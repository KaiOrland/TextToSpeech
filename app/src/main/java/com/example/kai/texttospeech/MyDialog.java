package com.example.kai.texttospeech;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by kayor on 2/3/2018.
 */

public class MyDialog extends DialogFragment implements View.OnClickListener {

    private Button set, cancel;
    public EditText setInfo;
    private OnCompleteListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_dialog, null);//inflate the layout
        set = (Button)view.findViewById(R.id.setBtn);//get all layout elements
        cancel = (Button)view.findViewById(R.id.cancelBtn);
        setInfo = (EditText)view.findViewById(R.id.setInfo);
        set.setOnClickListener(this);//set onclick listeners
        cancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnCompleteListener)context;//register the listener
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){//switch on which button was clicked
            case R.id.setBtn://send info if set button has been clicked
                this.mListener.onComplete(setInfo.getText().toString());
                dismiss();
                break;
            case R.id.cancelBtn://close the dialog
                dismiss();
                break;
        }
    }

    public static interface OnCompleteListener {//interface to be implemented by preferences activity
        public abstract void onComplete(String input);
    }

}
