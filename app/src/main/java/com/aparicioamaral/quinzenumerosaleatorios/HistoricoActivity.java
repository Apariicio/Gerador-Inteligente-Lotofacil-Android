package com.aparicioamaral.quinzenumerosaleatorios;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    ListView listView;
    TextView txtContadorHistorico;
    EditText inputBuscaJogo;
    Button btnBuscarJogo;
    SharedPreferences bancoDeDados;

    List<String> listaOriginalDeJogos;
    List<String> listaExibida;

    private static final String SEPARADOR = "####";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        ocultarBarrasDeNavegacao();

        listView = findViewById(R.id.listaHistorico);
        txtContadorHistorico = findViewById(R.id.txtContadorHistorico);
        inputBuscaJogo = findViewById(R.id.inputBuscaJogo);
        btnBuscarJogo = findViewById(R.id.btnBuscarJogo);

        bancoDeDados = getSharedPreferences("HistoricoJogos", MODE_PRIVATE);

        carregarListaInvertida();

        // Botão de busca com SCROLL (rolagem)
        btnBuscarJogo.setOnClickListener(v -> irParaJogoEspecifico());

        // Clique simples para compartilhar
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String itemTexto = listaExibida.get(position);
            compartilharJogo(itemTexto);
        });

        // Clique longo para deletar
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            String itemExibido = listaExibida.get(position);
            int indexReal = descobrirIndexPeloTexto(itemExibido);

            if (indexReal != -1) {
                confirmarDelecao(indexReal);
            }
            return true;
        });
    }

    private void irParaJogoEspecifico() {
        String busca = inputBuscaJogo.getText().toString().trim();

        if (busca.isEmpty()) return;

        try {
            int numeroJogo = Integer.parseInt(busca);
            int totalJogos = listaOriginalDeJogos.size();

            if (numeroJogo < 1 || numeroJogo > totalJogos) {
                Toast.makeText(this, "Jogo não encontrado!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cálculo para lista invertida: TOTAL - NÚMERO
            int posicaoVisual = totalJogos - numeroJogo;

            listView.setSelection(posicaoVisual);
            inputBuscaJogo.setText(""); // Limpa o campo
            ocultarTeclado();

        } catch (Exception e) {
            Toast.makeText(this, "Digite apenas números", Toast.LENGTH_SHORT).show();
        }
    }

    private void ocultarTeclado() {
        try {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if(getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {}
    }

    private int descobrirIndexPeloTexto(String textoVisivel) {
        try {
            String[] partes = textoVisivel.split(":");
            String parteNumero = partes[0].replace("Jogo", "").trim();
            int numeroJogo = Integer.parseInt(parteNumero);
            return numeroJogo - 1;
        } catch (Exception e) {
            return -1;
        }
    }

    private void ocultarBarrasDeNavegacao() {
        try {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } catch (Exception e) {}
    }

    public void carregarListaInvertida() {
        listaOriginalDeJogos = new ArrayList<>();
        listaExibida = new ArrayList<>();

        String historicoGeral = bancoDeDados.getString("historico_ordenado", "");

        if (!historicoGeral.isEmpty()) {
            String[] jogos = historicoGeral.split(SEPARADOR);
            for (String j : jogos) {
                if (!j.trim().isEmpty()) {
                    listaOriginalDeJogos.add(j);
                }
            }
            for (int i = listaOriginalDeJogos.size() - 1; i >= 0; i--) {
                String linha = "Jogo " + (i + 1) + " : " + listaOriginalDeJogos.get(i);
                listaExibida.add(linha);
            }
        }

        if(txtContadorHistorico != null) {
            txtContadorHistorico.setText("Total de Jogos: " + listaOriginalDeJogos.size());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_historico, // Seu arquivo visual PRETO
                R.id.text1,              // O ID do TextView dentro dele
                listaExibida
        );
        listView.setAdapter(adapter);
    }

    public void confirmarDelecao(final int indexNoBanco) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Apagar Jogo?");
        alerta.setMessage("Deseja excluir este jogo?");
        alerta.setPositiveButton("SIM", (dialog, which) -> deletarJogo(indexNoBanco));
        alerta.setNegativeButton("Não", null);
        alerta.show();
    }

    public void deletarJogo(int index) {
        listaOriginalDeJogos.remove(index);

        StringBuilder novoHistorico = new StringBuilder();
        for (int i = 0; i < listaOriginalDeJogos.size(); i++) {
            novoHistorico.append(listaOriginalDeJogos.get(i));
            if (i < listaOriginalDeJogos.size() - 1) {
                novoHistorico.append(SEPARADOR);
            }
        }

        SharedPreferences.Editor editor = bancoDeDados.edit();
        if (listaOriginalDeJogos.isEmpty()) {
            editor.remove("historico_ordenado");
        } else {
            editor.putString("historico_ordenado", novoHistorico.toString());
        }
        editor.apply();

        Toast.makeText(this, "Apagado!", Toast.LENGTH_SHORT).show();
        carregarListaInvertida();
    }

    public void compartilharJogo(String jogo) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Jogo Lotofácil: \n" + jogo);
        startActivity(Intent.createChooser(intent, "Compartilhar"));
    }
}