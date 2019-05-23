package summit.baseproject.view.test.webview;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;

import summit.baseproject.R;
import summit.baseproject.databinding.ActivityWebviewBinding;
import summit.baseproject.util.FileManager;
import summit.baseproject.view.BaseActivity;

public class WebviewActivity extends BaseActivity {

    ActivityWebviewBinding mView;
    WebView mChildWebView;

    // 다운로드 완료 Receiver
    private BroadcastReceiver downloadReceiver;

    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        init();
    }

    private void init(){
        webViewSetting();
        mView.wvMain.loadUrl("http://www.naver.com");
        Log.d( TAG, "Current start = " + System.currentTimeMillis() );
    }

    private void webViewSetting(){

        mView.wvMain.setDownloadListener(downloadListener);
        mView.wvMain.setWebViewClient( new CustomWebViewClient() );
        mView.wvMain.setWebChromeClient(new CustomWebCromeClient());
        WebSettings webSettings = mView.wvMain.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAppCacheEnabled(true);
        //html5 local storage 사용설정
        webSettings.setDomStorageEnabled(true);
        //디버깅
        WebView.setWebContentsDebuggingEnabled(true);

    }

    DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            try {
                MimeTypeMap mtm = MimeTypeMap.getSingleton();
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri downloadUri = Uri.parse(url);

                final String fileName = URLDecoder.decode(downloadUri.getQueryParameter("ORIG_FILE_NM1"), "UTF-8");

                // MIME Type을 확장자를 통해 예측한다.
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
                String mimeType2 = mtm.getMimeTypeFromExtension(fileExtension);

                // Download 디렉토리에 저장하도록 요청을 작성
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                request.setTitle(fileName);
                request.setDescription(url);
                request.setMimeType(mimeType2);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                path.mkdirs();

                IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                downloadReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
//                        Toast.makeText(MainActivity.this, fileName + getString( R.string.newline_download_complete ), Toast.LENGTH_SHORT).show();
                        ShowToast( fileName + getString( R.string.newline_download_complete ) );
                        unregisterReceiver(downloadReceiver);
                    }
                };
                registerReceiver(downloadReceiver, completeFilter);

                downloadManager.enqueue(request);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString() );
                setPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() { }
                    @Override
                    public void onPermissionDenied(ArrayList<String> arrayList) { }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

        }
    };

    public void closeChildWebView() {
        if( mChildWebView != null) {
            mChildWebView.destroy();
            mView.lyRoot.removeView(mChildWebView);
            mChildWebView = null;
        }
    }

    PermissionListener writePermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //read, write 퍼미션 체크
            try{
                if( mUrl.contains("url=")){
                    Uri tempUri = Uri.parse(mUrl);
                    onDownloadStart( tempUri.getQueryParameter("url") );
                }
            } catch (Exception e ){
                e.printStackTrace();
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            finish();
        }
    };

    PermissionListener cameraPermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //카메라 퍼미션 체크 후 이동
//            Toast.makeText(MainActivity.this, "CAMERA", Toast.LENGTH_SHORT).show();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(cameraIntent);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> arrayList) {}
    };

    @Override
    public void onBackPressed() {

        if( mChildWebView != null ){
            if (mChildWebView.canGoBack()) {
                mChildWebView.goBack();
            } else {
                closeChildWebView();
            }
        }
        else if( mView.wvMain.canGoBack() ){
            mView.wvMain.goBack();
        } else {
            finish();
        }

    }

    private class CustomWebCromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            // Geolocation API 사용
            callback.invoke(origin, true, false);
        }

        @Override
        public void onCloseWindow(WebView w) {
            super.onCloseWindow(w);
            finish();
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {

            mChildWebView = new WebView(view.getContext());
            mChildWebView.setWebViewClient(new CustomWebViewClient());

            mChildWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onCloseWindow(WebView window) {
                    super.onCloseWindow(window);
                    closeChildWebView();
                }
            });

            final WebSettings settings = mChildWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setPluginState(WebSettings.PluginState.ON);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);

            mChildWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            mView.lyRoot.addView(mChildWebView);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mChildWebView);
            resultMsg.sendToTarget();
            return true;
        }

    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            //페이지 끝날 때 호출
            Log.d( TAG, "Current end " + System.currentTimeMillis() );
            super.onPageFinished(view, url);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url != null && url.indexOf("tel:") > -1) {
                Uri phoneNum = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_DIAL, phoneNum));
                return true;

            } else if (url != null && url.indexOf("skbizmall://startCamera") > -1) {
                setPermission( cameraPermissionListener, Manifest.permission.CAMERA );
                return true;

            } else if (url != null && url.indexOf("skbizmall://openFile") > -1) {

                mUrl = url;

                setPermission( writePermissionListener, Manifest.permission.WRITE_EXTERNAL_STORAGE );
                return true;
            } else if (url != null && url.indexOf("skbizmall://outLink") > -1) {

                //Outlink
                try {
                    if( url.contains("url=")){
                        Uri tempUri = Uri.parse(url);
                        Uri outLinkUri = Uri.parse( tempUri.getQueryParameter("url") );
                        Intent intent = new Intent(Intent.ACTION_VIEW, outLinkUri);
                        startActivity(intent);
                    }
                } catch ( Exception e ){
                    e.printStackTrace();
                }


                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

    }

    public void onDownloadStart(String url ) {
        try {
            MimeTypeMap mtm = MimeTypeMap.getSingleton();
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(url);

            String[] fileNameList = url.split("/");

            final String fileName;

            try {
                fileName = fileNameList[fileNameList.length-1 ];
            } catch ( Exception e ){
                return;
            }


//            final String fileName = URLDecoder.decode(downloadUri.getQueryParameter("ORIG_FILE_NM1"), "UTF-8");

            // MIME Type을 확장자를 통해 예측한다.
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
            final String mimeType = mtm.getMimeTypeFromExtension(fileExtension);

            // Download 디렉토리에 저장하도록 요청을 작성
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setTitle(fileName);
            request.setDescription(url);
            request.setMimeType(mimeType);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();
            path.mkdirs();


            if( FileManager.isDownloadFileExists( path.getAbsolutePath()+"/"+fileName) ){
                FileManager.deleteDownloadFile( path.getAbsolutePath()+"/"+fileName );
            }

            IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            downloadReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    unregisterReceiver(downloadReceiver);

                    if( !mAct.isDestroyed() ){
                        ShowToast( fileName + getString( R.string.newline_download_complete ) );
                        FileManager.openFile( mCtx, path.getAbsolutePath(), fileName );
                    }

                }
            };
            registerReceiver(downloadReceiver, completeFilter);

            downloadManager.enqueue(request);

        } catch (Exception e) {
            e.printStackTrace();
            ShowToast( getString( R.string.download_failed ));
            setPermission(new PermissionListener() {
                @Override
                public void onPermissionGranted() {

                }

                @Override
                public void onPermissionDenied(ArrayList<String> arrayList) {

                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
}