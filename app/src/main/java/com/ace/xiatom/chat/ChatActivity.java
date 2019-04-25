package com.ace.xiatom.chat;
import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

import java.net.InetAddress;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private ChatService.NotifyBinder chatbinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            chatbinder = (ChatService.NotifyBinder)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    ListView chat_list;
    chatAdapter adapter;
    List<chat_content> historyMessages;
    private final int UPDATE_TEXT = 1;
    AbstractXMPPConnection xmpptcpConnection;
    private SQLiteDatabase db;
    private mySQLite dbHelper;
    MessageBoxManager messageBox;
    String username ;
    String password;
    String sendto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_ui);

        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        username = intent.getStringExtra("name");
        password = intent.getStringExtra("password");
        sendto = intent.getStringExtra("sendto");
        actionBar.setTitle(sendto);
        Intent bindIntent = new Intent(this,ChatService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
        dbHelper = new mySQLite(this,"homework.db",null,1);
        db = dbHelper.getWritableDatabase();
        messageBox = new MessageBoxManager(db);
        chat_list = findViewById(R.id.chatList);
        chat_list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chat_list.setStackFromBottom(true);
        //异步处理收到的消息
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case UPDATE_TEXT:
                        String message = msg.obj.toString();
                        messageBox.insertMeg(new chat_content(true,message));
                        historyMessages.add(new chat_content(true,message));
                        adapter.notifyDataSetChanged();
                }
            }
        };


        //申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }

        Button sendMsgButton = findViewById(R.id.send);
        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ChatManager chatManager = ChatManager.getInstanceFor(xmpptcpConnection);
                    EntityBareJid jid = JidCreate.entityBareFrom(sendto+"@localhost");
                    Chat chat = chatManager.chatWith(jid);
                    EditText msg = findViewById(R.id.sendMsg);
                    String message = msg.getText().toString();
                    if(message.equals(""))
                        return;
                    msg.setText("");
                    chat.send(message);
                    messageBox.insertMeg(new chat_content(false,message));
                    historyMessages.add(new chat_content(false,message));
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
                    builder.setHostAddress(InetAddress.getByName("10.240.252.96"));
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
                    xmpptcpConnection.login(username, password);
                    //接受消息
                    ChatManager chatManager = ChatManager.getInstanceFor(xmpptcpConnection);
                    chatManager.addIncomingListener(new IncomingChatMessageListener() {
                        @Override
                        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                            android.os.Message msg = new android.os.Message();
                            msg.what = UPDATE_TEXT;
                            msg.obj = message.getBody();
                            handler.sendMessage(msg);
                            System.out.println("新消息，来自" + from + ":" + message.getBody());
                            chatbinder.startShinning();
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
        if(chatbinder!=null)
            chatbinder.stopShinning();
        historyMessages = messageBox.getMessages();
        adapter = new chatAdapter(ChatActivity.this, R.layout.item, historyMessages);
        chat_list.setAdapter(adapter);
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        unbindService(connection);
        Log.i("msg","disconnect");
        super.onDestroy();
    }
}
