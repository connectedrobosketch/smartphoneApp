package com.example.connectedrobosketch;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button cbtn;
    Button dbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cbtn=(Button)findViewById(R.id.camerabutton);
        cbtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //TODO Auto-Generated method stub
                Intent camera = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivity(camera);
            }});
        dbtn=(Button)findViewById(R.id.drawingButton);
        dbtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) { }
            });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
