package com.aparicioamaral.quinzenumerosaleatorios;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ResultadoVarreduraActivity extends AppCompatActivity {

    ListView listaConflitos;
    TextView txtResumo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_varredura);

        listaConflitos = findViewById(R.id.listaConflitos);
        txtResumo = findViewById(R.id.txtResumo);

        // 1. Recebe a lista que veio da MainActivity
        ArrayList<String> listaDeProblemas = getIntent().getStringArrayListExtra("lista_erros");

        if (listaDeProblemas != null) {
            // Atualiza o texto do topo
            txtResumo.setText("Sucesso! " + listaDeProblemas.size() + " jogos gerados, já foram premiados na LotoFácil oficial:");

            // 2. Mostra na Lista usando seu layout bonito (item_historico)
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.item_historico,
                    listaDeProblemas
            );
            listaConflitos.setAdapter(adapter);
        }
    }
}