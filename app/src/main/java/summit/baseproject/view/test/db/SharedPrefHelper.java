package summit.baseproject.view.test.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

public class SharedPrefHelper {

	static public final String FIRST_INSTALL = "FIRST_INSTALL";

	static public final String PREV_ID = "PREV_ID";
	static public final String PREV_PW = "PREV_PW";

	private static SharedPrefHelper sph = new SharedPrefHelper();
	public static SharedPrefHelper getInstance() {
		if(sph == null){ sph = new SharedPrefHelper(); }
		return sph;
	}

	private SharedPrefHelper(){}

	public void allClear( Context ctx ){
		PreferenceManager.getDefaultSharedPreferences( ctx ).edit().clear().apply();

	}
	public void setIsFirstInstall(Context ctx, boolean isFirstInstall){
		PreferenceManager.getDefaultSharedPreferences( ctx ).edit().putBoolean( FIRST_INSTALL, isFirstInstall).apply();
	}
	public boolean getIsFirstInstall(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences( ctx ).getBoolean( FIRST_INSTALL , true );
	}

	public void setPrevId(Context ctx, String id){
		PreferenceManager.getDefaultSharedPreferences( ctx ).edit().putString( PREV_ID, id).apply();
	}
	public String getPrevId(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences( ctx ).getString( PREV_ID , "" );
	}

	public void setPrevPw(Context ctx, String id){
		PreferenceManager.getDefaultSharedPreferences( ctx ).edit().putString( PREV_PW, id).apply();
	}
	public String getPrevPw(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences( ctx ).getString( PREV_PW , "" );
	}


}
