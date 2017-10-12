package com.kinvey.sample.Mymusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.api.client.http.HttpTransport;
import com.kinvey.android.Client;
import com.kinvey.sample.Mymusic.fragments.LoginFragment;
import com.kinvey.sample.Mymusic.model.UpdateEntity;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {
    private static final Level LOGGING_LEVEL = Level.FINEST;
    private Context context;

    public static final String TAG = "Kinvey - StatusShare";

    public static final String COL_UPDATES = "Updates";
    public static final String COL_COMMENTS = "Comments";

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private Uri mImageCaptureUri;

    public Bitmap bitmap = null;
    public String path = null;

    private List<UpdateEntity> shareList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_holder);
        Logger.getLogger(HttpTransport.class.getName()).setLevel(LOGGING_LEVEL);
        replaceFragment(new LoginFragment(), false);
        verifyStoragePermissions(this);
    }

//
//    public void addFragment(SherlockFragment frag, boolean addToBackStack) {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.add(R.id.fragmentBox, frag, frag.toString());
//        if (addToBackStack){
//            ft.addToBackStack(frag.toString());
//        }
//        ft.commit();
//    }

    public void replaceFragment(Fragment frag, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentBox, frag);
        if (addToBackStack) {
            ft.addToBackStack(frag.toString());
        }
        ft.commit();
    }
//
//    public void removeFragment(SherlockFragment frag){
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.remove(frag);
//        ft.commit();
//    }


    /**
     * This method will be called after the user either selects a photo from their gallery or takes a picture with their camera
     *
     * @param requestCode - either PICK_FROM_FILE or PICK_FROM_CAMERA
     * @param resultCode  - hopefully it's RESULT_OK!
     * @param data        - contains the Path to the selected image
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }


        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
            path = getRealPathFromURI(data.getData()); //from Gallery
            //path = getGalleryPath();
            if (path == null) {
                path = data.getData().getPath(); //from File Manager
            }

            if (path != null) {
                //path = "/storage/emulated/0/DCIM/Camera/1.jpg";
                path  = getRealPathFromURI_API19(this, data.getData());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = true;
                options.inJustDecodeBounds = true;


                // First decode with inJustDecodeBounds=true to check dimensions
                bitmap = BitmapFactory.decodeFile(path, options);


                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, 200, 150);

                options.inJustDecodeBounds = false;

                // Decode bitmap with inSampleSize set

                int h = 512; // height in pixels
                int w = 512; // width in pixels
                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path, options), h, w, false);
                Log.i(Client.TAG, path);
            }
            Log.i(Client.TAG, "activity result, bitmap from file is -> " + String.valueOf(bitmap == null));

        } else if (requestCode == PICK_FROM_CAMERA) {
            path = mImageCaptureUri.getPath();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = true;
            options.inJustDecodeBounds = true;


            // First decode with inJustDecodeBounds=true to check dimensions
            bitmap = BitmapFactory.decodeFile(path, options);


            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 200, 150);

            options.inJustDecodeBounds = false;

            // Decode bitmap with inSampleSize set

            int h = 512; // height in pixels
            int w = 512; // width in pixels
            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path, options), h, w, false);

            Log.i(Client.TAG, "activity result, bitmap from camera is -> " + String.valueOf(bitmap == null));
            Log.i(Client.TAG, "activity result, path from camera is -> " + String.valueOf(path == null));

        } else {
            Log.e(TAG, "That's not a valid request code! -> " + requestCode);
        }


    }

    private static String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/" +Environment.DIRECTORY_PICTURES;
    }

    /**
     * This method gets a file location from a URI
     *
     * @param contentUri the URI of the image to upload
     * @return the file path of the image
     */
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);

        if (cursor == null) {
            return null;
        }

        String ret = "";
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(proj[0]);
        if (columnIndex == -1) {
            //picassa bug
            //see: http://jimmi1977.blogspot.com/2012/01/android-api-quirks-getting-image-from.html

            columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);

        }
        return cursor.getString(columnIndex);
    }



    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }



    public void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        mImageCaptureUri = Uri.fromFile(file);

        try {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);

            startActivityForResult(intent, PICK_FROM_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * This method wraps the code to kick off the "chooser" intent, which allows user to select where to select image from
     */
    public void startFilePicker() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);

    }


    /**
     * used to scale images
     *
     * @param options   scaling options
     * @param reqWidth  the target width
     * @param reqHeight the target height
     * @return width/height of the sample image, as close as possible to input req*
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int stretch_width = Math.round((float) width / (float) reqWidth);
        int stretch_height = Math.round((float) height / (float) reqHeight);

        if (stretch_width <= stretch_height) return stretch_height;
        else return stretch_width;
    }

    public static String getSince(Date d, Calendar c) {
        long secsSince = (c.getTime().getTime() - d.getTime()) / 1000L;

        if (secsSince < 60) {
            return "now";
        } else if (secsSince < (60 * 60)) {
            return secsSince / 60 + "m ago";
        } else if (secsSince < (60 * 60 * 24)) {
            return secsSince / (60 * 60) + "h ago";
        } else {
            return secsSince / (60 * 60 * 24) + "d ago";
        }
    }

    public List<UpdateEntity> getShareList() {
        return shareList;
    }

    public void setShareList(List<UpdateEntity> shareList) {
        this.shareList = shareList;
    }
}

