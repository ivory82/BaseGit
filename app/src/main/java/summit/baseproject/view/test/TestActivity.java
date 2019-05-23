package summit.baseproject.view.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.gun0912.tedpermission.PermissionListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import summit.baseproject.R;
import summit.baseproject.databinding.ActivityTestBinding;
import summit.baseproject.util.BackPressCloseHandler;
import summit.baseproject.view.BaseActivity;
import summit.baseproject.view.test.db.RealmActivity;
import summit.baseproject.view.test.image.ImageUseActivity;
import summit.baseproject.view.test.motion.MotionActivity;
import summit.baseproject.view.test.network.RetrofitActivity;
import summit.baseproject.view.test.permission.PermissionActivity;
import summit.baseproject.view.test.popup.PopupActivity;
import summit.baseproject.view.test.webview.WebviewActivity;

import static android.content.Context.SENSOR_SERVICE;

public class TestActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    ActivityTestBinding mView;
    BackPressCloseHandler backPressCloseHandler;

    static final int SELECT_PHOTO = 1001;
    static final int INPUT_FILE_REQUEST_CODE = 1002;

    ShortcutManager mShortcutManager;

    ArrayList<ImageFileVO> mImgList = new ArrayList<>();

    //Shake
    SensorManager mSensorManager;
    Sensor mAccelerometer;

    private long mShakeTime;
    private static final int SHAKE_SKIP_TIME = 500;
    //중력가속도
//    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final float SHAKE_THRESHOLD_GRAVITY = 1.5F;
    private int mShakeCount = 0;

    //브로드 캐시트 리시버
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_test);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        batteryCheck();

        init();

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void batteryCheck(){
        Intent batteryIntent = this.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int rawlevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        double scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        double level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = rawlevel / scale;
        }

        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        String result = "";

        result += "rawlevel: " + rawlevel + "\n";
        result += "scale: " + scale + "\n";
        result += "level: " + level + "\n";
        result += "isCharging: " + isCharging + "\n";
        result += "chargePlug: " + chargePlug + "\n";
        result += "usbCharge: " + usbCharge + "\n";
        result += "acCharge: " + acCharge + "\n";

        ShowToast( result );

        Log.d(TAG, "rawlevel: " + rawlevel);
        Log.d(TAG, "scale: " + scale);
        Log.d(TAG, "level: " + level);

        Log.d(TAG, "isCharging: " + isCharging);
        Log.d(TAG, "chargePlug: " + chargePlug);
        Log.d(TAG, "usbCharge: " + usbCharge);
        Log.d(TAG, "acCharge: " + acCharge);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener( this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL );

        IntentFilter intentFilter = new IntentFilter( Intent.ACTION_POWER_DISCONNECTED );
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ShowToast("액티비티 : 충전해제");

            }
        };
        this.registerReceiver(mReceiver, intentFilter);
    }

    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener( this );
        this.unregisterReceiver( mReceiver );
    }

    @Override
    public void onSensorChanged( SensorEvent event ){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
            float axisX = event.values[ 0 ];
            float axisY = event.values[ 1 ];
            float axisZ = event.values[ 2 ];

            float gravityX = axisX / SensorManager.GRAVITY_EARTH;
            float gravityY = axisY / SensorManager.GRAVITY_EARTH;
            float gravityZ = axisZ / SensorManager.GRAVITY_EARTH;

            Float f = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ;
            double squaredD = Math.sqrt( f.doubleValue() );
            float gForece = ( float ) squaredD;
            if( gForece > SHAKE_THRESHOLD_GRAVITY ){
                long currentTime = System.currentTimeMillis();
                if( mShakeTime + SHAKE_SKIP_TIME > currentTime ){
                    return;
                }
                mShakeTime = currentTime;
                mShakeCount++;
                Log.d( TAG, "onSensorChanged: Shake 발생" + mShakeCount );
                if( mShakeCount % 3  == 0 ){
                    mView.ivSensor.setBackgroundColor( Color.RED );
                } else if( mShakeCount % 3  == 1 ){
                    mView.ivSensor.setBackgroundColor( Color.YELLOW );
                } else {
                    mView.ivSensor.setBackgroundColor( Color.BLUE );
                }
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected static double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    private void init() {

        setTitle("타이틀");



        visibleView(mView.topBar.ivMenu);


        mView.btnDynamicLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse("https://base-53bed.firebaseapp.com"))
                        .setDomainUriPrefix("https://example.page.link")
                        // Open links with this app on Android
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                        .buildDynamicLink();

                Uri dynamicLinkUri = dynamicLink.getUri();
                ShowToast( dynamicLink.toString() );
            }
        });

        mView.btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowToast("충전 터치");
                batteryCheck();
            }
        });


        mView.btnSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShowToast("센서 터치");

                ShowToast( "dist:" + calculateDistance( 4, -62 ) ) ;
            }
        });

        //네비게이션바 Open/Close
        mView.topBar.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mView.mainDrawLy.isDrawerOpen(GravityCompat.START)) {
                    mView.mainDrawLy.closeDrawer(GravityCompat.START);
                } else {
                    mView.mainDrawLy.openDrawer(GravityCompat.START);
                }
            }
        });

        mView.btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(ImageUseActivity.class, false);
            }
        });

        mView.btnPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popup act 이동
                startNextActivity(PopupActivity.class, false);
            }
        });

        mView.btnPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //permission act 이동
                startNextActivity(PermissionActivity.class, false);
            }
        });

        mView.btnPermission2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //permission act 이동
