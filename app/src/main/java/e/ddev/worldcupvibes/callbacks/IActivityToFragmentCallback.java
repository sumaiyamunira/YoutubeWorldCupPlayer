package e.ddev.worldcupvibes.callbacks;

import android.content.res.Configuration;
import android.view.KeyEvent;

/**
 * Created by Sumaiya on 05-Nov-17.
 */

public interface IActivityToFragmentCallback {
    void activityOnConfigurationChanged(Configuration newConfig);

    void activityOnkeyDown(int keyCode, KeyEvent event);

    void activityOnpause();

    void activityOnResume();

    void activityOnDestroy();

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

}
