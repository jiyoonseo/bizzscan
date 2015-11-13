package com.jiyoonseo.ocrtest1;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final int TAKE_PICTURE = 0;
    private final int SELECT_FILE = 1;
    private String resultUrl = "result.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);

        //Keep the debug log
        Log.d("Steps***", "onCreate()");



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);

        //Keep the debug log
        Log.d("Steps***", "onCreateOptiionsMenu()");

        return true;
    }

    public void captureImageFromSdCard( View view ) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        //Keep the debug log
        Log.d("Steps***", "captureImageFromSdCard()");

        startActivityForResult(intent, SELECT_FILE);
    }


    public static final int MEDIA_TYPE_IMAGE = 1;

    private static Uri getOutputMediaFileUri(){

        //Keep the debug log
        Log.d("Steps***", "getOutputMediaFileUri()");

        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        //Keep the debug log
        Log.d("Steps***", "getOutputMediaFile()");

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ABBYY");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        //Keep the debug log
        Log.d("Steps***", "getOutputMediaFile() -- step 01");
        Log.d("Steps***", "getOutputMediaFile() -- step 01" + mediaStorageDir.toString());


        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            //Keep the debug log
            Log.d("Steps***", "getOutputMediaFile() -- step 01- a");

            if (! mediaStorageDir.mkdirs()){
                //Keep the debug log
                Log.d("Steps***", "getOutputMediaFile() -- step 01 - b");

                return null;
            }
        }

        //Keep the debug log
        Log.d("Steps***", "getOutputMediaFile() -- step 02");



        // Create a media file name
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "image.jpg" );

        //Keep the debug log
        Log.d("Steps***", "getOutputMediaFile() -- step 02" + mediaFile.toString());



        return mediaFile;
    }


    /**
     * temp button - show resultActiity
     * @param view
     */
    public void resultView(View view){
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivityForResult(intent, 0);


    }

    public void captureImageFromCamera( View view) {


        //Keep the debug log
        Log.d("Steps***", "captureImageFromCamera()");


        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        String imageFilePath = null;

        switch (requestCode) {
            case TAKE_PICTURE:
                imageFilePath = getOutputMediaFileUri().getPath();
                break;
            case SELECT_FILE: {
                Uri imageUri = data.getData();

                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cur = getContentResolver().query(imageUri, projection, null, null, null);
                cur.moveToFirst();
                imageFilePath = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            break;
        }

        //Remove output file
        deleteFile(resultUrl);

        Intent results = new Intent( this, ResultsActivity.class);
        results.putExtra("IMAGE_PATH", imageFilePath);
        results.putExtra("RESULT_PATH", resultUrl);
        startActivity(results);
    }


}
