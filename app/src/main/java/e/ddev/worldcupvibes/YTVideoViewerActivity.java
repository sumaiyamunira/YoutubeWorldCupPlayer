
package e.ddev.worldcupvibes;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import e.ddev.worldcupvibes.DataModel.YTVideoDTO;
import e.ddev.worldcupvibes.Utils.CustomHorizontalViewPager;
import e.ddev.worldcupvibes.Utils.CustomLinearLayoutManager;
import e.ddev.worldcupvibes.Utils.HttpParser;
import e.ddev.worldcupvibes.Utils.YTVideoConstants;
import e.ddev.worldcupvibes.Utils.YTVideoHelper;
import e.ddev.worldcupvibes.adapter.YTPopularVideosAdapter;
import e.ddev.worldcupvibes.adapter.YTVideoSwipeAdapter;
import e.ddev.worldcupvibes.callbacks.YTListItemClickListener;

public class YTVideoViewerActivity extends AppCompatActivity {
    private static final String TAG = "YTVideoViewerActivity";
    private CustomHorizontalViewPager view_pager;
    public ArrayList<YTVideoDTO> swipeFragments = new ArrayList<>();
    private YTVideoSwipeAdapter ytVideoSwipeAdapter;
    private YTVideoViewerActivity.ActivityToFragmentCallback activityToFragmentInteraction;
    private Intent intentExtra;
    private boolean alreadyRequest = false;
    public String nextPageToken = "";
    public boolean isSingleItem = false;
    public static String NEXT_PAGE_TOKEN = "NEXT_PAGE_TOKEN";
    public static String IS_SINGLE_ITEM = "IS_SINGLE_ITEM";
    public static String IS_FROM_SEARCH = "IS_FROM_SEARCH";
    public static String SEARCH_KEY = "SEARCH_KEY";
    public YTPopularVideosAdapter yt_video_live_adapter;
    public RecyclerView popular_videos_rv;
    private boolean isFromSearch;
    private String searchKey = "world cup 2018";
    public boolean isInitializationComplete = false;
    int lastVisibleItem, totalItemCount;
    int visibleThreshold = 5;

    private ArrayList<YTVideoDTO> lastaddedItems;
    public static String EXTRA_SELECTED_ITEM_POSITION = "selectedItemPosition";
    public static String EXTRA_YTVIDEODTO_ARRAYLIST = "ytVideoArrayList";
    private ArrayList<YTVideoDTO> YT_SWIPABLE_VIDEO_LIST = new ArrayList<>();
    private HashSet<String> duplicateVideo = new HashSet<>();
    private int SELECTED_INDEX;

    public interface ActivityToFragmentCallback {
        void activityOnConfigurationChanged(Configuration newConfig);

        void activityOnkeyDown(int keyCode, KeyEvent event);

        void activityOnpause();

        void activityOnResume();

