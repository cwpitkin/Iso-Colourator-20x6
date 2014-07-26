package com.cwp.iso_colourator_20x6.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
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

    private ImageView mImageView;
    private Bitmap mRefBitmap;
    private Bitmap mNewBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageView);

        mImageView.setOnTouchListener(new ImageView.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float[] targetHsv = new float[3];
                float[] currentHsv =new float[3];
                int pixel = getTargetColour(event.getX(),event.getY());
                Color.colorToHSV(pixel, targetHsv);
                int height = mRefBitmap.getHeight();
                int width = mRefBitmap.getWidth();
                mNewBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                int alpha, red, green, blue, grey;
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        pixel = mRefBitmap.getPixel(i, j);
                        Color.colorToHSV(pixel,currentHsv);
                        alpha = Color.alpha(pixel);
                        red = Color.red(pixel);
                        green = Color.green(pixel);
                        blue = Color.blue(pixel);
                        if(currentHsv[0] <= targetHsv[0] + 15 && currentHsv[0] >= targetHsv[0] - 15 && currentHsv[1] <= targetHsv[1] + 15 && currentHsv[1] >= targetHsv[1] - 15  ) {
                            mNewBitmap.setPixel(i, j, Color.argb(alpha, red, green, blue));
                        } else {
                            grey = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                            mNewBitmap.setPixel(i, j, Color.argb(alpha, grey, grey, grey));
                        }
                    }
                }
                mImageView.setImageBitmap(mNewBitmap);
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
                    mImageView.setImageURI(imageUri);
                    mRefBitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                }
            }
        } else if(resultCode != RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
        }
    }

    private int getTargetColour(float eventX, float eventY) {
        float eventXY[] = new float[] {eventX,eventY};
        Matrix invertMatrix = new Matrix();
        mImageView.getImageMatrix().invert(invertMatrix);
        invertMatrix.mapPoints(eventXY);
        int x = Integer.valueOf((int)eventXY[0]);
        int y = Integer.valueOf((int)eventXY[1]);
        //Limit x, y range within bitmap
        if(x < 0){
            x = 0;
        }else if(x > mRefBitmap.getWidth()-1){
            x = mRefBitmap.getWidth()-1;
        }

        if(y < 0){
            y = 0;
        }else if(y > mRefBitmap.getHeight()-1){
            y = mRefBitmap.getHeight()-1;
        }

        return mRefBitmap.getPixel(x, y);
    }
}
