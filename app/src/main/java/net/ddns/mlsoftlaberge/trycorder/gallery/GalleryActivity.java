package net.ddns.mlsoftlaberge.trycorder.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import net.ddns.mlsoftlaberge.trycorder.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mlsoft on 28/02/16.
 */
public class GalleryActivity extends Activity implements
        AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {

    private ImageButton mBacktopButton;
    private Button mBackButton;
    private Button mGalleryButton;
    private Button mSendButton;

    private ImageSwitcher mImageSwitcher;
    private Gallery mImageGallery;

    private List<Uri> mImageUris = new ArrayList<Uri>();
    private int currenturi=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);
        // the search button
        mBacktopButton = (ImageButton) findViewById(R.id.backtop_button);
        mBacktopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                buttonback();
            }
        });
        // the search button
        mBackButton = (Button) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                buttonback();
            }
        });
        // the search button
        mGalleryButton = (Button) findViewById(R.id.gallery_app_button);
        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                buttongallery();
            }
        });
        // the search button
        mSendButton = (Button) findViewById(R.id.photo_send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                buttonsend();
            }
        });

        // load the list of uris from camera directory
        loadimageuris();

        // obtain and program the switcher and gallery
        mImageSwitcher = (ImageSwitcher) findViewById(R.id.image_switcher);
        mImageGallery = (Gallery) findViewById(R.id.image_gallery);

        mImageSwitcher.setFactory(this);
        mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        mImageGallery.setAdapter(new ImageAdapter(this));
        mImageGallery.setOnItemSelectedListener(this);

    }

    // permits this activity to hide status and action bars, and proceed full screen
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    // ==================================================================================

    private void buttonsound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.keyok2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    private void buttonback() {
        finish();
    }

    // start the external gallery application
    private void buttongallery() {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // send the current content to someone
    private void buttonsend() {
        if(mImageUris==null) return;
        Uri uri = mImageUris.get(currenturi);
        // send this image to someone
    }

    // =====================================================================================
    // ======================== Image list loader ==========================================

    private void loadimageuris() {
        String path = Environment.getExternalStorageDirectory().toString()+"/DCIM/Camera";
        Log.d("Files", "Path: " + path);
        File f = new File(path);
        File file[] = f.listFiles();
        Log.d("Files", "Size: "+ file.length);
        for (int i=0; i < file.length; i++)
        {
            String name = file[i].getName();
            if(name.contains(".jpg")) {
                Log.d("Files", "FileName:" + name);
                Uri uri = Uri.parse(path + "/" + name);
                mImageUris.add(uri);
            }
        }
    }

    // ===================================================================================
    // ====================== Image Gallery callbacks ===================================

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        currenturi=position;
        if(mImageUris!=null) {
            mImageSwitcher.setImageURI(mImageUris.get(position));
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    public View makeView() {
        ImageView i = new ImageView(this);
        i.setBackgroundColor(0xFF000000);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(Gallery.LayoutParams.MATCH_PARENT,
                Gallery.LayoutParams.MATCH_PARENT));
        return i;
    }

    // ===================================================================================
    // ====================== Image Gallery Adapter ======================================

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            if(mImageUris!=null) {
                return (mImageUris.size());
            } else {
                return(0);
            }
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);
            if(mImageUris!=null) {
                File f = new File(mImageUris.get(position).getPath());
                Bitmap b = decodeFile(f);
                BitmapDrawable pic = new BitmapDrawable(b);
                i.setImageDrawable(pic);
            }
            i.setAdjustViewBounds(true);
            i.setLayoutParams(new Gallery.LayoutParams(
                    Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.WRAP_CONTENT));
            i.setBackgroundResource(R.drawable.picture_frame);
            return i;
        }

    }

    // ==================================================================================
    //decodes image and scales it to reduce memory consumption in gallery

    private Bitmap decodeFile(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=100;

            //Find the correct scale value. It should be the power of 2.
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;

            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;

            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        }

        catch (FileNotFoundException e) {}

        return null;
    }

}
