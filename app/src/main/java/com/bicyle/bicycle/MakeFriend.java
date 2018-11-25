package com.bicyle.bicycle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class MakeFriend extends AppCompatActivity {

    String friendNickName = "";
    String myNickname = "";
    String friendUid = "";
    String senderUid = "";
    String myUid;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_friend);

        Intent intent = getIntent();
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myNickname = intent.getStringExtra("myNickName");
        senderUid = intent.getStringExtra("senderUid");
        friendNickName = intent.getStringExtra("senderNickName");

        Toast.makeText(getApplicationContext(), senderUid, Toast.LENGTH_LONG).show();

        Button positiveBtn = findViewById(R.id.postiveBtn);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                Query query = mDatabase.child("Profiles").orderByChild("uid").equalTo(senderUid);
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
//                                friendUid = dataSnap.child("uid").getValue().toString();
//                                mDatabase.child("FrdRelship").child(myUid).push().child("uid").setValue(friendUid);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
                mDatabase.child("FrdRelship").child(myUid).push().child("uid").setValue(senderUid);
                mDatabase.child("FrdRelship").child(senderUid).push().child("uid").setValue(myUid);
                finish();
            }
        });

        Button negativeBtn = (Button) findViewById(R.id.negativeBtn);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                finish();
            }
        });
    }

}