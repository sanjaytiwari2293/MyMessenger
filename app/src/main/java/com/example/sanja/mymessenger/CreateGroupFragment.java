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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CreateGroupFragment extends Fragment implements ChooseUsersAdapter.ClickListener1, View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("User");
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mAuthListener;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedPreferences;
    FirebaseUser firebaseUser;
    String displayNameFirebase;
    String fname1;
    String lname1;
    String uid;
    ArrayList<User> userArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    static ArrayList<User> groupMembersArrayList = new ArrayList<>();
    User loggedInUser;
    ImageView imageViewGroupPic;
    EditText editTextGrpTitle;
    Button buttonCreateGrp;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    String groupTitle;
    String groupPicUrl=null;
    SharedPreferences sharedPreferencesCGrp;
    public static final String CreateGrpPref = "createGrp";

    public CreateGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        displayNameFirebase = firebaseUser.getDisplayName();
        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        fname1 = sharedPreferences.getString("fname","");
        lname1 = sharedPreferences.getString("lname","");
        uid = sharedPreferences.getString("uid","");
        recyclerView = (RecyclerView)getActivity().findViewById(R.id.recyclerViewCreateGrp);
        imageViewGroupPic = (ImageView)getActivity().findViewById(R.id.imageViewGroupPic);
        editTextGrpTitle = (EditText)getActivity().findViewById(R.id.editTextNewGrpTitle);
        buttonCreateGrp = (Button)getActivity().findViewById(R.id.buttonNewGrpCreate);

        imageViewGroupPic.setOnClickListener(this);
        buttonCreateGrp.setOnClickListener(this);

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userArrayList = new ArrayList<User>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);
                    userArrayList.add(user);
                }
                getAllUsersExceptLoggedInUser(userArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
                mListener.onCreateGrpFragmentInteraction();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void getAllUsersExceptLoggedInUser(ArrayList<User> userArrayList) {

        Log.d("create grp","size before"+userArrayList.size());
        groupMembersArrayList = new ArrayList<>();

        for (int i=0;i<userArrayList.size();i++){
            User user = userArrayList.get(i);
            if (user.getUid().equals(uid)){
                loggedInUser = user;
                userArrayList.remove(user);
                break;
            }
            else {
                continue;
            }

        }
        Log.d("create grp","size after"+userArrayList.size());
        displayUsers(userArrayList);

    }

    private void displayUsers(ArrayList<User> userArrayList) {
        //recycler view(for displaying all users)
        if (getActivity()!=null){
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ChooseUsersAdapter adapter = new ChooseUsersAdapter(userArrayList, getActivity());
            recyclerView.setAdapter(adapter);
            adapter.setClickListener1(this);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCreateGrpFragmentInteraction();
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
    public void onCheckboxClicked(int position, boolean b) {

        Log.d("create grp ","position "+ position);
        User user1 = userArrayList.get(position);
        /*if (groupMembersArrayList.contains(user1)){
            groupMembersArrayList.remove(user1);
            Log.d("create grp ","pos removed"+groupMembersArrayList.size()+"<>"+ user1);

        }else {
            groupMembersArrayList.add(user1);
            Log.d("create grp ","pos added "+groupMembersArrayList.size()+"<>"+ user1);
        }*/

        /*if (sharedPreferencesCGrp.contains(user1.getUid())){
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.remove(user1.getUid());
            editor1.commit();
            groupMembersArrayList.remove(user1);
            Log.d("create grp ","pos removed"+groupMembersArrayList.size()+"<>"+ user1);
        }
        else {
            SharedPreferences.Editor editor = sharedPreferencesCGrp.edit();
            editor.putString(user1.getUid(), user1.getUid());
            editor.commit();
            groupMembersArrayList.add(user1);
            Log.d("create grp ","pos added "+groupMembersArrayList.size()+"<>"+ user1);
        }*/

        if (b){

            groupMembersArrayList.add(user1);
            Log.d("create grp ","pos added "+groupMembersArrayList.size()+"<>"+ user1);
        }
        else {
            groupMembersArrayList.remove(user1);
            Log.d("create grp ","pos removed"+groupMembersArrayList.size()+"<>"+ user1);
        }

    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.imageViewGroupPic){
            Log.d("create grp ","image clicked");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 102);

        }
        else if (v.getId()==R.id.buttonNewGrpCreate){
            Log.d("create grp ","create clicked");
            groupTitle = editTextGrpTitle.getText().toString();


            if (groupTitle.length()==0 | groupTitle==null){
                Toast.makeText(getActivity(), "Enter some title for the group", Toast.LENGTH_SHORT).show();
            }else if (groupMembersArrayList.size()==0){
                Toast.makeText(getActivity(), "Atleast 1 contact must be selected", Toast.LENGTH_SHORT).show();
            }
            else{
                //add loggedIn User(Creator) to the grp and create it.
                editTextGrpTitle.setText("");
                groupMembersArrayList.add(loggedInUser);
                //Toast.makeText(getActivity(), "Group created", Toast.LENGTH_SHORT).show();

                GroupDetails groupDetails = new GroupDetails();
                groupDetails.setCreatedBy(fname1+" "+lname1);
                groupDetails.setCreatorId(uid);
                groupDetails.setGroupPic(groupPicUrl);
                groupDetails.setGroupTitle(groupTitle);
                groupDetails.setMembersList(groupMembersArrayList);
                Log.d("create grp","temp after "+groupMembersArrayList.size());

                DatabaseReference groupRef = mUserRef.child(uid).child("Group").push();
                String key = groupRef.getKey();
                groupDetails.setGroupId(key);
                groupRef.setValue(groupDetails);

                ArrayList<User> tempGroup = new ArrayList<>();
                tempGroup = groupMembersArrayList;
                Log.d("create grp","temp before "+tempGroup.size());
                //tempGroup.remove(loggedInUser);
                for (int i=0;i<tempGroup.size();i++){

                    User tempUser = tempGroup.get(i);
                    if (tempUser.getUid()==uid){
                        continue;
                    }
                    else{
                        mUserRef.child(tempUser.getUid()).child("Group").child(groupDetails.getGroupId()).setValue(groupDetails);
                        Log.d("create grp","temp after "+groupDetails.toString());
                    }
                }




            }

        }
        else {
            Log.d("create grp ","wrong click");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==102 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            Uri uri = data.getData();
            Date date = new Date();
            long epoch = date.getTime();
            String time = String.valueOf(epoch);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data12 =  baos.toByteArray();

                StorageReference imageRef = storageReference.child("images/"+displayNameFirebase+time+".JPEG");
                UploadTask uploadTask = imageRef.putBytes(data12);
                uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri uri12 = taskSnapshot.getDownloadUrl();
                        groupPicUrl = uri12.toString();

                        //setting img into imageview
                        if (groupPicUrl!=null){
                            Picasso.with(getActivity()).load(groupPicUrl).fit().centerCrop().into(imageViewGroupPic);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }



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
        void onCreateGrpFragmentInteraction();
    }
}
