package com.bawp.self;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bawp.self.Util.journalApi;
import com.bawp.self.model.journalclass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class journal extends AppCompatActivity implements View.OnClickListener {

    public static final  int GALLERY_CODE=1;
    private static final String TAG = "Journal";
    private TextView currentUserText;
    private TextView date;
    private EditText title;
    private EditText thought;
    private Button saveButton;
    private ImageButton addImageButton;
    private ProgressBar post_progressbar;
    private ImageView image;

    private String currentUserId;
    private String currentUsername;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //connection
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference=db.collection("journal");
    //private CollectionReference data=db.collection()

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
//important don't forget to instanciate
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();

        post_progressbar=findViewById(R.id.progressBar);
        title=findViewById(R.id.post_title);
        thought=findViewById(R.id.post_thought);
        currentUserText=findViewById(R.id.post_text);
        date=findViewById(R.id.post_date);
        saveButton=findViewById(R.id.post_save);
        addImageButton=findViewById(R.id.imageButton);
        image=findViewById(R.id.post_imageview);
        saveButton.setOnClickListener(this);
        addImageButton.setOnClickListener(this);



        post_progressbar.setVisibility(View.INVISIBLE);

        if(journalApi.getInstance()!=null)
        {
            currentUserId=journalApi.getInstance().getUserId();
            currentUsername=journalApi.getInstance().getUsername();
            currentUserText.setText(currentUsername);
        }
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user=firebaseAuth.getCurrentUser();
                if(user!=null)
                {

                }
                else
                {

                }
            }
        };

        }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imageButton:
                //using Implicit intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
            case R.id.post_save:
                saveJournal();
                break;
        }


    }

    private void saveJournal() {
        final String titles=title.getText().toString().trim();
        final String thoughts=thought.getText().toString().trim();
        post_progressbar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(titles) && !TextUtils.isEmpty(thoughts) && imageUri!=null)
        {
            //storing images
            //cant store image and text at same place
            //store images in storage
            //creating filepath wih unique id by using Timestamp feature of firebase (i.e with time in secound as image name)
            final StorageReference filepath=storageReference
                    .child("journal_images")
                    .child("my_image"+ Timestamp.now().getSeconds());


            filepath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                           filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   String imageUrl=uri.toString();
                                   journalclass journalclass=new journalclass();
                                   journalclass.setTile(titles);
                                   journalclass.setThought(thoughts);
                                   journalclass.setImageUrl(imageUrl);
                                   journalclass.setTimeadded(new Timestamp(new Date()));
                                   journalclass.setUsername(currentUsername);
                                   journalclass.setUserId(currentUserId);

                                   collectionReference.add(journalclass)
                                           .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                       @Override
                                       public void onSuccess(DocumentReference documentReference) {
                                           post_progressbar.setVisibility(View.INVISIBLE);
                                           startActivity(new Intent(journal.this,JournalListActivity.class));
                                           finish();

                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Log.d(TAG, "onFailure: " + e.getMessage());

                                       }
                                   });


                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {

                               }
                           });
                           //journa

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    post_progressbar.setVisibility(View.INVISIBLE);
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri=data.getData(); // we have the actual path to the image
                image.setImageURI(imageUri);

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
//after app stop authListener remove because is expensive
    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth!=null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
