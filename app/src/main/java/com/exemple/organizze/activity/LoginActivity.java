package com.exemple.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.exemple.organizze.R;
import com.exemple.organizze.config.ConfiguracaoFirebase;
import com.exemple.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail, editSenha;
    private Button buttonEntrar;

    private Usuario usuario;
    private FirebaseAuth autenticacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        buttonEntrar = findViewById(R.id.buttonEntrar);

        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String campoEmail = editEmail.getText().toString();
                String campoSenha = editSenha.getText().toString();

                if(!campoEmail.isEmpty() && !campoSenha.isEmpty()){

                    usuario = new Usuario();
                    usuario.setEmail(campoEmail);
                    usuario.setSenha(campoSenha);
                    validarLogin();
                }else{
                    Toast.makeText(LoginActivity.this, "Preencha os campos corretamente", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void validarLogin() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abrirTelaPrincipal();
                }else{
                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e senha não correspondem";
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuário não cadastrado";
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }


}