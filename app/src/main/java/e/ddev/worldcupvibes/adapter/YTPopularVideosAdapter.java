package e.ddev.worldcupvibes.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


import e.ddev.worldcupvibes.DataModel.YTVideoDTO;
import e.ddev.worldcupvibes.R;
import e.ddev.worldcupvibes.Utils.YTVideoHelper;
import e.ddev.worldcupvibes.YTVideoListActivity;
import e.ddev.worldcupvibes.YTVideoViewerActivity;
import e.ddev.worldcupvibes.callbacks.YTListItemClickListener;

/**
 * Created by Sumaiya on 05-Nov-17.
 */

public class YTPopularVideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = "YTLiveVideosAdapter";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private int type;
    public static final int MAIN_LIST =0;
    public static final int PLAYER_LIST =1;
    public static final int SEARCH_LIST =2;

    private boolean isPickerView = false;
    private boolean isMainPlayerView = false;



    private Activity mActivity;
    private ArrayList<YTVideoDTO> mTAgList;

    private YTListItemClickListener addListener;
    private String nextPageToken;
    private String searchKey = "";
    private String playerCurrentSelected_VideoID ="";

    public YTPopularVideosAdapter(Activity activity, ArrayList<YTVideoDTO> tagList, String nextPageToken, int type, String searchKey, boolean isPickerView) {
        this.mActivity = activity;
        this.mTAgList = new ArrayList<YTVideoDTO>();
        this.type = type;
        this.searchKey = searchKey;
        this.nextPageToken = nextPageToken;
        this.isPickerView = isPickerView;
    }


    public void setCurrentSelectedPlayerVideoID(String videoID) {
        this.playerCurrentSelected_VideoID = videoID;
    }

    public void setIsMainPlayerView(boolean isMainPlayerView) {
        this.isMainPlayerView = isMainPlayerView;
    }


    public void setCallBackListener(YTListItemClickListener callBackListener) {
        this.addListener = callBackListener;
    }

    public void updateNextPageToken (String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }



    public void setAddItems(ArrayList<YTVideoDTO> tagList) {
        if(type == MAIN_LIST){
            final int prevPos = tagList.size();
            mTAgList.addAll(tagList);
            notifyItemRangeInserted(prevPos, tagList.size());
        } else {
            final int prevPos = tagList.size();
            mTAgList.addAll(tagList);
            notifyItemRangeInserted(prevPos, tagList.size());
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        if(type == MAIN_LIST){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yt_video_popular_layout, null);
            return new YTPopularVideosAdapter.VideoHolder(view);

        } else if (type == SEARCH_LIST){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yt_video_popular_layout, null);
            return new YTPopularVideosAdapter.VideoHolder(view);
        } else if (type == PLAYER_LIST){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_player_view_list_item, null);
            return new YTPopularVideosAdapter.VideoHolderPlayer(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");



    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(type == MAIN_LIST) {
           if (holder instanceof VideoHolder){
                final YTVideoDTO dto = mTAgList.get(position);
                YTPopularVideosAdapter.VideoHolder videoHolder = (YTPopularVideosAdapter.VideoHolder) holder;
                if (dto.getImgThumbnailHigh() != null && dto.getImgThumbnailHigh().length() > 0) {
                    YTVideoHelper.loadRatioImage(mActivity,dto.getImgThumbnailHigh(), videoHolder.yt_video_image);
                }

                if(dto.getVideoType().equalsIgnoreCase("LIVE")) {
                    videoHolder.txt_video_duration.setVisibility(View.GONE);
                } else {
                    if((""+dto.getContentDuration()).length()>0){
                        if(!dto.getContentDuration().contains(":")) {
                            videoHolder.txt_video_duration.setText("0:"+dto.getContentDuration());
                        } else if(dto.getContentDuration().endsWith(":")) {
                            videoHolder.txt_video_duration.setText(dto.getContentDuration()+"0");

                        } else {
                            videoHolder.txt_video_duration.setText(""+dto.getContentDuration());
                        }

                        if(videoHolder.txt_video_duration.getText().toString().equalsIgnoreCase("0:0")){
                            videoHolder.txt_video_duration.setVisibility(View.GONE);
                        } else {
                            videoHolder.txt_video_duration.setVisibility(View.VISIBLE);
                        }
                    } else {
                        videoHolder.txt_video_duration.setVisibility(View.GONE);
                    }
                }



                if((""+dto.getViewCount()).length()>0){
                    videoHolder.viewer_count.setText(""+dto.getViewCount()+ " Views");
                    videoHolder.viewer_count.setVisibility(View.VISIBLE);
                } else {
                    videoHolder.viewer_count.setVisibility(View.GONE);
                }


                if((""+dto.getDescription()).length()>0){
                    videoHolder.yt_video_details.setText(""+dto.getDescription());
                    videoHolder.yt_video_details.setVisibility(View.VISIBLE);
                } else {
                    videoHolder.yt_video_details.setVisibility(View.GONE);
                }


                videoHolder.video_title.setText(dto.getTitle());
                videoHolder.settings_img_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //showMenu(view, dto, mActivity);
                        //NEED TO DO WORK HERE
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, dto.getFormattedLinkURL());
                        sendIntent.setType("text/plain");
                        mActivity.startActivity(Intent.createChooser(sendIntent, "Share"));
                    }
                });
                videoHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isMainPlayerView){
                            addListener.onItemClick(dto, position);
                        } else {
                            if(!isPickerView) {
                                YTVideoViewerActivity.startVideoViewerActivity(mActivity,  mTAgList, position, nextPageToken, false, "world cup 2018");
                            }

                        }
                    }
                });

                if(isPickerView){
                    videoHolder.relative_bottom_panel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(YTVideoListActivity.KEY_RESULT_VIRAL_LINK, dto.getFormattedLinkURL());
                            mActivity.setResult(Activity.RESULT_OK,returnIntent);
                            mActivity.finish();
                        }
                    });

                    videoHolder.yt_video_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            YTVideoViewerActivity.startVideoViewerActivity(mActivity,  mTAgList, position, nextPageToken, false, "world cup 2018");
                        }
                    });

                    videoHolder.settings_img_view.setVisibility(View.GONE);

                }
            }

        } else if(type == SEARCH_LIST) {
            final YTVideoDTO dto = mTAgList.get(position);
            YTPopularVideosAdapter.VideoHolder videoHolder = (YTPopularVideosAdapter.VideoHolder) holder;
            if (dto.getImgThumbnailHigh() != null && dto.getImgThumbnailHigh().length() > 0) {
                YTVideoHelper.loadRatioImage(mActivity, dto.getImgThumbnailHigh(), videoHolder.yt_video_image);
            }

            if((""+dto.getContentDuration()).length()>0){
                if(!dto.getContentDuration().contains(":")) {
                    videoHolder.txt_video_duration.setText("0:"+dto.getContentDuration());
                } else if(dto.getContentDuration().endsWith(":")) {
                    videoHolder.txt_video_duration.setText(dto.getContentDuration()+"0");

                } else {
                    videoHolder.txt_video_duration.setText(""+dto.getContentDuration());
                }

                if(videoHolder.txt_video_duration.getText().toString().equalsIgnoreCase("0:0")){
                    videoHolder.txt_video_duration.setVisibility(View.GONE);
                } else {
                    videoHolder.txt_video_duration.setVisibility(View.VISIBLE);
                }

            } else {
                videoHolder.txt_video_duration.setVisibility(View.GONE);
            }

            if((""+dto.getViewCount()).length()>0){
                videoHolder.viewer_count.setText(""+dto.getViewCount()+ " Views");
                videoHolder.viewer_count.setVisibility(View.VISIBLE);
            } else {
                videoHolder.viewer_count.setVisibility(View.GONE);
            }


            if((""+dto.getDescription()).length()>0){
                videoHolder.yt_video_details.setText(""+dto.getDescription());
                videoHolder.yt_video_details.setVisibility(View.VISIBLE);
            } else {
                videoHolder.yt_video_details.setVisibility(View.GONE);
            }


            videoHolder.video_title.setText(dto.getTitle());
            videoHolder.settings_img_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            videoHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isPickerView){
                        YTVideoViewerActivity.startVideoViewerActivity(mActivity,  mTAgList, position, nextPageToken, true, searchKey);
                    }

                }
            });

            if(isPickerView) {
                videoHolder.relative_bottom_panel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(YTVideoListActivity.KEY_RESULT_VIRAL_LINK, dto.getFormattedLinkURL());
                        mActivity.setResult(Activity.RESULT_OK,returnIntent);
                        mActivity.finish();
                    }
                });

                videoHolder.yt_video_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YTVideoViewerActivity.startVideoViewerActivity(mActivity,  mTAgList, position-1, nextPageToken, false, "");
                    }
                });

                videoHolder.settings_img_view.setVisibility(View.GONE);
            }

        } else if(type == PLAYER_LIST) {
            final YTVideoDTO dto = mTAgList.get(position);
            YTPopularVideosAdapter.VideoHolderPlayer videoHolder = (YTPopularVideosAdapter.VideoHolderPlayer) holder;
            if (dto.getImgThumbnailHigh() != null && dto.getImgThumbnailHigh().length() > 0) {
                YTVideoHelper.loadRatioImage(mActivity, dto.getImgThumbnailHigh(), videoHolder.yt_video_image);
            }

            if((""+dto.getContentDuration()).length()>0){
                if(!dto.getContentDuration().contains(":")) {
                    videoHolder.txt_video_duration.setText("0:"+dto.getContentDuration());
                } else if(dto.getContentDuration().endsWith(":")) {
                    videoHolder.txt_video_duration.setText(dto.getContentDuration()+"0");

                } else {
                    videoHolder.txt_video_duration.setText(""+dto.getContentDuration());
                }

                if(videoHolder.txt_video_duration.getText().toString().equalsIgnoreCase("0:0")){
                    videoHolder.txt_video_duration.setVisibility(View.GONE);
                } else {
                    videoHolder.txt_video_duration.setVisibility(View.VISIBLE);
                }

            } else {
                videoHolder.txt_video_duration.setVisibility(View.GONE);
            }

            if((""+dto.getTitle()).length()>0){
                videoHolder.yt_video_image.getLayoutParams().height = (int) mActivity.getResources().getDimension(R.dimen.image_view_height_in_player);
                videoHolder.yt_video_image.requestLayout();
                videoHolder.yt_video_image.invalidate();
                videoHolder.video_title.setText(""+dto.getTitle());
                videoHolder.video_title.setVisibility(View.VISIBLE);
            } else {
                videoHolder.yt_video_image.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
                videoHolder.yt_video_image.requestLayout();
                videoHolder.yt_video_image.invalidate();
                videoHolder.video_title.setText("");
                videoHolder.video_title.setVisibility(View.GONE);
            }
            videoHolder.yt_video_details.setText(""+dto.getDescription());

            if((""+playerCurrentSelected_VideoID).length()>0 && playerCurrentSelected_VideoID.equalsIgnoreCase(dto.getVideoID())){
                videoHolder.view.setAlpha(0.3f);
                videoHolder.view.setEnabled(false);
                videoHolder.view.setClickable(false);
            } else {
                videoHolder.view.setAlpha(1.0f);
                videoHolder.view.setEnabled(true);
                videoHolder.view.setClickable(true);
            }

            videoHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addListener.onItemClick(dto, position);
                }
            });

        }




    }

    @Override
    public int getItemCount() {
        return mTAgList == null ? 0 : mTAgList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView yt_video_details, video_title, txt_video_duration, viewer_count, days_ago;
        private ImageView yt_video_image, settings_img_view, hidden_view;
        private RelativeLayout relative_bottom_panel;

        public VideoHolder(View itemView) {
            super(itemView);
            view = itemView;
            yt_video_details = (TextView) itemView.findViewById(R.id.yt_video_details);
            video_title = (TextView) itemView.findViewById(R.id.txt_item_title);
            yt_video_image = (ImageView) itemView.findViewById(R.id.yt_video_image);
            settings_img_view = (ImageView) itemView.findViewById(R.id.settings_img_view);
            txt_video_duration = (TextView) itemView.findViewById(R.id.txt_video_duration);
            viewer_count = (TextView) itemView.findViewById(R.id.viewer_count);
            hidden_view = (ImageView) itemView.findViewById(R.id.hidden_view);
            days_ago = (TextView) itemView.findViewById(R.id.days_ago);
            relative_bottom_panel = (RelativeLayout) itemView.findViewById(R.id.relative_bottom_panel);
        }
    }


    public class VideoHolderPlayer extends RecyclerView.ViewHolder {
        private View view;
        private TextView yt_video_details, video_title;
        private ImageView yt_video_image;
        private TextView txt_video_duration;

        public VideoHolderPlayer(View itemView) {
            super(itemView);
            view = itemView;
            yt_video_details = (TextView) itemView.findViewById(R.id.yt_video_details);
            video_title = (TextView) itemView.findViewById(R.id.txt_item_title);
            yt_video_image = (ImageView) itemView.findViewById(R.id.yt_video_image);
            txt_video_duration = (TextView) itemView.findViewById(R.id.txt_video_duration);
        }
    }


}

