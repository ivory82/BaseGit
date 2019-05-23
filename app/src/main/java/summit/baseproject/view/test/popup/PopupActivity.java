package summit.baseproject.view.test.popup;

import android.content.DialogInterface;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import summit.baseproject.R;
import summit.baseproject.databinding.ActivityPopupBinding;
import summit.baseproject.dialog.PopupDialog;
import summit.baseproject.view.BaseActivity;

public class PopupActivity extends BaseActivity {

    ActivityPopupBinding mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_popup);

        init();
    }

    private void init(){

        setTitle("팝업예제");

        mView.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupDialog.show( mCtx, "제목", "내용" );
            }
        });

        mView.btnDefault2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupDialog.show( mCtx, "제목", "내용" ,"확인", null, "취소", null );
            }
        });

        mView.btnEventListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupDialog.show(mCtx, "제목", "내용", "확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowToast("Click 확인");
                    }
                }, "취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowToast("Click 취소");
                    }
                });
            }
        });

        mView.btnProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
            }
        });

        mView.btnProgress2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImgProgress();
            }
        });

    }

    private void setTitle( String title ){
        mView.topBar.tvToolBarTitle.setText( title );
    }
}









