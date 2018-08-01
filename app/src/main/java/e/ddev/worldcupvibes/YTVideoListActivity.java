package e.ddev.worldcupvibes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import e.ddev.worldcupvibes.DataModel.YTVideoDTO;
import e.ddev.worldcupvibes.Utils.CustomLinearLayoutManager;
import e.ddev.worldcupvibes.Utils.HttpParser;
import e.ddev.worldcupvibes.Utils.YTVideoHelper;
import e.ddev.worldcupvibes.Utils.YouTubeConstant;
import e.ddev.worldcupvibes.adapter.YTPopularVideosAdapter;

public class YTVideoListActivity extends AppCompatActivity {

    public String TAG = "YTVideoListActivity";
    public static String SEARCH_VALUE = "EXTRA_SEARCH_VALUE";
    public static String VIEW_TYPE = "EXTRA_VIEW_TYPE";
    public static String IS_PICKER_VIEW = "EXTRA_IS_PICKER_VIEW";

    private static final int VIEW_TYPE_MAIN = 0;
    private static final int VIEW_TYPE_SEARCH = 1;
    private static final int VIEW_TYPE_PICKER = 2;
    public static final int PICKER_RESULT_CODE = 130;
    public static final String KEY_RESULT_VIRAL_LINK = "key_result_viral_link";


    private android.support.v7.widget.RecyclerView popular_videos_rv;
    private ArrayList<YTVideoDTO> YTVideoDTOArrayListPopular;

    public static final String API_KEY = YouTubeConstant.getApiKey();
    private LinearLayoutManager linearLayoutManagerPopular;
    private ArrayList<YTVideoDTO> lastaddedItems;
    private String nextPageToken = "";
    private HashMap<String, YTVideoDTO> mapYTVideoDTOwithVideoID;

