package com.exemple.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.exemple.organizze.R;
import com.exemple.organizze.config.ConfiguracaoFirebase;
import com.exemple.organizze.helper.Base64Custom;
import com.exemple.organizze.helper.DateCustom;
import com.exemple.organizze.model.Movimentacao;
import com.exemple.organizze.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {
    private EditText editValor, editData, editCategoria, editDescricao;

    private Movimentacao movimentacao;
    private DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        editCategoria = findViewById(R.id.editCategoria);
        editValor = findViewById(R.id.editValor);
        editDescricao = findViewById(R.id.editDescricao);
        editData = findViewById(R.id.editData);

        editData.setText(DateCustom.dataAtual());
        recuperarReceitaTotal();
    }
    public void salvarReceita(View view){
        if(validarCamposReceitas()){

        String data = editData.getText().toString();
        movimentacao = new Movimentacao();
        movimentacao.setValor(Double.parseDouble(editValor.getText().toString()));
        movimentacao.setData(data);
        movimentacao.setCategoria(editCategoria.getText().toString());
        movimentacao.setDescricao(editDescricao.getText().toString());
        movimentacao.setTipo("r");

        Double receitaAtualizada = receitaTotal + Double.parseDouble(editValor.getText().toString());
        atualizarReceita(receitaAtualizada);

        movimentacao.salvar(data);
            Toast.makeText(getApplicationContext(), "Receita adicionada", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public boolean validarCamposReceitas(){
       String campoValor = editValor.getText().toString();
       String campoData = editData.getText().toString();
       String campoCategoria = editCategoria.getText().toString();
       String campoDescricao = editDescricao.getText().toString();
       if(!campoCategoria.isEmpty() && !campoData.isEmpty() && !campoDescricao.isEmpty() && !campoValor.isEmpty()){
           return true;
       }else {
           Toast.makeText(ReceitasActivity.this, "Preencha todos os campos", Toast.LENGTH_LONG).show();
           return false;
       }
    }
    public void recuperarReceitaTotal(){
        DatabaseReference usuarioRef = databaseRef.child("usuarios")
                .child(Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail()));
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void atualizarReceita(Double despesa){
        DatabaseReference usuarioRef = databaseRef.child("usuarios")
                .child(Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail()));
        usuarioRef.child("receitaTotal").setValue(despesa);
    }
}