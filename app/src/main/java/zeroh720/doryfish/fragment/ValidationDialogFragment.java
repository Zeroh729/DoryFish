package zeroh720.doryfish.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import zeroh720.doryfish.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidationDialogFragment extends DialogFragment {
    public OnCreateViewListener onCreateViewListener;
    public TextView tv_popupContent;
    public Button btn_disapprove;
    public Button btn_approve;
    public Button btn_notnow;

    public ValidationDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_validation_dialog, container, false);
        tv_popupContent = (TextView)v.findViewById(R.id.tv_validationContent);
        btn_approve = (Button)v.findViewById(R.id.btn_like);
        btn_disapprove = (Button)v.findViewById(R.id.btn_dislike);
        btn_notnow = (Button)v.findViewById(R.id.btn_ntnow);

        if(onCreateViewListener != null)
            onCreateViewListener.doneLoading();
        return v;
    }

    public interface OnCreateViewListener{
        void doneLoading();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return d;
    }
}
