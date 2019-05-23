package summit.baseproject.view.test.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface APIClient {

    String RETRO_URL = "https://api.github.com/";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RETRO_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    APIClient RETRO_API = retrofit.create( APIClient.class );

    @GET("repos/{owner}/{repo}/contributors")
    Call<List<ContributorVO>> repoContributors(@Path("owner") String owner, @Path("repo") String repo);


    String DATA_URL = "http://openapi.epost.go.kr/postal/retrieveLotNumberAdressAreaCdService/retrieveLotNumberAdressAreaCdService/";
    Retrofit opendata = new Retrofit.Builder()
            .baseUrl(DATA_URL)
            .addConverterFactory( SimpleXmlConverterFactory.createNonStrict(new Persister(new AnnotationStrategy() ) ) )
            .build();
    APIClient DATA_API = opendata.create( APIClient.class );


    @GET("getSiGunGuList")
    Call<NewAddressVO> getNewAddress(@Query(value = "serviceKey", encoded = true ) String key,
                                  @Query("brtcCd") String brtcCd );

}
