package com.teamvoyager.whatsappstatussaver.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

import com.teamvoyager.whatsappstatussaver.Activity_VideoPlayer;
import com.teamvoyager.whatsappstatussaver.Adapters.VideoAdapter;
import com.teamvoyager.whatsappstatussaver.Models.StatusModel;
import com.teamvoyager.whatsappstatussaver.R;
import com.teamvoyager.whatsappstatussaver.Utils.MyConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoFragment extends Fragment {

    @BindView(R.id.recylerView_vedio) RecyclerView recyclerView;
    @BindView(R.id.proBar_vedio) ProgressBar progressBar;
    ArrayList<StatusModel> videoModelArrayList;
    Handler handler=new Handler();
    VideoAdapter videoAdapter;

    Bitmap mBlurredBitmap = null;
    Bitmap mBitmap1 = null;
    Bitmap mBitmap2 = null;
    RenderScript rs;
    View v1;
    View v2;
    private static final String TAG = "MyTag";
    TriggerVF triggerVF;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        triggerVF= (TriggerVF) context;

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_vedio,container,false);
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        videoModelArrayList=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        GridLayoutManager manager=new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        getStatusVideo();


        v1 = view.findViewById(R.id.imgBg);
        rs = RenderScript.create(getContext());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged:  newState = " + newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);



                Log.d(TAG, "onScrolled: dx = " + dx + "  dy = " + dy);
                int f = manager.findFirstVisibleItemPosition();
                int l = manager.findLastVisibleItemPosition();
                Log.d(TAG, "onScrolled: " + f + " : " + l);
                Bitmap blurredBitmap = captureView(v1);


                for (int i = f; i <= l; i++) {//optimization is possible here
                    v2 = recyclerView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.btn_savetoGallery);
                    mBitmap1 = loadBitmap(v1, v2,blurredBitmap);
                    setBackgroundOnView(v2, mBitmap1);
