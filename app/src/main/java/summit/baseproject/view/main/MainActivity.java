package summit.baseproject.view.main;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import summit.baseproject.R;

import summit.baseproject.databinding.ActivityMainBinding;
import summit.baseproject.view.BaseActivity;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mView = DataBindingUtil.setContentView(this, R.layout.activity_main);

        init();
    }

    private void init(){
        visibleView( mView.topBar.ivMenu );

        mView.topBar.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call presenter
            }
        });
    }


}
