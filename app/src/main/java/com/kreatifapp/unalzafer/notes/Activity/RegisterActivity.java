package com.kreatifapp.unalzafer.notes.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.kreatifapp.unalzafer.notes.Model.UserModel;
import com.kreatifapp.unalzafer.notes.R;

public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth auth;
    private EditText etEmail,etPassword,etNameSurname,etPassword2;
    private Button btRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Üye Ol");

        etEmail=(EditText)findViewById(R.id.etEmail);
        etPassword=(EditText)findViewById(R.id.etPassword);
        etPassword2=(EditText) findViewById(R.id.etPassword2);
        btRegister=(Button)findViewById(R.id.btRegister);
        etNameSurname=(EditText)findViewById(R.id.etNameSurname);

        //klavye ilk harf büyük olsun
        etNameSurname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        //FirebaseAuth sınıfının nesnelerini kullanmak için getInstance kullanıyoruz.
        auth=FirebaseAuth.getInstance();

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kolay kullanmak için edittexlerdeki değerleri alıyoruz.
                final String email=etEmail.getText().toString().trim();
                String password=etPassword.getText().toString();
                String password2=etPassword2.getText().toString();
                final String name=etNameSurname.getText().toString();
                final String photoUrl="";

                //örnek olabilecek giriş senaryoları kontrolü biz basit bişeyler yaptık
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Lütfen emailinizi giriniz",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!email.contains("@")) {
                    Toast.makeText(getApplicationContext(), "Lütfen geçerli bir mail adresi giriniz", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Lütfen parolanızı giriniz",Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(password2)){
                    Toast.makeText(getApplicationContext(),"Parolalarınız Eşleşmiyor.",Toast.LENGTH_SHORT).show();
                }
                else if (password.length()<6){
                    Toast.makeText(getApplicationContext(),"Parola en az 6 haneli olmalıdır",Toast.LENGTH_SHORT).show();
                }else {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        UserModel userModel=new UserModel(name,email,photoUrl);
                                        FirebaseDatabase.getInstance().getReference("Note").child("User")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                }else {

                                                }
                                            }
                                        });

                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Hata Oluştu Tekrar Deneyiniz."+task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}
