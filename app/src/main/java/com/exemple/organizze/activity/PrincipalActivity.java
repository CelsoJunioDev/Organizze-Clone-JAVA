package com.exemple.organizze.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.exemple.organizze.adapter.AdapterMovimentacao;
import com.exemple.organizze.config.ConfiguracaoFirebase;
import com.exemple.organizze.helper.Base64Custom;
import com.exemple.organizze.model.Movimentacao;
import com.exemple.organizze.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.organizze.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView textSaudacao, textSaldo;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoUsuario = 0.0;

    private DatabaseReference usuarioRef;

    private ValueEventListener valueEventListenerResumo;
    private ValueEventListener valueEventListenerMovimentacoes;

    private RecyclerView recyclerMovimentos;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private DatabaseReference movimentacaoRef;
    private String mesAnoSelecionado;
    private Movimentacao movimentacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
       Toolbar toolbar = findViewById(R.id.toolbar);
       toolbar.setTitle("Organizze");
       setSupportActionBar(toolbar);
       //getSupportActionBar().setTitle("Organizze - Clone");

       recyclerMovimentos = findViewById(R.id.recyclerMovimentos);

       textSaudacao = findViewById(R.id.textSaudacao);
       textSaldo = findViewById(R.id.textSaldo);
       calendarView = findViewById(R.id.calendarView);
        configuraCalendarView();
        swipe();

        //Configurar adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, this);

        //Configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerMovimentos.setHasFixedSize(true);
        recyclerMovimentos.setLayoutManager(layoutManager);
        recyclerMovimentos.setAdapter(adapterMovimentacao);


    }

    public void swipe(){
        final ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() { //cria obj do tipo Callback e da o nome de itemTouch
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE; //movimento drag and drop inativo
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END; //ativa o swipe inicio/fim e fim/inicio
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //Log.i("swipe", "Item foi arrastado");
            excluirMovimentacao(viewHolder);
            }
        };
        //anexa o itemTouch ao recyclerView
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerMovimentos);
    }
    public void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir movimentação da conta");
        alertDialog.setMessage("Tem certeza de que deseja excluir a movimentação?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int position = viewHolder.getAdapterPosition();
                movimentacao = movimentacoes.get(position); //pega a posição clicada/deslizada da lista movimentacoes

                movimentacaoRef = databaseRef.child("movimentacao")
                        .child(Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail()))
                        .child(mesAnoSelecionado);
                movimentacaoRef.child(movimentacao.getKey()).removeValue();
                adapterMovimentacao.notifyItemRemoved(position);
                atualizarSaldo();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Exclusão cancelada", Toast.LENGTH_SHORT).show();
                adapterMovimentacao.notifyDataSetChanged(); //atualiza a lista
            }
        });
        AlertDialog alert =  alertDialog.create();
        alert.show();

    }

    public void atualizarSaldo(){
        usuarioRef = databaseRef.child("usuarios")
                .child(Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail()));
        if (movimentacao.getTipo().equals("r")){
            receitaTotal = receitaTotal - movimentacao.getValor();
            usuarioRef.child("receitaTotal").setValue(receitaTotal);
        }
        if (movimentacao.getTipo().equals("d")){
            despesaTotal = despesaTotal - movimentacao.getValor();
            usuarioRef.child("despesaTotal").setValue(despesaTotal);
        }

    }
   public void recuperarMovimentacoes(){
        movimentacaoRef = databaseRef.child("movimentacao")
                                      .child(Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail()))
                                        .child(mesAnoSelecionado);
       //Log.i("DadosRetorno", "Log 01: " +mesAnoSelecionado);
        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movimentacoes.clear();//limpa os dados antes de ininiar
                for (DataSnapshot dados: dataSnapshot.getChildren()){ //dataSnapshot possui todos os valores do BD, mas queremos especificar, por isso usa getChildren
                    //Log.i("Dados", "Log 01: " +dados.toString()); //Lista todas as movimentacoes
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setKey(dados.getKey());
                    //Log.i("DadosRetorno", "Log 02: " +movimentacao.getCategoria());
                    movimentacoes.add(movimentacao); //pega a lista movimentacoes e adiciona um objeto movimentacao
                }
                adapterMovimentacao.notifyDataSetChanged(); //para avisar as mudanças na movimentacao
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


   }

    public void recuperarResumo(){
                 usuarioRef = databaseRef.child("usuarios")
                .child(Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail()));
       valueEventListenerResumo = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.00"); //FORMATAR CASAS DECIMAIS
                String resumoFormatado = decimalFormat.format(resumoUsuario);

                textSaudacao.setText("Olá, " +usuario.getNome());
                textSaldo.setText("R$ " +resumoFormatado);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_princial, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sair:
                logOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void adicionarReceita (View view){

        startActivity(new Intent(getApplicationContext(), ReceitasActivity.class));
    }
    public void adicionarDespesa(View view){
        startActivity(new Intent(getApplicationContext(), DespesasActivity.class));
    }

    public void logOut (){
       autenticacao.signOut();
       startActivity(new Intent(this, MainActivity.class));
       finish();
    }

    private void configuraCalendarView() {
        final CalendarDay dataAtual = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d",(dataAtual.getMonth()+1) );
        mesAnoSelecionado = String.valueOf(mesSelecionado +"" +dataAtual.getYear());
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d",(date.getMonth()+1) );
                mesAnoSelecionado = String.valueOf(mesSelecionado +"" +date.getYear());
                //Log.i("MES: ", "Mes: " +mesAnoSelecionado);
                movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
                recuperarMovimentacoes();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMovimentacoes();

    }
    //REMOVE O EVENTLISTENER AO SAIR DO APP PARA NAO FICAR CARREGANDO ATOA
    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerResumo);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
    }

}