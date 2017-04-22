package org.rti.rcd.researchstack;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


public class PamImageAdapter extends BaseAdapter {

    private Context mContext;

    public PamImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }


    // references to our images (as an example)
    private Integer[] mThumbIds = {
            R.drawable.pam_1_1,
            R.drawable.pam_2_1,
            R.drawable.pam_3_1,
            R.drawable.pam_4_1,
            R.drawable.pam_5_1,
            R.drawable.pam_6_1,
            R.drawable.pam_7_1,
            R.drawable.pam_8_1,
            R.drawable.pam_9_1,
            R.drawable.pam_10_1,
            R.drawable.pam_11_1,
            R.drawable.pam_12_1,
            R.drawable.pam_13_1,
            R.drawable.pam_14_1,
            R.drawable.pam_15_1,
            R.drawable.pam_16_1,
    };
}
