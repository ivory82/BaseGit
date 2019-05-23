package summit.baseproject;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class BaseApplication extends Application {

    public static final String DB_FILE_NAME = "base.realm";
    public static Boolean DEBUG_MODE;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder().name( DB_FILE_NAME ).build();
        Realm.setDefaultConfiguration(config);

        //개발할때에만, 릴리즈시 삭제.
        try {
            Realm.getInstance(config);
        } catch (RealmMigrationNeededException e){
            try {
                Log.d( "BaseApplication", "delete realm");
                Realm.deleteRealm(config);
                //Realm file has been deleted.
            } catch (Exception ex){
                Log.d( "BaseApplication", "delete realm: " + ex.toString() );
//                throw ex;
            }
        }

        DEBUG_MODE = isDebuggable( this );

    }

    private boolean isDebuggable(Context context) {
        boolean debuggable = false;

        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            /* debuggable variable will remain false */
        }

        return debuggable;
    }
}
