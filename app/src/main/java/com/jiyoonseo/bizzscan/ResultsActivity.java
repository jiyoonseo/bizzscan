package com.jiyoonseo.bizzscan;


import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Created by junes on 10/26/2015.
 */
public class ResultsActivity extends AppCompatActivity {

    String outputPath, imageUrl;
    TextView tv, resultTitle;
    TextView item01, item02, item03, item04, item05, item06, item07, item08;
    TextView item01_title, item02_title, item03_title, item04_title, item05_title, item06_title, item07_title, item08_title;
    //String name, title, company, phone, fax, address, url, email;
    String name =null , title =null , company =null , phone =null , fax =null , address =null , url =null , email = null;

    ImageView frontCard;
    //Uri resultCard;

    final String TAG = "RESULTS_ACTIVITY";
    final String TAB_ARRAY = "RESULTS_ARRAY";

    List<String> result = new ArrayList<>();
    Map map = new HashMap<>();

    ScrollView sv;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        tv = new TextView(this);
        //setContentView(tv);

        setContentView(R.layout.activity_result);

        // * test = (TextView) findViewById(R.id.tester_string);
        //Toast.makeText(getApplicationContext(), ("test text:" + test.getText()), Toast.LENGTH_SHORT).show();
        //test.setText("123");

        setContentView(tv);

        imageUrl = "unknown";