//                startNextActivity(PermissionActivity.class, false);


            }
        });


        mView.btnDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPermission(rwStorageListener, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        mView.btnNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(RetrofitActivity.class, false);
            }
        });

        mView.btnCreateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvtEncodingImage();
//                mImgList.clear();
//                imageChooser();
            }
        });

        mView.btnWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(WebviewActivity.class, false);
            }
        });
        mView.btnMotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(MotionActivity.class, false);

            }
        });

        mView.btnAppShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    // INIT SHORTCUT MANAGER
                    mShortcutManager = getSystemService(ShortcutManager.class);
                    boolean noDynamicShortcuts = mShortcutManager.getDynamicShortcuts().size() == 0;
                    if (noDynamicShortcuts) {
                        ShowToast("숏컷 생성 ");
                        createDynamicShortcut();
                    } else {
                        ShowToast("다이나밋 숏컷 개수: " + mShortcutManager.getDynamicShortcuts().size());
                    }
                } else {
                    ShowToast("숏컷기능은 안드로이드 7.1.1이상부터 지원합니다.");
                }

            }
        });

        mView.btnAppPinShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mShortcutManager = getSystemService(ShortcutManager.class);
                    createPinShortcut();
                } else {
                    ShowToast("고정숏컷기능은 안드로이드 8.0 이상부터 지원합니다.");
                }

            }
        });
        mView.btnGPS.setOnClickListener(
                (View.OnClickListener) new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        getGpsSatelliteCount();

                        //gps테스트
//                        startNextActivity();
//                        startNextActivity(LocationActivity.class, false);
                        ShowToast(getWifiMacAddress());
                        Log.i(TAG, getWifiMacAddress());
                    }
                }
        );


        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    public InputFilter textSetFilter(String lang) {
        Pattern ps;

        if (lang.equals("kor")) {
            ps = Pattern.compile("^[ㄱ-ㅣ가-힣\\s]*$"); //한글 및 공백문자만 허용
        } else {
            ps = Pattern.compile("[a-zA-Z\\s-]*$"); //영어 및 하이픈 문자만 허용
        }

        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        };

        return filter;
    }

    public static String getWifiMacAddress() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName))
                    continue;
                byte[] mac = intf.getHardwareAddress();
                if (mac == null)
                    return "";

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }

                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString() );
        }

        return "";
    }


    int j = 0;

    public static String cvtDateTime(long timeMill) {
        String strDate = "";

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMill);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        strDate = sdf.format(cal.getTime());

        return strDate;
    }

    public static String getCurrentTime() {
        return cvtDateTime(System.currentTimeMillis());
    }

    @SuppressLint("MissingPermission")
    private int getGpsSatelliteCount() {
        LocationManager locManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        final GpsStatus gs = locManager.getGpsStatus(null);

        int i = 0;
        final Iterator<GpsSatellite> it = gs.getSatellites().iterator();

        while (it.hasNext()) {
            GpsSatellite satellite = it.next();

            // [수정 : 2013/10/25]
            // 단순 위성 갯수가 아니라 사용할 수 있게 잡히는 위성의 갯수가 중요하다.
            if (satellite.usedInFix()) {
                j++; // i 값 보다는 이 값이 GPS 위성 사용 여부를 확인하는데 더 중요하다.
            }
            i++;
        }

        toast("i: " + i + ", j : " + j);

        return j;
    }


    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void createDynamicShortcut() {
        ShortcutInfo dynamicShortcut;

        Intent intent = new Intent(Intent.ACTION_VIEW, null, this, RealmActivity.class);

        dynamicShortcut = new ShortcutInfo.Builder(this, "id1")
                .setShortLabel(getString(R.string.dyn_shortcut_short_label1))
                .setLongLabel(getString(R.string.dyn_shortcut_long_label1))
                .setIcon(Icon.createWithResource(this, R.drawable.db))
                .setIntent(intent)
                .build();

        mShortcutManager.setDynamicShortcuts(Arrays.asList(dynamicShortcut));
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createPinShortcut() {
        // INIT SHORTCUT MANAGER
        Intent intent = new Intent(Intent.ACTION_VIEW, null, mCtx, RetrofitActivity.class);

        if (mShortcutManager.isRequestPinShortcutSupported()) {
            ShortcutInfo pinShortcutInfo = new ShortcutInfo.Builder(mCtx, "my-shortcut")
                    .setShortLabel(getString(R.string.pin_shortcut_short_label1))
                    .setLongLabel(getString(R.string.pin_shortcut_long_label1))
                    .setIcon(Icon.createWithResource(mCtx, R.drawable.pin))
                    .setIntent(intent)
                    .build();

            Intent pinnedShortcutCallbackIntent = mShortcutManager.createShortcutResultIntent(pinShortcutInfo);
            PendingIntent successCallback = PendingIntent.getBroadcast(mCtx, 0, pinnedShortcutCallbackIntent, 0);

            mShortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.getIntentSender());

        } else {
            ShowToast("해당 디바이스의 런처에서 고정숏컷기능이 지원되지 않습니다.");
        }
    }

    private File mLastPhotoFile;
    boolean bDelLastImgFile = true;
    private String mCameraPhotoPath;

    public void imageChooser() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                mLastPhotoFile = createImageFile2();
                bDelLastImgFile = true;
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Unable to create Image File");
            }

            // Continue only if the File was successfully created
            if (mLastPhotoFile != null) {
                //old
//                mCameraPhotoPath = "file:" + mLastPhotoFile.getAbsolutePath();
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mLastPhotoFile));

                Uri photoURI = FileProvider.getUriForFile(this, "summit.baseproject.fileprovider", mLastPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);


            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //테스트 여러이미지 선택
        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
        String imageFileName = "JPEG_" + timeStamp;
        Log.d(TAG, "imageFileName: " + imageFileName);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File imageFile = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",  /* suffix */
