package com.jiyoonseo.bizzscan;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

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


    public void saveContact(View v){
        Intent addPersonIntent = new Intent(Intent.ACTION_INSERT);
        addPersonIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        addPersonIntent.putExtra(ContactsContract.Intents.Insert.NAME, "Julia Siolva");
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, "President");
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.COMPANY, "My new Company");
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.IM_PROTOCOL, "www.helloworld.com");
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.PHONE, "123 3423 4234");
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, "fax number");
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, "hello.world@com.com");
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.POSTAL, "1234 cad da st. app 23, Redmond, WA 98052");

        startActivityForResult(addPersonIntent, 0);
    }

    public void sc(View view){
        Toast.makeText(getApplicationContext(), ("SAVE BUTTON HAS BEEN PRESSED"), Toast.LENGTH_LONG).show();

        String theId = "";
        String DisplayName = "Aho CoolHA ha";
        String MobileWork = "123456";
        String FaxWork = "1111";
        //String WorkNumber = "2222";
        String emailID = "email@nomail.com";
        String userCompany = "bad";
        String jobTitle = "abcd";
        String website_url = "www.asdbe.com";
        String work_address = "3000 cool pl. Apt 2, Redmond, WA 98052";
        String photo_uri = "/storage/external_SD/DCIM/Camera/20151209_185643.jpg";

        ArrayList< ContentProviderOperation > ops = new ArrayList <ContentProviderOperation> ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());



        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Work / TYPE_WORK
        if (MobileWork != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileWork)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Home Numbers / TYPE_FAX_WORK
        if (FaxWork != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, FaxWork)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK)
                    .build());
        }

        //------------------------------------------------------ Work Numbers / TYPE_OTHER
        /*
        if (WorkNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)
                    .build());
        }
        */

        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Email
        if (work_address != null) {
            Log.e("contact", work_address);
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.SipAddress.LABEL, work_address)
                    .withValue(ContactsContract.CommonDataKinds.SipAddress.TYPE, ContactsContract.CommonDataKinds.SipAddress.TYPE_WORK)
                    .build());
        }

        //String website_url = "www.fgfgg.com";
        if (website_url != null){
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Website.URL, website_url)
                    .withValue(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_WORK)
                    .build());
        }

        //String photo_uri = "";
        if(photo_uri != null){
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.parse(photo_uri) ); // Uri.parse(imageUrl)
                ByteArrayOutputStream image = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, image);

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, image.toByteArray())
                        .build());
            }catch(Exception e){
                Log.e("Photo exception", e.toString());
            }


        }

        //------------------------------------------------------ Organization
        if (!userCompany.equals("") && !jobTitle.equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, userCompany)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(getApplicationContext(), "SUCCESSFULLY APPLIED :D" , Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(0));
            intent.setData(uri);
            getApplicationContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("error :", e.getMessage());
        }



    }

}
