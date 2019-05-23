package summit.baseproject.view.test;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;

import summit.baseproject.R;
import summit.baseproject.databinding.ActivityPopupBinding;
import summit.baseproject.view.BaseActivity;

public class StandardActivity extends BaseActivity {

    ActivityPopupBinding mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_motion);

    }

    private void init(){

    }
}
