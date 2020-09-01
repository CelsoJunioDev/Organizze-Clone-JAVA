package com.exemple.organizze.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.exemple.organizze.R;
import com.exemple.organizze.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button buttonCadastrar;
    private TextView textLogar;

    private FirebaseAuth autenticacao; //pra recuperar se o usuario já está autenticado (depois de fazer o cadastro)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        buttonCadastrar = findViewById(R.id.buttonCadastrar);
        textLogar = findViewById(R.id.textLogar);

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cadastro = new Intent(getApplicationContext(), CadastroActivity.class);
                startActivity(cadastro);
            }
        });
        textLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logar = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(logar);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado();
    }

    private void verificarUsuarioLogado() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();
        if(autenticacao.getCurrentUser() !=null){
            abrirTelaPrincipal();
        }
    }
    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));

    }

}