        Bundle extras = getIntent().getExtras();
        if( extras != null) {
            imageUrl = extras.getString("IMAGE_PATH" );
            outputPath = extras.getString("RESULT_PATH");
            //resultCard = Uri.parse("RESULT_PATH");
        }
        // Starting recognition process
        new AsyncProcessTask(this).execute(imageUrl, outputPath);  //temp


    }

    public void updateResults(Boolean success) {
        if (!success)
            return;
        try {
            //StringBuffer contents = new StringBuffer();

            FileInputStream fis = openFileInput(outputPath);
            try {
                Reader reader = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufReader = new BufferedReader(reader);
                String text = null;
                while ((text = bufReader.readLine()) != null) {
                    // contents.append(text).append(System.getProperty("line.separator"));
                    // * test.append(text);
                    String[] subStr = text.split("  +");
                    Log.e("afterPATTERN====", subStr.toString());

                    int i ;
                    for(i = 0 ; subStr.length > i ; i++){
                        String after = subStr[i].trim().replaceAll(" +", " ").replaceAll("[*-+^:]","");;

                        Log.e("afterString====", (after+"*") );
                        Log.e("afterString=len===", (Integer.toString(after.length())+"*") );
                        after.replaceAll("^\\s+", "");

                        if(after != null && !after.isEmpty() && after.length() > 1 && !after.matches("\\s+")){
                            result.add(after);
                        }

                    }
                }

                //result.add(text);
                Log.e("RESULT ARRAY", result.toString());
            } finally {

                fis.close();
            }

            //set view with result information

            setContentView(R.layout.activity_result);

            // ll = (LinearLayout)  this.findViewById(R.id.reesut_l_layout);

            // * test = (TextView) findViewById(R.id.tester_string);
            // * Toast.makeText(getApplicationContext(), ("test text:" + test.getText()), Toast.LENGTH_SHORT).show();
            // * test.setText(result.toString());
            // * String empty = " ";
            // * test.setText(empty);

            frontCard = (ImageView) findViewById(R.id.bcard_image_front_result);
            //frontCard.setImageURI(null);
            String sc = outputPath + imageUrl;

            /**
             *  Toast.makeText(getApplicationContext(), ("output path text:" + imageUrl), Toast.LENGTH_SHORT).show();
             */

            // * test.setText(imageUrl);
            Uri usc = Uri.parse(imageUrl);
            frontCard.setImageURI(null);
            frontCard.setImageURI(usc);
            //frontCard.setRotation((float)180);

            resultTitle = (TextView) findViewById(R.id.result_name_textview);
            // resultTitle.setText(result.get(0));
            resultTitle.setText("Result");

            /*
            TextView test = (TextView) findViewById(R.id.item01_title);
            test.setText("HELLO WORLD!!!");
            */

            /**
             * ready to use 'result<String>()' result: arraylist
             * loop through all the list in 'result' and see what each String is
             */
            for(int i= 0 ; i<result.size(); i++){
                if(isEmail(result.get(i)) ){
                    String[] subSt = result.get(i).split(" ");
                    for(int j = 0; j<subSt.length; j++){
                        if( isEmail(subSt[j]) ){
                            Log.e("stringVAL: ", "TRUE - email..");
                            map.put("email", result.get(i));
                            email = result.get(i);
                            Log.e("result email : ", email);
                        }else if(isURL(subSt[j])){
                            Log.e("stringVAL: ", "TRUE - url..");
                            map.put("url", result.get(i));
                            url = result.get(i);
                            Log.e("result url : ", url);
                        }
                    }
                }else if(isURL(result.get(i))){
                    Log.e("stringVAL: ", "TRUE - url..");
                    map.put("url", result.get(i));
                    url = result.get(i);
                    Log.e("result : url 2 ", url);
                }else if(isPhone((result.get(i)))){

                    if(!map.containsKey("phone")){
                        Log.e("stringVAL: ", "TRUE - phone..");
                        map.put("phone", result.get(i));
                        phone = result.get(i);
                        Log.e("result phone: ", phone);
                    }else{
                        Log.e("stringVAL: ", "TRUE - fax..");
                        map.put("fax", result.get(i));
                        fax = result.get(i);
                        Log.e("result fax: ", fax);
                    }

                }else if(isPositionTitle(result.get(i))){
                    Log.e("stringVAL: ", "TRUE - title..");
                    map.put("title", result.get(i));
                    title = result.get(i);
                    Log.e("result title: ", title);
                }else if(isAddress(result.get(i))){
                    Log.e("stringVAL: ", "TRUE - address..");
                    map.put("address", result.get(i));
                    address = result.get(i);
                    Log.e("result address: ", address);
                }else{
                    if(!map.containsKey("name")){
                        Log.e("stringVAL: ", "TRUE - name..");
                        map.put("name", result.get(i));
                        name = result.get(i);
                        Log.e("result name: ", name);
                    }else if(!map.containsKey("company")){
                        Log.e("stringVAL: ", "TRUE - company..");
                        map.put("company", result.get(i));
                        company = result.get(i);
                        Log.e("result company: ", company);
                    }else{
                        if(!map.containsKey("address")){
                            Log.e("stringVAL: ", "TRUE - address..");
                            map.put("address", result.get(i));
                            address = result.get(i);
                            Log.e("result address: ", address);

                        }else{
                            Log.e("stringVAL: ", "TRUE - address more..");
                            address = map.get("address").toString() + result.get(i);
                            map.put("address", address);
                            Log.e("result address(added): ", address);

                        }
                    }
                }

            }


            /**
             * Map result :D
             */
            Log.e("map result: ", map.toString());
            Log.e("result name: ", name);
            Log.e("result title: ", title);
            Log.e("result phone: ", phone);
            Log.e("result email: ", email);
            Log.e("result url: ", url);
            Log.e("result address: ", address);
            Log.e("result fax: ", fax);


            // *****************************
            /**
             *

            Log.e("RESULT temp email", email);
            item04 = (TextView) findViewById(R.id.item04);
            item04.setText("hellohelloellohello");

            item04_title = (TextView) findViewById(R.id.item04_title);
            item04_title.setText("Email");
             */
            // *****************************


            item01_title = (TextView) findViewById(R.id.item01_title);
            item01_title.setText("Name");
            item01 = (TextView) findViewById(R.id.item01);
            item01.setText(map.get("name").toString());
            if(map.containsKey("name")){
                Log.e("RESULT output name", map.get("name").toString());
                //item01 = (TextView) findViewById(R.id.item01);
                item01.setText(map.get("name").toString());

            }

            item02_title = (TextView) findViewById(R.id.item02_title);
            item02_title.setText("Title");
            item02 = (TextView) findViewById(R.id.item02);
            item02.setText("");
            if(map.containsKey("title")){
                Log.e("RESULT output title", map.get("title").toString());
                //item02 = (TextView) findViewById(R.id.item02);
                item02.setText(map.get("title").toString());

            }

            item03_title = (TextView) findViewById(R.id.item03_title);
            item03_title.setText("Company");
            item03 = (TextView) findViewById(R.id.item03);
            item03.setText("");
            if(map.containsKey("company")){
                Log.e("RESULT output company", map.get("company").toString());
                //item03 = (TextView) findViewById(R.id.item03);
                item03.setText(map.get("company").toString());
            }

            item04_title = (TextView) findViewById(R.id.item04_title);
            item04_title.setText("Email");
            item04 = (TextView) findViewById(R.id.item04);
            item04.setText("");
            if(map.containsKey("email") ){
                Log.e("RESULT output email", map.get("email").toString());
                //item04 = (TextView) findViewById(R.id.item04);
                item04.setText(map.get("email").toString());

            }


            item05_title = (TextView) findViewById(R.id.item05_title);
            item05_title.setText("Phone");
            item05 = (TextView) findViewById(R.id.item05);
            item05.setText("");
            if( map.containsKey("phone")){
                Log.e("RESULT output phone", map.get("phone").toString());
                item05 = (TextView) findViewById(R.id.item05);
                item05.setText(map.get("phone").toString());


            }

            item06_title = (TextView) findViewById(R.id.item06_title);
            item06_title.setText("URL");
            item06 = (TextView) findViewById(R.id.item06);
            item06.setText("");
            if( map.containsKey("url")){
                Log.e("RESULT output url", map.get("url").toString());
                //item06 = (TextView) findViewById(R.id.item06);
                item06.setText(map.get("url").toString());
            }



            item07_title = (TextView) findViewById(R.id.item07_title);
            item07_title.setText("Fax");
            item07 = (TextView) findViewById(R.id.item07);
            item07.setText("");
            if( map.containsKey("fax")){
                Log.e("RESULT output fax", map.get("fax").toString());

                //item07 = (TextView) findViewById(R.id.item07);
                item07.setText(map.get("fax").toString());


            }

            item08_title = (TextView) findViewById(R.id.item08_title);
            item08_title.setText("Address");
            item08 = (TextView) findViewById(R.id.item08);
            item08.setText("");
            if(map.containsKey("fax")){
                Log.e("RESULT output address", map.get("address").toString());

                //item08 = (TextView) findViewById(R.id.item08);
                item08.setText(map.get("address").toString());


            }


            // END OF INPUT ITEM ELEMENTS # 1







            //displayMessage(contents.toString());

            /**
            set dynamic scroll view : sv
            // review later :D TODO

            TextView tvv=new TextView(this);
            tvv.setText("test");    // set text size; set text color
            tvv.setTextColor(Color.parseColor("#50A7B3"));  // "#50A7B3" : sky blue color

            this.ll.addView(tvv);
             */



        } catch (Exception e) {
            displayMessage("Error: " + e.getMessage());
        }
    }

    /**
     * public functions -> to implement result strings
     * @param text
     */
    public boolean isEmail(String text){
        if (text.matches(".*" + "@" + ".*")) {
            Log.e("string validation:", "EMAIL - true");
            return true;
        }else{
            return false;
        }
    }

    public boolean isURL(String text){
        if (text.matches(".*" + "com")) {
            Log.e("string validation:", "isURL - true");
            return true;
        }else{
            return false;
        }
    }

    public boolean isPhone(String text){
        if(text.matches("[\\s\\S]+\\b\\d{3}[-.\\s\\S]+?\\d{3}[-.\\s]?\\d{4}\\b") || text.matches("[\\s\\S]?\\b\\d{3}[-.\\s\\S]+?\\d{3}[-.\\s]?\\d{4}\\b")){
            Log.e("string validation:", "isPhone - true");
            return true;
        }else{
            return false;
        }
    }

    public boolean isPositionTitle(String text){
        String title = text.replaceAll(" +", "");
        if(title.matches("\\w*yst\\b") || title.matches("\\w*ant\\b") || title.matches("\\w*list\\b") || title.matches("\\w*tor\\b") || title.matches("\\w*dent\\b") ){
            Log.e("string validation:", "isPositionTitle - true");
            return true;
        }else{
            return false;
        }
    }

    public boolean isAddress(String text){
        if(text.matches("\\d+\\s+([a-zA-Z]+|[a-zA-Z]+\\s[a-zA-Z]+)" )){
            return true;
        }else{
            return false;
        }
    }


    public void displayMessage( String text )
    {
        tv.post( new MessagePoster( text ) );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_result, menu);
        return true;
    }




    public void editContact(View v){
        Intent addPersonIntent = new Intent(Intent.ACTION_INSERT);
        addPersonIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        addPersonIntent.putExtra(ContactsContract.Intents.Insert.NAME, map.get("name").toString());
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, map.get("title").toString());
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.COMPANY, map.get("company").toString());
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.IM_PROTOCOL, map.get("url").toString());
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.PHONE, map.get("phone").toString());
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, map.get("fax").toString());
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, map.get("email").toString());
        addPersonIntent.putExtra(ContactsContract.Intents.Insert.POSTAL, map.get("address").toString());

        startActivityForResult(addPersonIntent, 0);
    }

    public void saveContact(View view){
        Toast.makeText(getApplicationContext(), ("SAVE BUTTON HAS BEEN PRESSED"), Toast.LENGTH_LONG).show();

        String theId = "";
        String DisplayName = map.get("name").toString() ;// "Aho CoolHA ha";
        String MobileWork = map.get("phone").toString(); //"123456";
        String FaxWork = map.get("fax").toString(); //"1111";
        //String WorkNumber = "2222";
        String emailID = map.get("email").toString(); //"email@nomail.com";
        String userCompany = map.get("company").toString(); //"bad";
        String jobTitle = map.get("title").toString(); //"abcd";
        String website_url = map.get("url").toString(); //"www.asdbe.com";
        String work_address = map.get("address").toString(); //"3000 cool pl. Apt 2, Redmond, WA 98052";

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

        Log.e("IMAGEURL", imageUrl);  // "/storage/external_SD/DCIM/Camera/20151209_185643.jpg"
        /**
        String photo_uri = "";
        Toast.makeText(getApplicationContext(), "PHOTO URI:D" + imageUrl, Toast.LENGTH_LONG).show();
        Log.e("IMAGEURL", imageUrl);  // Uri.parse(imageUrl)
        if(imageUrl != null){
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO_URI, imageUrl)
                    .build());
        }
        */
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

    class MessagePoster implements Runnable {
        public MessagePoster( String message )
        {
            _message = message;
        }

        public void run() {
            tv.append(_message + "\n");
            //setContentView( tv );
            Log.i(TAB_ARRAY, result.toString()); // show all the text in the arraylist

            // setContentView(R.layout.activity_result);



            // email ; url ; phone ; fax ; title ; name ; address ; company

            item01_title = (TextView) findViewById(R.id.item01_title);
            item01_title.setText("Name");
            item01 = (TextView) findViewById(R.id.item01);
            item01.setText("");
            if(name != null){
                Log.e("RESULT output name", name);
                //item01 = (TextView) findViewById(R.id.item01);
                item01.setText(name);

            }

            item02_title = (TextView) findViewById(R.id.item02_title);
            item02_title.setText("Title");
            item02 = (TextView) findViewById(R.id.item02);
            item02.setText("");
            if(title != null){
                Log.e("RESULT output title", title);
                //item02 = (TextView) findViewById(R.id.item02);
                item02.setText(title);

            }

            item03_title = (TextView) findViewById(R.id.item03_title);
            item03_title.setText("Company");
            item03 = (TextView) findViewById(R.id.item03);
            item03.setText("");
            if(company != null){
                Log.e("RESULT output company", company);
                //item03 = (TextView) findViewById(R.id.item03);
                item03.setText(company);
            }

            item04_title = (TextView) findViewById(R.id.item04_title);
            item04_title.setText("Email");
            item04 = (TextView) findViewById(R.id.item04);
            item04.setText("");
            if(email != null){
                Log.e("RESULT output email", email);
                //item04 = (TextView) findViewById(R.id.item04);
                item04.setText(email);

            }


            item05_title = (TextView) findViewById(R.id.item05_title);
            item05_title.setText("Phone");
            item05 = (TextView) findViewById(R.id.item05);
            item05.setText("");
            if(phone != null){
                Log.e("RESULT output phone", phone);
                //item05 = (TextView) findViewById(R.id.item05);
                item05.setText(phone);


            }

            item06_title = (TextView) findViewById(R.id.item06_title);
            item06_title.setText("URL");
            item06 = (TextView) findViewById(R.id.item06);
            item06.setText("");
            if(url != null){
                Log.e("RESULT output url", url);
                //item06 = (TextView) findViewById(R.id.item06);
                item06.setText(url);
            }



            item07_title = (TextView) findViewById(R.id.item07_title);
            item07_title.setText("Fax");
            item07 = (TextView) findViewById(R.id.item07);
            item07.setText("");
            if(fax != null){
                Log.e("RESULT output fax", fax);

                // item07 = (TextView) findViewById(R.id.item07);
                item07.setText(fax);


            }

            item08_title = (TextView) findViewById(R.id.item08_title);
            item08_title.setText("Address");
            item08 = (TextView) findViewById(R.id.item08);
            item08.setText("");
            if(address != null){
                Log.e("RESULT output address", address);
                //item08 = (TextView) findViewById(R.id.item08);
                item08.setText(address);


            }

            // END OF INPUT ITEM ELEMENTS # 2





        }

        private final String _message;
    }
}



