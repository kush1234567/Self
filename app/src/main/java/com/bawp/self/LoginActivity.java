package com.bawp.self;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bawp.self.Util.journalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView emailAddress;
    private EditText password;
    private ProgressBar progressBar;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();

        progressBar=findViewById(R.id.login_progressbar);
        Button login = findViewById(R.id.login_button);
        Button createAccount = findViewById(R.id.create_button);
        emailAddress = findViewById(R.id.email);
        password = findViewById(R.id.password);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateAccount.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmailPasswordUser(emailAddress.getText().toString().trim(), password.getText().toString().trim());


            }
        });


    }

    private void loginEmailPasswordUser(final String email, String pwd) {
        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                               final String currentUserId = currentUser.getUid();

                                collectionReference.whereEqualTo("userId", currentUserId)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                if (e != null) {

                                                }
                                                progressBar.setVisibility(View.INVISIBLE);
                                                assert queryDocumentSnapshots != null;
                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                        journalApi journalApi = new journalApi();
                                                        journalApi.setUsername(snapshot.getString("username"));
                                                        journalApi.setUserId(snapshot.getString("userId"));

                                                        //Go to ListActivity
                                                        startActivity(new Intent(LoginActivity.this, journal.class));
                                                    }
                                                }
                                            }
                                        });

                            }
                            else
                            {
                                progressBar.setVisibility(View.INVISIBLE);
                                emailAddress.setText("");
                                password.setText("");
                                Toast.makeText(LoginActivity.this, "INVALID USER ID AND PASSWORD", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);

                }
            });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this,
                    "Empty Fields Not Allowed",
                    Toast.LENGTH_LONG)
                    .show();

        }
    }

}