package summit.baseproject.view.test.image;

import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import summit.baseproject.R;
import summit.baseproject.databinding.ActivityImageuseBinding;
import summit.baseproject.view.BaseActivity;

public class ImageUseActivity extends BaseActivity {

    ActivityImageuseBinding mView;

    private static final String marvelImgUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/04/MarvelLogo.svg/1200px-MarvelLogo.svg.png" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageuse);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_imageuse);

        init();

    }

    private void init(){

        setTitle("이미지예제");

        mView.btnGlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mView.ivGlide.getDrawable() == null ){
                    Glide.with( mCtx ).load(marvelImgUrl).into( mView.ivGlide );
                } else {
                    Glide.with( mCtx ).clear( mView.ivGlide );
                }
            }
        });

        mView.btnCropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Glide.with( mCtx )
                        .asBitmap()
                        .load( marvelImgUrl )
                        .into( new BitmapImageViewTarget( mView.ivCircle )
                        {
                            @Override
                            protected void setResource( Bitmap resource )
                            {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create( mCtx.getResources(), resource );
                                circularBitmapDrawable.setCircular( true );
                                mView.ivCircle.setImageDrawable( circularBitmapDrawable );
                            }
                        } );

            }
        });
    }

    private void setTitle( String title ){
        mView.topBar.tvToolBarTitle.setText( title );
    }
}
