package com.yng.partyhunt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.app.partyhunt.R;

/**
 * Created by yng1905 on 6/24/14.
 */
public class PictureDetailScreen extends ActionBarActivity {

    private ImageView img1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picturedetail_screen);

        img1 = (ImageView) findViewById(R.id.imageView);

        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("picture");

        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);

        img1.setImageBitmap(bmp);
    }



}

