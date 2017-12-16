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
 * Created by sanja on 7/21/2017.
 */

public class ShowStatusAdapter extends RecyclerView.Adapter<ShowStatusAdapter.ViewHolder> {

    List<User> mList;
    Context mContext;
    LayoutInflater inflater;

    ClickListener clickListener;

    public ShowStatusAdapter(List<User> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_item_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        User user = mList.get(position);
        holder.textViewName.setText(user.getFname()+" "+user.getLname());
        holder.textViewStatus.setText(user.getStatus());
        String url = user.getImageUrl();
        if (url!=null){
            Picasso.with(mContext).load(url).fit().centerCrop().into(holder.imageView);
        }
        else{
            Picasso.with(mContext).load(R.drawable.default_pic).fit().centerCrop().into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView textViewName;
        TextView textViewStatus;
        View container;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById(R.id.imageViewRowImage1);
            textViewName = (TextView)itemView.findViewById(R.id.textViewRowName1);
            textViewStatus = (TextView)itemView.findViewById(R.id.textViewFrndStatus);
            container = itemView.findViewById(R.id.LinearStatusRoot);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId()==R.id.LinearStatusRoot){
                clickListener.onContainerClick(getAdapterPosition());

            }
            else {
                Log.d("show status adapter","wrong click");
            }

        }
    }

    public static interface ClickListener{

        public void onContainerClick(int position);

    }

    public void onClickListener(ClickListener clickListener){

        this.clickListener = clickListener;
    }
}
