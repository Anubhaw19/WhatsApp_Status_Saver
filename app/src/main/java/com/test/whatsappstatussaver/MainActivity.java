package com.test.whatsappstatussaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.appbar.AppBarLayout;
import com.test.whatsappstatussaver.Adapters.PagerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.test.whatsappstatussaver.Fragments.ImageFragment;
import com.test.whatsappstatussaver.Fragments.VideoFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ImageFragment.Trigger , VideoFragment.TriggerVF {

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    Handler handler = new Handler();
    private static final String TAG = "MyTag";
    static final String facebook_link = "https://www.facebook.com/teamvoyagerfb";

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    private RenderScript rs = null;
    ImageView imgForTab;
    View imgBg;
    Bitmap mBlurBitmap;
    AppBarLayout appBarLayout;

    int tabLayoutHeight=1;
    int tabLayoutWidth=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rs = RenderScript.create(this);

        handlePermission();


        //setting adMob.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        //Banner Ads
        mAdView = findViewById(R.id.adView);
        appBarLayout=findViewById(R.id.appBar_layout);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //Interstitial Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5098396899135570/5155019605");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), 1, this));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mBlurBitmap = createBlurBitmap();

                Log.d(TAG, "triggerEvent: "+tabLayout.getHeight()+" "+tabLayout.getMeasuredHeight()+" "+appBarLayout.getHeight()+" "+appBarLayout.getMeasuredHeight()+" "+tabLayoutWidth+" "+tabLayoutHeight);

                Bitmap croppedBitmap=Bitmap.createBitmap(mBlurBitmap,0,0,tabLayout.getWidth(),132);
                BitmapDrawable ob = new BitmapDrawable(getResources(), croppedBitmap);
                tabLayout.setBackground(ob);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

        imgBg=findViewById(R.id.screen);


//        viewPager.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//
//                mBlurBitmap = createBlurBitmap();
//                mBlurBitmap=Bitmap.createBitmap(mBlurBitmap,0,0,1080,24);
//                BitmapDrawable ob = new BitmapDrawable(getResources(), mBlurBitmap);
//                tabLayout.setBackground(ob);
//
//            }
//        });
//        imgForTab=findViewById(R.id.)



        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "onAdFailedToLoad: ");
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened: ");
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked: ");
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication: ");
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed: ");
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });



    }

    private void handlePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        102);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 102) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Storage Granted", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Read Storage Granted");
                viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), 1, this));
                tabLayout.setupWithViewPager(viewPager);

            } else {
                Toast.makeText(this, "Can't view without Permission Granted", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Read Storage DECLINED");
                handlePermission();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "open saved gallery ", Toast.LENGTH_SHORT).show();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, Gallery.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }, 0);

                return true;
            case R.id.item2:
                Toast.makeText(this, "Rate us", Toast.LENGTH_SHORT).show();
                openBrowser();
                return true;
            case R.id.item3:
                Toast.makeText(this, "About us", Toast.LENGTH_SHORT).show();
                openBrowser();
                return true;
            case R.id.item4:
                Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show();
                openBrowser();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void openBrowser() {



        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook_link));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }



    public Bitmap captureView(View view) {
        //Find the view we are after
        //Create a Bitmap with the same dimensions
        Log.d(TAG, "captureView::::::: "+tabLayout.getMeasuredWidth()+" "+tabLayout.getWidth()+" "+
                tabLayout.getHeight()+" "+tabLayout.getMeasuredHeight()+" "+tabLayout.getMeasuredHeight());
        Bitmap image = Bitmap.createBitmap(tabLayout.getMeasuredWidth(),
                tabLayout.getHeight(),
                Bitmap.Config.ARGB_4444); //reduce quality and remove opacity
        //Draw the view inside the Bitmap
        Canvas canvas = new Canvas(image);
        view.draw(canvas);

        //Make it frosty
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF, 0x00222222); // lighten
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.setColorFilter(filter);
        canvas.drawBitmap(image, 0, 0, paint);

        return image;
    }

    public static void blurBitmapWithRenderscript(RenderScript rs, Bitmap bitmap2) {
        //this will blur the bitmapOriginal with a radius of 25 and save it in bitmapOriginal
        final Allocation input = Allocation.createFromBitmap(rs, bitmap2); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        // must be >0 and <= 25
        script.setRadius(25f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap2);
    }

    public Bitmap createBlurBitmap() {
        Bitmap bitmap = captureView(imgBg);
        if (bitmap != null) {
            blurBitmapWithRenderscript(rs, bitmap);
        }

        return bitmap;
    }


    @Override
    public void triggerEvent() {
        mBlurBitmap = createBlurBitmap();

        Log.d(TAG, "triggerEvent: "+tabLayout.getHeight()+" "+tabLayout.getMeasuredHeight()+" "+appBarLayout.getHeight()+" "+appBarLayout.getMeasuredHeight()+" "+tabLayoutWidth+" "+tabLayoutHeight);

        Bitmap croppedBitmap=Bitmap.createBitmap(mBlurBitmap,0,0,tabLayout.getWidth(),132);
        BitmapDrawable ob = new BitmapDrawable(getResources(), croppedBitmap);
        tabLayout.setBackground(ob);
    }
    @Override
    public void triggerEventVF() {

        mBlurBitmap = createBlurBitmap();

        Log.d(TAG, "triggerEvent: "+tabLayout.getHeight()+" "+tabLayout.getMeasuredHeight()+" "+appBarLayout.getHeight()+" "+appBarLayout.getMeasuredHeight()+" "+tabLayoutWidth+" "+tabLayoutHeight);

        Bitmap croppedBitmap=Bitmap.createBitmap(mBlurBitmap,0,0,tabLayout.getWidth(),132);
        BitmapDrawable ob = new BitmapDrawable(getResources(), croppedBitmap);
        tabLayout.setBackground(ob);
    }



    @Override
    protected void onDestroy() {
        if (mBlurBitmap != null) {
            mBlurBitmap.recycle();
        }
        super.onDestroy();
    }


}