//                    mBitmap2 = loadBitmap(v1, v2);
//                    setBackgroundOnView(v2, mBitmap2);

                }
                triggerVF.triggerEventVF();


            }
        });

    }

    private void getStatusVideo() { //this is the code for searching for status files.(almost same as searching image.)

        if(MyConstants.STATUS_DIRECTORY.exists())
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //listing all status files present in the [WhatsApp/Media/.Statuses] folder.
                    File[] statusFiles= MyConstants.STATUS_DIRECTORY.listFiles();

                    if(statusFiles !=null && statusFiles.length>0)
                    {
                        Arrays.sort(statusFiles);
                        for(final File status : statusFiles)
                        {
                            StatusModel statusModel=new StatusModel(status,status.getName(),status.getAbsolutePath());

                            statusModel.setThumbnail(getThumbNail(statusModel));

                            if(statusModel.isVideo())
                            {
                                videoModelArrayList.add(statusModel);
                                Log.d("inside video=", "run: status model ");
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                videoAdapter=new VideoAdapter(getContext(),videoModelArrayList, VideoFragment.this);
                                recyclerView.setAdapter(videoAdapter);
                                videoAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(),"directory not found",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private Bitmap getThumbNail(StatusModel statusModel) {
        if(statusModel.isVideo())
        {/**[MediaStore.Video.Thumbnails.MICRO_KIND] this is deprecated*/
            return ThumbnailUtils.createVideoThumbnail(String.valueOf(statusModel.getFile().getAbsoluteFile()),
                    MediaStore.Video.Thumbnails.MICRO_KIND);
        }
        else
        {
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(statusModel.getFile().getAbsolutePath()),
                    MyConstants.THUMBSIZE,MyConstants.THUMBSIZE);
        }
    }

    public void downloadVideo(StatusModel statusModel) throws IOException {
        File file = new File(MyConstants.APP_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs(); //create new file.
        }
        File destinationFile = new File(file + File.separator + statusModel.getTitle());
        if (destinationFile.exists()) {
            destinationFile.delete();
        }
        copyFile(statusModel.getFile(),destinationFile);
        Toast.makeText(getActivity(),"downloaded",Toast.LENGTH_SHORT).show();

        //refreshing the Gallery,after saving the image.
        Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); /**(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE) may be depricated from API LEVEL 29 */
        intent.setData(Uri.fromFile(destinationFile));
        getActivity().sendBroadcast(intent);
    }

    private void copyFile(File file, File destinationFile) throws IOException {
        if(!destinationFile.getParentFile().exists())
        {
            destinationFile.getParentFile().mkdirs();
        }
        if(!destinationFile.exists())
        {
            destinationFile.createNewFile();
        }
        FileChannel source=null;
        FileChannel destination=null;

        source=new FileInputStream(file).getChannel();
        destination=new FileOutputStream(destinationFile).getChannel();
        destination.transferFrom(source,0,source.size());

        source.close();
        destination.close();
    }

    public void watch(StatusModel statusModel) {
        Intent intent=new Intent(getContext(), Activity_VideoPlayer.class);
        intent.putExtra("path",statusModel.getPath());
        startActivity(intent);
    }


    void setBackgroundOnView(View view, Bitmap bitmap) {
        Drawable d;
        if (bitmap != null) {
            d = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            ((RoundedBitmapDrawable) d).setCornerRadius(getResources().getDimensionPixelOffset(R.dimen.rounded_corner));

        } else {
            d = ContextCompat.getDrawable(getContext(), R.drawable.white_background);
        }
        view.setBackground(d);
    }


    private Bitmap loadBitmap(View backgroundView, View targetView,Bitmap blurredBitmap) {
        Rect backgroundBounds = new Rect();
        backgroundView.getHitRect(backgroundBounds);
        if (!targetView.getLocalVisibleRect(backgroundBounds)) {
            // NONE of the imageView is within the visible window
            return null;
        }

//        Bitmap blurredBitmap = captureView(backgroundView);
        //capture only the area covered by our target view
        int[] loc = new int[2];
        int[] bgLoc = new int[2];
        backgroundView.getLocationInWindow(bgLoc);
        targetView.getLocationInWindow(loc);
        Log.d("Test", "Blurred Background" + blurredBitmap.getHeight() + " width " + blurredBitmap.getWidth() + " bg" + backgroundView.getHeight());
        Log.d("Test", "background  " + bgLoc[0] + " " + bgLoc[1]);
        Log.d("Test", "target      " + loc[0] + " " + loc[1]);
        int height = targetView.getHeight();
        int y = loc[1];
        if (bgLoc[1] >= loc[1]) {
            //view is going off the screen at the top
            height -= (bgLoc[1] - loc[1]);
            if (y < 0)
                y = 0;
        }
        if (y + height > blurredBitmap.getHeight()+bgLoc[1]) {
//            height = blurredBitmap.getHeight() - y;
            height = blurredBitmap.getHeight()+bgLoc[1] - y;
            Log.d("TAG", "Height = " + height);
            if (height <= 0) {
                //below the screen
                return null;
            }
        }
        Log.d("TAG", "loadBitmap: "+targetView.getX()+"  :: "+targetView.getY()+" : "+targetView.getMeasuredWidth());
        Matrix matrix = new Matrix();

        int startPoint=y-bgLoc[1];
        if (startPoint<0){
            startPoint=0;
        }
        matrix.setScale(0.5f, 0.5f);
        Bitmap bitmap = Bitmap.createBitmap(blurredBitmap,
                (int) targetView.getX(),
                startPoint,
                targetView.getMeasuredWidth(),
                height,
                matrix,
                true);

        return bitmap;
    }

    Bitmap captureView(View view) {
        if (mBlurredBitmap != null) {
            return mBlurredBitmap;
        }
        //Find the view we are after
        //Create a Bitmap with the same dimensions
        mBlurredBitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_4444); //reduce quality and remove opacity
        //Draw the view inside the Bitmap
        Canvas canvas = new Canvas(mBlurredBitmap);
        view.draw(canvas);

        //blur it
        blurBitmapWithRenderscript(rs, mBlurredBitmap);

        //Make it frosty
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF, 0x00222222); // lighten
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.setColorFilter(filter);
        canvas.drawBitmap(mBlurredBitmap, 0, 0, paint);

        return mBlurredBitmap;
    }

//    .}

    static void blurBitmapWithRenderscript(RenderScript rs, Bitmap bitmap2) {
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

    public interface TriggerVF{
        void triggerEventVF();
    }
}
