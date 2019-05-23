package summit.baseproject.view.test.motion;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import summit.baseproject.R;
import summit.baseproject.databinding.ActivityScrollingBinding;
import summit.baseproject.view.BaseActivity;

public class MotionActivity  extends BaseActivity {

    ActivityScrollingBinding mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.motion_10_coordinatorlayout);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_scrolling);
        init();
    }

    private void init(){

    }
}
