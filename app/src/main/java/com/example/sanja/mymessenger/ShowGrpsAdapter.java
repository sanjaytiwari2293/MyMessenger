package com.example.sanja.mymessenger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sanja on 7/19/2017.
 */

public class ShowGrpsAdapter extends RecyclerView.Adapter<ShowGrpsAdapter.ViewHolder>{

    List<GroupDetails> mData;
    Context mContext;
    LayoutInflater inflater;

    ClickListener clickListener;

    public ShowGrpsAdapter(List<GroupDetails> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        GroupDetails groupDetails = mData.get(position);
        holder.textView.setText(groupDetails.getGroupTitle());
        String imgUrl = groupDetails.getGroupPic();
        if (imgUrl!=null){
            Picasso.with(mContext).load(imgUrl).fit().centerCrop().into(holder.imageView);
        }
        else {
            Picasso.with(mContext).load(R.drawable.group_pic1).fit().centerCrop().into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView textView;
        View container;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById(R.id.imageViewRowImage);
            textView = (TextView)itemView.findViewById(R.id.textViewRowName);
            container = itemView.findViewById(R.id.LinearRoot);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId()==R.id.LinearRoot){
                clickListener.onContainerClick(getAdapterPosition());
            }

        }
    }

    public static interface ClickListener{

        public void onContainerClick(int position);
    }

    public void onGrpClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }
}
