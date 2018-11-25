package com.bicyle.bicycle;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MessageSender {

    // USE YOUR SERVER KEY!!!!!!!
    private static final String SERVER_KEY =
            "AAAAxVCdggY:APA91bEF-SURCP0UTXyLfAMQr5RL7pDQ5cuR1j4CRu7XDRaAqYL0snzWcIXursnSsqPscnoA1NQenjWtRqWNMIMTQ8QKJq-dpSjQLeK-QzLjPT14htGWu00OsYT1h20QmSAsrxv4Hj7I";
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static MessageSender sInstance;

    private static final String DATA = "data";
    private static final String TO = "to";
    private static final String MESSAGE = "message";
    private static final String TITLE = "title";
    private static final String SENDER_ID = "sender_id";
//    String sendNickname;
//    final String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static MessageSender getsInstance() {
        if (sInstance == null) {
            sInstance = new MessageSender();
        }
        return sInstance;
    }

    private MessageSender() {

    }

    public void inviteFrd(final String token, final String senderId, final String sendNickname, final String frdNick) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject root = new JSONObject();
                    final JSONObject data = new JSONObject();

                    data.put(MESSAGE, sendNickname);
                    data.put(TITLE, frdNick);

                    data.put(SENDER_ID, senderId);
                    root.put(DATA, data);
                    root.put(TO, token);

                    URL Url = new URL(FCM_MESSAGE_URL);
                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    os.write(root.toString().getBytes("utf-8"));
                    os.flush();
                    conn.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}