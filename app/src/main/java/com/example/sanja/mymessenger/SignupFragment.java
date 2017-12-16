package com.example.sanja.mymessenger;

import android.content.Context;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SignupFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    EditText editTextFName;
    EditText editTextLName;
    EditText editTextUserName;
    EditText editTextEmail;
    EditText editTextPass;
    EditText editTextRepeatPass;
    RadioGroup radioGroup;
    RadioButton radioButtonMale;
    RadioButton radioButtonFemale;
    Button buttonCancel;
    Button buttonSignup;
    String gender = null;
    User user = null;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity.isAccount=false;
        mAuth = FirebaseAuth.getInstance();

        editTextFName = (EditText)getActivity().findViewById(R.id.editTextFirstName);
        editTextLName = (EditText)getActivity().findViewById(R.id.editText2LName);
        editTextUserName = (EditText)getActivity().findViewById(R.id.editTextUsername);
        editTextEmail = (EditText)getActivity().findViewById(R.id.editTextEmailSignup);
        editTextPass = (EditText)getActivity().findViewById(R.id.editTextPassSignup);
        editTextRepeatPass = (EditText)getActivity().findViewById(R.id.editTextRepeatPass);
        radioGroup = (RadioGroup)getActivity().findViewById(R.id.radioGroup);
        radioButtonMale = (RadioButton)getActivity().findViewById(R.id.radioButtonMale);
        radioButtonFemale = (RadioButton)getActivity().findViewById(R.id.radioButtonFemale);
        buttonCancel = (Button)getActivity().findViewById(R.id.buttonCancelSignup);
        buttonSignup = (Button)getActivity().findViewById(R.id.buttonSignupSignup);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = signupUser();
                if (user!=null){
                    Log.d("signup ","signup "+user.toString());
                    mListener.onSignupFragmentInteraction(1,user);
                }
                else{
                    Log.d("signup ","signup null found");
                }


            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("signup ","cancel "+user.toString());
                mListener.onSignupFragmentInteraction(0,user);
            }
        });

    }

    private User signupUser() {
        User user1 = null;
        String fname = editTextFName.getText().toString();
        String lname = editTextLName.getText().toString();
        String uname = editTextUserName.getText().toString();
        String email = editTextEmail.getText().toString();
        String pass = editTextPass.getText().toString();
        String rpass = editTextRepeatPass.getText().toString();

        int rId = radioGroup.getCheckedRadioButtonId();
        if (rId ==R.id.radioButtonMale){
            gender ="Male";
        }else{
            gender ="Female";
        }

        if(fname.length()==0 | lname.length()==0 | uname.length()==0 | email.length()==0 | pass.length()==0 |
                rpass.length()==0 | rId== -1 | !(pass.equals(rpass)) | !(email.contains("@"))){

            Toast.makeText(getActivity(), "Enter correct details", Toast.LENGTH_SHORT).show();
            return null;
        }
        else{
            Log.d("Success","");
            user1 = new User();
            user1.setFname(fname);
            user1.setLname(lname);
            user1.setUname(uname);
            user1.setEmail(email);
            user1.setPassword(pass);
            user1.setGender(gender);
            return user1;
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int uri, User user) {
        if (mListener != null) {
            mListener.onSignupFragmentInteraction(uri, user);
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
        void onSignupFragmentInteraction(int uri, User user);
    }
}
