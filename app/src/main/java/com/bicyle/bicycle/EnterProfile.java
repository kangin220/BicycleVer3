package com.bicyle.bicycle;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EnterProfile extends AppCompatActivity {
    ProfDataSet prof = new ProfDataSet();
    private String uid;
    private String devToken;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_profile);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        devToken = intent.getStringExtra("devToken");

        final EditText nicknameEtext = (EditText) findViewById(R.id.nicknameEtext);

        nicknameEtext.setHint("닉네임을 적어주세요.");
        Query query = mDatabase.child("Profiles").orderByChild("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean existUser = dataSnapshot.exists();
                if(existUser) {
                    for(DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                        nicknameEtext.setText(dataSnap.child("nickname").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        RadioGroup genderRgroup = (RadioGroup) findViewById(R.id.genderRgroup);
        genderRgroup.clearCheck();

        Button submitBtn = (Button) findViewById(R.id.submitBtn);

        genderRgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.maleRbtn: prof.setGender("M");
                        break;
                    case R.id.femaleRbtn: prof.setGender("F");
                        break;
                }
            }
        });

        final String[] locList = getResources().getStringArray(R.array.locList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locList);
        Spinner locSpinner = (Spinner)findViewById(R.id.locSpinner);
        locSpinner.setAdapter(adapter);
        locSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prof.setLocation(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] ageList = new String[100];
        for(int i = 5; i < 100 ; i++) {
            ageList[i-5] = String.valueOf(i);
        }
        ArrayAdapter<String> ageAdpt = new ArrayAdapter<>(this,  android.R.layout.simple_dropdown_item_1line, ageList);
        Spinner ageSpinner = (Spinner)findViewById(R.id.ageSpinner);
        ageSpinner.setAdapter(ageAdpt);
        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prof.setAge(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nicknameEtext.getText().toString().isEmpty() || prof.getLocation().equals("") || prof.getGender().equals("")) {
                    Toast.makeText(EnterProfile.this, "필요한 정보가 부족합니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Query query = mDatabase.child("Profiles").orderByChild("nickname").equalTo(nicknameEtext.getText().toString());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean existUser = dataSnapshot.exists();
                            if(existUser) {
                                Toast.makeText(EnterProfile.this, "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                prof.setNickname(nicknameEtext.getText().toString());
                                prof.setUid(uid);
                                prof.setDeviceToken(devToken);
                                mDatabase.child("Profiles").child(uid).setValue(prof);
                                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                                intent.putExtra("prof", prof);
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

}
