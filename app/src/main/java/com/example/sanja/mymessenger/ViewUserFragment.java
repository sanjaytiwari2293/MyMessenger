package com.example.sanja.mymessenger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewUserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ViewUserFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public static final String VIEW = "user";
    User viewUser;
    ImageView imageView;
    TextView textViewName;
    Button addFriend;
    Button createMsg;
    ImageButton sendMsg;
    ImageButton cancelCreateMsg;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String fname1;
    String lname1;
    String uid1;
    String viewedUserEmail;
    EditText message;
    ArrayList<ChatMsges> chatListForLoggedInUser = new ArrayList<>();
    ArrayList<ChatMsges> chatListForOtherUser = new ArrayList<>();

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("User");
    DatabaseReference loggedUserRef;
    DatabaseReference viewedUserRef;

    private String loggedInUserEmail;
    private String MsgRef;


    public ViewUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle!=null){
            viewUser = (User) getArguments().getSerializable(VIEW);
        }
        return inflater.inflate(R.layout.fragment_view_user, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("view","user "+viewUser.toString());
        textViewName = (TextView)getActivity().findViewById(R.id.textViewViewUserName);
        imageView = (ImageView)getActivity().findViewById(R.id.imageViewViewPic1);
        addFriend = (Button)getActivity().findViewById(R.id.buttonViewUserAddFriend);
        createMsg = (Button)getActivity().findViewById(R.id.buttonViewUserSendMsg);
        viewedUserEmail = viewUser.getEmail();

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        loggedUserRef = mUserRef.child(sharedpreferences.getString("uid",""));
        Log.d("demo","loggedIn "+loggedUserRef);
        viewedUserRef = mUserRef.child(viewUser.getUid());
        Log.d("demo","loggedIn "+viewedUserRef);
        loggedInUserEmail = sharedpreferences.getString("email","");
        //MsgRef = sharedpreferences.getString("uid","")+viewUser.getUid();

        fname1 = sharedpreferences.getString("fname","");
        lname1 = sharedpreferences.getString("lname","");
        uid1 = sharedpreferences.getString("uid","");

        if (viewUser.getImageUrl()!=null){
            Picasso.with(getContext()).load(viewUser.getImageUrl()).fit().centerCrop().into(imageView);
            Log.d("view","user "+viewUser.getImageUrl());
        }
        else{
            Picasso.with(getContext()).load(R.drawable.default_pic).fit().centerInside().into(imageView);
            Log.d("view","user "+viewUser.getImageUrl());
        }
        textViewName.setText(viewUser.getFname()+" "+viewUser.getLname());
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAddFriendFunction();

            }
        });

        createMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMsg();

            }
        });
    }

    private void callAddFriendFunction() {
        //create sent rqsts node for sender and
        DatabaseReference sentReqRef = loggedUserRef.child("sentRequests");
        DatabaseReference subReqRef = sentReqRef.child(viewUser.getUid());
        User user = new User();
        user.setGender(viewUser.getGender());
        user.setImageUrl(viewUser.getImageUrl());
        user.setFname(viewUser.getFname());
        user.setLname(viewUser.getLname());
        subReqRef.setValue(user);

        //friend rests node for receiver;
        User user1 = new User();
        user1.setGender(sharedpreferences.getString("gender",""));
        user1.setImageUrl(sharedpreferences.getString("image",""));
        user1.setFname(sharedpreferences.getString("fname",""));
        user1.setLname(sharedpreferences.getString("lname",""));
        DatabaseReference frndReqRef = viewedUserRef.child("friendRequests");
        frndReqRef.child(sharedpreferences.getString("uid","")).setValue(user1);
    }

    private void createMsg() {

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.message);
        dialog.setCancelable(true);
        sendMsg = (ImageButton)dialog.findViewById(R.id.imageButtonNewMsgSend);
        cancelCreateMsg = (ImageButton)dialog.findViewById(R.id.imageButtonNewMsgCancel);
        message = (EditText)dialog.findViewById(R.id.editTextNewMsgTxt);
        TextView sender = (TextView)dialog.findViewById(R.id.textViewNewMsgSenderName);
        TextView receiver = (TextView)dialog.findViewById(R.id.textViewNewMessageReceiverName);
        receiver.setText(viewUser.getFname()+" "+viewUser.getLname());
        sender.setText(fname1+" "+lname1);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m = message.getText().toString();
                if (m!=null && m.length()!=0){
                    sendMessageFunction(m);
                    message.setText(null);
                    Log.d("view","SENDING");
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(dialog.getContext(), "Please enter some text", Toast.LENGTH_SHORT).show();
                }


            }
        });

        cancelCreateMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();


    }

    private void sendMessageFunction(String msg) {
        Log.d("demo","send msg called" +msg);

        ChatMsges chat = new ChatMsges();
        chat.setMsgType("0");
        chat.setReceiverId(viewUser.getUid());
        chat.setReceiverName(viewUser.getFname()+" "+viewUser.getLname());
        chat.setSenderId(sharedpreferences.getString("uid",""));
        chat.setSenderName(fname1+" "+lname1);
        chat.setMessage(msg);

        Date date = new Date();
        long epoch = date.getTime();
        String time = String.valueOf(epoch);
        chat.setTime(time);

        //for sender
        DatabaseReference subUserRef = mUserRef.child(sharedpreferences.getString("uid",""));
        DatabaseReference chatRef = subUserRef.child("Chat");
        DatabaseReference subChatRef = chatRef.child(sharedpreferences.getString("uid","")+viewUser.getUid());
        //

        DatabaseReference msgRef = subChatRef.push();
        chat.setMsgKey(msgRef.getKey());
        Log.d("demo","chat obj "+chat);
        msgRef.setValue(chat);

        ////////for receiver
        DatabaseReference otherUserRef = mUserRef.child(viewUser.getUid());
        DatabaseReference otherUserChatRef = otherUserRef.child("Chat");
        DatabaseReference otherUserSubChatRef =  otherUserChatRef.child(viewUser.getUid()+sharedpreferences.getString("uid",""));
        otherUserSubChatRef.child(chat.getMsgKey()).setValue(chat);

        Log.d("demo","send msg sent<>");
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
