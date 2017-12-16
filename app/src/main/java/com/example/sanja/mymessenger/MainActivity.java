package com.example.sanja.mymessenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        SignupFragment.OnFragmentInteractionListener, UserAccountFragment.OnFragmentInteractionListener,
        MultiViewFragment.OnFragmentInteractionListener, AccountBlankFragment.OnFragmentInteractionListener,
        ViewUserFragment.OnFragmentInteractionListener, PersonalChatFragment.OnFragmentInteractionListener,
        ShowGroupsFragment.OnFragmentInteractionListener, ShowStatusFragment.OnFragmentInteractionListener,
        CreateGroupFragment.OnFragmentInteractionListener, GroupChatFragment.OnFragmentInteractionListener,
        CameraFragment.OnFragmentInteractionListener{


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("User");
    ArrayList<User> usersArrayList = new ArrayList<>();
    public static final String USER = "user";
    static User user1;
    static boolean isAccount = false;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    String uid, fname1, lname1;
    User sessionUser;
    static boolean isSessionUser =false;



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2371f9")));
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = this.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        uid = sharedPreferences.getString("uid","");
        fname1 = sharedPreferences.getString("fname","");
        lname1 = sharedPreferences.getString("lname","");
        Log.d("demo","sp "+sharedPreferences.getAll());
        RelativeLayout container = (RelativeLayout)findViewById(R.id.container);

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    User user =  snapshot.getValue(User.class);
                    usersArrayList.add(user);
                }
                Log.d("main","user arraylist "+usersArrayList.size()+" "+usersArrayList.toString());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Handler handler = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            public void run() {
                //do your stuff here after DELAY sec
                Log.d("test","calling func now");
                checkSessionUser();
            }
        };
        handler.postDelayed(r, 4000);




    }

    private void checkSessionUser() {

        Log.d("method","CALLED");
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser!=null){

            if (sharedPreferences.getAll().size()!=0){

                Log.d("demo","sp inside "+sharedPreferences.getAll());
                if (usersArrayList.size()!=0){

                    for (int i=0;i<usersArrayList.size();i++){

                        sessionUser = usersArrayList.get(i);
                        if (sessionUser.getUid().equals(uid)){
                            Log.d("seession","user "+sessionUser.toString());
                            isSessionUser=true;
                            break;
                        }
                        else {
                            Log.d("seession","user not found ");
                        }
                    }
                    UserAccountFragment fragment = new UserAccountFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(USER, sessionUser);
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment, "Account").commit();

                }
                else{
                    Log.d("seession","user size ZERO");
                }



            }


        }
        else{
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment(), "Login")
                    .commit();
            Log.d("seession","ADDED");
        }

    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount()==1 && isAccount && !isSessionUser){
            Log.d("test","normal user");
            Toast.makeText(this, "Please logout", Toast.LENGTH_SHORT).show();
        }
        else if(getSupportFragmentManager().getBackStackEntryCount()==0 && isSessionUser){
            Log.d("test","session user");

            Toast.makeText(this, "Please logout", Toast.LENGTH_SHORT).show();
        }

        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLoginFragmentInteraction(final String email, final String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    User user = getUserFullDetails(email);   //method call
                    if(user!= null){
                        UserAccountFragment fragment = new UserAccountFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(USER, user);
                        fragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment, "Account")
                                .addToBackStack(null).commit();
                    }

                }
                else {
                    Toast.makeText(MainActivity.this, "Enter Correct Details", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onSignupFragmentInteraction(int uri, User user) {

        if (uri==0){
            Log.d("main","cancel btn clicked");

        }else if (uri==1 && user!=null){
            Log.d("main","signup btn clicked");
            Log.d("main","USER "+user.toString());
            signupNewUser(user);
        }
        else{
            Log.d("main","SOMETHING WENT WRONG. "+user.toString());
        }
        getSupportFragmentManager().popBackStack();
    }

    public User getUserFullDetails(String email){

         if (usersArrayList.size()!=0){

             for (int i=0; i<usersArrayList.size(); i++){
                 User user = usersArrayList.get(i);
                 if (user.getEmail().equals(email)){
                     return user;
                 }
                 else {
                     continue;
                 }
             }

         }
        else{
             Toast.makeText(this, "Invalid Entry", Toast.LENGTH_SHORT).show();
         }

        return null;
    }

    private void signupNewUser(final User user) {

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(user.getFname()+" "+user.getLname()).build();
                    firebaseUser.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("main","Successfully added the user");
                                Toast.makeText(MainActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();

                                DatabaseReference newUserRef = mUserRef.push();
                                user.setUid(newUserRef.getKey());
                                newUserRef.setValue(user);

                            }

                        }
                    });

                }
                else {

                    Toast.makeText(MainActivity.this, "Error occured. Please try again", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onUserAccountFragmentInteraction() {

        //Log.d("main","count backstack before"+getSupportFragmentManager().getBackStackEntryCount());

        /*while(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
            Log.d("main","count backstack inside "+getSupportFragmentManager().getBackStackEntryCount());
        }
        Log.d("main","count backstack after "+getSupportFragmentManager().getBackStackEntryCount());*/

        callLogout();

    }

    private void callLogout() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        if (isSessionUser){
            isSessionUser=false;
        }
        mAuth.signOut();
        finish();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAccountBlankFragmentInteraction(String s, User user) {

        if (s.equals("save")){
            this.user1 = user;
            getSupportFragmentManager().popBackStack();
        }
        else if (s.equals("cancel")){
            Log.d("main","cancelled");
            getSupportFragmentManager().popBackStack();
        }
        else if (s.equals("logout")){
            Log.d("main","edit "+getSupportFragmentManager().getBackStackEntryCount());
           callLogout();

        }

    }

    @Override
    public void onPChatFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCreateGrpFragmentInteraction() {
     callLogout();
    }

    @Override
    public void onCameraFragmentInteraction() {

        getSupportFragmentManager().popBackStack();

    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }*/
}
