package summit.baseproject.view.test.db;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import io.realm.Realm;
import io.realm.RealmResults;
import summit.baseproject.R;
import summit.baseproject.databinding.ActivityRealmBinding;
import summit.baseproject.view.BaseActivity;
import summit.baseproject.view.test.db.vo.UserVO;

public class RealmActivity extends BaseActivity {

    Realm mRealm;
    ActivityRealmBinding mView;
    SharedPrefHelper mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_realm);

        setTitle("Realm");
        realm_init();
        sharedpref_init();
    }

    private void setTitle( String title ){
        mView.topBar.tvToolBarTitle.setText( title );
    }

    private void realm_init(){
        mRealm = Realm.getDefaultInstance();

        //insert
        mView.btnSave.setOnClickListener((View v) ->{
            if( mView.etId.getText().toString().trim().length()
                    + mView.etPw.getText().toString().trim().length() > 0 ){
                mRealm.beginTransaction();

                //생성시 pk 같이 대입
                UserVO tmpUser = mRealm.createObject(UserVO.class, mView.etId.getText().toString() );
                //obj에 값 대입 할 때 pk는 빼고 설정
                tmpUser.setPw( mView.etPw.getText().toString() );
                mRealm.commitTransaction();
            } else {
                ShowToast("빈 칸을 채워주세요.");
            }
        });

        //select
        mView.btnLoad.setOnClickListener((View v) ->{
            RealmResults<UserVO> userList = mRealm.where( UserVO.class ).findAll();

            if( userList.size() > 0 ){
                ShowToast( userList.size() + "건 조회");
            }

            String tmpString = "";
            for( UserVO tmpUserVO : userList ){
                tmpString += "id :" + tmpUserVO.getId() + "pw: " + tmpUserVO.getPw() +"\n";
            }

            if( tmpString.length() <= 0 ){
                mView.tvResult.setText( "데이터가 없습니다." );
            } else {
                mView.tvResult.setText( tmpString );
            }
        });

        mView.btnClear.setOnClickListener((View v) ->{
            //permission act 이동
            if( mView.etId.getText().toString().length() <= 0 ){
                ShowToast("삭제할 아이디를 입력해주세요.");
                return;
            }

            mRealm.beginTransaction();
            UserVO tmpUserVO = mRealm.where( UserVO.class ).equalTo("id", mView.etId.getText().toString() ).findFirst();

            if( tmpUserVO == null ){
                ShowToast("데이터가 없습니다.");
            } else {
                mView.tvResult.setText(  "id :" + tmpUserVO.getId() + "pw: " + tmpUserVO.getPw() );
                ShowToast("위 데이터가 삭제되었습니다.");
            }
            mRealm.commitTransaction();
        });



    }

    private void sharedpref_init(){
        mSharedPref = SharedPrefHelper.getInstance();

        mView.btnSave2.setOnClickListener( view ->{
            ShowToast("아이디와 패스워드가 저장됨.");
            mSharedPref.setPrevId( mCtx, mView.etId.getText().toString() );
            mSharedPref.setPrevPw( mCtx, mView.etPw.getText().toString() );
        });


        mView.btnLoad2.setOnClickListener( view ->{
            ShowToast("아이디와 패스워드가 로드됨.");
            mView.etId.setText( mSharedPref.getPrevId( mCtx ) );
            mView.etPw.setText( mSharedPref.getPrevPw( mCtx ) );
        });

        mView.btnClear2.setOnClickListener( this::sharedPref_allClear );
    }

    public void sharedPref_allClear( View view ){
        ShowToast("SharePref에 저장된 값 초기화");
        mSharedPref.allClear( mCtx );
        mView.etId.setText( "" );
        mView.etPw.setText( "" );
    }

}


