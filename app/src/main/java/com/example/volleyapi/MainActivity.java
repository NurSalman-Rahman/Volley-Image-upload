package com.example.volleyapi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class MainActivity extends AppCompatActivity {
    Button upload;
    CircleImageView imageView;
    ImageView imageSelect;
    Bitmap bitmap;
    File srfile;

    /*
      SET OF encode String Base64 ,
        Image String Part
       sent data server this function  (encodeimage)*/
    String encodeimage;


    //api url initial
    private static final String apiurl = "https://alhasan.dev/interns/services/image.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intfuction();   //Find Id and Inital
        intListener();  // Action ,clickable

    }


    private void intfuction() {

        upload = findViewById(R.id.btnid);
        imageView = findViewById(R.id.imgavtid);
        imageSelect = findViewById(R.id.iconid);


    }

    private void intListener()
    {

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upladtoserver();
            }
        });

        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override

                            //Select Image Icon Permision Manage
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                isStoragePermissionGranted();
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_PICK);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();


            }
        });


    }


    // when click button then upload pic server
    //Response.Listner (onResponse)
    //Response.ErrorListener (onErrorRespone(Vollley Error error )


    private void upladtoserver() {


        StringRequest request = new StringRequest(Request.Method.POST, apiurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // Toast.makeText(MainActivity.this, "Succesfull " + response, Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Succesfull " + response, Toast.LENGTH_SHORT).show();

                Log.d("Succesfull", "onResponse: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("avatar", encodeimage);


                return map;
            }

            /*@Override
            protected Map<String, Dat
            aPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));

                return params;
            }*/

        };


        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);


    }


    /* OnActivityResult  function to implemet dialog box  to open call OnActivityResult Function
      Which is Recive Dioalog  box result.
 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check Request Code and resultCode
        if (requestCode == 111 && resultCode == RESULT_OK) {
            // this is bitmap object ,that is get data  and getExra


            //  bitmap = (Bitmap)data.getExtras().get("data");

            String path = getRealPathFromURI(data.getData());

            if (path == null) return;

            File imgFile = new File(path);

            try {
                imgFile = new Compressor(this).compressToFile(imgFile);

            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            try {

                bitmap = new Compressor(this).compressToBitmap(imgFile);

                imageView.setImageBitmap(bitmap);
                //server encoded image accept ,base 64
                encodebitmap(bitmap);
                //  dialog.show();
                if (bitmap == null) {
                    //profileIV.setImageBitmap(bitmap);
                    Toast.makeText(this, "Please Select Image Again !", Toast.LENGTH_SHORT).show();
                }

//                    alarDialog("profile");
                //selectPhotoTV.setText("Change Photo");
            } catch (IOException e) {
                return;
            }


            // data pass the imageview

        }
    }


    //bitmap can not encode bitmap so is possible  byteArrayOutStream

    private void encodebitmap(Bitmap bitmap)
    {

        //byteArray object Create
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // picture Compress and parametter pass Bitmap Class and byteArray object.
        // but it si not suffcient so next step byte aary.

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        //byte array declear
        byte[] byteofimages = byteArrayOutputStream.toByteArray();
        //SET OF encode String Base64 ,
        // Image String Part
        // sent data server this function

        encodeimage = android.util.Base64.encodeToString(byteofimages, Base64.DEFAULT);

    }

    public String getRealPathFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri,
                proj,
                null,
                null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }



    public boolean isStoragePermissionGranted() {
        // Toast.makeText(this, "isStorage", Toast.LENGTH_SHORT).show();
        //   Log.d("image_check", "onClick: profile permission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                chooseFromGallery();
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            chooseFromGallery();
            return true;
        }
    }



    private void chooseFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111);
    }


}
