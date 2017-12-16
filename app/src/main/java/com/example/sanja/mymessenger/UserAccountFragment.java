package com.example.sanja.mymessenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserAccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class UserAccountFragment extends Fragment implements View.OnClickListener,
        MultiViewFragment.OnFragmentInteractionListener, ShowGroupsFragment.OnFragmentInteractionListener,
        ShowStatusFragment.OnFragmentInteractionListener{

    public static final String USER = "user";
    public static final String USERSTAT = "userstat";
    private OnFragmentInteractionListener mListener;
    User user = null;
    public static final String MyPREFERENCES = "MyPrefs" ;

    public static final String FragStatus = "status" ;
    SharedPreferences fragStausPreferences;
    SharedPreferences sharedpreferences;
    TextView userName;
    ImageView profilePicture;
    ImageButton imageButtonEdit;
    Button buttonStatus;
    Button buttonChats;
    Button buttonGroups;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("User");
    ArrayList<User> userArrayListExceptLoggedInUser = new ArrayList<>();
    public static final String AVAIL_USERS = "users";
    public static final String STAT = "edit";
    public static final String CurrentUSER = "user";
    User userDetails;
    boolean check = true;
    ArrayList<ArrayList<ChatMsges>> chatListArrayList = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener listener;
    FirebaseUser firebaseUser;

    public UserAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        if (getArguments().getSerializable(USER)!=null){
            user = (User) getArguments().getSerializable(USER);
            Log.d("account","user details "+user.toString());
        }
        else{
            Log.d("account","user got in get arg is null");
        }
        return inflater.inflate(R.layout.fragment_user_account, container, false);
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
                mListener.onUserAccountFragmentInteraction();
                mAuth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity.isAccount=true;
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        fragStausPreferences = getActivity().getSharedPreferences(FragStatus, Context.MODE_PRIVATE);
      /*  SharedPreferences.Editor fragEditor = fragStausPreferences.edit();
        fragEditor.putString("status","chat");
        fragEditor.commit();
        Log.d("FRAG STATUS","chat"+fragStausPreferences.getAll());*/


        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userArrayListExceptLoggedInUser = new ArrayList<User>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    User otherUser = snapshot.getValue(User.class);
                    if (otherUser.getUid().equals(user.getUid())){
                        continue;
                    }
                    else{
                        userArrayListExceptLoggedInUser.add(otherUser);
                    }

                }
                Log.d("account","othere Users "+userArrayListExceptLoggedInUser.toString());
                checkFragStatus();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
      /*  Handler handler = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            public void run() {
                //do your stuff here after DELAY sec
                Log.d("test","calling func now");
                checkFragStatus();

            }
        };
        handler.postDelayed(r, 2000);*/



       // callMultiViewFragment(userArrayListExceptLoggedInUser);

        userName = (TextView)getActivity().findViewById(R.id.textViewUserName);
        profilePicture = (ImageView)getActivity().findViewById(R.id.imageViewProfilePic);
        imageButtonEdit = (ImageButton)getActivity().findViewById(R.id.imageButtonEdit);
        buttonStatus = (Button)getActivity().findViewById(R.id.buttonStatus);
        buttonChats = (Button)getActivity().findViewById(R.id.buttonMyChats);
        buttonGroups = (Button)getActivity().findViewById(R.id.buttonMyGroups);

        buttonStatus.setOnClickListener(this);
        buttonChats.setOnClickListener(this);
        buttonGroups.setOnClickListener(this);

        profilePicture.setOnClickListener(this);
        imageButtonEdit.setOnClickListener(this);

      /*  getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.relative3Container, new AccountBlankFragment(), "AccBlank")
                .commit();*/

        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        //details of logged in user saved in shared preferences.
        editor.putString("email",user.getEmail());
        editor.putString("fname",user.getFname());
        editor.putString("lname",user.getLname());
        editor.putString("uid", user.getUid());
        editor.putString("gender", user.getGender());
        editor.putString("image",user.getImageUrl());
        editor.commit();


        String uid = sharedpreferences.getString("uid","");
        /*mUserRef.child(uid).child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chatListArrayList = new ArrayList<ArrayList<ChatMsges>>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    ArrayList<ChatMsges> insideList = new ArrayList<ChatMsges>();
                    long c = snapshot.getChildrenCount();
                    Log.d("acc ","count "+c);
                    for (DataSnapshot snapshot1 :snapshot.getChildren()){

                        ChatMsges value= snapshot1.getValue(ChatMsges.class);
                        Log.d("acc ","value "+value);
                        insideList.add(value);
                    }
                    chatListArrayList.add(insideList);
                    //Log.d("user acc","arr inside list "+insideList.toString());
                }
                Log.d("user acc","arr arr list "+chatListArrayList.size()+chatListArrayList.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        userName.setText("Hi! "+user.getFname());
        if (MainActivity.user1!=null){
            userDetails = MainActivity.user1;
        }
        else{
            userDetails = user;
        }
        if (userDetails.getImageUrl()!=null){
            Picasso.with(getContext()).load(userDetails.getImageUrl()).fit().centerCrop().into(profilePicture);

        }
        else{
            Picasso.with(getContext()).load(R.drawable.default_pic).fit().centerCrop().into(profilePicture);
        }
    }

    private void checkFragStatus() {

        if (fragStausPreferences.getString("status","").equals("chat")){
            callMultiViewFragment(userArrayListExceptLoggedInUser);
        }
        else if(fragStausPreferences.getString("status","").equals("group")){
            callShowMyGroupsFragment();
        }
        else if (fragStausPreferences.getString("status","").equals("status1")){
            onClick(buttonStatus);
        }
        else {
            callMultiViewFragment(userArrayListExceptLoggedInUser);
        }


    }

    private void callMultiViewFragment(ArrayList<User> userArrayListExceptLoggedInUser) {

        Log.d("account","called");
        MultiViewFragment multiViewFragment = new MultiViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(AVAIL_USERS, userArrayListExceptLoggedInUser);
        multiViewFragment.setArguments(bundle);

       // if (getChildFragmentManager().getBackStackEntryCount()==0){
            if (getActivity()!=null){
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.relative3Container, multiViewFragment, "multi")
                        .commitAllowingStateLoss();
            }

        //}
       /* else{
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.relative3Container, multiViewFragment, "multi")
                    .addToBackStack(null).commitAllowingStateLoss();
        }*/
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onUserAccountFragmentInteraction();
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
    public void onClick(View view) {

        if (view.getId()==R.id.buttonMyChats){
            Log.d("account","show chats");
            callMultiViewFragment(userArrayListExceptLoggedInUser);
        }
        else if(view.getId()==R.id.buttonStatus){
            Log.d("account","show status");
            ShowStatusFragment fragment = new ShowStatusFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(USERSTAT, user);
            fragment.setArguments(bundle);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.relative3Container, fragment, "status")
                    .commit();

        }
        else if (view.getId()==R.id.buttonMyGroups){
            Log.d("account","show groups");
            callShowMyGroupsFragment();

        }
        else if (view.getId()==R.id.imageViewProfilePic){
            Log.d("account","Profile Pic");

        }
        else if (view.getId()==R.id.imageButtonEdit){
            Log.d("account","Edit Button");
            AccountBlankFragment fragment = new AccountBlankFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(STAT, 1);
            bundle.putSerializable(CurrentUSER, userDetails );
            fragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, "Edit")
                        .addToBackStack(null).commit();


        }
        else {
            Log.d("account","wrong selection");
        }

    }

    private void callShowMyGroupsFragment() {

        getChildFragmentManager().beginTransaction()
                .replace(R.id.relative3Container, new ShowGroupsFragment(), "groups")
                .commitAllowingStateLoss();

    }



    @Override
    public void onFragmentInteraction(Uri uri) {

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
        void onUserAccountFragmentInteraction();
    }
}
