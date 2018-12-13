package com.whf.messagerelayer.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.whf.messagerelayer.R;

/**
 * Created by WHF on 2017/3/28.
 */
public class ContactHolder extends RecyclerView.ViewHolder{

    public View mItemView;
    public TextView mContactName,mContactNum;
    public ImageView mRightIcon;

    public ContactHolder(View itemView) {
        super(itemView);
        this.mItemView = itemView;
        mContactName = (TextView) itemView.findViewById(R.id.text_contact_name);
        mContactNum = (TextView) itemView.findViewById(R.id.text_contact_num);
        mRightIcon = (ImageView) itemView.findViewById(R.id.image_contact_selecter);
    }
}
