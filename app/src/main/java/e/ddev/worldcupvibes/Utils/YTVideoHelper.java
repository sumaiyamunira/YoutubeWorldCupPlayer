package e.ddev.worldcupvibes.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import e.ddev.worldcupvibes.DataModel.YTVideoDTO;

/**
 * Created by Sumaiya on 05-Nov-17.
 */

public class YTVideoHelper {
    private boolean isVideoSessionRunning;

    private static YTVideoHelper ytVideoHelper;

    public static YTVideoHelper getInstance() {
        if (ytVideoHelper == null) {
            ytVideoHelper = new YTVideoHelper();
        }
        return ytVideoHelper;
    }

    public void setVideoSessionRunning(boolean videoSessionRunning) {
        isVideoSessionRunning = videoSessionRunning;
    }


    public static ArrayList<YTVideoDTO> parseYTVideoDTOList(String response) {
        ArrayList<YTVideoDTO> parseYTVideoDTO = new ArrayList<YTVideoDTO>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectItem = jsonArray.getJSONObject(i);
                YTVideoDTO ytVideoDTO = new YTVideoDTO();

                ytVideoDTO.setVideoID(jsonObjectItem.getJSONObject("id").getString("videoId"));

                if ((jsonObjectItem.getJSONObject("snippet")).has("publishedAt")) {
                    ytVideoDTO.setPublishedAt(jsonObjectItem.getJSONObject("snippet").getString("publishedAt"));
                } else {
                    ytVideoDTO.setPublishedAt("");
                }


                if ((jsonObjectItem.getJSONObject("snippet")).has("publishedAt")) {
                    ytVideoDTO.setVideoType(jsonObjectItem.getJSONObject("snippet").getString("liveBroadcastContent"));
                } else {
                    ytVideoDTO.setVideoType("");
                }


                ytVideoDTO.setTitle(jsonObjectItem.getJSONObject("snippet").getString("title"));
                ytVideoDTO.setDescription(jsonObjectItem.getJSONObject("snippet").getString("description"));

                ytVideoDTO.setChannelID(jsonObjectItem.getJSONObject("snippet").getString("channelId"));
                ytVideoDTO.setImgThumbnailDefault(jsonObjectItem.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url"));

                ytVideoDTO.setImgThumbnailHigh(jsonObjectItem.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url"));

                parseYTVideoDTO.add(ytVideoDTO);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return parseYTVideoDTO;

    }

    public static ArrayList<YTVideoDTO> parseYTVideoDetailsDTOList(String response, ArrayList<YTVideoDTO> parseYTVideoDTO) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectItem = jsonArray.getJSONObject(i);
                YTVideoDTO ytVideoDTO = new YTVideoDTO();

                ytVideoDTO.setVideoID(jsonObjectItem.getString("id"));
                if ((jsonObjectItem.getJSONObject("contentDetails")).has("duration")) {
                    String duration = getDuration((jsonObjectItem.getJSONObject("contentDetails")).getString("duration"));
                    ytVideoDTO.setContentDuration(duration);
                } else {
                    ytVideoDTO.setContentDuration("");
                }


                if ((jsonObjectItem.getJSONObject("statistics")).has("viewCount")) {
                    try {
                        double viewCount = Double.parseDouble(jsonObjectItem.getJSONObject("statistics").getString("viewCount"));
                        String viewCountFormated = viewerCountFormat(viewCount, 0);
                        ytVideoDTO.setViewCount("" + viewCountFormated);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ytVideoDTO.setViewCount("");
                    }


                } else {
                    ytVideoDTO.setViewCount("");
                }

                ytVideoDTO.setEmbaddable(jsonObjectItem.getJSONObject("status").getBoolean("embeddable"));
                ytVideoDTO.setCopyRightIssue(jsonObjectItem.getJSONObject("contentDetails").getBoolean("licensedContent"));


                if (i < parseYTVideoDTO.size()) {
                    if (ytVideoDTO.getVideoID().equalsIgnoreCase(parseYTVideoDTO.get(i).getVideoID())) {
                        parseYTVideoDTO.get(i).setCopyRightIssue(ytVideoDTO.isCopyRightIssue());
                        parseYTVideoDTO.get(i).setContentDuration(ytVideoDTO.getContentDuration());
                        parseYTVideoDTO.get(i).setEmbaddable(ytVideoDTO.isEmbaddable());
                        parseYTVideoDTO.get(i).setViewCount(ytVideoDTO.getViewCount());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return parseYTVideoDTO;

    }


    public static String getDuration(String ytdate) {
        try {
            String result = ytdate.replace("PT", "").replace("H", ":").replace("M", ":").replace("S", "");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";

        }
    }


    public static char[] c = new char[]{'K', 'M', 'B', 'T'};

    public static String viewerCountFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : viewerCountFormat(d, iteration + 1));
    }


    public static void loadRatioImage(Context context, String url, final ImageView iv) {
        Glide.with(context).load(url).into(iv);
    }


    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}
