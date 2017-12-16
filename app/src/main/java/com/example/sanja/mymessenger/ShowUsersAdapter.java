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
import java.util.zip.Inflater;

/**
 * Created by sanja on 6/22/2017.
 */

public class ShowUsersAdapter extends RecyclerView.Adapter<ShowUsersAdapter.ViewHolder> {

    List<User> mData;
    Context mContext;
    LayoutInflater inflater;

    public ClickListener clickListener;

    public ShowUsersAdapter(List<User> mData, Context mContext) {
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

        User user = mData.get(position);
        holder.textViewName.setText(user.getFname()+" "+user.getLname());
        String imageUrl = user.getImageUrl();
        if (imageUrl!=null){
            Picasso.with(mContext).load(imageUrl).fit().centerCrop().into(holder.imageView);
        }
        else {
            Picasso.with(mContext).load(R.drawable.default_pic).fit().centerCrop().into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView textViewName;
        View container;


        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById(R.id.imageViewRowImage);
            textViewName = (TextView)itemView.findViewById(R.id.textViewRowName);
            container = itemView.findViewById(R.id.LinearRoot);

            container.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (v.getId()==R.id.LinearRoot){
                Log.d("adapter","container");
                clickListener.onContainerClick(getAdapterPosition());
            }

        }
    }

    public static interface ClickListener{

        public void onContainerClick(int position);
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }
}
