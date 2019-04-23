package com.ace.xiatom.chat;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.net.InetAddress;


public class MainActivity extends AppCompatActivity {

    private final int UPDATE_TEXT = 1;
    AbstractXMPPConnection xmpptcpConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = findViewById(R.id.content);
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(android.os.Message msg) {
               switch (msg.what){
                   case UPDATE_TEXT:
                       textView.setText(msg.obj.toString());
                }
            }
        };
        Button btn = findViewById(R.id.send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Log.i("msg","发送消息");
                    ChatManager chatManager  = ChatManager.getInstanceFor(xmpptcpConnection);
                    EntityBareJid jid = JidCreate.entityBareFrom("ace@localhost");
                    Chat chat = chatManager.chatWith(jid);
                    EditText msg = findViewById(R.id.sendMsg);
                    String message = msg.getText().toString();
                    chat.send(message);
                    Log.i("msg", chat.getXmppAddressOfChatPartner().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
                try {

//                    builder.setXmppDomain(Inet4Address.getLocalHost().getHostAddress());
                    builder.setXmppDomain("localhost");
                    builder.setHostAddress(InetAddress.getByName("10.240.252.96"));
                    builder.setPort(5222);
                    builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                    builder.setCompressionEnabled(true);
                    builder.setSendPresence(true);
                    xmpptcpConnection = new XMPPTCPConnection(builder.build());
                    if (!xmpptcpConnection.isConnected()) {
                        Log.i("msg","connect");
                        xmpptcpConnection.connect();
                    } else {
                        Log.i("msg","already connect");
                    }

                    Presence presence = new Presence(Presence.Type.available);
                    presence.setStatus("在线");
                    //设置在线
                    xmpptcpConnection.sendStanza(presence);
                    xmpptcpConnection.login("ace", "1023");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        try{
            Thread.sleep(3000);
        }catch (Exception e){
            e.printStackTrace();
        }
        ChatManager chatManager  = ChatManager.getInstanceFor(xmpptcpConnection);
        chatManager.addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                android.os.Message msg = new android.os.Message();
                msg.what = UPDATE_TEXT;
                msg.obj = "新消息，来自"+from+":"+message.getBody();
                handler.sendMessage(msg);
                System.out.println("新消息，来自"+from+":"+message.getBody());
            }
        });

    }
}
