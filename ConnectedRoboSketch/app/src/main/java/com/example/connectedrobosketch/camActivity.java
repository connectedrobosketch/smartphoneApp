package com.example.connectedrobosketch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class camActivity extends AppCompatActivity {

    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        // Get the image
        Intent intent = getIntent();
        currentPhotoPath = intent.getStringExtra("path");
        File imgFile = new File(currentPhotoPath);
        if(imgFile.exists()) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView iv = (ImageView) findViewById(R.id.imageView3);
            iv.setImageBitmap(imageBitmap);
        }
    }

    // Called when send button is clicked
    public void sendImage(View view){
        Button button = (Button) findViewById(R.id.sendButton);
        button.setText("Hello");
    }
}
