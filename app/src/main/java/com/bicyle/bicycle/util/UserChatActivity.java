package com.bicyle.bicycle.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.bicyle.bicycle.ProfDataSet;
import com.bicyle.bicycle.R;
import com.bicyle.bicycle.channel.Channel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserChatActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    // Views
    private ListView mListView;
    private EditText mEdtMessage;
    // Values
    private UserChatAdapter mAdapter;
    private String userName;
//    public FirebaseUser fUser;

    private Channel mCurrentChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_chat);

        ProfDataSet friendProf = (ProfDataSet) getIntent().getSerializableExtra("friendProf");
        ProfDataSet myProf = (ProfDataSet) getIntent().getSerializableExtra("myProf");

//        fUser = FirebaseAuth.getInstance().getCurrentUser();
        initViews(myProf.getNickname());
        createChannel(myProf.getUid(), friendProf.getUid());
        initFirebaseDatabase();
//        initValues();
        userName = myProf.getNickname();
    }

    private void createChannel(String myUid, String frdUid) {
        mCurrentChannel = new Channel();
//        ProfDataSet friendProf = (ProfDataSet) getIntent().getSerializableExtra("friendProf");
//        ProfDataSet myProf = (ProfDataSet) getIntent().getSerializableExtra("myProf");
        List<String> friendList = new ArrayList<>();
        friendList.add(frdUid);
        friendList.add(myUid);
        mCurrentChannel.setId(MyUtil.getChannelId(friendList));
    }


    private void initViews(String myNickname) {
        mListView = (ListView) findViewById(R.id.list_message);
        mAdapter = new UserChatAdapter(this, 0, myNickname);
        mListView.setAdapter(mAdapter);

        mEdtMessage = (EditText) findViewById(R.id.edit_message);
        findViewById(R.id.btn_send).setOnClickListener(this);
    }

    private void initFirebaseDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Channel").child(mCurrentChannel.getId());
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserChatData chatData = dataSnapshot.getValue(UserChatData.class);
                chatData.firebaseKey = dataSnapshot.getKey();
                mAdapter.add(chatData);
                mListView.smoothScrollToPosition(mAdapter.getCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String firebaseKey = dataSnapshot.getKey();
                int count = mAdapter.getCount();
                for (int i = 0; i < count; i++) {
                    if (mAdapter.getItem(i).firebaseKey.equals(firebaseKey)) {
                        mAdapter.remove(mAdapter.getItem(i));
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

//    private void initValues() {
//        userName = fUser.getDisplayName();
//
//    }

    protected void onDestory() {
        super.onDestroy();
        mDatabaseReference.removeEventListener(mChildEventListener);
    }

    @Override
    public void onClick(View v) {
        String message = mEdtMessage.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            mEdtMessage.setText("");
            UserChatData chatData = new UserChatData();
            chatData.chatUserName = userName;
            chatData.chatMessage = message;
            chatData.chatTime = System.currentTimeMillis();
            mDatabaseReference.push().setValue(chatData);
        }
    }
}
