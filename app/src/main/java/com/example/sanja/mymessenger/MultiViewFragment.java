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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MultiViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MultiViewFragment extends Fragment implements ShowUsersAdapter.ClickListener {

    private OnFragmentInteractionListener mListener;
    RecyclerView recyclerViewMultiView;
    public static final String AVAIL_USERS = "users";
    public static final String VIEW = "user";
    ArrayList<User> userArrayListOtherUsers = new ArrayList<>();

    public static final String FragStatus = "status" ;
    SharedPreferences fragStausPreferences;

    public MultiViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getArguments().getSerializable(AVAIL_USERS)!=null){
            userArrayListOtherUsers = (ArrayList<User>) getArguments().getSerializable(AVAIL_USERS);

        }

        return inflater.inflate(R.layout.fragment_multi_view, container, false);
    }

    private void displayList(ArrayList<User> userArrayListOtherUsers) {

        recyclerViewMultiView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ShowUsersAdapter adapter = new ShowUsersAdapter(userArrayListOtherUsers, getActivity());
        recyclerViewMultiView.setAdapter(adapter);
        adapter.setClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fragStausPreferences = getActivity().getSharedPreferences(FragStatus, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = fragStausPreferences.edit();
        editor.putString("status","chat");
        editor.commit();
        Log.d("FRAG STATUS","chat"+fragStausPreferences.getAll());

        recyclerViewMultiView = (RecyclerView)getActivity().findViewById(R.id.recyclerViewMulti);
        displayList(userArrayListOtherUsers);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
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
        Log.d("multi","position "+userArrayListOtherUsers.get(position));
        PersonalChatFragment fragment = new PersonalChatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIEW, userArrayListOtherUsers.get(position));
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, "View")
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
