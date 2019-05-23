package summit.baseproject.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.webkit.MimeTypeMap;

import java.io.File;

public class FileManager {


    public static void openFile(Context ctx, String path, String filename ) {

        try{
            File file = new File(path+"/"+filename);
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String type = map.getMimeTypeFromExtension(ext);

            if (type == null)
                type = "*/*";

            Intent intent = new Intent(Intent.ACTION_VIEW);
            //Uri data = Uri.fromFile(file);
            Uri data = FileProvider.getUriForFile(ctx, ctx.getApplicationContext().getPackageName() + ".summit.baseproject.provider", file );

            intent.setDataAndType(data, type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            ctx.startActivity( intent );
        } catch (Exception e ){
            e.printStackTrace();
        }
    }

    public static boolean isDownloadFileExists(String pathName ){
        File folder1 = new File(pathName);
        return folder1.exists();
    }

    public static boolean deleteDownloadFile( String pathName ){
        File folder1 = new File(pathName );
        return folder1.delete();
    }
}
