package com.bicyle.bicycle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bicyle.bicycle.Board.BoardActivity;
import com.bicyle.bicycle.util.UserChatActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {
    ListView listView;
    Toolbar mainToolbar;
    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;

    ListView frdListView;
    EditText frdEditText;
    Button frdSrchBtn;

    ArrayAdapter mFriendAdapter;
    List<ProfDataSet> mProfileDataList = new ArrayList<>(); // get datas from freind relation table
    ArrayList<String> mFriendList = new ArrayList<>(); // show for Listview

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    ProfDataSet myProf;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Intent intent = getIntent();
        myProf = (ProfDataSet) intent.getSerializableExtra("prof");

        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(mainToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.addDrawerListener(dtToggle);

        final String[] naviMenuList = getResources().getStringArray(R.array.naviMainMenu);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, naviMenuList);

        listView = (ListView) findViewById(R.id.drawer_menulist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                        mapIntent.putExtra("prof", myProf);
                        startActivity(mapIntent);
                        break;
                    case 1:
                        Intent boardIntent = new Intent(getApplicationContext(), BoardActivity.class);
                        boardIntent.putExtra("prof", myProf);
                        startActivity(boardIntent);
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "동호회선택", Toast.LENGTH_SHORT).show();
                        break;
                }
                dlDrawer.closeDrawer(Gravity.LEFT);
            }
        });

        frdSrchBtn = findViewById(R.id.frdSrchBtn);
        mFriendAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mFriendList);
        frdListView = findViewById(R.id.frdList);
        frdListView.setAdapter(mFriendAdapter);
        frdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatIntent = new Intent(MainScreen.this, UserChatActivity.class);
                chatIntent.putExtra("myProf", myProf);
                chatIntent.putExtra("friendProf", mProfileDataList.get(position));
                startActivity(chatIntent);
            }
        });

        //데이터 베이스에서 친구 목록 불러오는 부분
        mDatabase.child("FrdRelship").child(myProf.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> friendUidList = new ArrayList<>();
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    String friendUid = dataSnap.child("uid").getValue().toString();
                    friendUidList.add(friendUid);
                }
                getFriendProfile(friendUidList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Get friend List fail", Toast.LENGTH_SHORT).show();
            }
        });

        frdListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(view.getContext());
                alt_bld.setMessage("친구 목록에서 삭제하시겠습니까?").setCancelable(false)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String strFrd = (String)parent.getAdapter().getItem(position);
                                Query query = mDatabase.child("Profiles").orderByChild("nickname").equalTo(strFrd);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                                            String delFrdUid = dataSnap.child("uid").getValue().toString();
                                            Query qry = mDatabase.child("FrdRelship").child(myProf.getUid()).orderByChild("uid").equalTo(delFrdUid);
                                            qry.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot inDataSnapshot) {
                                                    for(DataSnapshot inDataSanp : inDataSnapshot.getChildren()) {
                                                        inDataSanp.child("uid").getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            qry = mDatabase.child("FrdRelship").child(delFrdUid).orderByChild("uid").equalTo(myProf.getUid());
                                            qry.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot inDataSnapshot) {
                                                    for(DataSnapshot inDataSanp : inDataSnapshot.getChildren()) {
                                                        inDataSanp.child("uid").getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            mFriendList.remove(strFrd);
                                            mFriendAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                dialog.cancel();
                            }
                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = alt_bld.create();

                alert.setTitle("친구삭제");

                alert.show();
                return true;
            }
        });


        frdEditText = (EditText) findViewById(R.id.frdSearch);
        frdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //친구 추가가 되어 있는 목록에서 친구검색
            @Override
            public void afterTextChanged(Editable s) {
                String filterText = s.toString();
                if(filterText.length() > 0) {
                    frdListView.setFilterText(filterText);
                }
                else {
                    frdListView.clearTextFilter();
                }
            }
        });

        //데이터 베이스에서 닉네임을 검색해서 친구 요청을 보내는 부분
        frdSrchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(!mFriendList.contains(frdEditText.getText().toString())) {
                    if(myProf.getNickname().equals(frdEditText.getText().toString())) {
                        AlertDialog.Builder alt_bld = new AlertDialog.Builder(v.getContext());
                        alt_bld.setMessage("자기 자신은 친구추가를 할 수 없습니다.").setCancelable(false)
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = alt_bld.create();

                        alert.setTitle("친구요청");

                        alert.show();
                    }
                    else {
                        Query query = mDatabase.child("Profiles").orderByChild("nickname").equalTo(frdEditText.getText().toString());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                boolean existUser = dataSnapshot.exists();
                                if(existUser) {
//                            String snapData = dataSnapshot.getValue().toString();
//                            int indexOfToken = snapData.indexOf("deviceToken=");
//                            final String frdToken = snapData.substring(indexOfToken+12, snapData.length()-2);
                                    for(final DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                                        AlertDialog.Builder alt_bld = new AlertDialog.Builder(v.getContext());
                                        alt_bld.setMessage("친구 요청을 보내시겠습니까?").setCancelable(false)
                                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        MessageSender.getsInstance().inviteFrd(dataSnap.child("deviceToken").getValue().toString(), myProf.getUid(), myProf.getNickname(), dataSnap.child("nickname").getValue().toString());
                                                        dialog.cancel();
                                                    }
                                                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        AlertDialog alert = alt_bld.create();

                                        alert.setTitle("친구요청");

                                        alert.show();
                                    }
                                }
                                else {
                                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(v.getContext());
                                    alt_bld.setMessage("존재하지 않는 닉네임입니다.").setCancelable(false)
                                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert = alt_bld.create();

                                    alert.setTitle("친구요청");

                                    alert.show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(v.getContext());
                    alt_bld.setMessage("이미 친구인 상대입니다.").setCancelable(false)
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alt_bld.create();

                    alert.setTitle("친구요청");

                    alert.show();

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_func_menu, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (dtToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.profUpdat:
                Intent profIntent = new Intent(getApplicationContext(), EnterProfile.class);
                profIntent.putExtra("uid", myProf.getUid());
                profIntent.putExtra("devToken", myProf.getDeviceToken());
                startActivity(profIntent);
                break;
            case R.id.logoutFunc:
                signOut();
                Intent startIntent = new Intent(getApplicationContext(), StartLogin.class);
                startActivity(startIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFriendProfile(List<String> friendUidList) {
        for (String uid : friendUidList) {
            Query query = mDatabase.child("Profiles").child(uid);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ProfDataSet profile = dataSnapshot.getValue(ProfDataSet.class);
                    if (profile != null) {
                        mProfileDataList.add(profile);
                        mFriendList.add(profile.getNickname());
                        mFriendAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                mAuth.signOut();
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if(status.isSuccess()) {
                                setResult(1);
                            }
                            else {
                                setResult(0);
                            }
                            //finish();
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                setResult(-1);
                //finish();
            }
        });
    }

}