package e.ddev.worldcupvibes.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import java.util.List;

import e.ddev.worldcupvibes.DataModel.YTVideoDTO;
import e.ddev.worldcupvibes.fragment.YTVideoViewFragment;

/**
 * Created by Sumaiya on 05-Nov-17.
 */

public class YTVideoSwipeAdapter extends FragmentStatePagerAdapter {
    private List<YTVideoDTO> fragments;

    public YTVideoSwipeAdapter(FragmentManager fm, List<YTVideoDTO> fragments) {
        super(fm);
        this.fragments = fragments;
    }


    @Override
    public Fragment getItem(int position) {
        boolean isSingleItem = false;
        if(getCount()==1){
            isSingleItem = true;
        }

        return YTVideoViewFragment.newInstance(fragments.get(position), isSingleItem);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void setAddItems(List<YTVideoDTO> fragments) {
        this.fragments = fragments;

    }


}