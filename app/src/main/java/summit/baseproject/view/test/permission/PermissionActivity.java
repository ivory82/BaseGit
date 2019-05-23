package summit.baseproject.view.test.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.View;

import com.gun0912.tedpermission.PermissionListener;

import java.util.ArrayList;

import summit.baseproject.R;
import summit.baseproject.databinding.ActivityPermissionBinding;
import summit.baseproject.view.BaseActivity;

public class PermissionActivity extends BaseActivity {
    ActivityPermissionBinding mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        mView = DataBindingUtil.setContentView(this, R.layout.activity_permission);

        init();
    }

    private void init(){
        mView.btnChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowToast("전화,카메라 퍼미션 체크" + isPermissionGranted(  Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA ) );
            }
        });

        mView.btnReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        ShowToast( "퍼미션 허용");
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        ShowToast( "퍼미션 거부");
                    }
                }, Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA);
            }
        });
    }

    private boolean isPermissionGranted( String... Permisions ) {

        for( String permission : Permisions ){
            if( ContextCompat.checkSelfPermission(this, permission ) == PackageManager.PERMISSION_DENIED ){
                return false;
            }
        }

        return true;

    }
}
