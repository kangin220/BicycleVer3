package com.bicyle.bicycle;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class StartLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    FirebaseUser currentUser;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        currentUser = mAuth.getCurrentUser();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });

//        Button logoutBtn = (Button) findViewById(R.id.logoutBtn);
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//                AlertDialog.Builder alt_bld = new AlertDialog.Builder(view.getContext());
//                alt_bld.setMessage("로그아웃 하시겠습니까?").setCancelable(false)
//                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                signOut();
//                            }
//                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                AlertDialog alert = alt_bld.create();
//
//                alert.setTitle("로그아웃");
//
//                alert.show();
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //구글 로그인 성공해서 파베에 인증
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                //구글 로그인 실패
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(StartLogin.this, "인증 실패", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(StartLogin.this, "구글 로그인 인증 성공", Toast.LENGTH_SHORT).show();
                            final String uidExtra = mAuth.getCurrentUser().getUid();
                            final String devTokenExtra = FirebaseInstanceId.getInstance().getToken();
                            Query query = mDatabase.child("Profiles").orderByChild("uid").equalTo(uidExtra);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean existUser = dataSnapshot.exists();
                                    if(existUser) {
                                        String loadUid, loadNickname, loadLocation, loadAge, loadGender, loadDevToken;
                                        loadUid = dataSnapshot.child(uidExtra).child("uid").getValue(String.class);
                                        loadNickname = dataSnapshot.child(uidExtra).child("nickname").getValue(String.class);
                                        loadLocation = dataSnapshot.child(uidExtra).child("location").getValue(String.class);
                                        loadAge = dataSnapshot.child(uidExtra).child("age").getValue(String.class);
                                        loadGender = dataSnapshot.child(uidExtra).child("gender").getValue(String.class);
                                        loadDevToken = dataSnapshot.child(uidExtra).child("deviceToken").getValue(String.class);

                                        ProfDataSet profExtra = new ProfDataSet(loadUid, loadNickname, loadLocation, loadAge, loadGender, loadDevToken);

                                        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                                        intent.putExtra("prof", profExtra);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Intent intent = new Intent(getApplicationContext(), EnterProfile.class);
                                        intent.putExtra("uid", uidExtra);
                                        intent.putExtra("devToken", devTokenExtra);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}