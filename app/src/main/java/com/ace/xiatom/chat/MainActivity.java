package com.ace.xiatom.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

    AbstractXMPPConnection xmpptcpConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btn2 = findViewById(R.id.init);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    Log.i("msg","发送消息");
                    ChatManager chatManager  = ChatManager.getInstanceFor(xmpptcpConnection);
                    EntityBareJid jid = JidCreate.entityBareFrom("bubble@10.241.107.22");
                    Chat chat = chatManager.chatWith(jid);
                    chat.send("nihao");
                    chat.send("你好，我是夏冰");
                    Log.i("msg", chat.getXmppAddressOfChatPartner().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Button btn = findViewById(R.id.send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Log.i("msg","发送消息");
                    ChatManager chatManager  = ChatManager.getInstanceFor(xmpptcpConnection);
                    EntityBareJid jid = JidCreate.entityBareFrom("ace@localhost");
                    Chat chat = chatManager.chatWith(jid);
                    chat.send("nihao");
                    chat.send("你好，我是夏冰");
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
                    ChatManager chatManager  = ChatManager.getInstanceFor(xmpptcpConnection);
                    chatManager.addIncomingListener(new IncomingChatMessageListener() {
                        @Override
                        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                            System.out.println("新消息，来自"+from+":"+message.getBody());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
