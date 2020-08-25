package com.bawp.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bawp.self.Util.journalApi;
import com.bawp.self.model.journalclass;
import com.bawp.self.ui.journalrecyclerviewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class JournalListActivity extends AppCompatActivity {
   private FirebaseAuth firebaseAuth;
   private FirebaseAuth.AuthStateListener authStateListener;
   private FirebaseUser user;
   private FirebaseFirestore db=FirebaseFirestore.getInstance();
   private StorageReference storageReference;

    private List<journalclass> journalclassList;
    private RecyclerView recyclerView;
    private journalrecyclerviewAdapter JournalrecyclerviewAdapter;

    private CollectionReference collectionReference=db.collection("journal");
    private TextView noJournalEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
       // storageReference= FirebaseStorage.getInstance().getReference();
       firebaseAuth=FirebaseAuth.getInstance();
       user=firebaseAuth.getCurrentUser();

        noJournalEntry=findViewById(R.id.list_no_thoughts);
        journalclassList=new ArrayList<>();
        recyclerView=findViewById(R.id.recyler_view);
        //recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_add:
                if(user!=null && firebaseAuth!=null){
                    startActivity(new Intent(JournalListActivity.this,journal.class));
                    //finish();
                }
                break;
            case R.id.action_signout:
                if(user!=null && firebaseAuth!=null)
                {
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalListActivity.this,MainActivity.class));
                    //finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.whereEqualTo("userId",journalApi.getInstance().getUserId())
                                 .get()
                                  .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                      @Override
                                      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                          if(!queryDocumentSnapshots.isEmpty()) {
                                              for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                  journalclass journalclass= snapshot.toObject(com.bawp.self.model.journalclass.class);
                                                  journalclassList.add(journalclass);
                                              }

                                              //Invoke recycler view
                                              JournalrecyclerviewAdapter=new journalrecyclerviewAdapter(JournalListActivity.this,journalclassList);
                                              recyclerView.setAdapter(JournalrecyclerviewAdapter);
                                              JournalrecyclerviewAdapter.notifyDataSetChanged();
                                          }
                                          else
                                          {
                                              noJournalEntry.setVisibility(View.VISIBLE);

                                          }
                                      }

                                  }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


}
