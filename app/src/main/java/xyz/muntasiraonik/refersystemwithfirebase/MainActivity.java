package xyz.muntasiraonik.refersystemwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Button L,RG;

    private EditText IEmail,IPass;

    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IEmail = findViewById(R.id.email);
        IPass = findViewById(R.id.password);
        L = findViewById(R.id.login);
        RG = findViewById(R.id.register);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {

            finish();

            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));


        }

        L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        RG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             UserReg();
            }
        });
    }


    private void userLogin(){
        String email = IEmail.getText().toString().trim();
        String password  = IPass.getText().toString().trim();


        if(TextUtils.isEmpty(email)){
            IEmail.setError("Please enter email");
            return;
        }

        if(TextUtils.isEmpty(password)){
            IPass.setError("Please enter password");
            return;
        }





        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                        }else{

                            Toast.makeText(MainActivity.this,"The email or password is incorrect",Toast.LENGTH_LONG).show();
                        }


                    }
                });

    }

    private void UserReg(){
        final String email = IEmail.getText().toString().trim();
        final String password  = IPass.getText().toString().trim();


        if(TextUtils.isEmpty(email)){
            IEmail.setError("Please enter email");
            return;
        }

        if(TextUtils.isEmpty(password)){
            IPass.setError("Please enter password");
            return;
        }


        if (password.length() < 7) {
            Toast.makeText(this, "Password too short, enter minimum 7 characters!", Toast.LENGTH_LONG).show();
            return;
        }


        final String name   = email.substring(0, email.lastIndexOf("@"));
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference NRef = rootRef.child("UserId");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                        rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("Coins").setValue(0);


                                        if (name.contains(".")){
                                            String m = name.replace(".","");
                                            rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("UserName").setValue(m);
                                        }else {
                                            rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("UserName").setValue(name);
                                        }

                                        rootRef.child("UserName").child(name).setValue(firebaseAuth.getCurrentUser().getUid());
                                        rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("Pass").setValue(password);

                                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                        finish();
                                    } else {

                                        Toast.makeText(MainActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                                    }



                                }
                            });


            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        NRef.addListenerForSingleValueEvent(eventListener);


    }


}
