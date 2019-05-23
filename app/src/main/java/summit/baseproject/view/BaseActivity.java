package summit.baseproject.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import summit.baseproject.R;
import summit.baseproject.dialog.LoadingDialog;
import summit.baseproject.dialog.ProgressDialog;

public class BaseActivity extends AppCompatActivity {

    public static String TAG;


    LoadingDialog mLoadingDialog;
    ProgressDialog mProgressDialog;
    public Activity mAct;
    public Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAct = this;
        mCtx = this;
        TAG = getClass().getSimpleName();
        mLoadingDialog = LoadingDialog.createDialog( this );
        mProgressDialog = ProgressDialog.createDialog( this );
    }


    public void ShowToast( @Nullable String msg ){
        Toast.makeText( mCtx, msg, Toast.LENGTH_SHORT ).show();
    }

    public void toast( @Nullable String msg ){
        Toast.makeText( mCtx, msg, Toast.LENGTH_SHORT ).show();
    }
    public void longtoast( @Nullable String msg ){
        Toast.makeText( mCtx, msg, Toast.LENGTH_LONG ).show();
    }

    public void startNextActivity( @NonNull Class nextClass, @NonNull boolean bClose ){
        Intent intent = new Intent(mCtx, nextClass);
        startActivity(intent);
        if( bClose ){
            finish();
        }
    }

    public boolean isVisible( @NonNull View v ){

        if( v.getVisibility() == View.VISIBLE ) {
            return true;
        }
        return false;
    }

    public void visibleView( @NonNull View... views  ){

        for( View v : views) {
            if( v == null ) continue;
            v.setVisibility(View.VISIBLE);
        }
    }
    public void invisibleView( @NonNull View... views  ){
        for( View v : views) {
            if( v == null ) continue;
            v.setVisibility(View.INVISIBLE);
        }
    }
    public void goneView( @NonNull View... views ){
        for( View v : views){
            if( v == null ) continue;
            v.setVisibility( View.GONE );
        }
    }

    public void setPermission( @NonNull PermissionListener listener, @NonNull String... permissions){

        TedPermission.with(this)
                .setPermissionListener(listener)
                .setDeniedMessage(getString(R.string.permission_request))
                .setPermissions(permissions)
                .check();
    }

    public boolean isShowing(){
        if( mProgressDialog == null ){
            return false;
        }

        return mProgressDialog.isShowing();
    }

    public boolean isImgShowing(){
        if( mLoadingDialog == null ){
            return false;
        }

        return mLoadingDialog.isShowing();
    }

    public void showProgress(){
        if( mProgressDialog == null ){
            mProgressDialog = ProgressDialog.createDialog( mCtx );
        }

        if( mProgressDialog.isShowing() ){
            mProgressDialog.dismiss();
        }
        mProgressDialog.show();
    }

    public void showImgProgress(){
        if( mLoadingDialog == null ){
            mLoadingDialog = LoadingDialog.createDialog( mCtx );
        }

        if( mLoadingDialog.isShowing() ){
            mLoadingDialog.dismiss();
        }
        mLoadingDialog.show();
    }

    public void dismissProgress(){
        if( isDestroyed() ){
            return;
        }

        if( mProgressDialog == null || !mProgressDialog.isShowing()){
            return;
        }
        mProgressDialog.dismiss();
    }

    public void dismissImgProgress(){
        if( isDestroyed() ){
            return;
        }

        if( mLoadingDialog == null || !mLoadingDialog.isShowing()){
            return;
        }
        mLoadingDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( isImgShowing() ){
            dismissImgProgress();
        }

        if( isShowing() ){
            dismissProgress();
        }
    }
}






