package com.ace.xiatom.chat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.*;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ListView lv;
    chatAdapter adapter;
    List<chat_content> data;
    private final int UPDATE_TEXT = 1;
    AbstractXMPPConnection xmpptcpConnection;
    private String username;
    private SQLiteDatabase db;
    private mySQLite dbHelper;
    getMessages messageBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new mySQLite(this,"homework.db",null,1);
        db = dbHelper.getWritableDatabase();
        messageBox = new getMessages(username,db);

        lv = findViewById(R.id.chatList);
        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lv.setStackFromBottom(true);


        final Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case UPDATE_TEXT:
                        String mm = msg.obj.toString();
                        messageBox.insertMeg(new chat_content(true,mm));
                        data.add(new chat_content(true,mm));
                        adapter.notifyDataSetChanged();
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, 1);
        }
        Button btn = findViewById(R.id.send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ChatManager chatManager = ChatManager.getInstanceFor(xmpptcpConnection);
                    EntityBareJid jid = JidCreate.entityBareFrom("bubble@localhost");
                    Chat chat = chatManager.chatWith(jid);
                    EditText msg = findViewById(R.id.sendMsg);
                    String message = msg.getText().toString();
                    msg.setText("");
                    chat.send(message);
                    messageBox.insertMeg(new chat_content(false,message));
                    data.add(new chat_content(false,message));
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
                try {
                    builder.setXmppDomain("localhost");
                    builder.setHostAddress(InetAddress.getByName("10.236.221.206"));
                    builder.setPort(5222);
                    builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                    builder.setCompressionEnabled(true);
                    builder.setSendPresence(true);
                    xmpptcpConnection = new XMPPTCPConnection(builder.build());
                    if (!xmpptcpConnection.isConnected()) {
                        Log.i("msg", "connect");
                        xmpptcpConnection.connect();
                    } else {
                        Log.i("msg", "already connect");
                    }
                    Presence presence = new Presence(Presence.Type.available);
                    presence.setStatus("在线");
                    //设置在线
                    xmpptcpConnection.sendStanza(presence);
                    xmpptcpConnection.login("bubble", "1234");
                    //接受消息
                    ChatManager chatManager = ChatManager.getInstanceFor(xmpptcpConnection);
                    chatManager.addIncomingListener(new IncomingChatMessageListener() {
                        @Override
                        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                            android.os.Message msg = new android.os.Message();
                            msg.what = UPDATE_TEXT;
                            msg.obj = message.getBody();
//                            msg.obj = new String[]{};
                            handler.sendMessage(msg);
                            System.out.println("新消息，来自" + from + ":" + message.getBody());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    @Override
    protected void onResume() {
        Log.i("msg","getting msg");
        data = messageBox.getMessages();
        adapter = new chatAdapter(MainActivity.this, R.layout.item, data);
        lv.setAdapter(adapter);
        Log.i("msg","getting suc");
        super.onResume();
    }

}
