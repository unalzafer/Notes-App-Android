package com.kreatifapp.unalzafer.notes.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kreatifapp.unalzafer.notes.Model.NotesModel;
import com.kreatifapp.unalzafer.notes.R;

import java.io.IOException;

public class AddNotes extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    EditText etTitle,etText;
    ImageView ivPhoto;
    Button btSaveNote;
    String color= String.valueOf(R.color.colorRandom1);
    String pushDatabaseId;
    ImageButton ibColorBlue,ibColorPurble,ibColorGreen,ibColorOrange,ibColorRed,ibColorPink;


    private StorageReference storageReference;
    private static final int GALLERY_INTENT=2;
    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        getSupportActionBar().setTitle("Not Ekle");

        etTitle=(EditText)findViewById(R.id.etTitle);
        etText=(EditText)findViewById(R.id.etText);
        ivPhoto=(ImageView)findViewById(R.id.ivPhoto);
        btSaveNote=(Button)findViewById(R.id.btSaveNote);

        ibColorBlue=(ImageButton)findViewById(R.id.ibColorBlue);
        ibColorPurble=(ImageButton)findViewById(R.id.ibColorPurble);
        ibColorGreen=(ImageButton)findViewById(R.id.ibColorGreen);
        ibColorOrange=(ImageButton)findViewById(R.id.ibColorOrange);
        ibColorRed=(ImageButton)findViewById(R.id.ibColorRed);
        ibColorPink=(ImageButton)findViewById(R.id.ibColorPink);

        ibColorBlue.setOnClickListener(new setOnClickColorList());
        ibColorPurble.setOnClickListener(new setOnClickColorList());
        ibColorGreen.setOnClickListener(new setOnClickColorList());
        ibColorOrange.setOnClickListener(new setOnClickColorList());
        ibColorRed.setOnClickListener(new setOnClickColorList());
        ibColorPink.setOnClickListener(new setOnClickColorList());

        //klavye ilk harf büyük olsun
        etTitle.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        //kullanıcı veri al
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        //Database notları bölümüne referance yap
        databaseReference=FirebaseDatabase.getInstance().getReference("Note").child("User").child(user.getUid()).child("MyNotes");
        //Database ve Storage için key
        pushDatabaseId= String.valueOf(databaseReference.push().getKey());

        storageReference= FirebaseStorage.getInstance().getReference().child(pushDatabaseId);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Galeriye ulaşmak için
                showFileImage();
            }
        });
        btSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Resim yüklemeyi başlat
                if (filePath == null) {
                    if (!etTitle.getText().toString().equals("") || !etText.getText().toString().equals("")) {
                        writeUser(pushDatabaseId,etTitle.getText().toString(), etText.getText().toString(), "", color);
                    } else {
                        Toast.makeText(getApplicationContext(), "Not Başlığı veya Açıklaması Olmadan Not Ekleyemezsiniz! ", Toast.LENGTH_LONG).show();
                    }

                } else if (!etTitle.getText().toString().equals("") && !etText.getText().toString().equals("") && filePath != null) {
                    uploadFile();
                } else
                    Toast.makeText(getApplicationContext(), "Boş Not Ekleyemezsiniz! ", Toast.LENGTH_LONG).show();


            }
        });



    }
    public void writeUser(String idNote,String title, String text, String photoUrl,String color) {
        //Eğer fotoğraf ekleme de başarılı ise verileri database e yaz


        NotesModel user = new NotesModel(idNote,title,text,photoUrl,color);
        databaseReference.child(idNote).setValue(user);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddNotes.this);
        alertDialogBuilder.setTitle("Başarılı");
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher_foreground);
        alertDialogBuilder.setMessage("Notunuz Kaydetildi.");
        alertDialogBuilder
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
       /* alertDialogBuilder
                .setNegativeButton("Yeni Not Ekle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pushDatabaseId= String.valueOf(databaseReference.push().getKey());
                        dialog.dismiss();
                    }
                });*/
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivPhoto.setImageBitmap(bitmap);

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
                    writeUser(pushDatabaseId,etTitle.getText().toString(), etText.getText().toString(), uri.toString(), color);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // hata var ise
                Toast.makeText(getApplicationContext(),"Url Bulunamadı: "+exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
}
