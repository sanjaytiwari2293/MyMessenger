package com.example.sanja.mymessenger;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowGroupsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ShowGroupsFragment extends Fragment implements ShowGrpsAdapter.ClickListener {

    LinearLayout newGroupLayout;
    TextView textViewUnavailable;
    private OnFragmentInteractionListener mListener;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("User");
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    String fname1;
    String lname1;
    String uid;
    ArrayList<GroupDetails> groupDetailsArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    public static final String GrpChat = "grpchat";

    public static final String FragStatus = "status" ;
    SharedPreferences fragStausPreferences;

    public ShowGroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /*getActivity().setTitle("Groups");*/
        return inflater.inflate(R.layout.fragment_show_groups, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fragStausPreferences = getActivity().getSharedPreferences(FragStatus, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = fragStausPreferences.edit();
        editor.putString("status","group");
        editor.commit();
        Log.d("FRAG STATUS","group"+fragStausPreferences.getAll());

        sharedPreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        fname1 = sharedPreferences.getString("fname","");
        lname1 = sharedPreferences.getString("lname","");
        uid = sharedPreferences.getString("uid","");
        newGroupLayout = (LinearLayout)getActivity().findViewById(R.id.linearLayoutNewGroup);
        textViewUnavailable = (TextView)getActivity().findViewById(R.id.textViewUnavailable);
        recyclerView = (RecyclerView)getActivity().findViewById(R.id.recyclerViewShowGrps);

        mUserRef.child(uid).child("Group").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupDetailsArrayList = new ArrayList<GroupDetails>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupDetails groupDetails = snapshot.getValue(GroupDetails.class);
                    groupDetailsArrayList.add(groupDetails);
                }
                displayGroups(groupDetailsArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        newGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("show grps","new grp clicked ");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new CreateGroupFragment(), "createGrp")
                        .addToBackStack(null).commit();
            }
        });

    }

    private void displayGroups(ArrayList<GroupDetails> groupDetailsArrayList) {

        if (groupDetailsArrayList.size()==0){
            //no grps available
            textViewUnavailable.setVisibility(View.VISIBLE);
        }
        else{
            Log.d("show grps","grps present");
            if (getActivity()!=null){
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                ShowGrpsAdapter adapter = new ShowGrpsAdapter(groupDetailsArrayList, getActivity());
                recyclerView.setAdapter(adapter);
                adapter.onGrpClickListener(this);
            }
        }

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

        Log.d("show grps","grp clicked "+groupDetailsArrayList.get(position));
        GroupDetails details = groupDetailsArrayList.get(position);
        GroupChatFragment groupChatFragment = new GroupChatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GrpChat, details);
        groupChatFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, groupChatFragment, "grpchat")
                .addToBackStack(null).commit();
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
