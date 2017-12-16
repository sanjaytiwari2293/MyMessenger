package com.example.sanja.mymessenger;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountBlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AccountBlankFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    FrameLayout frameLayout;
    ImageView imageViewEdit;
    EditText editTextEditFName;
    EditText editTextEditLName;
    EditText editTextEditUName;
    RadioGroup radioGroup;
    RadioButton radioButtonMale;
    RadioButton radioButtonFemale;
    Button buttonSave;
    Button buttonCancel;
    public static final String STAT = "edit";
    int stat = 0;
    User user = null;
    ImageButton gallery;
    ImageButton delete;
    String imageURL = null;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("User");
    public static final String CurrentUSER = "user";
    boolean isDeleted = false;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference rootRef = storage.getReference();
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public AccountBlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle!=null){
            stat = getArguments().getInt(STAT);
            user = (User) getArguments().getSerializable(CurrentUSER);
            Log.d("acc blank","user "+user.toString());

        }

        return inflater.inflate(R.layout.fragment_account_blank, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        imageViewEdit = (ImageView)getActivity().findViewById(R.id.imageViewEditProfilePic);
        imageViewEdit.setScaleType(ImageView.ScaleType.FIT_CENTER);
        editTextEditFName = (EditText)getActivity().findViewById(R.id.editTextEditFName);
        editTextEditLName = (EditText)getActivity().findViewById(R.id.editTextEditLName);
        editTextEditUName = (EditText)getActivity().findViewById(R.id.editTextEditUName);
        radioGroup = (RadioGroup)getActivity().findViewById(R.id.radioGroupEdit);
        radioButtonFemale = (RadioButton)getActivity().findViewById(R.id.radioButtonEditFemale);
        radioButtonMale = (RadioButton)getActivity().findViewById(R.id.radioButtonEditMale);
        buttonSave = (Button) getActivity().findViewById(R.id.buttonEditSave);
        buttonCancel = (Button) getActivity().findViewById(R.id.buttonEditCancel);
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.frameLayoutEdit);

        mAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String displayName = firebaseUser.getDisplayName();

        if (stat==1){
            frameLayout.setVisibility(View.VISIBLE);
        }
       /* else {
            frameLayout.setVisibility(View.INVISIBLE);
        }*/

        if (user!=null){
            editTextEditFName.setText(user.getFname());
            editTextEditLName.setText(user.getLname());
            editTextEditUName.setText(user.getUname());
            if (user.getGender().equals("Male")){
                radioButtonMale.setChecked(true);
            }
            else {
                radioButtonFemale.setChecked(true);
            }
            if (user.getImageUrl()!=null){
                Picasso.with(getContext()).load(user.getImageUrl()).fit().centerCrop().into(imageViewEdit);
                imageURL = user.getImageUrl();
            }

        }

        imageViewEdit.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);



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
                mListener.onAccountBlankFragmentInteraction("logout", null);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String s, User user) {
        if (mListener != null) {
            mListener.onAccountBlankFragmentInteraction(s,user);
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
        if (v.getId()==R.id.buttonEditSave){
            Log.d("account blank","Save");
            String fname = editTextEditFName.getText().toString();
            String lname = editTextEditLName.getText().toString();
            String uname = editTextEditUName.getText().toString();
            int id = radioGroup.getCheckedRadioButtonId();
            String gender;
            if (id==R.id.radioButtonEditFemale){
                gender = "Female";
            }
            else if(id==R.id.radioButtonEditMale){
                gender = "Male";
            }
            else {
                gender=null;
            }
            callSaveFunction(fname, lname, uname, gender);

           // mListener.onAccountBlankFragmentInteraction("save");

        }
        else if (v.getId()==R.id.buttonEditCancel){
            Log.d("account blank","Cancel");
           // mListener.onAccountBlankFragmentInteraction("cancel");
            mListener.onAccountBlankFragmentInteraction("cancel", null);

        }
        else if (v.getId()==R.id.imageViewEditProfilePic){
            Log.d("account blank","Picture");
            showDialog();
        }
      /*  else if (v.getId()==R.id.imageButtonChooseNewPic){
            Log.d("account blank","Open Gallery");

        }
        else if (v.getId()==R.id.imageButtonDeletePrevPic){
            Log.d("account blank","Delete Pic");

        }*/
        else{
            Log.d("account blank","Wrong");
        }
    }

    private void showDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(true);

        //remember below has dialog not getActivity;
        //https://stackoverflow.com/questions/28193552/null-pointer-exception-on-setonclicklistener
        gallery = (ImageButton)dialog.findViewById(R.id.imageButtonChooseNewPic);
        delete = (ImageButton)dialog.findViewById(R.id.imageButtonDeletePrevPic);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("account blank","Open Gallery");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 100);
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("account blank","Delete Pic");
                imageViewEdit.setImageResource(R.drawable.default_pic);
                isDeleted = true;
                dialog.dismiss();
            }
        });


        dialog.show();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==100 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            Uri uri = data.getData();

            try {
                Date date = new Date();
                Long epoch = date.getTime();
                String time = ""+epoch;


                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();

                StorageReference imageRef = rootRef.child("images/"+ firebaseUser.getDisplayName()+time+".JPEG");

                UploadTask uploadTask = imageRef.putBytes(data1);
                uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri uri1 = taskSnapshot.getDownloadUrl();
                        imageURL = uri1.toString();
                        Picasso.with(getContext()).load(imageURL).centerCrop().fit().into(imageViewEdit);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo","FAILED to UPLOAD IMG");
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void callSaveFunction(String fname, String lname, String uname, String gender) {

        if (fname!=null && lname!=null && uname!=null && gender!=null){

            if (isDeleted){
                imageURL = null;
            }
            User user = new User();
            user.setFname(fname);
            user.setLname(lname);
            user.setUname(uname);
            user.setGender(gender);
            user.setImageUrl(imageURL);

            Log.d("acc blank","shared pref "+sharedpreferences.getString("uid",null));
            DatabaseReference loginUserRef = mUserRef.child(sharedpreferences.getString("uid",""));
            loginUserRef.child("fname").setValue(user.getFname());
            loginUserRef.child("lname").setValue(user.getLname());
            loginUserRef.child("uname").setValue(user.getUname());
            loginUserRef.child("gender").setValue(user.getGender());
            loginUserRef.child("imageUrl").setValue(user.getImageUrl());

            mListener.onAccountBlankFragmentInteraction("save", user);
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
        void onAccountBlankFragmentInteraction(String s, User user);
    }
}
