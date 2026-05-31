package com.aparicioamaral.quinzenumerosaleatorios;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoricoManualActivity extends AppCompatActivity {

    ListView listaManuais;
    Button btnApagarTudoManual; // Mapeamento do botão vermelho
    ArrayList<String> listaVisual;
    ArrayList<String> listaChaves;

    // Variável que guarda quais jogos estão selecionados
    private Set<Integer> posicoesSelecionadas = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_manual);

        // --- ATIVA O MODO TELA CHEIA ---
        ocultarBarrasDeNavegacao();

        listaManuais = findViewById(R.id.listaManuais);
        btnApagarTudoManual = findViewById(R.id.btnApagarTudoManual);

        carregarLista();

        btnApagarTudoManual.setOnClickListener(v -> acaoBotaoApagar());
    }

    // --- MÉTODOS PARA TELA CHEIA ---
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

    // --- CLASSE AUXILIAR PARA AJUDAR NA ORDENAÇÃO ---
    private class ItemJogo {
        int numeroConcurso;
        String textoParaTela;
        String chaveParaDeletar;

        public ItemJogo(int numeroConcurso, String textoParaTela, String chaveParaDeletar) {
            this.numeroConcurso = numeroConcurso;
            this.textoParaTela = textoParaTela;
            this.chaveParaDeletar = chaveParaDeletar;
        }
    }

    public void carregarLista() {
        Map<String, ?> todosManuais = DadosOficiais.lerApenasManuais(this);

        listaVisual = new ArrayList<>();
        listaChaves = new ArrayList<>();
        posicoesSelecionadas.clear();
        atualizarTextoBotao();

        if (todosManuais.isEmpty()) {
            listaVisual.add("Nenhum cadastro manual encontrado.");
        } else {
            // 1. Criamos uma lista temporária para poder ordenar
            List<ItemJogo> listaTemporaria = new ArrayList<>();

            for (Map.Entry<String, ?> entry : todosManuais.entrySet()) {
                String jogoNumeros = entry.getKey();
                String infoConcurso = entry.getValue().toString();

                int numConcurso = 0;
                try {
                    String[] partes = infoConcurso.split(" ");
                    if (partes.length > 1) {
                        numConcurso = Integer.parseInt(partes[1]);
                    }
                } catch (Exception e) {
                    numConcurso = 0;
                }

                String textoBonito = infoConcurso + "\n" + jogoNumeros;
                listaTemporaria.add(new ItemJogo(numConcurso, textoBonito, jogoNumeros));
            }

            // 2. MÁGICA DA ORDENAÇÃO
            Collections.sort(listaTemporaria, new Comparator<ItemJogo>() {
                @Override
                public int compare(ItemJogo jogo1, ItemJogo jogo2) {
                    return Integer.compare(jogo2.numeroConcurso, jogo1.numeroConcurso);
                }
            });

            // 3. Passa os dados para as listas finais
            for (ItemJogo item : listaTemporaria) {
                listaVisual.add(item.textoParaTela);
                listaChaves.add(item.chaveParaDeletar);
            }
        }

        // 4. Adaptador customizado
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.item_historico,
                listaVisual
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (posicoesSelecionadas.contains(position)) {
                    view.setBackgroundColor(Color.parseColor("#40D32F2F")); // Vermelho
                } else {
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
                return view;
            }
        };
        listaManuais.setAdapter(adapter);

        // CLIQUE SIMPLES
        listaManuais.setOnItemClickListener((parent, view, position, id) -> {
            if (listaVisual.get(0).equals("Nenhum cadastro manual encontrado.")) return;

            if (!posicoesSelecionadas.isEmpty()) {
                if (posicoesSelecionadas.contains(position)) posicoesSelecionadas.remove(position);
                else posicoesSelecionadas.add(position);

                atualizarTextoBotao();
                adapter.notifyDataSetChanged();
            }
        });

        // CLIQUE LONGO
        listaManuais.setOnItemLongClickListener((parent, view, position, id) -> {
            if (listaVisual.get(0).equals("Nenhum cadastro manual encontrado.")) return true;

            if (posicoesSelecionadas.contains(position)) posicoesSelecionadas.remove(position);
            else posicoesSelecionadas.add(position);

            atualizarTextoBotao();
            adapter.notifyDataSetChanged();
            return true;
        });
    }

    private void atualizarTextoBotao() {
        if (posicoesSelecionadas.isEmpty()) {
            btnApagarTudoManual.setText("🗑️ APAGAR TUDO");
        } else {
            btnApagarTudoManual.setText("🗑️ DELETAR SELECIONADOS (" + posicoesSelecionadas.size() + ")");
        }
    }

    private void acaoBotaoApagar() {
        if (listaVisual.isEmpty() || listaVisual.get(0).equals("Nenhum cadastro manual encontrado.")) return;

        if (!posicoesSelecionadas.isEmpty()) {
            // DELETAR SÓ OS SELECIONADOS
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("Excluir Selecionados?");
            alerta.setMessage("Deseja apagar os " + posicoesSelecionadas.size() + " registros selecionados?");
            alerta.setIcon(android.R.drawable.ic_menu_delete);
            alerta.setPositiveButton("Sim, Apagar", (dialog, which) -> {
                for (int pos : posicoesSelecionadas) {
                    String chaveParaDeletar = listaChaves.get(pos);
                    DadosOficiais.deletarResultadoManual(this, chaveParaDeletar);
                }
                Toast.makeText(this, posicoesSelecionadas.size() + " apagados!", Toast.LENGTH_SHORT).show();
                carregarLista();
            });
            alerta.setNegativeButton("Cancelar", null);
            alerta.show();

        } else {
            // DELETAR TUDO
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("Excluir Tudo?");
            alerta.setMessage("Isso vai apagar DEFINITIVAMENTE TODOS os seus cadastros manuais. Tem certeza?");
            alerta.setIcon(android.R.drawable.ic_dialog_alert);
            alerta.setPositiveButton("Sim, Limpar Tudo", (dialog, which) -> {
                for (String chave : listaChaves) {
                    DadosOficiais.deletarResultadoManual(this, chave);
                }
                Toast.makeText(this, "Tudo apagado!", Toast.LENGTH_SHORT).show();
                carregarLista();
            });
            alerta.setNegativeButton("Cancelar", null);
            alerta.show();
        }
    }
}