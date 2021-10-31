package xyz.muntasiraonik.refersystemwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private EditText inRef;
    private TextView myId;
    private FirebaseAuth firebaseAuth;
    String UID,userN;
    private LinearLayout cardView;
    DatabaseReference coinsRef,refRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Button submit = findViewById(R.id.button4);
        Button copy = findViewById(R.id.button2);
        Button Lout = findViewById(R.id.logout);
        inRef = findViewById(R.id.editText6);
        myId = findViewById(R.id.textView14);
        cardView = findViewById(R.id.CardView);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        UID =(user.getUid());
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        coinsRef = rootRef.child(UID).child("Coins");
        refRef = rootRef.child(UID).child("TRef");
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", myId.getText());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "REFERRAL CODE Copied to Clipboard",
                        Toast.LENGTH_LONG).show();
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitRef();
            }
        });

        Lout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

    void incrementCoins(final long value) {
        coinsRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(value);
                } else {
                    Long points = currentData.getValue(Long.class);
                    assert points != null;
                    int x = points.intValue();
                    Long incrementedPoints = x + value;
                    currentData.setValue(incrementedPoints);

                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot currentData) {
                if (databaseError != null) {
                    Log.d("TAG", "Firebase counter increment failed!");
                } else {
                    Log.d("TAG", "Firebase counter increment succeeded!");
                }
            }
        });
    }


    void incrementRefcoins(final long value) {
        refRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(value);
                } else {
                    Long points = currentData.getValue(Long.class);
                    assert points != null;
                    int x = points.intValue();
                    Long incrementedPoints = x + value;
                    currentData.setValue(incrementedPoints);

                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot currentData) {
                if (databaseError != null) {
                    Log.d("TAG", "Firebase counter increment failed!");
                } else {
                    Log.d("TAG", "Firebase counter increment succeeded!");
                }
            }
        });
    }






    private void SubmitRef(){

        final String ref = inRef.getText().toString().trim();
        if (TextUtils.isEmpty(ref)) {
            Toast.makeText(this, "Please enter Ref Username", Toast.LENGTH_LONG).show();
            return;
        }



        if (!ref.equals(userN)){

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ezzeearnRef = rootRef.child("UserName");
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(ref).exists()){

                        String uid = dataSnapshot.child(ref).getValue(String.class);
                        assert uid != null;
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        refRef = rootRef.child(uid).child("Coins");
                        rootRef.child(UID).child("DREF").setValue(1);
                        GiveReward();

                        Toast.makeText(ProfileActivity.this, "Successfully refer done ", Toast.LENGTH_LONG).show();



                    }else{

                        Toast.makeText(ProfileActivity.this, "Invalid Ref Id", Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            ezzeearnRef.addValueEventListener(eventListener);
        }else {

            Toast.makeText(this, "You can't ref yourself", Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference sREf = rootRef.child(UID);
        ValueEventListener eventListener  = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("UserName").exists()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    String email = user.getEmail();

                    if (email!=null){
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        final String name   = email.substring(0, email.lastIndexOf("@"));
                        if (name.contains(".")){
                            String m = name.replace(".","");
                            rootRef.child("UserName").child(m).setValue(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                            rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("UserName").setValue(m);
                        }else {
                            rootRef.child("UserName").child(name).setValue(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                            rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("UserName").setValue(name);
                        }

                    }






                }else{
                    String username = dataSnapshot.child("UserName").getValue(String.class);
                    assert username != null;
                    myId.setText(username);
                    userN = username;
                }

                if (!dataSnapshot.child("DREF").exists()){
                    cardView.setVisibility(View.VISIBLE);


                }else {
                    cardView.setVisibility(View.GONE);

                }
                if (dataSnapshot.child("Coins").exists()){
                    Long C = dataSnapshot.child("Coins").getValue(Long.class);
                    Toast.makeText(ProfileActivity.this, "You Have " + C + " Points",Toast.LENGTH_LONG).show();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        sREf.addValueEventListener(eventListener);

    }


    boolean thatThingHappened = false;
    private void GiveReward(){
        if (!thatThingHappened){
            thatThingHappened = true;
            incrementRefcoins(20);
            incrementCoins(20);
            Toast.makeText(ProfileActivity.this,"you have got 20 coins",Toast.LENGTH_LONG).show();
        }


    }

}
