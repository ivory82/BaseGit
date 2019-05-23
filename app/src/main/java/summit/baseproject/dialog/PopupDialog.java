package summit.baseproject.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import summit.baseproject.R;

public class PopupDialog extends AlertDialog {

    private static AlertDialog mAD;

    protected PopupDialog(Context context) {
        super(context, 0);
    }

    public static void show( Context context, String title, String msg ){

        init();

        AlertDialog.Builder adb = new AlertDialog.Builder( context );
        adb.setTitle(title)
                .setMessage( msg )
                .setPositiveButton( R.string.btn_ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        mAD = adb.create();
        mAD.show();
    }


    public static void show( Context context, String title, String msg, String posBtnName ){

        init();

        AlertDialog.Builder adb = new AlertDialog.Builder( context );
        adb.setTitle(title)
                .setMessage( msg )
                .setPositiveButton(posBtnName, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        mAD = adb.create();
        mAD.show();
    }

    public static void show( Context context, String title, String msg, String posBtnName, OnClickListener posListener ){

        init();

        AlertDialog.Builder adb = new AlertDialog.Builder( context );
        adb.setTitle(title)
                .setMessage( msg )
                .setPositiveButton(posBtnName, posListener );

        mAD = adb.create();
        mAD.show();
    }

    public static void show( Context context, String title, String msg, String posBtnName, OnClickListener posListener,
                             String negBtnName, OnClickListener negListener){

        init();

        AlertDialog.Builder adb = new AlertDialog.Builder( context );
        adb.setTitle(title)
                .setMessage( msg )
                .setPositiveButton(posBtnName, posListener )
                .setNegativeButton(negBtnName, negListener);


        mAD = adb.create();
        mAD.show();
    }


    private static void init(){
        if( mAD != null ){
            if( mAD.isShowing() ){
                mAD.dismiss();
            }
            mAD = null;
        }
    }


    public static void close(){
        if( mAD == null || !mAD.isShowing() ){
            mAD = null;
            return;
        }
        mAD.dismiss();
    }
}




















