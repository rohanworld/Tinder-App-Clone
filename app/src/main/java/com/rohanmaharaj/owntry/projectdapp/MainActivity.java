package com.rohanmaharaj.owntry.projectdapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    private cards cards_data[];
    private arrayAdapter arrayAdapter;
    private FirebaseAuth mAuth;
    private String currentUid;
    private DatabaseReference userDb;
    ListView listView;
    List<cards> rowItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();

        //Checking and Showing MAle or Female Choices
        checkUserSex();

        //Now Remove the below Values of teh adapter
        rowItems = new ArrayList<cards>();

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems );
        //now we need to change arrayadapter so that we can managae alot more information on this adapter

        //so if u want to add data after arrayAdapter u need to notify the adpter like this belwo
        //al.add("Added after Adapter");
        // arrayAdapter.notifyDataSetChanged();

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                cards  obj = (cards) dataObject;
                String userId = obj.getUserId();
                userDb.child(oppositeSex).child(userId).child("connections").child("nope").child(currentUid).setValue(true);
                Toast.makeText(MainActivity.this, "Left!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onRightCardExit(Object dataObject) {

                cards  obj = (cards) dataObject;
                String userId = obj.getUserId();
                userDb.child(oppositeSex).child(userId).child("connections").child("yeps").child(currentUid).setValue(true);
                isConnectionMatched(userId);
                Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) { }
            @Override
            public void onScroll(float scrollProgressPercent) { }
        });
        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show());
    }

    private void isConnectionMatched(String userId) {
        DatabaseReference currentUserConnectionsDb = userDb.child(userSex).child(currentUid).child("connections").child("yeps").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(MainActivity.this, "New Connection", Toast.LENGTH_SHORT).show();
                    //now we save the same info to db
                    userDb.child(oppositeSex).child(snapshot.getKey()).child("connections").child("matches").child(currentUid).setValue(true);
                    userDb.child(userSex).child(currentUid).child("connections").child("matches").child(snapshot.getKey()).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void logoutuser(View view) {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class));
        finish();
        return;
    }
    private String userSex;
    private String oppositeSex;
    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference maleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equals(user.getUid())){
                    userSex = "Male";
                    oppositeSex = "Female";
                    getOppositeSexUsers();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        DatabaseReference femaleDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Female");
        femaleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equals(user.getUid())){
                    userSex = "Female";
                    oppositeSex = "Male";
                    getOppositeSexUsers();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void getOppositeSexUsers(){
        DatabaseReference oppositeSexDb = FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeSex);
        oppositeSexDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //here and condition is to not to show the left swiped person again
                if(snapshot.exists() && !snapshot.child("connections").child("nope").hasChild(currentUid)  && !snapshot.child("connections").child("yeps").hasChild(currentUid)){
                    //Nowwe can go ahead and add it to arraylist i.e. add it to swipecardview
                    cards item =  new cards(snapshot.getKey(), snapshot.child("name").getValue().toString());
                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void gotoSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("userSex", userSex);
        startActivity(intent);
    }
}