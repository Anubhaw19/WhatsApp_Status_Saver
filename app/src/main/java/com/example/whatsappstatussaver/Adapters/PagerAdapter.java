package com.example.whatsappstatussaver.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.whatsappstatussaver.Fragments.ImageFragment;
import com.example.whatsappstatussaver.Fragments.VideoFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    private ImageFragment imageFragment;
    private VideoFragment videoFragment;

    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);          /**this may be depricated*/
        imageFragment=new ImageFragment();
      videoFragment =new VideoFragment();
    }
//    public PagerAdapter(@NonNull FragmentManager fm) {
//        super(fm, behavior);
//        imageFragment=new ImageFragment();
//        vedioFragment=new VedioFragment();
//    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0)
        {
            return imageFragment;
        }
        else
        {
            return videoFragment;
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0)
        {
            return "images";
        }
        else {
            return "videos";
        }
    }

    @Override
    public int getCount() {
        return 2;//since we have only two fargments
    }
}