//                storageDir      /* directory */
//        );
//        imageFile.deleteOnExit();

        File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + imageFileName + ".jpg");
        if( ! imageFile.createNewFile() ){
            ShowToast(" 파일 생성 실패 ");
        }

        return imageFile;
    }

    private File createImageFile2() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCameraPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void cvtEncodingImage() {

        //

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);


//        String path = "/storage/emulated/0/DCIM/Camera/20181023_180645.jpg";


//        try{
//            InputStream inputStream = new FileInputStream( path );//You can get an inputStream using any IO API
//            byte[] bytes;
//            byte[] buffer = new byte[8192];
//            int bytesRead;
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            try {
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    output.write(buffer, 0, bytesRead);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            bytes = output.toByteArray();
//            String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
//        }
//        catch ( Exception e ){
//            e.toString();
//        }


    }

    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    PermissionListener rwStorageListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //permission act 이동
            startNextActivity(RealmActivity.class, false);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };

    private void setTitle(String title) {
        mView.topBar.tvToolBarTitle.setText(title);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_menu1) {
            ShowToast("네비1");
        } else if (id == R.id.nav_menu2) {
            ShowToast("네비1");
        }

        mView.mainDrawLy.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Check that the response is a good one
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    //카메라 찍고 난후
                    if (mCameraPhotoPath != null) {
                        bDelLastImgFile = false;
                        FileToBase64String(mCameraPhotoPath);
                    }
                } else {
                    //갤러리 선택 후
                    tempFileDelete(mLastPhotoFile);
                    bDelLastImgFile = false;
                    if (data.getData() != null) {

                        String dataString = data.getDataString();
                        if (dataString != null) {
                            String wholeID = DocumentsContract.getDocumentId(Uri.parse(dataString.replace("%3A", ":")));
                            // Split at colon, use second item in the array
                            String id = wholeID.split(":")[1];
                            String[] column = {MediaStore.Images.Media.DATA};
                            // where id is equal to
                            String sel = MediaStore.Images.Media._ID + "=?";
                            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
                            String filePath = "";
                            int columnIndex = cursor.getColumnIndex(column[0]);
                            if (cursor.moveToFirst()) {
                                filePath = cursor.getString(columnIndex);
                            }
                            cursor.close();
                            FileToBase64String(filePath);
                        }
                    } else {
                        if (data.getClipData() != null) {

                            ClipData clipData = data.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item image = clipData.getItemAt(i);
                                Uri uri = image.getUri();

                                // Will return "image:x*"
                                String wholeID = DocumentsContract.getDocumentId(uri);
                                // Split at colon, use second item in the array
                                String id = wholeID.split(":")[1];
                                String[] column = {MediaStore.Images.Media.DATA};
                                // where id is equal to
                                String sel = MediaStore.Images.Media._ID + "=?";
                                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
                                String filePath = "";
                                int columnIndex = cursor.getColumnIndex(column[0]);
                                if (cursor.moveToFirst()) {
                                    filePath = cursor.getString(columnIndex);
                                }
                                cursor.close();
                                FileToBase64String(filePath);
                            }
                        }
                    }
                }
            }
        } else if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {

            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            mImgList.clear();
            FileToBase64String(imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            mView.ivTest.setImageBitmap(bitmap);

//            Bitmap cmpBitmap = compressUriQuality(this, data.getData());
//            mView.ivTest2.setImageBitmap(cmpBitmap );

//            try {
//                Bitmap b = BitmapFactory.decodeStream(new FlushedInputStream(this.getContentResolver().openInputStream(pickedImage)));
//                mView.ivTest2.setImageBitmap(b);
//            } catch (Exception e ){
//                e.toString();
//            }


//            try {
//                Bitmap bmp = BitmapFactory.decodeStream(new PatchInputStream(getContentResolver().openInputStream(pickedImage)));
//
//                mView.ivTest2.setImageBitmap(bmp );
//            } catch (Exception e ){
//                e.toString();
//            }


//            String imagePath2 = "/storage/emulated/0/DCIM/Camera/after2.jpg";
//            Bitmap bitmap2 = BitmapFactory.decodeFile(imagePath2);
//            mView.ivTest2.setImageBitmap( bitmap2 );

        }

        Log.d(TAG, "mImgList.size(): " + mImgList.size());

    }

    public class PatchInputStream extends FilterInputStream {
        public PatchInputStream(InputStream in) {
            super(in);
        }

        public long skip(long n) throws IOException {
            long m = 0L;
            while (m < n) {
                long _m = in.skip(n - m);
                if (_m == 0L) break;
                m += _m;
            }
            return m;
        }
    }

    public static Bitmap compressUriQuality(Context mContext, Uri selectedImage) {
        InputStream imageStream = null;
        Bitmap bmp = null;
        try {
            imageStream = mContext.getContentResolver().openInputStream(
                    selectedImage);
            bmp = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);

            if (imageStream != null)
                imageStream.close();
            stream.close();
            stream = null;
        } catch (FileNotFoundException e) {
            Log.e( TAG, e.toString() );
            e.printStackTrace();
        } catch (IOException e) {
            Log.e( TAG, e.toString() );
        }
        return bmp;
    }

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }


    private void tempFileDelete(@Nullable File imgFile) {

        if (imgFile == null) {
            return;
        }

        if (imgFile.exists()) {
            Log.d(TAG, " imgFile exists");
            if( !imgFile.delete() ){
                Log.d(TAG, " imgFile delete fail");
            }
        } else {
            Log.d(TAG, " imgFile not exists ");
        }
    }

    public static void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath, String filename) {

        File file = new File(strFilePath);

        if (!file.exists())
            file.mkdirs();

        File fileCacheItem = new File(strFilePath + filename);

        try( OutputStream out = new FileOutputStream(fileCacheItem)) {
            if( !fileCacheItem.createNewFile()){
                Log.e( TAG, "create file failed");
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        } catch (Exception e) {
            Log.e( TAG, e.toString() );
        }
    }

    public final void notifyMediaStoreScanner(String path) {
        File file = new File(path);
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            Log.e( TAG, e.toString() );
        }
    }

    private void galleryAddPic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }


    private void galleryAddPic(String path) {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);


    }

    public void FileToBase64String(String path) {

        if (path.length() <= 0 || !path.contains("/")) {
            return;
        }

        galleryAddPic(path);
//        notifyMediaStoreScanner( path );
        //convert file to byte[]
        try {
            File file = new File(path);
            if (!file.exists()) {
                return;
            }

            String fileName = path.split("/")[path.split("/").length - 1];

            int size = (int) file.length();
            byte[] bytes = new byte[size];

            try( BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file)) ){
                buf.read(bytes, 0, bytes.length);
            } catch (Exception e ){
                Log.e( TAG, e.toString() );
            }

            String encodingString = Base64.encodeToString(bytes, Base64.DEFAULT);

            Log.d(TAG, encodingString);

            byte bytePic[] = null;
            try {
                bytePic = Base64.decode(encodingString, Base64.DEFAULT);
            } catch (Exception e) {
                Log.e( TAG, e.toString() );
            }

            String cvt = bytesToHex(bytePic);
            byte[] bytePic2 = hexStringToByteArray(cvt);

            if (bytePic2 != null) {
                ByteArrayInputStream imageStream = new ByteArrayInputStream(bytePic2);
                Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                imageStream.reset();
                mView.ivTest2.setImageBitmap(theImage); //Setting image to the image view
            }

            //convert byte[] to file
//            try (FileOutputStream fos = new FileOutputStream("/storage/emulated/0/DCIM/Camera/"+"after2.jpg")) {
//                fos.write( Base64.decode( encodingString, Base64.DEFAULT ) );
//                //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
//            }


//            Glide.with(this).load(encodingString).into(mView.ivTest2);

            mImgList.add(new ImageFileVO(fileName, encodingString));
            Log.d(TAG, "add : " + fileName + "size: " + encodingString.length());

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2 + 4];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        hexChars[hexChars.length - 4] = 'F';
        hexChars[hexChars.length - 3] = 'F';
        hexChars[hexChars.length - 2] = 'D';
        hexChars[hexChars.length - 1] = '9';


        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }


    @Override
    public void onDestroy() {

        if (bDelLastImgFile) {
            if (mLastPhotoFile != null) {
                if (mLastPhotoFile.exists()) {
                    if( !mLastPhotoFile.delete()){
                        Log.e( TAG, "delete file failed");
                    }
                }
            }
        }

        super.onDestroy();
    }


}
