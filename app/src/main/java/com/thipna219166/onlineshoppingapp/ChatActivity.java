package com.thipna219166.onlineshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.thipna219166.onlineshoppingapp.Model.Message;
import com.thipna219166.onlineshoppingapp.Model.User;
import com.thipna219166.onlineshoppingapp.Prevalent.Prevalent;
import com.thipna219166.onlineshoppingapp.Util.DateTime;
import com.thipna219166.onlineshoppingapp.ViewHolder.LeftMessageViewHolder;
import com.thipna219166.onlineshoppingapp.ViewHolder.RightMessageViewHolder;

import java.util.HashMap;

import io.paperdb.Paper;


///  12/2023 - coding

public class ChatActivity extends AppCompatActivity {

    //private String chatID = null;
    private boolean isBuyer;
    //private User user;
    private String nameBuyer;
    private String chatID;
    //  private boolean isNew;
    private DatabaseReference db;
    private RecyclerView mRecyclerView;
    private TextInputEditText contentMessEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        Intent intent = getIntent();
        isBuyer = intent.getBooleanExtra("isBuyer",true);

        if (isBuyer) {
            User user = Paper.book().read(Prevalent.currentOnlineUser);
            nameBuyer = user.getName();
            chatID = user.getPhone();
        }
        else {
            nameBuyer = intent.getStringExtra("nameBuyer");
            chatID = intent.getStringExtra("chatID");
        }
        //firebase
        db = FirebaseDatabase.getInstance().getReference();
        // RecyclerView
        mRecyclerView = findViewById(R.id.recyclerview_chat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // linearLayoutManager.setReverseLayout(false);
        // linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);



        // hien thi Toolbar
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(nameBuyer);



        displayMessages();







        // Message Input EditTexxt
        contentMessEditText = findViewById(R.id.et_chat);
        //contentMessEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        TextInputLayout contentMessLayout = findViewById(R.id.text_layout);

        contentMessEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() !=0) {
                    contentMessLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        contentMessEditText.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String content = textView.getText().toString().trim();
                    if (!content.equals("")) {
                        //String time = DateTime.getDatetimeNow();
                        Message message;
                        if (isBuyer) {
                            message = new Message(nameBuyer, content);
                        } else {
                            message = new Message("Cửa hàng ABC",content);
                        }
                        chat(message);
                    }
                }
                return false;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // back to login in activity
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chat(Message message){
        db.child("Chat").child(chatID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Object> messMap = new HashMap<>();
                messMap.put("Chat/" + chatID+ "/lastMessageContent", message.getContent());
                messMap.put("Chat/" + chatID+ "/lastSendTime" , ServerValue.TIMESTAMP);
                if (!snapshot.exists()){
                    messMap.put("Chat/" + chatID+ "/chatID", chatID);
                    messMap.put("Chat/" + chatID+ "/nameBuyer" , nameBuyer);
                }
                String messID = db.child("Message/"+chatID).push().getKey();
                messMap.put("Message/"+ chatID + "/"+ messID + "/sender",message.getSender());
                messMap.put("Message/"+ chatID + "/"+ messID + "/content",message.getContent());
                messMap.put("Message/"+ chatID + "/"+ messID +"/time",ServerValue.TIMESTAMP);

                db.updateChildren(messMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contentMessEditText.setText(null);
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    private void displayMessages(){

                    // recyclerview
                    DatabaseReference messMap= db.child("Message").child(chatID);

                    FirebaseRecyclerOptions<Message> options=new FirebaseRecyclerOptions.Builder<Message>().setQuery(messMap, Message.class).build();
                    FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder> adapter=new FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {
                        private static final int ITEM_TYPE_ANOTHER = 0;
                        private static final int ITEM_TYPE_ME =1;
                        @Override
                        public int getItemViewType(int position) {
                            if (isBuyer && getItem(position).getSender().equals(nameBuyer)) {
                                return ITEM_TYPE_ME;
                            } else if (!isBuyer && getItem(position).getSender().equals("Cửa hàng ABC")){
                                return ITEM_TYPE_ME;
                            } else {
                                return ITEM_TYPE_ANOTHER;
                            }
                        }
                        @Override
                        protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull final Message model) {
                            if (getItemViewType(position)==ITEM_TYPE_ME){
                                RightMessageViewHolder rightMessageViewHolder = (RightMessageViewHolder) holder;
                                rightMessageViewHolder.contentTextView.setText(model.getContent());
                                rightMessageViewHolder.timeTextView.setText(DateTime.getDateTime(model.getTime()));
                            }
                            else if (getItemViewType(position)==ITEM_TYPE_ANOTHER){
                                LeftMessageViewHolder leftMessageViewHolder = (LeftMessageViewHolder) holder;
                                String senderName = model.getSender();
                                leftMessageViewHolder.nameTextView.setText(senderName);
                                leftMessageViewHolder.contentTextView.setText(model.getContent());
                                leftMessageViewHolder.timeTextView.setText(DateTime.getDateTime(model.getTime()));
                            }

                        }

                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                            if (viewType == ITEM_TYPE_ME) {
                                return new RightMessageViewHolder(layoutInflater.inflate(R.layout.right_mess_item, parent, false));
                            } else {
                                return new LeftMessageViewHolder(layoutInflater.inflate(R.layout.left_mess_item, parent, false));
                            }
                        }
                        @Override
                        public void onDataChanged() {
                            super.onDataChanged();
                            mRecyclerView.scrollToPosition(getItemCount()-1);
                        }
                    };
                    mRecyclerView.setAdapter(adapter);
                    adapter.startListening();
                    mRecyclerView.scrollToPosition(adapter.getItemCount()-1);


    }
}