package com.cwp.iso_colourator_20x6.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.imageView);

        img.setOnTouchListener(new ImageView.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                Bitmap refBitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
                int height = refBitmap.getHeight();
                int width = refBitmap.getWidth();
                Bitmap greyBm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                int pixel,alpha,red,green,blue,grey;
                for(int i = 0;i<width;i++) {
                    for(int j = 0; j < height;j++) {
                        pixel = refBitmap.getPixel(i,j);
                        alpha = Color.alpha(pixel);
                        red = Color.red(pixel);
                        green = Color.green(pixel);
                        blue = Color.blue(pixel);
                        grey = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
                        greyBm.setPixel(i,j,Color.argb(alpha,grey,grey,grey));
                    }
                }
                img.setImageBitmap(greyBm);
                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_load_image) {
            Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
            choosePhotoIntent.setType("image/*");
            startActivityForResult(choosePhotoIntent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == 1) {
                if(data == null) {
                    Toast.makeText(this,getString(R.string.general_error),Toast.LENGTH_LONG).show();
                } else {
                    Uri imageUri = data.getData();
                    String imagePath = imageUri.getPath();
                    img.setImageURI(imageUri);
                }
            }
        } else if(resultCode != RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
        }
    }
}
