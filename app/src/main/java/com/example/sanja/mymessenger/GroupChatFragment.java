package com.example.sanja.mymessenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class GroupChatFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    public static final String GrpChat = "grpchat";
    GroupDetails groupDetails;
    ImageView imageViewGrpPic;
    TextView textViewTitle;
    EditText editTextTextMsg;
    ImageButton imageButtonSendPic;
    ImageButton imageButtonSendTxtMsg;
    TextView textViewMemberList;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedPreferences;
    String displayNameFirebase;
    String fname1, lname1, uid;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("User");
    ArrayList<User> membersList = new ArrayList<>();
    ArrayList<ChatMsges> messageList = new ArrayList<>();
    LinearLayout linearLayout;

    public GroupChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //this also inflating personal_chat.xml file
        Bundle bundle = this.getArguments();
        if (bundle!=null){
            groupDetails = (GroupDetails) getArguments().getSerializable(GrpChat);
        }
        Log.d("grpchat","grp details "+groupDetails.toString());
        return inflater.inflate(R.layout.fragment_personal_chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        displayNameFirebase = firebaseUser.getDisplayName();
        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        fname1 = sharedPreferences.getString("fname","");
        lname1 = sharedPreferences.getString("lname","");
        uid = sharedPreferences.getString("uid","");

        mUserRef.child(uid).child("Group").child(groupDetails.getGroupId()).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList = new ArrayList<ChatMsges>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    ChatMsges msges1 = snapshot.getValue(ChatMsges.class);
                    messageList.add(msges1);
                }
                displayPrevMessages(messageList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        linearLayout = (LinearLayout)getActivity().findViewById(R.id.linearLayoutScVwPChat);
        imageViewGrpPic = (ImageView)getActivity().findViewById(R.id.imageViewPCImage);
        textViewTitle = (TextView)getActivity().findViewById(R.id.textViewPCName);
        editTextTextMsg = (EditText)getActivity().findViewById(R.id.editTextPCMsg);
        imageButtonSendPic = (ImageButton)getActivity().findViewById(R.id.imageButtonPCSendImg);
        imageButtonSendTxtMsg = (ImageButton)getActivity().findViewById(R.id.imageButtonPCSendMsg);
        textViewMemberList = (TextView)getActivity().findViewById(R.id.textViewGrpMemberNameList);
        //
        membersList = groupDetails.getMembersList();
        //
        imageButtonSendPic.setOnClickListener(this);
        imageButtonSendTxtMsg.setOnClickListener(this);

        String imgUrl = groupDetails.getGroupPic();
        if (imgUrl!=null){
            Picasso.with(getActivity()).load(imgUrl).fit().centerCrop().into(imageViewGrpPic);
        }
        else{
            Picasso.with(getActivity()).load(R.drawable.group_pic1).fit().centerCrop().into(imageViewGrpPic);
        }
        textViewTitle.setText(groupDetails.getGroupTitle());
        String nameList="";

        for (int i=0;i<groupDetails.getMembersList().size();i++){

            User user = groupDetails.getMembersList().get(i);
            if (nameList.length()==0){
                nameList = nameList+user.getFname();
            }
            else{
                nameList = nameList+", "+user.getFname();
            }
        }
        textViewMemberList.setVisibility(View.VISIBLE);
        textViewMemberList.setText(nameList);

    }
    private void displayPrevMessages(ArrayList<ChatMsges> messageList) {

        linearLayout.removeAllViews();
        if (getActivity()!=null){

            Log.d("Error",""+messageList.size());
            for (int i=0;i<messageList.size();i++){

                final ChatMsges msges = messageList.get(i);
                if (msges.getSenderId().equals(uid)){
                    //loggedIn user
                    //right align
                    if (msges.getMsgType().equals("0")){
                        //text msg
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                        LinearLayout linearLayout1 = (LinearLayout) inflater.inflate(R.layout.message_layout, null);
                        linearLayout.addView(linearLayout1);

                        TextView textViewMsg = (TextView)linearLayout1.findViewById(R.id.txtMessage);
                        textViewMsg.setText(msges.getMessage());
                        TextView textViewSenderName = (TextView)linearLayout1.findViewById(R.id.txtViewSenderName);
                        textViewSenderName.setText(msges.getSenderName());

                        TextView time = (TextView)linearLayout1.findViewById(R.id.txtViewTIme);
                        PrettyTime prettyTime = new PrettyTime();
                        String timeString = msges.getTime();
                        long epoch = Long.parseLong(timeString);
                        time.setText(prettyTime.format(new Date(epoch)));

                        ImageButton delete = (ImageButton)linearLayout1.findViewById(R.id.imageButtonDelMsgRight);
                        delete.setTag(msges);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleDelete(msges, uid);
                            }
                        });
                    }
                    else{
                        //image
                        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                        LinearLayout linearLayout1 = (LinearLayout) layoutInflater.inflate(R.layout.image_layout,null);
                        linearLayout.addView(linearLayout1);

                        ImageView imgMsg = (ImageView) linearLayout1.findViewById(R.id.imageViewImageLayout);
                        String imgUrl = msges.getMessage();
                        if (imgUrl!=null){
                            Picasso.with(imgMsg.getContext()).load(imgUrl).fit().centerCrop().into(imgMsg);
                        }
                        else{
                            //do nothing;
                            Log.d("demo","null image url ");
                        }
                        ////for deleting the messages we have to set tag or something else.
                        ImageButton imageButtonDelete = (ImageButton)linearLayout1.findViewById(R.id.imageButton2);
                        imageButtonDelete.setTag(msges);
                        imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleDelete(msges, uid);
                            }
                        });
                        TextView senderName = (TextView)linearLayout1.findViewById(R.id.txtViewSenderNameImage);
                        senderName.setText(msges.getSenderName());
                        TextView time = (TextView)linearLayout1.findViewById(R.id.txtViewTime1Image);
                        PrettyTime prettyTime=new PrettyTime();
                        time.setText(prettyTime.format(new Date(Long.parseLong(msges.getTime()))));

                    }
                }
                else{
                    //other user
                    //left align
                    if (msges.getMsgType().equals("0")){
                        //text
                        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                        LinearLayout  linearLayout1 = (LinearLayout) layoutInflater.inflate(R.layout.left_oriented_layout, null);
                        linearLayout.addView(linearLayout1);

                        TextView textViewMsg = (TextView)linearLayout1.findViewById(R.id.textViewLeftMessage);
                        textViewMsg.setText(msges.getMessage());
                        TextView textViewSenderName = (TextView)linearLayout1.findViewById(R.id.textViewLeftSenderName);
                        textViewSenderName.setText(msges.getSenderName());

                        TextView time = (TextView)linearLayout1.findViewById(R.id.textViewLeftTime);
                        PrettyTime prettyTime = new PrettyTime();
                        String timeString = msges.getTime();
                        long epoch = Long.parseLong(timeString);
                        time.setText(prettyTime.format(new Date(epoch)));

                        ImageButton delete = (ImageButton)linearLayout1.findViewById(R.id.imageButtonDelMsgLeft1);
                        delete.setTag(msges);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleDelete(msges, uid);
                            }
                        });


                    }
                    else{
                        //image
                        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                        LinearLayout linearLayout1 = (LinearLayout) layoutInflater.inflate(R.layout.image_layout_left,null);
                        linearLayout.addView(linearLayout1);

                        ImageView imgMsg = (ImageView) linearLayout1.findViewById(R.id.imageViewImageLeft);
                        String imgUrl = msges.getMessage();
                        if (imgUrl!=null){
                            Picasso.with(imgMsg.getContext()).load(imgUrl).fit().centerCrop().into(imgMsg);
                        }
                        else{
                            //do nothing;
                            Log.d("demo","null image url ");
                        }
                        ////for deleting the messages we have to set tag or something else.
                        ImageButton imageButtonDelete = (ImageButton)linearLayout1.findViewById(R.id.imageButtonImageLeft);
                        imageButtonDelete.setTag(msges);
                        imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleDelete(msges, uid);
                            }
                        });
                        TextView senderName = (TextView)linearLayout1.findViewById(R.id.txtViewSenderNameImageLeft);
                        senderName.setText(msges.getSenderName());
                        TextView time = (TextView)linearLayout1.findViewById(R.id.txtViewTime1ImageLeft);
                        PrettyTime prettyTime=new PrettyTime();
                        time.setText(prettyTime.format(new Date(Long.parseLong(msges.getTime()))));

                    }

                }

            }
        }

    }

    private void handleDelete(ChatMsges msges, String uid) {

        mUserRef.child(uid).child("Group").child(groupDetails.getGroupId()).child("messages").child(msges.getMsgKey()).removeValue();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.imageButtonPCSendImg){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 103);

        }
        else if (v.getId()==R.id.imageButtonPCSendMsg){
            String msg = editTextTextMsg.getText().toString();
            editTextTextMsg.setText("");
            if (msg!=null && msg.length()!=0){
                ChatMsges msges = new ChatMsges();
                Date date = new Date();
                long epoch = date.getTime();
                String time = String.valueOf(epoch);
                msges.setMessage(msg);
                msges.setMsgType("0");
                msges.setSenderId(uid);
                msges.setSenderName(fname1+" "+lname1);
                msges.setTime(time);
                saveMsgObjectToFirebase(msges);

            }
            else{
                Toast.makeText(getActivity(), "Enter some text", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Log.d("grp chat","wrong click");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==103 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            Uri uri = data.getData();
            Date date = new Date();
            long epoch = date.getTime();
            String time = String.valueOf(epoch);
            final ChatMsges msges = new ChatMsges();
            msges.setSenderName(fname1+" "+lname1);
            msges.setSenderId(uid);
            msges.setMsgType("1");
            msges.setTime(time);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data12 = baos.toByteArray();

                StorageReference imageRef = storageReference.child("images/"+displayNameFirebase+time+".JPEG");
                UploadTask uploadTask = imageRef.putBytes(data12);
                uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri uri1 =  taskSnapshot.getDownloadUrl();
                        String imgUrl = uri1.toString();
                        msges.setMessage(imgUrl);
                        saveMsgObjectToFirebase(msges);
                    }
                });



            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void saveMsgObjectToFirebase(ChatMsges msges) {

        for (int i=0;i<membersList.size();i++){

            User user = membersList.get(i);
            DatabaseReference msgRef = mUserRef.child(user.getUid()).child("Group").child(groupDetails.getGroupId()).child("messages").push();
            String key = msgRef.getKey();
            msges.setMsgKey(key);
            msgRef.setValue(msges);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
