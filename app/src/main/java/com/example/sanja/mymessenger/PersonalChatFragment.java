package com.example.sanja.mymessenger;

import android.app.Dialog;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import java.util.zip.Inflater;

import static android.R.attr.bitmap;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonalChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PersonalChatFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public static final String VIEW = "user";
    public static final String CameraViewUser = "user";
    public static final String ChatMsgesArrayList = "chatMsgesArrayList";
    public static final String ViewUserChatMsgList = "viewUserChatMsgList";

    public static final String MyPREFERENCES = "MyPrefs" ;
    User viewUser;
    ImageView displayImage;
    TextView displayName;
    EditText typedMsg;
    ImageButton sendPhoto;
    ImageButton sendText;
    ImageButton openCamera;
    ScrollView scrollView;
    SharedPreferences sharedPreferences;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("User");
    String fname1;
    String lname1;
    String uid;
    String userDisplayName;
    static ArrayList<ChatMsges> chatMsgesArrayList = new ArrayList<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    LinearLayout linearLayout;

    FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    static ArrayList<ChatMsges> viewUserChatMsgList = new ArrayList<>();
    ImageButton dialogSendImg;
    ImageButton dialogSendVid;

    public PersonalChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        Log.d("demo","onsave");
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{ scrollView.getScrollX(), scrollView.getScrollY()});

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
            if(position != null)
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.scrollTo(position[0], position[1]);
                    }
                });
        }

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle!=null){
            viewUser = (User) getArguments().getSerializable(VIEW);
            Log.d("demo","PChat "+viewUser.toString());
        }
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_personal_chat, container, false);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.logout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.logoutIcon:
                Log.d("pc","logging out");
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        scrollView = (ScrollView) getActivity().findViewById(R.id.scrollViewPC);


        displayImage = (ImageView)getActivity().findViewById(R.id.imageViewPCImage);
        displayName = (TextView)getActivity().findViewById(R.id.textViewPCName);
        typedMsg = (EditText)getActivity().findViewById(R.id.editTextPCMsg);
        sendPhoto = (ImageButton)getActivity().findViewById(R.id.imageButtonPCSendImg);
        sendText = (ImageButton)getActivity().findViewById(R.id.imageButtonPCSendMsg);
        linearLayout = (LinearLayout)getActivity().findViewById(R.id.linearLayoutScVwPChat);
        openCamera = (ImageButton)getActivity().findViewById(R.id.imageButtonOpenCameraPC);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    getActivity().startActivityForResult(intent, 1);
                }*/

                CameraFragment fragment = new CameraFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(CameraViewUser, viewUser);
                bundle.putSerializable(ChatMsgesArrayList,chatMsgesArrayList);
                bundle.putSerializable(ViewUserChatMsgList, viewUserChatMsgList);
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, "camera")
                        .addToBackStack(null).commit();
            }
        });

        sharedPreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        fname1 = sharedPreferences.getString("fname","");
        lname1 = sharedPreferences.getString("lname","");
        uid = sharedPreferences.getString("uid","");
        auth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userDisplayName = firebaseUser.getDisplayName();

        //
        mUserRef.child(uid).child("Chat").child(uid+viewUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chatMsgesArrayList = new ArrayList<ChatMsges>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    ChatMsges msges = snapshot.getValue(ChatMsges.class);
                    chatMsgesArrayList.add(msges);

                }
                Log.d("PC","chat List "+chatMsgesArrayList.size()+"<>"+chatMsgesArrayList.toString());
                
                displayPrevMsges(chatMsgesArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /////////

        mUserRef.child(viewUser.getUid()).child("Chat").child(viewUser.getUid()+uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewUserChatMsgList = new ArrayList<ChatMsges>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    ChatMsges msges = snapshot.getValue(ChatMsges.class);
                    msges.setReadStatus("blue");
                    viewUserChatMsgList.add(msges);
                }
                mUserRef.child(viewUser.getUid()).child("Chat").child(viewUser.getUid()+uid).setValue(viewUserChatMsgList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ///////

        displayName.setText(viewUser.getFname()+" "+viewUser.getLname());
        String imageUrl = viewUser.getImageUrl();
        if (imageUrl!=null){
            Picasso.with(getContext()).load(imageUrl).fit().centerCrop().into(displayImage);
        }
        else{
            Picasso.with(getContext()).load(R.drawable.default_pic).fit().centerCrop().into(displayImage);
        }

        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = typedMsg.getText().toString();
                if (msg!=null && msg.length()!=0){
                    Log.d("PC","send msg called.");
                    sendMessage(msg);
                    typedMsg.setText("");
                }
                else{
                    Toast.makeText(getContext(), "Please enter some text.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialogView();
            }
        });
    }

    private void showDialogView() {

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_vimage);
        dialog.setCancelable(true);

        //remember below has dialog not getActivity;
        //https://stackoverflow.com/questions/28193552/null-pointer-exception-on-setonclicklistener
        dialogSendImg = (ImageButton)dialog.findViewById(R.id.imageButtonWantSendPic);
        dialogSendVid = (ImageButton)dialog.findViewById(R.id.imageButtonWantSendVideo);

        dialogSendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("account blank","Open Gallery");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 101);
                dialog.dismiss();
            }
        });
        dialogSendVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("account blank","Delete Pic");
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 107);
                dialog.dismiss();
            }
        });


        dialog.show();



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==101 && resultCode== RESULT_OK && data!=null && data.getData()!=null){

            Uri uri = data.getData();
            Date date = new Date();
            long epoch = date.getTime();
            String time = String.valueOf(epoch);
            final ChatMsges msges = new ChatMsges();

            msges.setMsgType("1");
            msges.setTime(time);
            msges.setReceiverId(viewUser.getUid());
            msges.setReceiverName(viewUser.getFname()+" "+viewUser.getLname());
            msges.setSenderId(uid);
            msges.setSenderName(fname1+" "+lname1);

            ///////
            msges.setReadStatus("black");
            /////

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();

                StorageReference imageRef = storageReference.child("images/"+userDisplayName+time+".JPEG");
                UploadTask uploadTask = imageRef.putBytes(data1);
                uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri url = taskSnapshot.getDownloadUrl();
                        msges.setMessage(url.toString());
                        saveMsgObjectToFirebase(msges);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode==107 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            Uri uri = data.getData();
            Date date = new Date();
            long epoch = date.getTime();
            String time = String.valueOf(epoch);
            final ChatMsges msges = new ChatMsges();

            msges.setMsgType("2");
            msges.setTime(time);
            msges.setReceiverId(viewUser.getUid());
            msges.setReceiverName(viewUser.getFname()+" "+viewUser.getLname());
            msges.setSenderId(uid);
            msges.setSenderName(fname1+" "+lname1);


            ///////
            msges.setReadStatus("black");
            /////

            try {

                StorageReference imageRef = storageReference.child("videos/"+userDisplayName+time+".mp4");
                UploadTask uploadTask = imageRef.putFile(uri);
                uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri url = taskSnapshot.getDownloadUrl();
                        msges.setMessage(url.toString());
                        saveMsgObjectToFirebase(msges);

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            Log.d("demo","inside result!!");
            Date date = new Date();
            long epoch = date.getTime();
            String time = String.valueOf(epoch);
            final ChatMsges msges = new ChatMsges();

            msges.setMsgType("1");
            msges.setTime(time);
            msges.setReceiverId(viewUser.getUid());
            msges.setReceiverName(viewUser.getFname()+" "+viewUser.getLname());
            msges.setSenderId(uid);
            msges.setSenderName(fname1+" "+lname1);

            ///////
            msges.setReadStatus("black");

            /*Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");*/
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();

            StorageReference imageRef = storageReference.child("images/"+userDisplayName+time+".JPEG");
            UploadTask uploadTask = imageRef.putBytes(data1);
            uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri url = taskSnapshot.getDownloadUrl();
                    msges.setMessage(url.toString());
                    saveMsgObjectToFirebase(msges);
                }
            });


        }
        else{
            Log.d("demo","not found any result");
        }
    }

    private void displayPrevMsges(ArrayList<ChatMsges> chatMsgesArrayList) {

        linearLayout.removeAllViews();

        if (getActivity()!=null){

            for (int i=0;i<chatMsgesArrayList.size();i++){

                final ChatMsges msges = chatMsgesArrayList.get(i);

                if (msges.getSenderId().equals(uid)){
                    //loggedIn user
                    //right align
                    if (msges.getMsgType().equals("0")){
                        //text
                        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                        LinearLayout  linearLayout1 = (LinearLayout) layoutInflater.inflate(R.layout.message_layout, null);
                        linearLayout.addView(linearLayout1);

                        TextView textViewMsg = (TextView)linearLayout1.findViewById(R.id.txtMessage);
                        textViewMsg.setText(msges.getMessage());
                        TextView textViewSenderName = (TextView)linearLayout1.findViewById(R.id.txtViewSenderName);
                        textViewSenderName.setText(msges.getSenderName());

                        ImageView imageView = (ImageView)linearLayout1.findViewById(R.id.imageViewTicks);
                        if (msges.getReadStatus().equals("blue")){
                            imageView.setImageResource(R.drawable.blue_tick);
                        }

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
                    else if(msges.getMsgType().equals("1")){
                        //image
                        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                        LinearLayout linearLayout1 = (LinearLayout) layoutInflater.inflate(R.layout.image_layout,null);
                        linearLayout.addView(linearLayout1);

                        ImageView imgMsg = (ImageView) linearLayout1.findViewById(R.id.imageViewImageLayout);
                        String imgUrl = msges.getMessage();
                        if (imgUrl!=null){
                            Picasso.with(imgMsg.getContext()).load(imgUrl).into(imgMsg);
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

                        //
                        ImageView imageView = (ImageView)linearLayout1.findViewById(R.id.imageViewTickImage);
                        if (msges.getReadStatus().equals("blue")){
                            imageView.setImageResource(R.drawable.blue_tick);
                        }
                        //

                        TextView time = (TextView)linearLayout1.findViewById(R.id.txtViewTime1Image);
                        PrettyTime prettyTime=new PrettyTime();
                        time.setText(prettyTime.format(new Date(Long.parseLong(msges.getTime()))));


                    }
                    else{
                        //video
                        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                        LinearLayout linearLayout1 = (LinearLayout) layoutInflater.inflate(R.layout.video_layout_right,null);
                        linearLayout.addView(linearLayout1);

                        ImageView imgMsg = (ImageView) linearLayout1.findViewById(R.id.imageViewPlayVideoR);
                        imgMsg.setTag(msges);
                        imgMsg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openVideo(msges);
                            }
                        });

                        ImageButton imageButtonDelete = (ImageButton)linearLayout1.findViewById(R.id.imageButtonDelVidRight);
                        imageButtonDelete.setTag(msges);
                        imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleDelete(msges, uid);
                            }
                        });

                        TextView senderName = (TextView)linearLayout1.findViewById(R.id.txtViewSenderNameVideoR);
                        senderName.setText(msges.getSenderName());

                        //
                        ImageView imageView = (ImageView)linearLayout1.findViewById(R.id.imageViewTickVideoR);
                        if (msges.getReadStatus().equals("blue")){
                            imageView.setImageResource(R.drawable.blue_tick);
                        }
                        //

                        TextView time = (TextView)linearLayout1.findViewById(R.id.txtViewTimeVideoR);
                        PrettyTime prettyTime=new PrettyTime();
                        time.setText(prettyTime.format(new Date(Long.parseLong(msges.getTime()))));
                    }


                }
                else{
                    //other user
                    //left align
                    if (msges.getMsgType().equals("0")){
                        //text
                        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
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
                    else if (msges.getMsgType().equals("1")){
                        //image
                        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
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
                    else{
                        //video
                        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                        LinearLayout linearLayout1 = (LinearLayout) layoutInflater.inflate(R.layout.video_layout_left,null);
                        linearLayout.addView(linearLayout1);

                        ImageView imgMsg = (ImageView) linearLayout1.findViewById(R.id.imageViewPlayVideoL);
                        imgMsg.setTag(msges);
                        imgMsg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openVideo(msges);
                            }
                        });

                        ImageButton imageButtonDelete = (ImageButton)linearLayout1.findViewById(R.id.imageButtonDelVidLeft);
                        imageButtonDelete.setTag(msges);
                        imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleDelete(msges, uid);
                            }
                        });

                        TextView senderName = (TextView)linearLayout1.findViewById(R.id.txtViewSenderNameVideoL);
                        senderName.setText(msges.getSenderName());

                        TextView time = (TextView)linearLayout1.findViewById(R.id.txtViewTimeVideoL);
                        PrettyTime prettyTime=new PrettyTime();
                        time.setText(prettyTime.format(new Date(Long.parseLong(msges.getTime()))));
                    }


                }
            }

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    // This method works but animates the scrolling
                    // which looks weird on first load
                    // scroll_view.fullScroll(View.FOCUS_DOWN);

                    // This method works even better because there are no animations.
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });

        }


        //scrollView.fullScroll(View.FOCUS_DOWN);

    }

    private void openVideo(ChatMsges msges) {

        Log.d("demo","Video "+msges.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(msges.getMessage()));
        startActivity(intent);

    }

    private void handleDelete(ChatMsges msges, String uid) {
        Log.d("PC","delete called");
        chatMsgesArrayList.remove(msges);
        mUserRef.child(uid).child("Chat").child(uid+viewUser.getUid()).setValue(chatMsgesArrayList);

    }

    private void sendMessage(String msg) {

        ChatMsges chat = new ChatMsges();
        chat.setMsgType("0");
        chat.setReceiverId(viewUser.getUid());
        chat.setReceiverName(viewUser.getFname()+" "+viewUser.getLname());
        chat.setSenderId(sharedPreferences.getString("uid",""));
        chat.setSenderName(fname1+" "+lname1);
        chat.setMessage(msg);

        ///////
        chat.setReadStatus("black");
        /////


        Date date = new Date();
        long epoch = date.getTime();
        String time = String.valueOf(epoch);
        chat.setTime(time);

        saveMsgObjectToFirebase(chat);


    }

    private void saveMsgObjectToFirebase(ChatMsges chat) {

        //for sender
        Log.d("Pchat","save msg firebase called");
        DatabaseReference subUserRef = mUserRef.child(sharedPreferences.getString("uid",""));
        DatabaseReference chatRef = subUserRef.child("Chat");
        DatabaseReference subChatRef = chatRef.child(sharedPreferences.getString("uid","")+viewUser.getUid());
        //

        chatMsgesArrayList.add(chat);
        subChatRef.setValue(chatMsgesArrayList);
        /*DatabaseReference msgRef = subChatRef.push();
        chat.setMsgKey(msgRef.getKey());
        Log.d("demo","chat obj "+chat);
        msgRef.setValue(chat);*/

        ////////for receiver
        DatabaseReference otherUserRef = mUserRef.child(viewUser.getUid());
        DatabaseReference otherUserChatRef = otherUserRef.child("Chat");
        DatabaseReference otherUserSubChatRef =  otherUserChatRef.child(viewUser.getUid()+sharedPreferences.getString("uid",""));
        /*otherUserSubChatRef.child(chat.getMsgKey()).setValue(chat);*/
        viewUserChatMsgList.add(chat);
        otherUserSubChatRef.setValue(viewUserChatMsgList);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPChatFragmentInteraction(uri);
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
    public void onPause() {
        super.onPause();
        Log.d("dem","PAUSED");
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
        void onPChatFragmentInteraction(Uri uri);
    }
}
