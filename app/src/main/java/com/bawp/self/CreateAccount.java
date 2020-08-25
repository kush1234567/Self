package com.bawp.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bawp.self.Util.journalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccount extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private Button createAccount_Account;
    private EditText username_account;
    private EditText email_account;
    private EditText password_account;
    private ProgressBar progressBar;

    // establishing connection
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        progressBar=findViewById(R.id.login_progressbar);
        createAccount_Account=findViewById(R.id.create_button_account);
        username_account=findViewById(R.id.username);
        email_account=findViewById(R.id.email_account);
        password_account=findViewById(R.id.password_account);
        createAccount_Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(email_account.getText().toString())
                        && !TextUtils.isEmpty(password_account.getText().toString())
                        && !TextUtils.isEmpty(username_account.getText().toString())) {

                    String email = email_account.getText().toString().trim();
                    String password = password_account.getText().toString().trim();
                    String username = username_account.getText().toString().trim();

                    createUserEmailAccount(email, password, username);

                }else {
                    Toast.makeText(CreateAccount.this,
                            "Empty Fields Not Allowed",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser=firebaseAuth.getCurrentUser();
                if(currentUser!=null)
                {
                    //user is already loggedin...
                }
                else
                {
                    // no user yet
                }

            }
        };
    }
    public void createUserEmailAccount(String email, final String password, final String username)
    {
        if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(username))
        {
            progressBar.setVisibility(View.VISIBLE);
            //In build function
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                //we take user to addJournal page
                                currentUser=firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                final String currentUserId=currentUser.getUid();

                                //create user map so we create a user in User collection
                                Map<String,String>userObj=new HashMap<>();
                                userObj.put("userId",currentUserId);
                                userObj.put("username",username);
                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(Objects.requireNonNull(task.getResult()).exists())
                                                                {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    String name=task.getResult()
                                                                            .getString("username");
                                                                    journalApi journalApi= com.bawp.self.Util.journalApi.getInstance();
                                                                    journalApi.setUsername(name);
                                                                    journalApi.setUserId(currentUserId);

                                                                     Intent intent=new Intent(CreateAccount.this,journal.class);
                                                                     intent.putExtra("username", name);
                                                                     intent.putExtra("userId",currentUserId);
                                                                     startActivity(intent);
                                                                }
                                                                else
                                                                {
                                                                     progressBar.setVisibility(View.INVISIBLE);
                                                                }


                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });



                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });


                            }
                            else
                            {
                                username_account.setText("");
                                email_account.setText("");
                                password_account.setText("");
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(),"already registered",Toast.LENGTH_LONG).show();

                                //something went wrong
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

}
