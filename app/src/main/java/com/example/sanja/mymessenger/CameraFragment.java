package com.example.sanja.mymessenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CameraFragment extends Fragment implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback{

    private OnFragmentInteractionListener mListener;
    Camera mCamera;
    SurfaceView mPreview;
    ImageButton click;
    ImageButton cancel;
    ImageButton send;
    public static final String CameraViewUser = "user";
    User user;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedPreferences;
    String uid, fname1, lname1;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener listener;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String userDisplayName;
    byte[] byteData;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("User");
    public static final String ChatMsgesArrayList = "chatMsgesArrayList";
    public static final String ViewUserChatMsgList = "viewUserChatMsgList";
    //ArrayList<ChatMsges> chatMsgesArrayList = new ArrayList<>();
    //ArrayList<ChatMsges> viewUserChatMsgList = new ArrayList<>();

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle!=null){
            user = (User) bundle.getSerializable(CameraViewUser);
            PersonalChatFragment.chatMsgesArrayList = (ArrayList<ChatMsges>) bundle.getSerializable(ChatMsgesArrayList);
            PersonalChatFragment.viewUserChatMsgList = (ArrayList<ChatMsges>) bundle.getSerializable(ViewUserChatMsgList);

            Log.d("camera","sizes loggedin user "+PersonalChatFragment.chatMsgesArrayList.size()+" <> viewUser "+PersonalChatFragment.viewUserChatMsgList.size());
        }
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        userDisplayName = firebaseUser.getDisplayName();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        uid = sharedPreferences.getString("uid","");
        fname1 = sharedPreferences.getString("fname","");
        lname1 = sharedPreferences.getString("lname","");

        mPreview = (SurfaceView)getActivity().findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cancel = (ImageButton)getActivity().findViewById(R.id.imageButtonCancelNewPic);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.startPreview();
                cancel.setVisibility(View.INVISIBLE);
                send.setVisibility(View.INVISIBLE);
            }
        });
        send = (ImageButton)getActivity().findViewById(R.id.imageButtonOkSendPic);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mCamera.startPreview();*/
                Date date = new Date();
                long epoch = date.getTime();
                String time = String.valueOf(epoch);
                final ChatMsges msges = new ChatMsges();

                msges.setMsgType("1");
                msges.setTime(time);
                msges.setReceiverId(user.getUid());
                msges.setReceiverName(user.getFname()+" "+user.getLname());
                msges.setSenderId(uid);
                msges.setSenderName(fname1+" "+lname1);

                ///////
                msges.setReadStatus("black");

                /*send.setVisibility(View.INVISIBLE);*/

                StorageReference imageRef = storageReference.child("images/"+userDisplayName+time+".JPEG");
                UploadTask uploadTask = imageRef.putBytes(byteData);
                uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri url = taskSnapshot.getDownloadUrl();
                        msges.setMessage(url.toString());
                        saveMsgObjectToFirebase(msges);
                    }
                });

                mListener.onCameraFragmentInteraction();
            }
        });
        click = (ImageButton)getActivity().findViewById(R.id.imageButtonTakePic);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSnapClick(v);
            }
        });

        mCamera = Camera.open();
    }

    private void saveMsgObjectToFirebase(ChatMsges msges) {


        //for sender
        Log.d("Pchat","save msg firebase called");
        DatabaseReference subUserRef = mUserRef.child(sharedPreferences.getString("uid",""));
        DatabaseReference chatRef = subUserRef.child("Chat");
        DatabaseReference subChatRef = chatRef.child(sharedPreferences.getString("uid","")+user.getUid());
        //

        PersonalChatFragment.chatMsgesArrayList.add(msges);
        subChatRef.setValue(PersonalChatFragment.chatMsgesArrayList);
        /*DatabaseReference msgRef = subChatRef.push();
        chat.setMsgKey(msgRef.getKey());
        Log.d("demo","chat obj "+chat);
        msgRef.setValue(chat);*/

        ////////for receiver
        DatabaseReference otherUserRef = mUserRef.child(user.getUid());
        DatabaseReference otherUserChatRef = otherUserRef.child("Chat");
        DatabaseReference otherUserSubChatRef =  otherUserChatRef.child(user.getUid()+sharedPreferences.getString("uid",""));
        /*otherUserSubChatRef.child(chat.getMsgKey()).setValue(chat);*/
        PersonalChatFragment.viewUserChatMsgList.add(msges);
        otherUserSubChatRef.setValue(PersonalChatFragment.viewUserChatMsgList);



    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
        Log.d("CAMERA","Destroy");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCameraFragmentInteraction();
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

    public void onSnapClick(View v) {
        mCamera.takePicture(this, null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        byteData = data;
        mCamera = camera;
        Log.d("demo","onPictureTaken called");
        cancel.setVisibility(View.VISIBLE);
        send.setVisibility(View.VISIBLE);


    }

    @Override
    public void onShutter() {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width,selected.height);
        mCamera.setParameters(params);

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

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
        void onCameraFragmentInteraction();
    }
}
