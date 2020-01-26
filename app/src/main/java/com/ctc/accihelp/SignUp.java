package com.ctc.accihelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class SignUp extends AppCompatActivity {
    private EditText reg_username_field;
    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass_field;
    private Button reg_btn;
    private Button reg_login_btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAuth = FirebaseAuth.getInstance();
        reg_username_field = findViewById(R.id.reg_username);
        reg_email_field = (EditText) findViewById( R.id.reg_email );
        reg_pass_field = (EditText) findViewById( R.id.reg_pass );
        reg_confirm_pass_field = (EditText) findViewById( R.id.reg_confirm_pass );
        reg_btn = (Button) findViewById( R.id.login_reg_btn );
        reg_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=reg_email_field.getText().toString();
                String pass=reg_pass_field.getText().toString();
                String comfirm_pass=reg_confirm_pass_field.getText().toString();
                if(!TextUtils.isEmpty( email )&&!TextUtils.isEmpty( pass )&!TextUtils.isEmpty( comfirm_pass )){
                    if(pass.equals( comfirm_pass )){
                        mAuth.createUserWithEmailAndPassword( email,pass ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent setupIntent=new Intent(SignUp.this,Progress.class);
                                    startActivity( setupIntent );
                                    finish();

                                }else{
                                    String errorMessage=task.getException().getMessage();
                                    Toast.makeText( SignUp.this,"Error"+errorMessage,Toast.LENGTH_LONG ).show();

                                }
                            }
                        } );
                    }else{
                        Toast.makeText( SignUp.this,"Confirm amd Password Field doesn't match",Toast.LENGTH_LONG ).show();

                    }

                }
            }
        } );
    }
}
