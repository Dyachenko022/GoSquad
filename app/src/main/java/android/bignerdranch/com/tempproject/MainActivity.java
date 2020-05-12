package android.bignerdranch.com.tempproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bignerdranch.com.tempproject.Objects.Cards;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ItemArrayAdapter mItemArrayAdapter;

    public ListView mListView;
    List<Cards> rowItems;



    private FirebaseAuth mAuth;
    private String currentUserId;
    private String gameOfInterest = "default";

    private DatabaseReference usersDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*SwipeFlingAdapterView swipeFlingAdapterView = findViewById(R.id.frame);
        AnimationDrawable animationDrawable = (AnimationDrawable) swipeFlingAdapterView.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();*/
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        Bundle extras = getIntent().getExtras();
        if(extras == null)
        {
           gameOfInterest = "default";
            Log.i("MainActivity", "showing all games ");
        }
        else {
            gameOfInterest = extras.getString("gameUid");
            Log.i("MainActivity", "failed to get a game name");
        }
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        checkUserGender();
        rowItems = new ArrayList<>();

        mItemArrayAdapter = new ItemArrayAdapter(this, R.layout.item, rowItems);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(mItemArrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                mItemArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUserId).setValue(true);
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yep").child(currentUserId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT);
            }
        });

    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionDb = usersDb.child(currentUserId)
                .child("connections").child("yep").child(userId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Toast.makeText(MainActivity.this, "It's a match!", Toast.LENGTH_SHORT).show();
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey())
                            .child("connections").child("matches").child(currentUserId).child("chatId")
                            .setValue(key);

                    usersDb.child(currentUserId)
                            .child("connections").child("matches").child(dataSnapshot.getKey()).child("chatId")
                            .setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginOrRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void getOppositeGenderUser()
    {
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!gameOfInterest.equals("default")) {
                    if (dataSnapshot.hasChild("gamingInterests")) {
                        if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUserId)
                                && !dataSnapshot.child("connections").child("yep").hasChild(currentUserId)
                                && dataSnapshot.child("gender").getValue().toString().equals(oppositeUserGender)
                                && dataSnapshot.child("gamingInterests").hasChild(gameOfInterest)) {
                            Cards item;
                            item = new Cards(dataSnapshot.getKey(),
                                    dataSnapshot.child("name").getValue().toString(),
                                    dataSnapshot.child("profileImageUrl").getValue().toString());

                            rowItems.add(item);
                            mItemArrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
                else
                {
                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUserId)
                            && !dataSnapshot.child("connections").child("yep").hasChild(currentUserId)
                            && dataSnapshot.child("gender").getValue().toString().equals(oppositeUserGender)) {
                        Cards item;
                        item = new Cards(dataSnapshot.getKey(),
                                dataSnapshot.child("name").getValue().toString(),
                                dataSnapshot.child("profileImageUrl").getValue().toString());

                        rowItems.add(item);
                        mItemArrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String userGender;
    private String oppositeUserGender;

    public void checkUserGender()
    {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("gender").getValue() != null)
                    {
                        userGender = dataSnapshot.child("gender").getValue().toString();
                        switch(userGender)
                        {
                            case "Male":
                                oppositeUserGender = "Female";
                                break;
                            case "Female":
                                oppositeUserGender = "Male";
                                break;
                        }
                        getOppositeGenderUser();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        return;
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }

    public void goToChooseGame(View view) {
        Intent intent = new Intent(MainActivity.this, ChooseGameActivity.class);
        startActivity(intent);
        return;
    }
}
