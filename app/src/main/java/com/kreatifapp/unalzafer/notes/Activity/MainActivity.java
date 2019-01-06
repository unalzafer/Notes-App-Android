package com.kreatifapp.unalzafer.notes.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kreatifapp.unalzafer.notes.Adapter.NoteAdapter;
import com.kreatifapp.unalzafer.notes.CustomItemClickListener;
import com.kreatifapp.unalzafer.notes.Model.NotesModel;
import com.kreatifapp.unalzafer.notes.R;

import java.util.ArrayList;

/**
 * Created by ÜNAL ZAFER on 30.12.2018.
 */

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth auth;
    private FloatingActionButton ivAddNote;
    private static int selectMenuView=0;

    private RecyclerView recyclerView;
    private ArrayList<NotesModel> notesModelArrayList;

    private DatabaseReference databaseReference;
    NoteAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Notlarım");

        //kullanıcı veri alıyoruz
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        ivAddNote=(FloatingActionButton)findViewById(R.id.ivAddNote);

        ivAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddNotes.class));
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        notesModelArrayList = new ArrayList<NotesModel>();
        databaseReference=FirebaseDatabase.getInstance().getReference("Note").child("User").child(user.getUid()).child("MyNotes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                  notesModelArrayList.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    Log.d("firebase :","title->"+ds.toString());

                    NotesModel notes = ds.getValue(NotesModel.class);
                    notesModelArrayList.add(notes);

                    if(selectMenuView==0) {
                        //Yan 2 li görüntü için
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }else {
                        //Alt alta sıralı şekilde görüntü için
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                    }

                }
                adapter = new NoteAdapter(notesModelArrayList, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        //Toast.makeText(getApplicationContext(),"Tıklanan:"+" "+notesModelArrayList.get(position).getTitle(),Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(MainActivity.this,NoteDetailActivity.class);
                        i.putExtra("putNote",notesModelArrayList.get(position).getIdNote());
                        startActivity(i);
                    }
                });
                adapter.notifyDataSetChanged();
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebase", "Failed to read value.", error.toException());
            }
        });





    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menus, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.lr_and_grid) {
            if(selectMenuView==0) {
                //Alt alta sıralı şekilde görüntü için
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                selectMenuView=1;
            }
            else {
                selectMenuView = 0;
                //Yan 2 li görüntü için
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                recyclerView.setLayoutManager(gridLayoutManager);
            }
            adapter.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.logout) {
            auth.signOut();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
