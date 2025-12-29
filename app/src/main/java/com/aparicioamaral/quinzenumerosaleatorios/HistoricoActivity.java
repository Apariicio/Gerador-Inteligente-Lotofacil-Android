package com.aparicioamaral.quinzenumerosaleatorios;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    ListView listView;
    TextView txtContadorHistorico;
    SharedPreferences bancoDeDados;

    // Lista principal que segura os dados reais para podermos deletar
    List<String> listaOriginalDeJogos;

    private static final String SEPARADOR = "####";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        // --- ATIVA O MODO TELA CHEIA ---
        ocultarBarrasDeNavegacao();

        listView = findViewById(R.id.listaHistorico);
        txtContadorHistorico = findViewById(R.id.txtContadorHistorico);
        bancoDeDados = getSharedPreferences("HistoricoJogos", MODE_PRIVATE);

        carregarListaInvertida();
    }

    // --- MÉTODOS PARA TELA CHEIA (NOVO) ---
    private void ocultarBarrasDeNavegacao() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ocultarBarrasDeNavegacao();
        }
    }
    // ---------------------------------------

    public void carregarListaInvertida() {
        String historicoGeral = bancoDeDados.getString("historico_ordenado", "");

        // Lista visual (O que aparece na tela: "Jogo 50: [1, 2...]")
        ArrayList<String> listaVisual = new ArrayList<>();
        if (historicoGeral.isEmpty()) {
            txtContadorHistorico.setText("Total de jogos gerados: 0");
            Toast.makeText(this, "Nenhum jogo salvo ainda!", Toast.LENGTH_SHORT).show();
            listView.setAdapter(null);
            return;
        }
        // 1. Converte a string do banco em uma Lista Editável
        String[] jogosArray = historicoGeral.split(SEPARADOR);
        listaOriginalDeJogos = new ArrayList<>(Arrays.asList(jogosArray));
        txtContadorHistorico.setText("Total de jogos gerados: " + listaOriginalDeJogos.size());

        // 2. LOOP INVERTIDO (Do último para o primeiro)
        for (int i = listaOriginalDeJogos.size() - 1; i >= 0; i--) {
            String jogo = listaOriginalDeJogos.get(i);

            // O número do jogo é o índice + 1 (ex: índice 49 vira "Jogo 50")
            int numeroDoJogo = i + 1;

            // Monta o texto
            listaVisual.add(numeroDoJogo + ": " + jogo);
        }
        // 3. Exibe na tela
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_historico,
                listaVisual
        );
        listView.setAdapter(adapter);

        // --- CLIQUE SIMPLES: COMPARTILHAR ---
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Matemática para achar o jogo certo na lista original (que não está invertida)
                int indiceReal = (listaOriginalDeJogos.size() - 1) - position;

                String jogoParaCompartilhar = listaOriginalDeJogos.get(indiceReal);
                compartilharJogo(jogoParaCompartilhar);
            }
        });

        // --- CLIQUE LONGO: DELETAR ---
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Mesma matemática para saber qual deletar
                int indiceReal = (listaOriginalDeJogos.size() - 1) - position;

                confirmarExclusao(indiceReal);
                return true;
            }
        });
    }

    // Pergunta se quer mesmo apagar
    public void confirmarExclusao(int indexNoBanco) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Excluir Jogo?");
        alerta.setMessage("Deseja apagar este jogo do histórico permanentemente?");
        alerta.setIcon(android.R.drawable.ic_menu_delete);

        alerta.setPositiveButton("Sim, Apagar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletarJogo(indexNoBanco);
            }
        });
        alerta.setNegativeButton("Cancelar", null);
        alerta.show();
    }

    // Remove do banco e atualiza a tela
    public void deletarJogo(int index) {
        // Remove da memória RAM
        listaOriginalDeJogos.remove(index);

        // Reconstrói a string com #### para salvar no banco
        StringBuilder novoHistorico = new StringBuilder();
        for (int i = 0; i < listaOriginalDeJogos.size(); i++) {
            novoHistorico.append(listaOriginalDeJogos.get(i));
            if (i < listaOriginalDeJogos.size() - 1) {
                novoHistorico.append(SEPARADOR);
            }
        }

        // Salva nas SharedPreferences
        SharedPreferences.Editor editor = bancoDeDados.edit();
        if (listaOriginalDeJogos.isEmpty()) {
            editor.remove("historico_ordenado");
        } else {
            editor.putString("historico_ordenado", novoHistorico.toString());
        }
        editor.apply();

        Toast.makeText(this, "Jogo apagado!", Toast.LENGTH_SHORT).show();

        // Recarrega a tela para atualizar a lista
        carregarListaInvertida();
    }

    public void compartilharJogo(String jogo) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Jogo do Histórico Lotofácil: \n" + jogo);
        startActivity(Intent.createChooser(intent, "Compartilhar via"));
    }
}