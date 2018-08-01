package e.ddev.worldcupvibes.Utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Sumaiya Munira on 6/24/2018.
 */

public abstract class PagerFragmentBase extends Fragment {
    private boolean mResumed;

    @Override
    public  void setUserVisibleHint(final boolean isVisibleToUser) {
        final boolean needUpdate = mResumed && isVisibleToUser != getUserVisibleHint();
        super.setUserVisibleHint(isVisibleToUser);
        if (needUpdate) {
            if (isVisibleToUser) {
                this.onVisible();
            } else {
                this.onInvisible();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mResumed = true;
        if (this.getUserVisibleHint()) {
            this.onVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mResumed = false;
        this.onInvisible();
    }

    /**
     * Returns true if the fragment is in resumed state and userVisibleHint was set to true
     *
     * @return true if resumed and visible
     */
    protected final boolean isResumedAndVisible() {
        return mResumed && getUserVisibleHint();
    }

    /**
     * Called when onResume was called and userVisibleHint is set to true or vice-versa
     */
    protected abstract void onVisible();

    /**
     * Called when onStop was called or userVisibleHint is set to false
     */
    protected abstract void onInvisible();

    /**
     * Called when select tab is clicked for some action.
     * @return true if consumed the action
     */
    public boolean onSelectedTabClicked() {
        return false;
    }

    private ListView mListview;



    public void setListView(ListView list,int index)
    {

        mListview=list;

    }


    public ListView getListView()
    {

        return mListview;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
