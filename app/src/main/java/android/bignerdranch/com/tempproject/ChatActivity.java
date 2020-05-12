package android.bignerdranch.com.tempproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bignerdranch.com.tempproject.Objects.ChatObject;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter chatAdapter;
    private RecyclerView.LayoutManager chatLayoutManager;

    private ArrayList<ChatObject> resultsChat = new ArrayList<ChatObject>();

    private String currentUserId, matchId, chatId;

    private EditText sendEditText;
    private Button sendButton;

    DatabaseReference mDatabaseUser, mDatabaseChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchId = getIntent().getExtras().getString("matchId");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
                .child("connections")
                .child("matches")
                .child(matchId)
                .child("chatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatId();



        mRecyclerView = (RecyclerView) findViewById(R.id.matches_recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        chatLayoutManager = new LinearLayoutManager(ChatActivity.this);

        mRecyclerView.setLayoutManager(chatLayoutManager);
        chatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(chatAdapter);

        sendEditText = findViewById(R.id.chat_message);
        sendButton = findViewById(R.id.send_message);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String sendMessageText = sendEditText.getText().toString();

        if(!sendMessageText.isEmpty())
        {
            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserId);
            newMessage.put("text", sendMessageText);

            newMessageDb.setValue(newMessage);
        }
        sendEditText.setText(null);
    }

    private void getChatId()
    {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    String message = null;
                    String createdByUser = null;

                    if(dataSnapshot.child("text").getValue() != null)
                    {
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue() != null)
                    {
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if(message != null && createdByUser != null)
                    {
                        Boolean currentUserBool = false;
                        if(createdByUser.equals(currentUserId))
                        {
                            currentUserBool = true;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserBool);
                        resultsChat.add(newMessage);
                        chatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView mMessage;
        public LinearLayout mContainer;
        public ChatViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            mMessage = itemView.findViewById(R.id.chat_message);
            mContainer = itemView.findViewById(R.id.chat_container);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>
    {
        private List<ChatObject> chatList;
        private Context context;

        public ChatAdapter(List<ChatObject> matchesList, Context context)
        {
            this.chatList = matchesList;
            this.context = context;
        }
        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View layourView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layourView.setLayoutParams(lp);
            ChatViewHolder rcv = new ChatViewHolder(layourView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            holder.mMessage.setText(chatList.get(position).getMessage());
            if(chatList.get(position).getCurrentUser())
            {
                holder.mMessage.setGravity(Gravity.END);
                holder.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));
            }
            else
            {
                holder.mMessage.setGravity(Gravity.START);
                holder.mContainer.setBackgroundColor(Color.rgb(87, 194, 255));
            }
        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }
    }

    private List<ChatObject> getDataSetChat() {
        return resultsChat;
    }
}
