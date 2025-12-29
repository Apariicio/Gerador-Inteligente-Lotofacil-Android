package com.aparicioamaral.quinzenumerosaleatorios;

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

    // Não precisamos mais da lista estática pesada, pois passamos só números
    public static ArrayList<String> listaTemporaria = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_varredura);

        ocultarBarrasDeNavegacao();

        listaConflitos = findViewById(R.id.listaConflitos);
        txtResumo = findViewById(R.id.txtResumo);

        // Recebe os contadores
        int total = getIntent().getIntExtra("total", 0);
        int q13 = getIntent().getIntExtra("q13", 0);
        int q14 = getIntent().getIntExtra("q14", 0);
        int q15 = getIntent().getIntExtra("q15", 0);

        // Esconde a lista (pois não vamos usar)
        listaConflitos.setVisibility(View.GONE); // ou View.INVISIBLE

        // Aumenta o tamanho do texto para ficar bonito
        txtResumo.setTextSize(18);

        // Monta o Relatório de Eficiência
        if (total > 0) {
            StringBuilder relatorio = new StringBuilder();
            relatorio.append("� RELATÓRIO DE EFICIÊNCIA �\n\n");
            relatorio.append("Jogos Analisados: ").append(total).append("\n");
            relatorio.append("----------------------------------\n");
            relatorio.append("� 15 Pontos: ").append(q15).append(" jogos\n");
            relatorio.append("� 14 Pontos: ").append(q14).append(" jogos\n");
            relatorio.append("� 13 Pontos: ").append(q13).append(" jogos\n");
            relatorio.append("----------------------------------\n\n");

            // Cálculo simples de % de "quase lá" (13, 14 ou 15)
            int totalPremiados = q13 + q14 + q15;
            double porcentagem = (double) totalPremiados / total * 100.0;
            String percFormatado = String.format(java.util.Locale.US, "%.1f", porcentagem);

            relatorio.append("TAXA DE SUCESSO (13+): ").append(percFormatado).append("%\n");
            relatorio.append("\nIsso significa que ").append(percFormatado);
            relatorio.append("% dos seus jogos já teriam dado lucro ou prêmio máximo na história!");

            txtResumo.setText(relatorio.toString());
        } else {
            txtResumo.setText("Nenhum dado para analisar.");
        }
    }

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
}