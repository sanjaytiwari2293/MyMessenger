package com.example.sanja.mymessenger;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    EditText emailLogin;
    EditText passLogin;
    Button buttonLogin;
    Button buttonSignup;
    String email;
    String password;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        emailLogin = (EditText) getActivity().findViewById(R.id.editTextEmailLogin);
        passLogin = (EditText) getActivity().findViewById(R.id.editTextPasswordLogin);
        buttonLogin = (Button)getActivity().findViewById(R.id.buttonLogin);
        buttonSignup = (Button)getActivity().findViewById(R.id.buttonSignup);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSignupFunction();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLoginFunction();
            }
        });
    }

    private void callLoginFunction() {
        email = emailLogin.getText().toString();
        password = passLogin.getText().toString();

        if (email!=null && password!=null && email.length()!=0 && password.length()!=0 ){
            mListener.onLoginFragmentInteraction(email,password);

        }
        else{
            Toast.makeText(getContext(), "Entry Invalid", Toast.LENGTH_SHORT).show();
        }
    }

    private void callSignupFunction() {
        //redirect to signup fragment..

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SignupFragment(), "Signup").addToBackStack(null).commit();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String email, String password) {
        if (mListener != null) {
            mListener.onLoginFragmentInteraction(email,password);
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
        void onLoginFragmentInteraction(String email, String password);
    }
}
