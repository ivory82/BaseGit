package summit.baseproject.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;

import summit.baseproject.R;
import summit.baseproject.databinding.DialogProgressBinding;

public class ProgressDialog extends Dialog {

    private static ProgressDialog progressDialog = null;

    private static DialogProgressBinding mView;

    public ProgressDialog(Context context) {
        super(context);
    }

    public static ProgressDialog createDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        mView = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_progress, null , false );
        progressDialog.setContentView( mView.getRoot() );
        progressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));

        return progressDialog;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss(){
        super.dismiss();
    }

}
