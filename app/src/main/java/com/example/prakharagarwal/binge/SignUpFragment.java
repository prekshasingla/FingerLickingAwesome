package com.example.prakharagarwal.binge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by prakharagarwal on 20/08/17.
 */

public class SignUpFragment extends Fragment implements
        View.OnClickListener{
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final int FB_SIGN_IN = 8001;
    TextView loginstatus;
    FirebaseAuth mAuth;
    CallbackManager mCallbackManager;
    EditText user_email;
    EditText user_password;
    Button signup;
    FirebaseDatabase mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_signup,container,false);
        rootView.findViewById(R.id.login_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.login_activity_container,new LoginFragment()).commit();
            }
        });
        user_email=(EditText) rootView.findViewById(R.id.user_email);
        user_password=(EditText) rootView.findViewById(R.id.user_password);
        signup=(Button)rootView.findViewById(R.id.signup_button);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_email.getText().toString().equals("") && user_password.getText().toString().equals(""))
                {

                }else{
                    userSignUpFirebase(user_email.getText().toString(),user_password.getText().toString());
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();


        mGoogleApiClient=((LoginActivity)getActivity()).getGAC();

        rootView.findViewById(R.id.google_sign_in_button).setOnClickListener(this);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.fb_sign_in_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FB_Logn", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FB_Logn", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FB_Logn", "facebook:onError", error);
                // ...
            }
        });
        mDatabase=FirebaseDatabase.getInstance();
        return rootView;
    }
    public FirebaseDatabase getDatabase(){
        return mDatabase;
    }
    private void userSignUpFirebase(final String email, final String password) {


        DatabaseReference ref = mDatabase.getReference().child("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkExistingUser(dataSnapshot,email,password);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void checkExistingUser(DataSnapshot dataSnapshot,String email,String password){
        boolean flag=true;
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            if (child.getKey().equals(email)) {
                flag=false;
                break;
            }


        }
        if(flag)
            updateUser(email,password);
        else{
            updateUser(null,null);
        }

    }

    private void updateUser(String email, String password) {

        if(email!=null && password!=null) {
            DatabaseReference ref1 = mDatabase.getReference().child("users").child(email);
            User user = new User();
            user.setPassword(password);
            ref1.setValue(user);

        }else{
            Toast.makeText(getActivity(), "User exists", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }


    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("G_Logn", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("G_Logn", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginstatus.setText(user.getEmail());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("G_Logn", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("FB_Logn", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("FB_Logn", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginstatus.setText(user.getDisplayName() + user.getEmail());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("FB_Logn", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;

        }

    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


}