    private boolean alreadyRequest, firstRequestMade;
    private YTPopularVideosAdapter ytPopularVideosAdapter;
    private String searchKey = "world cup 2018";
    int lastVisibleItem, totalItemCount;
    int visibleThreshold = 5;
    private LinearLayout default_loading_view;
    private int viewType = 0;
    private boolean isPickerView = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytvideo_list);
        viewType = VIEW_TYPE_MAIN;
        initUI();
        makeAPIrequest();
    }

    private void makeAPIrequest() {
        String url = "";
        if (!firstRequestMade) {
            String orderContent = "relevance";
            url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=25&order=" + orderContent + "&q=" + searchKey + "&type=video&key=" + API_KEY;
        } else {
            if (("" + nextPageToken).length() > 0) {
                String orderContent = "relevance";
                url = "https://www.googleapis.com/youtube/v3/search?pageToken=" + nextPageToken + "&part=snippet&maxResults=25&order=" + orderContent + "&q=" + searchKey + "&type=video&key=" + API_KEY;
            } else {
                Toast.makeText(YTVideoListActivity.this, "No more item", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetVideoListAsyncTask(url, false, getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetVideoListAsyncTask(url, false, getApplicationContext()).execute();
        }
    }

    private void initUI() {
        firstRequestMade = false;
        mapYTVideoDTOwithVideoID = new HashMap<>();
        default_loading_view = (LinearLayout) findViewById(R.id.default_loading_view);
        popular_videos_rv = (android.support.v7.widget.RecyclerView) findViewById(R.id.popular_videos_rv);
        YTVideoDTOArrayListPopular = new ArrayList<>();


        popular_videos_rv.setNestedScrollingEnabled(false);
        popular_videos_rv.setHasFixedSize(true);

        linearLayoutManagerPopular = new LinearLayoutManager(this, CustomLinearLayoutManager.VERTICAL, false);
        linearLayoutManagerPopular.setAutoMeasureEnabled(false);
        popular_videos_rv.setLayoutManager(linearLayoutManagerPopular);


        int adapterType = 0;

        adapterType = YTPopularVideosAdapter.MAIN_LIST;
        ytPopularVideosAdapter = new YTPopularVideosAdapter(this, YTVideoDTOArrayListPopular, nextPageToken, adapterType, searchKey, isPickerView);
        popular_videos_rv.setHasFixedSize(true);
        popular_videos_rv.setAdapter(ytPopularVideosAdapter);


        popular_videos_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManagerPopular.getItemCount();
                lastVisibleItem = linearLayoutManagerPopular.findLastVisibleItemPosition();
                if (!alreadyRequest && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    //End of the items
                    refreshItems();

                }
            }
        });


    }

    void refreshItems() {
        alreadyRequest = true;
        makeAPIrequest();
    }

    void onItemsLoadComplete() {
        firstRequestMade = true;
        alreadyRequest = false;
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class GetVideoListAsyncTask extends AsyncTask<Void, Void, Void> {
        private String response;
        private String url;
        private boolean isliveURL;
        private Context context;

        public GetVideoListAsyncTask(String url, boolean isliveURL, Context context) {
            this.url = ("" + url).replace(" ", "%20");
            this.isliveURL = isliveURL;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            default_loading_view.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                response = HttpParser.sendYouTubeHttpReq(url, context);
                Log.e(TAG, "youtube video list url:" + url + " response:" + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (!isliveURL) {
                    ArrayList<YTVideoDTO> Temp_YTVideoDTOArrayListPopular = YTVideoHelper.parseYTVideoDTOList(response);
                    if (Temp_YTVideoDTOArrayListPopular.size() > 0) {
                        if (new JSONObject(response).has("nextPageToken")) {
                            nextPageToken = new JSONObject(response).getString("nextPageToken");
                        } else {
                            nextPageToken = "";
                        }

                        String videoIds = "";
                        for (int i = 0; i < Temp_YTVideoDTOArrayListPopular.size(); i++) {
                            if (videoIds.equalsIgnoreCase("")) {
                                videoIds = Temp_YTVideoDTOArrayListPopular.get(i).getVideoID();
                            } else {
                                videoIds = videoIds + "," + Temp_YTVideoDTOArrayListPopular.get(i).getVideoID();
                            }

                            if (i == (Temp_YTVideoDTOArrayListPopular.size() - 1)) {
                                String urlForContentDetails = "https://www.googleapis.com/youtube/v3/videos?id=" + videoIds + "&part=contentDetails,status,statistics&key=" + API_KEY;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    new GetContentDetailsAsyncTask(urlForContentDetails, Temp_YTVideoDTOArrayListPopular, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    new GetContentDetailsAsyncTask(urlForContentDetails, Temp_YTVideoDTOArrayListPopular, context).execute();
                                }
                            }

                        }

                    } else {
                        onItemsLoadComplete();
                        Toast.makeText(YTVideoListActivity.this, "No more item", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onItemsLoadComplete();
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

        public GetContentDetailsAsyncTask(String url, ArrayList<YTVideoDTO> parseYTVideoDTOList, Context context) {
            this.url = ("" + url).replace(" ", "%20");
            this.isliveURL = isliveURL;
            this.parseYTVideoDTOList = parseYTVideoDTOList;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            default_loading_view.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                response = HttpParser.sendYouTubeHttpReq(url, context);
                Log.e(TAG, "youtube contentdetails list url:" + url + " response:" + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            default_loading_view.setVisibility(View.GONE);

            try {
                onItemsLoadComplete();
                if (!isliveURL) {
                    ArrayList<YTVideoDTO> Temp_YTVideoDTOArrayListPopular = YTVideoHelper.parseYTVideoDetailsDTOList(response, parseYTVideoDTOList);
                    lastaddedItems = new ArrayList<YTVideoDTO>();

                    if (Temp_YTVideoDTOArrayListPopular.size() > 0) {
                        for (int i = 0; i < Temp_YTVideoDTOArrayListPopular.size(); i++) {
                            if (!mapYTVideoDTOwithVideoID.containsKey(Temp_YTVideoDTOArrayListPopular.get(i).getVideoID())) {
                                mapYTVideoDTOwithVideoID.put(Temp_YTVideoDTOArrayListPopular.get(i).getVideoID(), Temp_YTVideoDTOArrayListPopular.get(i));
                                lastaddedItems.add(Temp_YTVideoDTOArrayListPopular.get(i));
                            }


                            if (i == (Temp_YTVideoDTOArrayListPopular.size() - 1)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        YTVideoDTOArrayListPopular = new ArrayList<YTVideoDTO>(mapYTVideoDTOwithVideoID.values());
                                        if (ytPopularVideosAdapter != null) {
                                            ytPopularVideosAdapter.setAddItems(lastaddedItems);
                                            ytPopularVideosAdapter.updateNextPageToken(nextPageToken);
                                        } else {
                                            //Constants.debugLog(TAG, "adapter null ");
                                            int adapterType = 0;
                                            boolean isPickerView = false;
                                            if (viewType == VIEW_TYPE_MAIN) {
                                                adapterType = YTPopularVideosAdapter.MAIN_LIST;
                                            } else if (viewType == VIEW_TYPE_SEARCH) {
                                                adapterType = YTPopularVideosAdapter.SEARCH_LIST;
                                            } else if (viewType == VIEW_TYPE_PICKER) {
                                                adapterType = YTPopularVideosAdapter.MAIN_LIST;
                                                isPickerView = true;
                                            }
                                            ytPopularVideosAdapter = new YTPopularVideosAdapter(YTVideoListActivity.this, YTVideoDTOArrayListPopular, nextPageToken, adapterType, searchKey, isPickerView);
                                            ytPopularVideosAdapter.setAddItems(YTVideoDTOArrayListPopular);
                                            popular_videos_rv.setAdapter(ytPopularVideosAdapter);
                                        }


                                    }
                                });

                            }
                        }

                    } else {
                        Toast.makeText(YTVideoListActivity.this, "No more item", Toast.LENGTH_SHORT).show();
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == YTVideoListActivity.PICKER_RESULT_CODE) {
            if (data != null) {
                if (data.hasExtra(YTVideoListActivity.KEY_RESULT_VIRAL_LINK)) {
                    String linkUrl = data.getStringExtra(YTVideoListActivity.KEY_RESULT_VIRAL_LINK);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(YTVideoListActivity.KEY_RESULT_VIRAL_LINK, linkUrl);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        }
    }
}
