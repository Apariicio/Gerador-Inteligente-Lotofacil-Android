package com.aparicioamaral.quinzenumerosaleatorios; // CONFIRA SE O SEU PACOTE É ESSE MESMO

import android.os.Bundle;
import android.view.View;
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

        ocultarBarrasDeNavegacao();

        listaConflitos = findViewById(R.id.listaConflitos);
        txtResumo = findViewById(R.id.txtResumo);

        // Recebe os contadores e a lista de detalhes
        int total = getIntent().getIntExtra("total", 0);
        int q11 = getIntent().getIntExtra("q11", 0);
        int q12 = getIntent().getIntExtra("q12", 0);
        int q13 = getIntent().getIntExtra("q13", 0);
        int q14 = getIntent().getIntExtra("q14", 0);
        int q15 = getIntent().getIntExtra("q15", 0);

        ArrayList<String> detalhes = getIntent().getStringArrayListExtra("detalhes_campeoes");

        txtResumo.setTextSize(16);

        if (total > 0) {
            StringBuilder relatorio = new StringBuilder();

            // � é o Gráfico
            relatorio.append("\uD83D\uDCCA ESTATÍSTICAS RÁPIDAS \uD83D\uDCCA\n");
            relatorio.append("Total Analisado: ").append(total).append(" jogos\n\n");

            // � é o Saco de Dinheiro
            relatorio.append("\uD83D\uDCB0 15 Pontos: ").append(q15).append("\n");
            relatorio.append("\uD83D\uDCB0 14 Pontos: ").append(q14).append("\n");

            // � é a Bola Azul
            relatorio.append("\uD83D\uDD35 13 Pontos: ").append(q13).append("\n");

            // ⚪ é a Bola Branca
            relatorio.append("\u26AA 12 Pontos: ").append(q12).append("\n");
            relatorio.append("\u26AA 11 Pontos: ").append(q11).append("\n");

            relatorio.append("----------------------------------\n");

            if (detalhes != null && !detalhes.isEmpty()) {
                // � Troféu de novo
                relatorio.append("\uD83C\uDFC6 GALERIA DE CAMPEÕES (14/15) ABAIXO:");
                listaConflitos.setVisibility(View.VISIBLE);

                // ATENÇÃO: Aqui estou usando 'item_historico' como estava no seu código original.
                // Se der erro, verifique se você tem esse arquivo XML.
                // Se não tiver, pode trocar por 'android.R.layout.simple_list_item_1' ou 'R.layout.item_lista_preta'
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        R.layout.item_historico,
                        detalhes
                );
                listaConflitos.setAdapter(adapter);

            } else {
                relatorio.append("Nenhum jogo com 14 ou 15 pontos encontrado no histórico.");
                listaConflitos.setVisibility(View.GONE);
            }

            txtResumo.setText(relatorio.toString());
        }
    }

    private void ocultarBarrasDeNavegacao() {
        try {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } catch (Exception e) {}
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) { ocultarBarrasDeNavegacao(); }
    }
}