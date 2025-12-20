package com.aparicioamaral.quinzenumerosaleatorios;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DadosOficiais {
    private static final String PREFS_NOVOS_RESULTADOS = "NovosResultadosOficiais";
    public static HashMap<String, String> carregarResultadosOficiais(Context context) {
        HashMap<String, String> mapaOficial = new HashMap<>();

        // --- PARTE 1: LER O ARQUIVO DE TEXTO ---
        try {
            InputStream is = context.getAssets().open("resultados.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String linha;

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String linhaLimpa = linha.trim().replace("\t", " ");
                String[] partes = linhaLimpa.split("\\s+");

                if (partes.length < 17) continue;

                String data = partes[partes.length - 1];
                String concurso = partes[partes.length - 2];

                List<Integer> numerosDoJogo = new ArrayList<>();
                for (int i = 0; i < partes.length - 2; i++) {
                    try {
                        numerosDoJogo.add(Integer.parseInt(partes[i]));
                    } catch (NumberFormatException e) { }
                }
                Collections.sort(numerosDoJogo);
                String infoCompleta = "Concurso " + concurso + " (" + data + ")";
                mapaOficial.put(numerosDoJogo.toString(), infoCompleta);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // --- PARTE 2: LER A MEMÓRIA (NOVOS CADASTROS) ---
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NOVOS_RESULTADOS, Context.MODE_PRIVATE);
        Map<String, ?> todosNovos = prefs.getAll();

        for (Map.Entry<String, ?> entry : todosNovos.entrySet()) {
            String jogo = entry.getKey();
            String info = entry.getValue().toString();
            mapaOficial.put(jogo, info);
        }

        return mapaOficial;
    }
    public static void salvarNovoResultado(Context context, String jogoFormatado, String concurso, String data) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NOVOS_RESULTADOS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String infoCompleta = "Concurso " + concurso + " (" + data + ")";
        editor.putString(jogoFormatado, infoCompleta);
        editor.apply();
    }
    // --- NOVA FUNÇÃO: DELETAR ---
    public static void deletarResultadoManual(Context context, String jogoFormatado) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NOVOS_RESULTADOS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(jogoFormatado); // Remove usando a chave (os números do jogo)
        editor.apply();
    }
    // --- NOVA FUNÇÃO: LER SÓ OS MANUAIS (PARA A LISTA DE EDIÇÃO) ---
    public static Map<String, ?> lerApenasManuais(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NOVOS_RESULTADOS, Context.MODE_PRIVATE);
        return prefs.getAll();
    }

    // --- NOVA FUNÇÃO: VERIFICAR SE O CONCURSO JÁ EXISTE ---
    public static boolean verificarSeConcursoJaExiste(Context context, String numeroConcurso) {
        // Carrega TODOS os resultados (do arquivo TXT e da memória manual)
        Map<String, String> todosResultados = carregarResultadosOficiais(context);

        // O formato salvo é sempre "Concurso 1234 (dd/mm/aaaa)"
        // Vamos procurar pelo texto "Concurso 1234 " (com espaço no final para não confundir 12 com 120)
        String termoDeBusca = "Concurso " + numeroConcurso + " ";

        for (String informacao : todosResultados.values()) {
            // O 'values()' pega apenas a parte do texto (Concurso...), ignorando os números do jogo
            if (informacao.startsWith(termoDeBusca)) {
                return true; // Encontrou!
            }
        }
        return false; // Não encontrou nada
    }
}