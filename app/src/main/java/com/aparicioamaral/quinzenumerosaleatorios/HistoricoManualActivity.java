package com.aparicioamaral.quinzenumerosaleatorios;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aparicioamaral.quinzenumerosaleatorios.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HistoricoManualActivity extends AppCompatActivity {

    ListView listaManuais;
    ArrayList<String> listaVisual;
    ArrayList<String> listaChaves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_manual);

        listaManuais = findViewById(R.id.listaManuais);
        carregarLista();
    }

    // --- CLASSE AUXILIAR PARA AJUDAR NA ORDENAÇÃO ---
    // Ela serve apenas para guardarmos o número do concurso e podermos ordenar depois
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

        if (todosManuais.isEmpty()) {
            Toast.makeText(this, "Nenhum jogo cadastrado manualmente.", Toast.LENGTH_SHORT).show();
            listaManuais.setAdapter(null);
            return;
        }

        // 1. Criamos uma lista temporária para poder ordenar
        List<ItemJogo> listaTemporaria = new ArrayList<>();

        for (Map.Entry<String, ?> entry : todosManuais.entrySet()) {
            String jogoNumeros = entry.getKey(); // A chave (os números)
            String infoConcurso = entry.getValue().toString(); // O valor (Ex: "Concurso 3550 (data)")

            // Tenta extrair o número do concurso do texto para poder ordenar
            int numConcurso = 0;
            try {
                // O texto é padrão: "Concurso 3550 (..."
                // Vamos quebrar o texto nos espaços e tentar pegar o segundo item (o número)
                String[] partes = infoConcurso.split(" ");
                if (partes.length > 1) {
                    numConcurso = Integer.parseInt(partes[1]);
                }
            } catch (Exception e) {
                numConcurso = 0; // Se der erro, fica como 0
            }

            // Adiciona na lista temporária
            String textoBonito = infoConcurso + "\n" + jogoNumeros;
            listaTemporaria.add(new ItemJogo(numConcurso, textoBonito, jogoNumeros));
        }

        // 2. AGORA FAZEMOS A MÁGICA DA ORDENAÇÃO
        // Ordena do MAIOR para o MENOR (Decrescente)
        Collections.sort(listaTemporaria, new Comparator<ItemJogo>() {
            @Override
            public int compare(ItemJogo jogo1, ItemJogo jogo2) {
                // Retorna positivo se o 2 for maior que o 1 (inverte a ordem)
                return Integer.compare(jogo2.numeroConcurso, jogo1.numeroConcurso);
            }
        });

        // 3. Passa os dados já ordenados para as listas finais que o Android usa
        for (ItemJogo item : listaTemporaria) {
            listaVisual.add(item.textoParaTela);
            listaChaves.add(item.chaveParaDeletar);
        }

        // 4. Mostra na tela
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_historico,
                listaVisual
        );
        listaManuais.setAdapter(adapter);

        listaManuais.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                confirmarDelecao(position);
                return true;
            }
        });
    }

    public void confirmarDelecao(int posicao) {
        String itemVisual = listaVisual.get(posicao);
        final String chaveParaDeletar = listaChaves.get(posicao);

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Excluir Registro?");
        alerta.setMessage("Tem certeza que deseja apagar este jogo?\n\n" + itemVisual);
        alerta.setIcon(android.R.drawable.ic_menu_delete);

        alerta.setPositiveButton("SIM, APAGAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DadosOficiais.deletarResultadoManual(HistoricoManualActivity.this, chaveParaDeletar);
                Toast.makeText(HistoricoManualActivity.this, "Apagado com sucesso!", Toast.LENGTH_SHORT).show();
                carregarLista(); // Recarrega a lista para atualizar a ordem e remover o item
            }
        });

        alerta.setNegativeButton("Cancelar", null);
        alerta.show();
    }
}