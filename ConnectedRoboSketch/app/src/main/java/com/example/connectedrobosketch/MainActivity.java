package com.example.connectedrobosketch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;
    String currentPhotoPath;

    Button cbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
        }

        cbtn=(Button)findViewById(R.id.camerabutton);
        cbtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //TODO Auto-Generated method stub
                takePicture();

            }});
    }

    private void takePicture(){
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(camera.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error has occurred, do something
            }
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                camera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(camera, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void getGalleryImage(View view){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(this, camActivity.class);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            File imgFile = new File(currentPhotoPath);
//            if(imgFile.exists()){
//                Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                ImageView iv = (ImageView) findViewById(R.id.imageView3);
//                iv.setImageBitmap(imageBitmap);
//            }
            intent.putExtra("path", currentPhotoPath);
            intent.putExtra("type", 1);
            startActivity(intent);
        }else if(requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK){
            String picturePath = "";
            Uri image = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            if(image != null){
//                Cursor cursor = getContentResolver().query(image, filePathColumn, null, null, null);
//                if(cursor != null){
//                    cursor.moveToFirst();
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    picturePath = cursor.getString(columnIndex);
//                    cursor.close();
//                }
//            }
            intent.putExtra("path", image.toString());
            intent.putExtra("type", 2);
            startActivity(intent);
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
