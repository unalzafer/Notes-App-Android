package com.kreatifapp.unalzafer.notes.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kreatifapp.unalzafer.notes.R;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;

    private EditText etEmail,etPassword;
    private Button btLogin,btRegister;
    android.app.AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Giriş Yap");

        etEmail=(EditText)findViewById(R.id.etEmail);
        etPassword=(EditText)findViewById(R.id.etPassword);
        btLogin=(Button)findViewById(R.id.btLogin);
        btRegister=(Button)findViewById(R.id.btRegister);

        //internet bağlantısı kontrolü yapıyoruz eğer yok ise uyarı veriyor
        if (!checkConnection()) {

            alertDialog = new android.app.AlertDialog.Builder(LoginActivity.this).create();
            alertDialog.setTitle("Dikkat Uyarı!");
            alertDialog.setIcon(R.mipmap.ic_launcher_round);
            alertDialog.setMessage("Lütfen İnternet Bağlantısını Aktif Şekilde Uygulamayı Açınız.");
            alertDialog.setCancelable(false);
            alertDialog.setButton("Anladım", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0); //Uygulamadan çıkış yapmamızı sağlar.
                }
            });
            alertDialog.show();
        }

        //FirebaseAuth sınıfının nesnelerini kullanmak için getInstance kullanıyoruz.
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user!=null)
            startActivity(new Intent(getApplicationContext(),MainActivity.class));


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Kolay kullanmak için edittexlerdeki değerleri alıyoruz.
                String email=etEmail.getText().toString();
                String password=etPassword.getText().toString();

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
                else if (password.length()<6){
                    Toast.makeText(getApplicationContext(),"Parola en az 6 haneli olmalıdır",Toast.LENGTH_SHORT).show();
                }else {
                    //Firebase ile bağlantıyı kurup mail ve şifre doğrulaması yapılır ve sonra giriş yapılır veya yapılmaz
                    //Eğer işlem başarılı olursa task.isSuccessful true

                    auth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //İşlem başarılı ise MainActivity'e gider
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"Bilgilerinizi Kontrol Ediniz."+task.getException(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }

            }
        });


        //Üye ol sayfasına gitmek için
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
    }

    //İnternet iznini kontrol edebilmek için
    public boolean checkConnection() {

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getActiveNetworkInfo() != null

                && conMgr.getActiveNetworkInfo().isAvailable()

                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;

        } else {

            return false;

        }

    }
}
