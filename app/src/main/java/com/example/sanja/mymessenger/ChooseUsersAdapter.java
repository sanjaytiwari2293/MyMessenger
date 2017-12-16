package com.example.sanja.mymessenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sanja on 7/18/2017.
 */

public class ChooseUsersAdapter extends RecyclerView.Adapter<ChooseUsersAdapter.ViewHolder> {

    List<User> mData;
    Context mContext;
    LayoutInflater inflater;
    SharedPreferences sharedPreferences;
    public static final String CreateGrpPref = "createGrp";

    ClickListener1 clickListener1;

    public ChooseUsersAdapter(List mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.new_group_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        /*sharedPreferences = mContext.getSharedPreferences(CreateGrpPref, Context.MODE_PRIVATE);*/
        User user = mData.get(position);
        /*if (CreateGroupFragment.groupMembersArrayList.contains(user)){
            holder.checkBox.setChecked(true);
        }
        else{
            holder.checkBox.setChecked(false);
        }*/
       /* if (sharedPreferences.getAll().size()==0){
            holder.checkBox.setChecked(false);
        }
        else if (sharedPreferences.contains(user.getUid())){
            holder.checkBox.setChecked(true);

        }
        else{
            holder.checkBox.setChecked(false);

        }*/
        if (user.isSelected()){
            holder.checkBox.setChecked(true);
        }
        else{
            holder.checkBox.setChecked(false);
        }
        holder.textView.setText(user.getFname()+" "+user.getLname());
        String url = user.getImageUrl();
        if (url!=null){
            Picasso.with(mContext).load(url).fit().centerCrop().into(holder.imageView);
        }
        else {
            Picasso.with(mContext).load(R.drawable.default_pic).fit().centerCrop().into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckBox checkBox;
        ImageView imageView;
        TextView textView;
        View container;

        public ViewHolder(View itemView) {
            super(itemView);

            checkBox = (CheckBox)itemView.findViewById(R.id.checkBoxNewGrp);
            imageView = (ImageView)itemView.findViewById(R.id.imageViewRowNewGrp);
            textView = (TextView)itemView.findViewById(R.id.textViewRowNewGrp);
            container = itemView.findViewById(R.id.linearRootNewGroup);

            checkBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.checkBoxNewGrp){

                if (mData.get(getAdapterPosition()).isSelected()){

                    mData.get(getAdapterPosition()).setSelected(false);
                    clickListener1.onCheckboxClicked(getAdapterPosition(),false);
                }
                else {
                    mData.get(getAdapterPosition()).setSelected(true);
                    clickListener1.onCheckboxClicked(getAdapterPosition(),true);
                }
            }

        }
    }

    public static interface ClickListener1{

        public void onCheckboxClicked(int position, boolean b);
    }

    public void setClickListener1(ClickListener1 listener1){
        this.clickListener1 = listener1;
    }
}
