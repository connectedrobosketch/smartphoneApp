package com.example.connectedrobosketch;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class camActivity extends AppCompatActivity {

    String currentPhotoPath;

    private static final String TAG = "MY_APP_DEBUG_TAG";

    File imgFile;
    int type = 0;
    Bitmap imageBitmap = null;

    static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice rpi = null;
    private BluetoothSocket sock;
    private static final UUID MY_UUID = UUID.fromString("ec338f74-79f3-11ea-bc55-0242ac130003");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Find image view
        ImageView iv = (ImageView) findViewById(R.id.imageView3);

        // Get the image
        Intent intent = getIntent();

        type = intent.getIntExtra("type", 0);
        if(type == 1){
            currentPhotoPath = intent.getStringExtra("path");
            imgFile = new File(currentPhotoPath);
            if(imgFile.exists()) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                iv.setImageBitmap(imageBitmap);
            }
        }else if(type == 2){
            String path = intent.getStringExtra("path");
            Uri uriPath = Uri.parse(path);

            try{
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriPath);
            }catch (IOException e){

            }

            if(imageBitmap != null){
                iv.setImageBitmap(imageBitmap);
            }
        }

    }

    // Called when send button is clicked
    public void sendImage(View view){
        sendImgBluetooth();
    }

    public void sendImgBluetooth(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        Bitmap imgBit = null;
        Bitmap scaled = null;
        if(type == 1){
            imgBit = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            scaled = Bitmap.createScaledBitmap(imgBit, imgBit.getWidth()/8, imgBit.getHeight()/8, true);
        }else if(type == 2){
            imgBit = imageBitmap;
            if(imgBit.getHeight() > 1000 || imgBit.getWidth() > 1000){
                double scale_x = (float) imgBit.getWidth()/1000;
                double scale_y = (float) imgBit.getHeight()/1000;

                if(scale_x > 1.0 || scale_y > 1.0){
                    if(scale_x > scale_y){
                        scaled = Bitmap.createScaledBitmap(imgBit, imgBit.getWidth()/ ((int) scale_x), imgBit.getHeight()/((int)scale_x), true);
                    }else{
                        scaled = Bitmap.createScaledBitmap(imgBit, imgBit.getWidth()/ ((int) scale_y), imgBit.getHeight()/((int)scale_y), true);
                    }
                }else{
                    scaled = imgBit;
                }
            }else{
                scaled = imgBit;
            }
        }else{
            // Error
            return;
        }



        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, out);


//        TextView tex = findViewById(R.id.blueList);
//        String str = "Image size = " + out.size();
//        tex.setText(str);
//
        if(pairedDevices.size() > 0){
            for(BluetoothDevice dev : pairedDevices) {
//                tex.append(dev.getAddress());
                if(dev.getAddress().compareTo("DC:A6:32:0A:23:18") == 0){
                    rpi = dev;
                }
            }
        }

        if(rpi != null){
            try{
                sock = rpi.createRfcommSocketToServiceRecord(MY_UUID);
            }catch(IOException createSockException){
                Log.e(TAG, "Could not open socket", createSockException);
            }

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                sock.connect();
            }catch (IOException connectException){
                try{
                    sock.close();
                }catch(IOException closeException){
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
            }

            try{
                tmpIn = sock.getInputStream();
            }catch(IOException inStreamException){
                Log.e(TAG, "Error occurred while getting input stream", inStreamException);
            }

            try{
                tmpOut = sock.getOutputStream();
            }catch(IOException outStreamException){
                Log.e(TAG, "Error occurred while getting output stream", outStreamException);
            }

            try{
                tmpOut.write(out.toByteArray());
            }catch(IOException writeException){
                Log.e(TAG, "Error occurred while reading to output stream", writeException);
            }

            try{
                Thread.sleep(2000);
            }catch(InterruptedException e){
                Log.e(TAG, "Sleep error", e);
            }

            try{
                sock.close();
            }catch(IOException closeException){
                Log.e(TAG, "Error occurred while closing the socket", closeException);
            }
        }


    }
}
