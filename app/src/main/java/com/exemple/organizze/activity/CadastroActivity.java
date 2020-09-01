package com.exemple.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.exemple.organizze.R;
import com.exemple.organizze.config.ConfiguracaoFirebase;
import com.exemple.organizze.helper.Base64Custom;
import com.exemple.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {
    private EditText editNome, editEmail, editSenha;
    private Button buttonCadastrar;
    private FirebaseAuth autenticacao;

    private Usuario usuario; // instancia a classe Usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().setTitle("Cadastro");

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);

        buttonCadastrar = findViewById(R.id.buttonCadastrar);

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoNome = editNome.getText().toString();
                String textoEmail = editEmail.getText().toString();
                String textoSenha = editSenha.getText().toString();

                if(!textoNome.isEmpty() && !textoEmail.isEmpty() && !textoSenha.isEmpty()){//se nao estiver vazio, dá valor ao nome, email e senha.
                    usuario = new Usuario();
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);
                    cadastrarUsuario(); //chama o metodo que vai realizar autenticação
                }else{
                    Toast.makeText(getApplicationContext(), "preencha os dados corretamente", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void cadastrarUsuario() {
        //Poderia usar: autenticacao = FirebaseAuth.getInstance(); mas cria classe para usar o metodo smepre que quiser
                        //cria classe "ConfiguracaoFirebase" para usar o metodo sempre que quiser
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha() //recupera os valores digitados e cria usuario e senha
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() { //saber se autenticou usuario
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                String idUsuario = Base64Custom.codificarBase64(usuario.getEmail()); //codificar email para salvar.
                usuario.setIdUsuario(idUsuario); //salvar o valor codificado no setter da classe Usuario
                usuario.salvar();
                finish();
            }else{
                String excecao = "";
                //cria um try para verificar o motivo de nao ter autenticado
                try {
                    throw task.getException();
                }catch (FirebaseAuthWeakPasswordException e){
                    excecao = "Digite uma senha mais forte";
                }catch (FirebaseAuthInvalidCredentialsException e){
                    excecao = "Digite um E-mail válido";
                }catch (FirebaseAuthUserCollisionException e){
                    excecao = "Usuário já cadastrado";
                }catch (Exception e){
                    excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                    e.printStackTrace(); //para printar a exceção no LOG.I
                }
                Toast.makeText(getApplicationContext(), excecao, Toast.LENGTH_LONG).show();
            }
            }
        });
    }



}