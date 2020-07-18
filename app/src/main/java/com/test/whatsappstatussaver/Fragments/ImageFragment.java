package com.test.whatsappstatussaver.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.whatsappstatussaver.Adapters.ImageAdapter;
import com.test.whatsappstatussaver.Models.StatusModel;
import com.test.whatsappstatussaver.R;
import com.test.whatsappstatussaver.Utils.MyConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageFragment extends Fragment {

    @BindView(R.id.recylerView)
    RecyclerView recyclerView;
    @BindView(R.id.proBar)
    ProgressBar progressBar;

    ArrayList<StatusModel> imageModelArrayList;
    Handler handler = new Handler();
    ImageAdapter imageAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        imageModelArrayList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        getStatus();
    }

    private void getStatus() { //this is the code for searching for status files.
        if (MyConstants.STATUS_DIRECTORY.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //listing all status files present in the [WhatsApp/Media/.Statuses] folder.
                    File[] statusFiles = MyConstants.STATUS_DIRECTORY.listFiles();

                    if (statusFiles != null && statusFiles.length > 0) {
                        Arrays.sort(statusFiles);
                        for (final File status : statusFiles) {
                            StatusModel statusModel = new StatusModel(status, status.getName(), status.getAbsolutePath());

                            statusModel.setThumbnail(getThumbNail(statusModel));

                            if (!statusModel.isVideo()) {
                                imageModelArrayList.add(statusModel);
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                imageAdapter = new ImageAdapter(getContext(), imageModelArrayList, ImageFragment.this);
                                recyclerView.setAdapter(imageAdapter);
                                imageAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "directory not found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private Bitmap getThumbNail(StatusModel statusModel) {
        if (statusModel.isVideo()) {/**[MediaStore.Video.Thumbnails.MICRO_KIND] this is deprecated*/
            return ThumbnailUtils.createVideoThumbnail(String.valueOf(statusModel.getFile().getAbsoluteFile()),
                    MediaStore.Video.Thumbnails.MICRO_KIND);
        } else {
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(statusModel.getFile().getAbsolutePath()),
                    MyConstants.THUMBSIZE, MyConstants.THUMBSIZE);
        }
    }

    public void downloadImage(StatusModel statusModel) throws IOException {
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

}
