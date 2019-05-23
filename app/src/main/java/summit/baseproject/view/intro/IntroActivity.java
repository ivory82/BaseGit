package summit.baseproject.view.intro;

import android.os.Bundle;
import android.os.Handler;

import summit.baseproject.BaseApplication;
import summit.baseproject.BuildConfig;
import summit.baseproject.R;
import summit.baseproject.view.BaseActivity;
import summit.baseproject.view.main.MainActivity;
import summit.baseproject.view.test.TestActivity;

public class IntroActivity extends BaseActivity {

    static final int TIME_DELAY = 500;


    Handler mNextHandler = new Handler();
    Runnable mNextRunable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

//        ShowToast("BuildConfig.DEBUG: " + BuildConfig.DEBUG );
//        ShowToast("DEBUG: " + BaseApplication.DEBUG_MODE );

//        mNextRunable = new Runnable() {
//            @Override
//            public void run() {
//                startNextActivity( TestActivity.class , true);
//                mNextHandler = null;
//            }
//        };
//
//        mNextHandler.postDelayed( mNextRunable, TIME_DELAY );

        //구글 권장방식으로 인트로 구현 변경
        startNextActivity( TestActivity.class , true);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if( mNextHandler != null ){
//            mNextHandler.removeCallbacks( mNextRunable);
//        }
    }




}
