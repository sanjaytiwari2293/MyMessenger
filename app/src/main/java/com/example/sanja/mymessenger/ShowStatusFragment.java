package com.example.sanja.mymessenger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowStatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ShowStatusFragment extends Fragment implements ShowStatusAdapter.ClickListener {

    private OnFragmentInteractionListener mListener;
    TextView textViewMyStatus;
    ImageButton imageButtonEditStatus;
    RecyclerView recyclerView;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("User");
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedPreferences;
    String fname1, lname1, uid;
    ArrayList<User> userArrayList = new ArrayList<>();
    User loggedInUser;
    public static final String USERSTAT = "userstat";

    public static final String FragStatus = "status" ;
    SharedPreferences fragStausPreferences;

    public ShowStatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle!=null){
            loggedInUser = (User) this.getArguments().getSerializable(USERSTAT);
        }

        return inflater.inflate(R.layout.fragment_show_status, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textViewMyStatus = (TextView)getActivity().findViewById(R.id.textViewMyStatus);
        imageButtonEditStatus = (ImageButton)getActivity().findViewById(R.id.imageButtonChangeStatus);
        recyclerView = (RecyclerView)getActivity().findViewById(R.id.recyclerViewShowStatus);

        fragStausPreferences = getActivity().getSharedPreferences(FragStatus, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = fragStausPreferences.edit();
        editor.putString("status","status1");
        editor.commit();
        Log.d("FRAG STATUS","status"+fragStausPreferences.getAll());

        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        fname1 = sharedPreferences.getString("fname","");
        lname1 = sharedPreferences.getString("lname","");
        uid = sharedPreferences.getString("uid","");



        mRootRef.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userArrayList = new ArrayList<User>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);
                    userArrayList.add(user);
                }
                Log.d("status","size "+userArrayList.size());
                excludeLoggedInUser(userArrayList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imageButtonEditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.status_dialog);
                dialog.setCancelable(true);
                final EditText editTextNewStatus = (EditText) dialog.findViewById(R.id.editTextNewStat);
                Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancelStat);
                Button buttonOk = (Button) dialog.findViewById(R.id.buttonOkStat);

                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String status = editTextNewStatus.getText().toString();
                        if (status!=null && status.length()!=0){
                            saveStatus(status);
                            editTextNewStatus.setText("");
                            /*Log.d("view","SENDING");*/
                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(dialog.getContext(), "Please enter some text", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void excludeLoggedInUser(ArrayList<User> userArrayList) {

        for (int i=0;i<userArrayList.size();i++){

            User user = userArrayList.get(i);
            if (user.getUid().equals(uid)){
                textViewMyStatus.setText(user.getStatus());
                userArrayList.remove(user);
                break;
            }
            else{
                continue;
            }

        }
        displayStatus(userArrayList);

    }

    private void displayStatus(ArrayList<User> userArrayList) {

        if (getActivity()!=null){
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ShowStatusAdapter adapter = new ShowStatusAdapter(userArrayList, getActivity());
            recyclerView.setAdapter(adapter);
            adapter.onClickListener(this);
        }
    }

    private void saveStatus(String status) {
        textViewMyStatus.setText(status);

        User user = new User();
        user.setImageUrl(loggedInUser.getImageUrl());
        user.setFname(loggedInUser.getFname());
        user.setLname(loggedInUser.getLname());
        user.setUid(loggedInUser.getUid());
        user.setStatus(status);

        mRootRef.child("status").child(uid).setValue(user);

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
    public void onContainerClick(int position) {

        Log.d("show stat frag","clicked "+position+" <> "+userArrayList.get(position));
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
