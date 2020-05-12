package android.bignerdranch.com.tempproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bignerdranch.com.tempproject.Objects.MatchesObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<MatchesObject> resultsMatches = new ArrayList<MatchesObject>();
    private RecyclerView.Adapter matchesAdapter;
    private RecyclerView.LayoutManager matchesLayoutManager;

    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.matches_recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        matchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);

        mRecyclerView.setLayoutManager(matchesLayoutManager);
        matchesAdapter = new MatchesAdapter(getDataSetMatches(), MatchesActivity.this);
        mRecyclerView.setAdapter(matchesAdapter);

        getUserMastchId();

    }

    private void getUserMastchId() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserId).child("connections").child("matches");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot match : dataSnapshot.getChildren())
                    {
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FetchMatchInformation(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";
                    if(dataSnapshot.child("name").getValue() != null)
                    {
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    if(dataSnapshot.child("profileImageUrl").getValue() != null)
                    {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

                    }
                    MatchesObject matchesObject = new MatchesObject(userId, name, profileImageUrl);
                    resultsMatches.add(matchesObject);
                    matchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }

    private class MatchesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView matchID, matchName;
        ImageView matchProfileImage;
        public MatchesViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            matchID = itemView.findViewById(R.id.matches_id);
            matchName = itemView.findViewById(R.id.matches_name);
            matchProfileImage = itemView.findViewById(R.id.matches_image);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("matchId", matchID.getText().toString());
            intent.putExtras(bundle);
            v.getContext().startActivity(intent);
            return;
        }
    }

    private class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolder>
    {

        private List<MatchesObject> matchesList;
        private Context context;

        public MatchesAdapter(List<MatchesObject> matchesList, Context context)
        {
            this.matchesList = matchesList;
            this.context = context;
        }
        @NonNull
        @Override
        public MatchesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View layourView = LayoutInflater.from(parent.getContext()).inflate(R.layout.matches_item, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layourView.setLayoutParams(lp);
            MatchesViewHolder rcv = new MatchesViewHolder(layourView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(@NonNull MatchesViewHolder holder, int position) {
            holder.matchID.setText(matchesList.get(position).getUserId());
            holder.matchName.setText(matchesList.get(position).getName());
            Glide.with(context).load(matchesList.get(position).getProfileImage()).error(R.mipmap.ic_launcher)
                    .into(holder.matchProfileImage);

        }

        @Override
        public int getItemCount() {
            return matchesList.size();
        }
    }
}
