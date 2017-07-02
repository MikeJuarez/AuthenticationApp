package michael_juarez.authenticationapp;

import android.content.Context;
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

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.accounts.AccountManager.KEY_PASSWORD;
import static android.support.v7.widget.AppCompatDrawableManager.get;

/**
 * Created by user on 6/28/2017.
 */

public class authentication_fragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final static String TAG = "Authentication Fragment";
    private final static String KEY_USER_NAME = "user_name";
    private final static String KEY_PASSWORD = "password";

    private Button mLoginButton;
    private EditText mUserName;
    private EditText mPassword;

        CharSequence user_name;
        CharSequence pass;

        @Override
        public void onStart() {
            super.onStart();
            mAuth.addAuthStateListener(mAuthListener);
        }

        @Override
        public void onStop() {
            super.onStop();
            if (mAuthListener != null) {
                mAuth.removeAuthStateListener(mAuthListener);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            super.onCreateView(inflater, container, savedInstanceState);
            View view = inflater.inflate(R.layout.authentication_fragment, container, false);

            mLoginButton = (Button) view.findViewById(R.id.login_button);
            mUserName = (EditText) view.findViewById(R.id.user_name);
            mPassword = (EditText) view.findViewById(R.id.password);



            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textValidate(mUserName.getText().toString()) && textValidate (mPassword.getText().toString()))
                        signIn(mUserName.getText().toString(), mPassword.getText().toString());
                    else
                        Toast.makeText(getActivity().getBaseContext(), R.string.fieldsareempty, Toast.LENGTH_LONG).show();
                }
            });

            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                    // ...
                }
            };

            if (savedInstanceState != null) {
                user_name = savedInstanceState.getCharSequence(KEY_USER_NAME);
                pass = savedInstanceState.getCharSequence(KEY_PASSWORD);

                mUserName.setText(user_name.toString(), TextView.BufferType.EDITABLE);
                mPassword.setText(pass.toString(), TextView.BufferType.EDITABLE);
            }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_USER_NAME, mUserName.getText());
        outState.putCharSequence(KEY_PASSWORD, mPassword.getText());
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed, creating this user", task.getException());
                            createAccount(mUserName.getText().toString(), mPassword.getText().toString());
                        }
                        else
                            Toast.makeText(getActivity().getApplicationContext(), R.string.auth_successful,
                                    Toast.LENGTH_SHORT).show();
                        // ...
                    }
                });
    }


    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "create user failed:", task.getException());
                            Toast.makeText(getActivity().getApplicationContext(),task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity().getApplicationContext(), R.string.create_account_successful,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private boolean textValidate(String text) {
        if (text.length() < 1 || text == null | text.isEmpty())
            return false;

        return true;
    }



}
