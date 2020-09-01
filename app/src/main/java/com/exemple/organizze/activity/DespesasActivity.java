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

public class DespesasActivity extends AppCompatActivity {
    private EditText editData, editCategoria, editDescricao, editValor;

    private Movimentacao movimentacao;
    private DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    private Double despesaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        editData = findViewById(R.id.editData);
        editCategoria = findViewById(R.id.editCategoria);
        editDescricao = findViewById(R.id.editDescricao);
        editValor = findViewById(R.id.editValor);

        editData.setText(DateCustom.dataAtual());
        recuperarDespesaTotal();
    }
    public void salvarDespesa(View view){
        if(validarCamposDespesa()){

        String data = editData.getText().toString(); //pega a data para usar no metodo da classe MOVIMENTAÇÃO
        movimentacao = new Movimentacao();
        movimentacao.setValor(Double.parseDouble(editValor.getText().toString()));
        movimentacao.setData(data);
        movimentacao.setCategoria(editCategoria.getText().toString());
        movimentacao.setDescricao(editDescricao.getText().toString());
        movimentacao.setTipo("d");

        Double despesaAtualizada = despesaTotal + Double.parseDouble(editValor.getText().toString());
        atualizarDespesa(despesaAtualizada);
        movimentacao.salvar(data);
        Toast.makeText(getApplicationContext(), "Despesa adicionada", Toast.LENGTH_SHORT).show();
        finish();
        }
    }
    public boolean validarCamposDespesa(){

        String campoValor = editValor.getText().toString();
        String campoData = editData.getText().toString();
        String campoCategoria = editCategoria.getText().toString();
        String campoDescricao = editDescricao.getText().toString();
        if(!campoValor.isEmpty() && !campoData.isEmpty() && !campoCategoria.isEmpty() && !campoDescricao.isEmpty()){
        return true;
        }else {
            Toast.makeText(DespesasActivity.this, "Preencha todos os campos", Toast.LENGTH_LONG).show();
            return false;
        }

    }
    public void recuperarDespesaTotal(){

        DatabaseReference usuarioRef = databaseRef.child("usuarios")
                .child(Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail())); //seleciona os dados do user atual
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class); //recupera os valores (agora, usuario.GET pode buscar qualquer valor do bd)
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void atualizarDespesa(Double despesa){

        DatabaseReference usuarioRef = databaseRef.child("usuarios")
                .child(Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail()));
        usuarioRef.child("despesaTotal").setValue(despesa);
    }
}