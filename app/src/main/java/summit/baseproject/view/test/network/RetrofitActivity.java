package summit.baseproject.view.test.network;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import summit.baseproject.R;
import summit.baseproject.databinding.ActivityRetrofitBinding;
import summit.baseproject.view.BaseActivity;

public class RetrofitActivity extends BaseActivity {

    ActivityRetrofitBinding mView;
    APIClient mApi;
    private static final String marvelImgUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/04/MarvelLogo.svg/1200px-MarvelLogo.svg.png" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageuse);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_retrofit);

        setTitle("RetroFit");

        init();

    }
    private void setTitle( String title ){
        mView.topBar.tvToolBarTitle.setText( title );
    }
    private void init(){


        mView.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgress();

                dataCall();

            }
        });


        mView.btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgress();

                openDataCall();

            }
        });



    }

    private void openDataCall(){

        //url : http://openapi.epost.go.kr/postal/retrieveLotNumberAdressAreaCdService/retrieveLotNumberAdressAreaCdService/getSiGunGuList?serviceKey=aVpbbjeu38%2BHGYmLQrB3P%2BWRaOfQ0XEvRpAWKuyk%2Fmt5RZogEbG1gBMIJwJTpPrist6WEqE3qqPBI8JqwORNYA%3D%3D&brtcCd=%EC%84%9C%EC%9A%B8&type=json
        APIClient.DATA_API.getNewAddress("aVpbbjeu38%2BHGYmLQrB3P%2BWRaOfQ0XEvRpAWKuyk%2Fmt5RZogEbG1gBMIJwJTpPrist6WEqE3qqPBI8JqwORNYA%3D%3D",
                "서울").enqueue(new Callback<NewAddressVO>() {
            @Override
            public void onResponse(Call<NewAddressVO> call, Response<NewAddressVO> response) {
                dismissProgress();
                if( response.body() instanceof NewAddressVO ){
                    NewAddressVO tmpVO = response.body();
                    String tmpString = "";
                    for( int i = 0 ; i < tmpVO.getSigunguList().size() ; i++ ){
                        tmpString += tmpVO.getSigunguList().get( i ).getSignguCd() + "\n";
                    }

                    mView.tvResult.setText( tmpString );
                }

            }

            @Override
            public void onFailure(Call<NewAddressVO> call, Throwable t) {
                dismissProgress();
                mView.tvResult.setText( t.toString() );
            }
        });
    }


    private void dataCall(){

        APIClient.RETRO_API.repoContributors("square", "retrofit").enqueue(new Callback<List<ContributorVO>>() {
            @Override
            public void onResponse(Call<List<ContributorVO>> call, Response<List<ContributorVO>> response) {
                Log.d( TAG, "!23");
                dismissProgress();
                mView.tvResult.setText( response.body().toString() );
            }

            @Override
            public void onFailure(Call<List<ContributorVO>> call, Throwable t) {
                Log.d( TAG, "321");
                dismissProgress();
                mView.tvResult.setText( t.toString() );
            }
        });
    }

}
