package com.aparicioamaral.quinzenumerosaleatorios;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoricoActivity extends AppCompatActivity {

    ListView listView;
    TextView txtContadorHistorico;
    EditText inputBuscaJogo;
    Button btnBuscarJogo;
    Button btnApagarTudoHistorico; // Mapeamento do botão vermelho
    SharedPreferences bancoDeDados;

    List<String> listaOriginalDeJogos;
    List<String> listaExibida;

    // Variável que guarda quais jogos estão selecionados
    private Set<Integer> posicoesSelecionadas = new HashSet<>();

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
        btnApagarTudoHistorico = findViewById(R.id.btnApagarTudoHistorico); // Conecta o botão XML ao Java

        bancoDeDados = getSharedPreferences("HistoricoJogos", MODE_PRIVATE);

        carregarListaInvertida();

        // Botão de busca com SCROLL (rolagem)
        btnBuscarJogo.setOnClickListener(v -> irParaJogoEspecifico());

        // Ação do Botão Vermelho (Apagar Tudo ou Apagar Selecionados)
        btnApagarTudoHistorico.setOnClickListener(v -> acaoBotaoApagar());
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
            String parteNumero = partes[0].replace("JOGO", "").trim();
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
        posicoesSelecionadas.clear(); // Limpa as seleções ao recarregar
        atualizarTextoBotao(); // Reseta o texto do botão vermelho

        String historicoGeral = bancoDeDados.getString("historico_ordenado", "");

        if (!historicoGeral.isEmpty()) {
            String[] jogos = historicoGeral.split(SEPARADOR);
            for (String j : jogos) {
                if (!j.trim().isEmpty()) {
                    listaOriginalDeJogos.add(j);
                }
            }
            for (int i = listaOriginalDeJogos.size() - 1; i >= 0; i--) {
                String linhaBruta = listaOriginalDeJogos.get(i);
                String numerosStr = linhaBruta;
                String dataStr = "";

                // Verifica se tem a etiqueta de data que criamos e separa
                if (linhaBruta.contains("&DATA&")) {
                    String[] partes = linhaBruta.split("&DATA&");
                    numerosStr = partes[0]; // Pega só os números

                    // Formata a data e hora para a linha de baixo
                    if (partes.length > 1) {
                        String dataHora = partes[1];
                        String[] dividida = dataHora.split(" ");
                        if (dividida.length == 2) {
                            dataStr = "\nDATA: " + dividida[0] + " HORA: " + dividida[1];
                        } else {
                            dataStr = "\nDATA: " + dataHora; // Caso de segurança
                        }
                    }
                }

                // Remove os colchetes dos números para ficar limpo como você pediu
                numerosStr = numerosStr.replace("[", "").replace("]", "");

                // Formata a linha final para a vitrine do Histórico
                String linha = "JOGO " + (i + 1) + ": " + numerosStr + dataStr;
                listaExibida.add(linha);
            }
        }

        if(txtContadorHistorico != null) {
            txtContadorHistorico.setText("Total de Jogos: " + listaOriginalDeJogos.size());
        }

        // ADAPTADOR CUSTOMIZADO PARA PINTAR OS JOGOS SELECIONADOS
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.item_historico,
                R.id.text1,
                listaExibida
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (posicoesSelecionadas.contains(position)) {
                    view.setBackgroundColor(Color.parseColor("#40D32F2F")); // Fundo vermelho suave
                } else {
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
                return view;
            }
        };
        listView.setAdapter(adapter);

        // CLIQUE SIMPLES
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (listaExibida.isEmpty()) return;

            // Se já tem algo selecionado, o clique simples apenas seleciona/deseleciona
            if (!posicoesSelecionadas.isEmpty()) {
                if (posicoesSelecionadas.contains(position)) {
                    posicoesSelecionadas.remove(position);
                } else {
                    posicoesSelecionadas.add(position);
                }
                atualizarTextoBotao();
                adapter.notifyDataSetChanged();
            } else {
                // Se não tem nada selecionado, compartilha o jogo
                String itemTexto = listaExibida.get(position);
                compartilharJogo(itemTexto);
            }
        });

        // CLIQUE LONGO (Ativa o modo de seleção)
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (listaExibida.isEmpty()) return true;

            if (posicoesSelecionadas.contains(position)) {
                posicoesSelecionadas.remove(position);
            } else {
                posicoesSelecionadas.add(position);
            }
            atualizarTextoBotao();
            adapter.notifyDataSetChanged();
            return true;
        });
    }

    private void atualizarTextoBotao() {
        if (posicoesSelecionadas.isEmpty()) {
            btnApagarTudoHistorico.setText("🗑️ LIMPAR HISTÓRICO COMPLETO");
        } else {
            btnApagarTudoHistorico.setText("🗑️ DELETAR SELECIONADOS (" + posicoesSelecionadas.size() + ")");
        }
    }

    private void acaoBotaoApagar() {
        if (!posicoesSelecionadas.isEmpty()) {
            // MODO: DELETAR SÓ OS SELECIONADOS
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("Apagar Selecionados?");
            alerta.setMessage("Deseja remover " + posicoesSelecionadas.size() + " jogos do histórico?");
            alerta.setPositiveButton("Sim", (dialog, which) -> {

                List<Integer> indexParaRemover = new ArrayList<>();
                for (int pos : posicoesSelecionadas) {
                    int realIndex = descobrirIndexPeloTexto(listaExibida.get(pos));
                    if (realIndex != -1) indexParaRemover.add(realIndex);
                }

                // Ordena do maior para o menor para não bagunçar os index ao remover
                Collections.sort(indexParaRemover, Collections.reverseOrder());

                for (int i : indexParaRemover) {
                    if (i < listaOriginalDeJogos.size()) {
                        listaOriginalDeJogos.remove(i);
                    }
                }
                salvarBancoDeDadosNovo();
                Toast.makeText(this, posicoesSelecionadas.size() + " jogos apagados!", Toast.LENGTH_SHORT).show();
                carregarListaInvertida();
            });
            alerta.setNegativeButton("Cancelar", null);
            alerta.show();

        } else {
            // MODO: DELETAR TUDO
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("LIXEIRA TOTAL");
            alerta.setMessage("Deseja deletar Permanentemente TODOS os jogos que você gerou no app?");
            alerta.setPositiveButton("Sim, Esvaziar", (dialog, which) -> {

                // 🌟 NOVO: TRAVA DE SEGURANÇA (SEGUNDA CONFIRMAÇÃO)
                AlertDialog.Builder travaSeguranca = new AlertDialog.Builder(this);
                travaSeguranca.setTitle("⚠️ AÇÃO IRREVERSÍVEL");
                travaSeguranca.setMessage("Tem certeza que deseja deletar? O histórico inteiro será apagado e não será possível recuperá-lo.");

                travaSeguranca.setPositiveButton("Sim, Tenho Certeza", (dialogTrava, whichTrava) -> {
                    // Aqui sim, a exclusão real acontece!
                    bancoDeDados.edit().remove("historico_ordenado").apply();
                    Toast.makeText(this, "Histórico completamente zerado!", Toast.LENGTH_SHORT).show();
                    carregarListaInvertida();
                });

                travaSeguranca.setNegativeButton("Cancelar", null);
                travaSeguranca.show();

            });
            alerta.setNegativeButton("Cancelar", null);
            alerta.show();
        }
    }

    private void salvarBancoDeDadosNovo() {
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
    }

    public void compartilharJogo(String jogo) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Jogo Lotofácil: \n" + jogo);
        startActivity(Intent.createChooser(intent, "Compartilhar"));
    }
}