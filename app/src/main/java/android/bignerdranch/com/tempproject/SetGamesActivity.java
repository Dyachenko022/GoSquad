package android.bignerdranch.com.tempproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bignerdranch.com.tempproject.Objects.GameObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SetGamesActivity extends AppCompatActivity {



    private String currentUser;
    private static final String TAG ="SetGamesActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mGamesLayoutManager;
    private RecyclerView.Adapter mGamesAdapter;
    private List<GameObject> games = new ArrayList<>();
    private Button setGamesButton;
    private List<String> gamesChosen = new ArrayList<>();
    private List<GamesChosenList> gamesMap = new ArrayList<>();
    DatabaseReference userDb;
    DatabaseReference gamesDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_games);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        gamesDb = FirebaseDatabase.getInstance().getReference().child("Games");
        userDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mRecyclerView = findViewById(R.id.games_recycler_view);
        setGamesButton = findViewById(R.id.set_games_button);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mGamesLayoutManager = new LinearLayoutManager(SetGamesActivity.this);

        mRecyclerView.setLayoutManager(mGamesLayoutManager);
        mGamesAdapter = new GamesAdapter(getDataSetGames(), SetGamesActivity.this);

        mRecyclerView.setAdapter(mGamesAdapter);

        loadGames();

        setGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGames();
                Intent intent = new Intent(SetGamesActivity.this, ChooseGameActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void setGames()
    {
        for(int i = 0; i < gamesMap.size(); i++)
        {
            if(!gamesChosen.contains(gamesMap.get(i).getGameName()))
            {
                gamesMap.remove(i);
                i--;
            }
        }
        for(int i = 0; i < gamesMap.size(); i++)
        {
            userDb.child(currentUser).child("gamingInterests").child(gamesMap.get(i).getGameUid()).setValue(true);
        }

    }


    private void loadGames()
    {
        gamesDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    GamesChosenList gamesChosenList = new GamesChosenList(dataSnapshot.getKey(),
                            dataSnapshot.child("name").getValue().toString());
                    gamesMap.add(gamesChosenList);
                    GameObject gameObject = new GameObject(dataSnapshot.child("name").getValue().toString(),
                        dataSnapshot.child("picture").getValue().toString());
                    mGamesAdapter.notifyDataSetChanged();
                    games.add(gameObject);
                    Log.i(TAG, "added" + gameObject.getGameName());

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


    private List<GameObject> getDataSetGames() {
        return games;
    }

    private class GamesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView mTextView;
        ImageView mImageView;
        CheckBox mCheckBox;

        public GamesViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            mTextView = itemView.findViewById(R.id.set_game_item_text);
            mImageView = itemView.findViewById(R.id.set_game_item_image);
            mCheckBox = itemView.findViewById(R.id.set_game_item_check_box);
        }

        @Override
        public void onClick(View v) {
            mCheckBox.setChecked(!mCheckBox.isChecked());
            if(mCheckBox.isChecked())
            {
                if(!gamesChosen.contains(mTextView.getText().toString()))
                {
                    gamesChosen.add(mTextView.getText().toString());
                }
            }
            else
            {
                if(gamesChosen.contains(mTextView.getText().toString()))
                {
                    gamesChosen.remove(mTextView.getText().toString());
                }
            }
        }
    }

    private class GamesAdapter extends RecyclerView.Adapter<GamesViewHolder>
    {
        private List<GameObject> adapterGames = new ArrayList<>();
        private Context mContext;

        public GamesAdapter(List<GameObject> items, Context context)
        {
            this.adapterGames = items;
            this.mContext = context;
        }



        @NonNull
        @Override
        public GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_game_item, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
            GamesViewHolder rcv = new GamesViewHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(@NonNull GamesViewHolder holder, int position) {
            holder.mTextView.setText(adapterGames.get(position).getGameName());
            Glide.with(mContext).load(adapterGames.get(position).getGameUrl()).into(holder.mImageView);
            holder.mCheckBox.setChecked(false);
        }

        @Override
        public int getItemCount() {
            return adapterGames.size();
        }
    }


}
