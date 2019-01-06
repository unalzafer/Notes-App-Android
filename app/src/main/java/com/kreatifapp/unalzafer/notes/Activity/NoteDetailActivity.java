package com.kreatifapp.unalzafer.notes.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kreatifapp.unalzafer.notes.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class NoteDetailActivity extends AppCompatActivity {

    ImageView ivNoteImage,ivDelete,ivUpdate,ivSync;
    ImageButton ibColorBlue,ibColorPurble,ibColorGreen,ibColorOrange,ibColorRed,ibColorPink;
    EditText etTitle,etText;
    LinearLayout ll_colors;
    String color;
    ImageView ivImageSelect,ivDeleteImage;

    private FirebaseUser user;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    String pushDatabaseId;
    private StorageReference storageReference;
    private static final int GALLERY_INTENT=2;
    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        getSupportActionBar().setTitle("Not Detay");

        ivNoteImage=(ImageView)findViewById(R.id.ivNoteImage);
        etTitle=(EditText)findViewById(R.id.etTitle);
        etText=(EditText)findViewById(R.id.etText);
        ivDelete=(ImageView)findViewById(R.id.ivDelete);
        ivUpdate=(ImageView)findViewById(R.id.ivUpdate);
        ivSync=(ImageView)findViewById(R.id.ivSync);
        ll_colors=(LinearLayout) findViewById(R.id.ll_colors);
        ivImageSelect=(ImageView)findViewById(R.id.ivImageSelect);
        ivDeleteImage=(ImageView)findViewById(R.id.ivDeleteImage);

        etTitle.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        etTitle.setEnabled(false);
        etText.setEnabled(false);

        //Renkler
        getColor();

        //kullanıcı veri alıyoruz
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        //Note için main sayfasından string bir değer aldık
        Bundle extras = getIntent().getExtras();
        String noteID = extras.getString("putNote");
        //database de ulaşacağımız ref
        databaseReference=FirebaseDatabase.getInstance().getReference("Note").child("User").child(user.getUid()).child("MyNotes").child(noteID);
        pushDatabaseId= String.valueOf(databaseReference.push().getKey());
        storageReference= FirebaseStorage.getInstance().getReference().child(pushDatabaseId);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) { //snapshot boş değilse
                    try {
                        if (!dataSnapshot.child("photoUrl").getValue().toString().equals(""))
                            //url değerimizi picasso veya glide kullanarak resim olarak gösteriyoruz yoksa gizliyoruz
                            Picasso.get().load(dataSnapshot.child("photoUrl").getValue().toString()).into(ivNoteImage);
                        else
                            ivNoteImage.setVisibility(View.GONE);

                        etTitle.setText(dataSnapshot.child("title").getValue().toString());
                        etText.setText(dataSnapshot.child("text").getValue().toString());
                        color=dataSnapshot.child("color").getValue().toString();
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Datasnapsho başarısız olursa burası çalışır
            }
        });

        ivDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Sadece Resmi silmek için
                ivNoteImage.setVisibility(View.GONE);
                databaseReference.child("photoUrl").setValue("");
            }
        });
        ivImageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Galeriye ulaşmak için eğer image boş ise
                showFileImage();
                ivNoteImage.setVisibility(View.VISIBLE);
            }
        });
        ivNoteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Galeriye ulaşmak için
                showFileImage();
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //silme işlemi
                databaseReference.removeValue();
                finish();
            }
        });

        ivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Güncelleme için edittext'leri düznelenebilir yap
                etTitle.setEnabled(true);
                etText.setEnabled(true);
                ivNoteImage.setClickable(true);
                ivUpdate.setVisibility(View.GONE);
                ivSync.setVisibility(View.VISIBLE);
                ll_colors.setVisibility(View.VISIBLE);
                ivImageSelect.setVisibility(View.VISIBLE);
                ivDeleteImage.setVisibility(View.VISIBLE);
                selectColor(color);
            }
        });

        ivSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Güncelleme işelemi başlat
                etTitle.setEnabled(false);
                etText.setEnabled(false);
                ivNoteImage.setClickable(false);

                uploadFile();
                ivDeleteImage.setVisibility(View.GONE);
                ivSync.setVisibility(View.GONE);
                ll_colors.setVisibility(View.GONE);
                ivImageSelect.setVisibility(View.GONE);
                ivUpdate.setVisibility(View.VISIBLE);
            }
        });


    }


    private void getColor(){
        ibColorBlue=(ImageButton)findViewById(R.id.ibColorBlue);
        ibColorPurble=(ImageButton)findViewById(R.id.ibColorPurble);
        ibColorGreen=(ImageButton)findViewById(R.id.ibColorGreen);
        ibColorOrange=(ImageButton)findViewById(R.id.ibColorOrange);
        ibColorRed=(ImageButton)findViewById(R.id.ibColorRed);
        ibColorPink=(ImageButton)findViewById(R.id.ibColorPink);

        ibColorBlue.setOnClickListener(new NoteDetailActivity.setOnClickColorList());
        ibColorPurble.setOnClickListener(new NoteDetailActivity.setOnClickColorList());
        ibColorGreen.setOnClickListener(new NoteDetailActivity.setOnClickColorList());
        ibColorOrange.setOnClickListener(new NoteDetailActivity.setOnClickColorList());
        ibColorRed.setOnClickListener(new NoteDetailActivity.setOnClickColorList());
        ibColorPink.setOnClickListener(new NoteDetailActivity.setOnClickColorList());

    }

    private class setOnClickColorList implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id=view.getId();
            ibColorBlue.setImageResource(0);
            ibColorPurble.setImageResource(0);
            ibColorGreen.setImageResource(0);
            ibColorOrange.setImageResource(0);
            ibColorRed.setImageResource(0);
            ibColorPink.setImageResource(0);
            switch (id){
                case R.id.ibColorBlue:
                    ibColorBlue.setImageResource(R.drawable.ic_checked);
                    color="blue";
                    break;
                case R.id.ibColorPurble:
                    ibColorPurble.setImageResource(R.drawable.ic_checked);
                    color="purble";
                    break;
                case R.id.ibColorGreen:
                    ibColorGreen.setImageResource(R.drawable.ic_checked);
                    color= "green";
                    break;
                case R.id.ibColorOrange:
                    ibColorOrange.setImageResource(R.drawable.ic_checked);
                    color= "orange";
                    break;
                case R.id.ibColorRed:
                    ibColorRed.setImageResource(R.drawable.ic_checked);
                    color= "red";
                    break;
                case R.id.ibColorPink:
                    ibColorPink.setImageResource(R.drawable.ic_checked);
                    color= "pink";
                    break;
            }
        }
    }
    private void selectColor(String colorName){
        switch (colorName){
            case "blue":
                ibColorBlue.performClick();
                break;
            case "purble":
                ibColorPurble.performClick();
                break;
            case "green":
                ibColorGreen.performClick();
                break;
            case "orange":
                ibColorOrange.performClick();
                break;
            case "red":
                ibColorRed.performClick();
                break;
            case "pink":
                ibColorPink.performClick();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivNoteImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void showFileImage() {
        //Galeriye ulaşmak için
        Intent i=new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,GALLERY_INTENT);
    }
    private void uploadFile() {


        databaseReference.child("title").setValue(etTitle.getText().toString());
        databaseReference.child("text").setValue(etText.getText().toString());
        databaseReference.child("color").setValue(color);

        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Not Ekleniyor");
            progressDialog.setMessage("Lütfen bekleyiniz...");
            progressDialog.setCancelable(false);
            progressDialog.show();


            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            getUrlStorage();
                            //Toast.makeText(getApplicationContext(), "Resim Yüklendi ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            progressDialog.dismiss();


                            Toast.makeText(getApplicationContext(),"Resim Yüklenemedi: "+exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();


                            progressDialog.setMessage("Yükleniyor " + ((int) progress) + "%...");
                        }
                    });
        }
        else {

        }
    }
    public void getUrlStorage(){
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                //Resim yüklendikten sonra linki alıp database'imize yazıyoruz
                databaseReference.child("photoUrl").setValue(uri.toString());


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // hata var ise
                Toast.makeText(getApplicationContext(),"Url Bulunamadı: "+exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
