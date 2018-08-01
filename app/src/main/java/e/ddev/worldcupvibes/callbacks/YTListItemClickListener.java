package e.ddev.worldcupvibes.callbacks;


import e.ddev.worldcupvibes.DataModel.YTVideoDTO;

/**
 * Created by Sumaiya on 08-Nov-17.
 */


public interface YTListItemClickListener {
    void onItemClick(YTVideoDTO ytVideoDTO, int position);
}
