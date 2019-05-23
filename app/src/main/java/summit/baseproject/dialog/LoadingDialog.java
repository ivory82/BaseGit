package summit.baseproject.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;

import summit.baseproject.R;
import summit.baseproject.databinding.DialogLoadingBinding;

public class LoadingDialog extends Dialog {

    private static LoadingDialog loadingDialog = null;

    private static DialogLoadingBinding mView;


    public LoadingDialog(Context context) {
        super(context);
    }

    public static LoadingDialog createDialog(Context context) {

        loadingDialog = new LoadingDialog(context);


        mView = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_loading, null , false );
        loadingDialog.setContentView( mView.getRoot() );

        loadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));

//        loadingDialog.getWindow().requestFeature( Window.FEATURE_NO_TITLE);
        return loadingDialog;
    }

    @Override
    public void show() {
        super.show();
        if( !mView.lottie.isAnimating() ){
            mView.lottie.playAnimation();
            mView.lottie.loop( true );
        }
    }

    @Override
    public void dismiss(){
        super.dismiss();
        if( mView.lottie.isAnimating() ){
            mView.lottie.cancelAnimation();
        }
    }
}