        void activityOnDestroy();

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);

    }

    public enum ListHolder {
        INSTANCE;
        private ArrayList<YTVideoDTO> streamersList;

        public static void setList(ArrayList<YTVideoDTO> streamersList) {
            INSTANCE.streamersList = streamersList;

        }

        public static ArrayList<YTVideoDTO> getStreamerList() {
            final ArrayList<YTVideoDTO> streamersList = INSTANCE.streamersList;
            return streamersList;
        }

        public static void remoteList() {
            INSTANCE.streamersList = null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        intentExtra = intent;
        if(intentExtra.hasExtra(YTVideoViewerActivity.NEXT_PAGE_TOKEN)){
            nextPageToken = intentExtra.getStringExtra(YTVideoViewerActivity.NEXT_PAGE_TOKEN);
        }

        if(intentExtra.hasExtra(YTVideoViewerActivity.IS_SINGLE_ITEM)){
            isSingleItem = intentExtra.getBooleanExtra(YTVideoViewerActivity.IS_SINGLE_ITEM, false);
        }
        if(intentExtra.hasExtra(YTVideoViewerActivity.IS_FROM_SEARCH)){
            isFromSearch = intentExtra.getBooleanExtra(YTVideoViewerActivity.IS_FROM_SEARCH, false);
        }

        if(intentExtra.hasExtra(YTVideoViewerActivity.SEARCH_KEY)){
            searchKey = intentExtra.getStringExtra(YTVideoViewerActivity.SEARCH_KEY);
        }

        if(intentExtra.hasExtra(YTVideoViewerActivity.EXTRA_YTVIDEODTO_ARRAYLIST)){
            YT_SWIPABLE_VIDEO_LIST = (ArrayList<YTVideoDTO>) intentExtra.getSerializableExtra(YTVideoViewerActivity.EXTRA_YTVIDEODTO_ARRAYLIST);
        }

        if(intentExtra.hasExtra(YTVideoViewerActivity.EXTRA_SELECTED_ITEM_POSITION)){
            SELECTED_INDEX =  intentExtra.getIntExtra(YTVideoViewerActivity.EXTRA_SELECTED_ITEM_POSITION,0);
        }


        YTVideoConstants.IS_ACTIVITY_OLD_SESSION = false;


        createViewPager();
        YTVideoHelper.getInstance().setVideoSessionRunning(true);

    }

    public static void startVideoViewerActivity(Activity mActivity, ArrayList<YTVideoDTO> ytVideoDTOS, int selectedIndex, String nextPageToken, boolean isFromSearch, String searchKey) {
        if (YTVideoHelper.isNetworkConnected(mActivity)) {
            YTVideoViewerActivity.ListHolder.setList(ytVideoDTOS);
            Intent mIntent = new Intent(mActivity, YTVideoViewerActivity.class);
            mIntent.putExtra(YTVideoViewerActivity.EXTRA_YTVIDEODTO_ARRAYLIST, ytVideoDTOS);
            mIntent.putExtra(YTVideoViewerActivity.EXTRA_SELECTED_ITEM_POSITION, selectedIndex);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            mIntent.putExtra(YTVideoViewerActivity.IS_FROM_SEARCH, isFromSearch);
            mIntent.putExtra(YTVideoViewerActivity.SEARCH_KEY, searchKey);
            mIntent.putExtra(YTVideoViewerActivity.NEXT_PAGE_TOKEN, nextPageToken);
            mActivity.startActivity(mIntent);
        } else {
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yt_video_view_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        intentExtra = getIntent();
        if(intentExtra.hasExtra(YTVideoViewerActivity.NEXT_PAGE_TOKEN)){
            nextPageToken = intentExtra.getStringExtra(YTVideoViewerActivity.NEXT_PAGE_TOKEN);
        }

        if(intentExtra.hasExtra(YTVideoViewerActivity.IS_SINGLE_ITEM)){
            isSingleItem = intentExtra.getBooleanExtra(YTVideoViewerActivity.IS_SINGLE_ITEM, false);
        }

        if(intentExtra.hasExtra(YTVideoViewerActivity.IS_FROM_SEARCH)){
            isFromSearch = intentExtra.getBooleanExtra(YTVideoViewerActivity.IS_FROM_SEARCH, false);
        }
        if(intentExtra.hasExtra(YTVideoViewerActivity.SEARCH_KEY)){
            searchKey = intentExtra.getStringExtra(YTVideoViewerActivity.SEARCH_KEY);
        }


        if(intentExtra.hasExtra(YTVideoViewerActivity.EXTRA_YTVIDEODTO_ARRAYLIST)){
            YT_SWIPABLE_VIDEO_LIST = (ArrayList<YTVideoDTO>) intentExtra.getSerializableExtra(YTVideoViewerActivity.EXTRA_YTVIDEODTO_ARRAYLIST);
        }
        if(intentExtra.hasExtra(YTVideoViewerActivity.EXTRA_SELECTED_ITEM_POSITION)){
            SELECTED_INDEX =  intentExtra.getIntExtra(YTVideoViewerActivity.EXTRA_SELECTED_ITEM_POSITION,0);
        }

        if (intentExtra.hasExtra(YTVideoConstants.LIVE_ACTIVITY_OLD_SESSION) && intentExtra.getBooleanExtra(YTVideoConstants.LIVE_ACTIVITY_OLD_SESSION, false)) {
            YTVideoConstants.IS_ACTIVITY_OLD_SESSION = true;
        } else {
            YTVideoConstants.IS_ACTIVITY_OLD_SESSION = false;
        }

        createViewPager();
        YTVideoHelper.getInstance().setVideoSessionRunning(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (activityToFragmentInteraction != null) {
            activityToFragmentInteraction.activityOnConfigurationChanged(newConfig);
        }
    }


    @Override
    protected void onPause() {
        if (activityToFragmentInteraction != null)
            activityToFragmentInteraction.activityOnpause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (activityToFragmentInteraction != null)
            activityToFragmentInteraction.activityOnDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (activityToFragmentInteraction != null)
            activityToFragmentInteraction.activityOnkeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;

        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (activityToFragmentInteraction != null)
            activityToFragmentInteraction.activityOnResume();
    }



    private void createViewPager() {
        view_pager = (CustomHorizontalViewPager) findViewById(R.id.view_pager);

        if (!YTVideoConstants.IS_ACTIVITY_OLD_SESSION) {
            for (YTVideoDTO ytVideo : YT_SWIPABLE_VIDEO_LIST) {
                duplicateVideo.add(ytVideo.getVideoID());
            }
        }

        swipeFragments = new ArrayList<>();
        swipeFragments.addAll(YT_SWIPABLE_VIDEO_LIST);
        ytVideoSwipeAdapter = new YTVideoSwipeAdapter(getSupportFragmentManager(), swipeFragments);

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (YT_SWIPABLE_VIDEO_LIST.size() - position <= 5  && !alreadyRequest) {
                    refreshItems();
                }

                if (activityToFragmentInteraction != null)
                    activityToFragmentInteraction.onPageSelected(position);

                SELECTED_INDEX = position;

               try{
                    if(position < YT_SWIPABLE_VIDEO_LIST.size()) {
                        yt_video_live_adapter.setCurrentSelectedPlayerVideoID(YT_SWIPABLE_VIDEO_LIST.get(position).getVideoID());
                    }
                    yt_video_live_adapter.notifyDataSetChanged();
                    popular_videos_rv.scrollToPosition(position);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (activityToFragmentInteraction != null)
                    activityToFragmentInteraction.onPageScrollStateChanged(state);
            }

        });


        view_pager.setAdapter(ytVideoSwipeAdapter);
        view_pager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        view_pager.setCurrentItem(SELECTED_INDEX);
        setUpBottomList();
    }

    private void setUpBottomList() {
        LinearLayout yt_bottom_view_holder = (LinearLayout) findViewById(R.id.yt_bottom_view_holder);
        yt_bottom_view_holder.setVisibility(View.VISIBLE);
        popular_videos_rv = (android.support.v7.widget.RecyclerView) findViewById(R.id.popular_recycle_view);
        popular_videos_rv.setHasFixedSize(true);
        final CustomLinearLayoutManager layoutManager = new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        popular_videos_rv.setLayoutManager(layoutManager);
        yt_video_live_adapter = new YTPopularVideosAdapter(this, YT_SWIPABLE_VIDEO_LIST, nextPageToken, YTPopularVideosAdapter.MAIN_LIST, searchKey, false);
        yt_video_live_adapter.setIsMainPlayerView(true);
        yt_video_live_adapter.setCallBackListener(new YTListItemClickListener() {
            @Override
            public void onItemClick(YTVideoDTO ytD, int position) {
                view_pager.setCurrentItem(position);
            }
        });


        if(YT_SWIPABLE_VIDEO_LIST != null) {
            yt_video_live_adapter.setAddItems(YT_SWIPABLE_VIDEO_LIST);
            if(SELECTED_INDEX < YT_SWIPABLE_VIDEO_LIST.size()) {
                yt_video_live_adapter.setCurrentSelectedPlayerVideoID(YT_SWIPABLE_VIDEO_LIST.get(SELECTED_INDEX).getVideoID());
            }
            popular_videos_rv.setAdapter(yt_video_live_adapter);

            if(isSingleItem) {
                if (!alreadyRequest) {
                    refreshItems();
                }
            }
        }

        popular_videos_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!alreadyRequest && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    //End of the items
                    refreshItems();

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //NEED TO TEST THIS
        if(!isInitializationComplete) return;
        super.onBackPressed();
    }

    public void refreshItems() {
        alreadyRequest = true;
        makeAPIrequest();
    }

    private void makeAPIrequest() {
        String url = "";
        if(!isSingleItem){
            if((""+nextPageToken).length()>0){
               String  orderContent = "relevance";
                url = "https://www.googleapis.com/youtube/v3/search?pageToken="+nextPageToken+"&part=snippet&maxResults=25&order="+orderContent+"&q="+searchKey+"&type=video&key=" + YTVideoListActivity.API_KEY;
            } else {
                Toast.makeText(YTVideoViewerActivity.this, "No more item", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetVideoListAsyncTask(url, false, getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetVideoListAsyncTask(url, false, getApplicationContext()).execute();
        }

    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class GetVideoListAsyncTask extends AsyncTask<Void, Void, Void> {
        private String response;
        private String url;
        private boolean isliveURL;
        private Context context;

        public GetVideoListAsyncTask(String url, boolean isliveURL, Context context) {
            this.url = (""+url).replace(" ", "%20");
            this.isliveURL = isliveURL;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                response = HttpParser.sendYouTubeHttpReq(url,context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            try {
                if(!isliveURL){
                    ArrayList<YTVideoDTO> Temp_YTVideoDTOArrayListPopular = new ArrayList<>();
                    if(isSingleItem){
                        Temp_YTVideoDTOArrayListPopular = YTVideoHelper.parseYTVideoDTOList(response);
                    } else {
                        Temp_YTVideoDTOArrayListPopular = YTVideoHelper.parseYTVideoDTOList(response);
                    }

                    if(Temp_YTVideoDTOArrayListPopular.size()>0){
                        if(new JSONObject(response).has("nextPageToken")){
                            nextPageToken = new JSONObject(response).getString("nextPageToken");
                        } else {
                            nextPageToken = "";
                        }

                        String videoIds = "";
                        for(int i =0; i<Temp_YTVideoDTOArrayListPopular.size();i++){
                            if(videoIds.equalsIgnoreCase("")){
                                videoIds = Temp_YTVideoDTOArrayListPopular.get(i).getVideoID();
                            } else {
                                videoIds = videoIds+","+Temp_YTVideoDTOArrayListPopular.get(i).getVideoID();
                            }

                            if(i == (Temp_YTVideoDTOArrayListPopular.size()-1)){
                                String urlForContentDetails = "https://www.googleapis.com/youtube/v3/videos?id="+videoIds+"&part=contentDetails,status,statistics&key="+YTVideoListActivity.API_KEY;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    new GetContentDetailsAsyncTask(urlForContentDetails, Temp_YTVideoDTOArrayListPopular, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    new GetContentDetailsAsyncTask(urlForContentDetails, Temp_YTVideoDTOArrayListPopular, context).execute();
                                }
                            }

                        }

                    } else {
                        Toast.makeText(YTVideoViewerActivity.this, "No more item", Toast.LENGTH_SHORT).show();
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }



        }
    }




    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class GetContentDetailsAsyncTask extends AsyncTask<Void, Void, Void> {
        private String response;
        private String url;
        private boolean isliveURL;
        private ArrayList<YTVideoDTO> parseYTVideoDTOList;
        private Context context;

        public GetContentDetailsAsyncTask(String url, ArrayList<YTVideoDTO> parseYTVideoDTOList, Context context)  {
            this.url = (""+url).replace(" ", "%20");
            this.isliveURL = isliveURL;
            this.parseYTVideoDTOList = parseYTVideoDTOList;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                response = HttpParser.sendYouTubeHttpReq(url,context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            alreadyRequest = false;
            try {
                if(!isliveURL){
                    ArrayList<YTVideoDTO> Temp_YTVideoDTOArrayListPopular = YTVideoHelper.parseYTVideoDetailsDTOList(response,parseYTVideoDTOList);
                    lastaddedItems = new ArrayList<YTVideoDTO>();

                    if(Temp_YTVideoDTOArrayListPopular.size()>0){
                        for(int i = 0; i<Temp_YTVideoDTOArrayListPopular.size(); i++){

                            if(!duplicateVideo.contains(Temp_YTVideoDTOArrayListPopular.get(i).getVideoID())){
                                duplicateVideo.add(Temp_YTVideoDTOArrayListPopular.get(i).getVideoID());
                                lastaddedItems.add(Temp_YTVideoDTOArrayListPopular.get(i));
                                YT_SWIPABLE_VIDEO_LIST.add(Temp_YTVideoDTOArrayListPopular.get(i));
                            }



                            if(i == (Temp_YTVideoDTOArrayListPopular.size()-1)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(yt_video_live_adapter!=null){
                                            yt_video_live_adapter.setAddItems(lastaddedItems);
                                            notifyViewPager(lastaddedItems);
                                        }
                                    }
                                });

                            }
                        }

                    } else {
                        Toast.makeText(YTVideoViewerActivity.this, "No more item", Toast.LENGTH_SHORT).show();
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyViewPager(ArrayList<YTVideoDTO> latestItems) {
        swipeFragments = new ArrayList<>();
        swipeFragments.addAll(YT_SWIPABLE_VIDEO_LIST);
        if (ytVideoSwipeAdapter != null) {
            ytVideoSwipeAdapter.setAddItems(swipeFragments);
            ytVideoSwipeAdapter.notifyDataSetChanged();
        }
    }

}
