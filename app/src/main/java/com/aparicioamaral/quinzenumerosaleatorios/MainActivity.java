package com.aparicioamaral.quinzenumerosaleatorios;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView txtResultado, txtContador;
    TextView lblSomaPrimos, lblParesImpares, lblFibRepetidos, txtAssinatura;
    Button btnSortear, btnCompartilhar, btnHistorico, btnConferir, btnVarredura, btnCadastrarOficial, btnGerenciarManuais;
    SharedPreferences bancoDeDados;

    private static final String SEPARADOR = "####";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ocultarBarrasDeNavegacao();

        txtResultado = findViewById(R.id.txtResultado);
        btnSortear = findViewById(R.id.btnSortear);
        btnCompartilhar = findViewById(R.id.btnCompartilhar);
        btnHistorico = findViewById(R.id.btnHistorico);
        btnConferir = findViewById(R.id.btnConferir);
        btnVarredura = findViewById(R.id.btnVarredura);
        btnCadastrarOficial = findViewById(R.id.btnCadastrarOficial);
        btnGerenciarManuais = findViewById(R.id.btnGerenciarManuais);

        lblSomaPrimos = findViewById(R.id.lblSomaPrimos);
        lblParesImpares = findViewById(R.id.lblParesImpares);
        lblFibRepetidos = findViewById(R.id.lblFibRepetidos);

        txtContador = findViewById(R.id.txtContador);
        txtAssinatura = findViewById(R.id.txtAssinatura);

        txtAssinatura.setPaintFlags(txtAssinatura.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);

        bancoDeDados = getSharedPreferences("HistoricoJogos", MODE_PRIVATE);

        txtResultado.setText("Clique para buscar...");
        lblSomaPrimos.setText("Soma: -- / Primos: --");
        lblParesImpares.setText("Pares: -- / Ímpares: --");
        lblFibRepetidos.setText("Fibonacci: -- / Repetidos: --");

        atualizarContadorTela();

        btnSortear.setOnClickListener(v -> buscarJogoEquilibrado());
        btnCompartilhar.setOnClickListener(v -> compartilharJogo());
        btnHistorico.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoricoActivity.class)));
        btnConferir.setOnClickListener(v -> abrirConferidor());
        btnVarredura.setOnClickListener(v -> fazerVarreduraGeral());
        btnCadastrarOficial.setOnClickListener(v -> abrirCadastroOficial());
        txtAssinatura.setOnClickListener(v -> mostrarRedesSociais());
        btnGerenciarManuais.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoricoManualActivity.class)));
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

    public void compartilharJogo() {
        String textoJogo = txtResultado.getText().toString();
        if (textoJogo.contains("Clique") || textoJogo.isEmpty()) {
            Toast.makeText(MainActivity.this, "Gere um jogo primeiro!", Toast.LENGTH_SHORT).show();
        } else {
            compartilharTexto("Olha esse jogo da Lotofácil: \n" + textoJogo);
        }
    }

    // --- VARREDURA ULTRA-RÁPIDA COM CACHE DE MEMÓRIA (SÓ ESTATÍSTICAS) ---
    public void fazerVarreduraGeral() {
        // Roda em uma Thread separada para não travar a tela (Opcional, mas recomendado se quiser fluidez total)
        new Thread(() -> {
            try {
                final String historico = bancoDeDados.getString("historico_ordenado", "");
                if (historico.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Histórico vazio.", Toast.LENGTH_SHORT).show());
                    return;
                }

                String[] meusJogosStr = historico.split(SEPARADOR);
                Map<String, String> oficiaisMap = DadosOficiais.carregarResultadosOficiais(this);

                if (oficiaisMap == null || oficiaisMap.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Sem resultados oficiais.", Toast.LENGTH_SHORT).show());
                    return;
                }

                // --- PASSO 1: OTIMIZAÇÃO (CACHE) ---
                // Convertemos todos os resultados oficiais para Sets de Inteiros UMA ÚNICA VEZ.
                // HashSet é infinitamente mais rápido para verificar se um número existe (contém).
                List<Set<Integer>> listaOficiaisCache = new ArrayList<>();

                for (String chaveNumeros : oficiaisMap.keySet()) {
                    ArrayList<Integer> nums = converterStringParaLista(chaveNumeros);
                    if (nums != null && !nums.isEmpty()) {
                        listaOficiaisCache.add(new HashSet<>(nums));
                    }
                }

                // Variáveis atômicas ou simples para contagem
                int qtd13 = 0;
                int qtd14 = 0;
                int qtd15 = 0;
                int totalJogosAnalisados = 0;

                // --- PASSO 2: COMPARAÇÃO RELÂMPAGO ---
                for (String meuJogoStr : meusJogosStr) {
                    if (meuJogoStr.trim().isEmpty()) continue;

                    // Converte o SEU jogo apenas uma vez
                    ArrayList<Integer> meuJogoLista = converterStringParaLista(meuJogoStr);
                    if (meuJogoLista.size() < 15) continue;

                    totalJogosAnalisados++;
                    int recordeDesteJogo = 0;

                    // Compara contra a lista já processada (Muito rápido)
                    for (Set<Integer> oficialSet : listaOficiaisCache) {
                        int acertos = 0;
                        // Verifica quantos números do meu jogo estão no oficial
                        for (Integer n : meuJogoLista) {
                            if (oficialSet.contains(n)) {
                                acertos++;
                            }
                        }

                        if (acertos > recordeDesteJogo) {
                            recordeDesteJogo = acertos;
                        }
                        // Se já achou 15, para de procurar para este jogo específico
                        if (recordeDesteJogo == 15) break;
                    }

                    if (recordeDesteJogo == 13) qtd13++;
                    else if (recordeDesteJogo == 14) qtd14++;
                    else if (recordeDesteJogo == 15) qtd15++;
                }

                // Prepara os dados finais para enviar para a tela (precisa ser final ou effectively final)
                final int t = totalJogosAnalisados;
                final int q13 = qtd13;
                final int q14 = qtd14;
                final int q15 = qtd15;

                // --- PASSO 3: VOLTA PARA A TELA DO USUÁRIO ---
                runOnUiThread(() -> {
                    Intent intent = new Intent(MainActivity.this, ResultadoVarreduraActivity.class);
                    intent.putExtra("total", t);
                    intent.putExtra("q13", q13);
                    intent.putExtra("q14", q14);
                    intent.putExtra("q15", q15);
                    startActivity(intent);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start(); // Inicia o processamento em segundo plano
    }

    // CLASSE AUXILIAR PARA AJUDAR NA ORDENAÇÃO (Necessária para a função acima)
    private static class ItemVarredura {
        int numConcurso;
        int pontos;
        int numJogo;
        String textoConcurso;
        String sequenciaJogo;

        public ItemVarredura(int numConcurso, int pontos, int numJogo, String textoConcurso, String sequenciaJogo) {
            this.numConcurso = numConcurso;
            this.pontos = pontos;
            this.numJogo = numJogo;
            this.textoConcurso = textoConcurso;
            this.sequenciaJogo = sequenciaJogo;
        }
    }

    private List<Integer> calcularDezenasFrias(Map<String, String> jogosOficiais) {
        List<PacoteJogo> listaOrdenada = new ArrayList<>();

        for (Map.Entry<String, String> entry : jogosOficiais.entrySet()) {
            String info = entry.getValue();
            try {
                String[] partes = info.split(" ");
                if (partes.length > 1) {
                    int numConcurso = Integer.parseInt(partes[1]);
                    ArrayList<Integer> numeros = converterStringParaLista(entry.getKey());
                    listaOrdenada.add(new PacoteJogo(numConcurso, numeros));
                }
            } catch (Exception e) { }
        }

        Collections.sort(listaOrdenada, (p1, p2) -> Integer.compare(p2.concurso, p1.concurso));

        int limite = Math.min(listaOrdenada.size(), 10);
        int[] frequencia = new int[26];

        for (int i = 0; i < limite; i++) {
            for (int num : listaOrdenada.get(i).numeros) {
                if (num >= 1 && num <= 25) frequencia[num]++;
            }
        }

        List<Integer> dezenasFrias = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            if (frequencia[i] <= 3) {
                dezenasFrias.add(i);
            }
        }
        return dezenasFrias;
    }

    private static class PacoteJogo {
        int concurso;
        ArrayList<Integer> numeros;
        public PacoteJogo(int c, ArrayList<Integer> n) { this.concurso = c; this.numeros = n; }
    }

    private ArrayList<Integer> converterStringParaLista(String numerosStr) {
        ArrayList<Integer> lista = new ArrayList<>();
        try {
            String limpa = numerosStr.replace("[", "").replace("]", "").replace(" ", "");
            String[] partes = limpa.split(",");
            for (String p : partes) {
                if (!p.isEmpty()) lista.add(Integer.parseInt(p.trim()));
            }
        } catch (Exception e) {}
        return lista;
    }

    public void buscarJogoEquilibrado() {
        Random gerador = new Random();
        ArrayList<Integer> listaDefinitiva = new ArrayList<>();
        int somaFinal = 0, paresFinal = 0, primosFinal = 0, fibonacciFinal = 0, repetidosFinal = 0;

        String historicoGeral = bancoDeDados.getString("historico_ordenado", "");
        List<String> meusJogosSalvos = new ArrayList<>();
        if (!historicoGeral.isEmpty()) {
            meusJogosSalvos = Arrays.asList(historicoGeral.split(SEPARADOR));
        }

        Map<String, String> jogosOficiais = DadosOficiais.carregarResultadosOficiais(this);

        ArrayList<Integer> numerosDoUltimoConcurso = new ArrayList<>();
        int maiorNumeroConcurso = 0;
        List<Integer> dezenasFrias = calcularDezenasFrias(jogosOficiais);

        for (Map.Entry<String, String> entry : jogosOficiais.entrySet()) {
            String info = entry.getValue();
            try {
                String[] partes = info.split(" ");
                if (partes.length > 1) {
                    int numConcurso = Integer.parseInt(partes[1]);
                    if (numConcurso > maiorNumeroConcurso) {
                        maiorNumeroConcurso = numConcurso;
                        numerosDoUltimoConcurso = converterStringParaLista(entry.getKey());
                    }
                }
            } catch (Exception e) { }
        }

        ArrayList<Integer> numerosDaMoldura = new ArrayList<>();
        Collections.addAll(numerosDaMoldura, 1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24, 25);
        ArrayList<Integer> numerosPrimos = new ArrayList<>();
        Collections.addAll(numerosPrimos, 2, 3, 5, 7, 11, 13, 17, 19, 23);
        ArrayList<Integer> numerosFibonacci = new ArrayList<>();
        Collections.addAll(numerosFibonacci, 1, 2, 3, 5, 8, 13, 21);

        while (true) {
            ArrayList<Integer> tentativa = new ArrayList<>();
            while (tentativa.size() < 15) {
                int num = gerador.nextInt(25) + 1;
                if (!tentativa.contains(num)) {
                    tentativa.add(num);
                }
            }
            Collections.sort(tentativa);

            int pares = 0, somaTotal = 0, naMoldura = 0, nosPrimos = 0, nosFibonacci = 0, repetidosDoUltimo = 0;
            int quantidadeFriasNoJogo = 0;

            for (Integer numero : tentativa) {
                if (numero % 2 == 0) pares++;
                somaTotal += numero;
                if (numerosDaMoldura.contains(numero)) naMoldura++;
                if (numerosPrimos.contains(numero)) nosPrimos++;
                if (numerosFibonacci.contains(numero)) nosFibonacci++;
                if (!numerosDoUltimoConcurso.isEmpty() && numerosDoUltimoConcurso.contains(numero)) repetidosDoUltimo++;
                if (dezenasFrias.contains(numero)) quantidadeFriasNoJogo++;
            }

            boolean paresOk = (pares >= 6 && pares <= 8);
            boolean somaOk = (somaTotal >= 175 && somaTotal <= 225);
            boolean molduraOk = (naMoldura >= 8 && naMoldura <= 11);
            boolean primosOk = (nosPrimos >= 4 && nosPrimos <= 6);
            boolean fibonacciOk = (nosFibonacci >= 3 && nosFibonacci <= 5);
            boolean repetidosOk = true;
            if (!numerosDoUltimoConcurso.isEmpty()) {
                repetidosOk = (repetidosDoUltimo >= 8 && repetidosDoUltimo <= 10);
            }
            boolean friasOk = true;
            if (!dezenasFrias.isEmpty()) {
                friasOk = (quantidadeFriasNoJogo >= 2 && quantidadeFriasNoJogo <= 6);
            }

            if (paresOk && somaOk && molduraOk && primosOk && fibonacciOk && repetidosOk && friasOk) {
                String assinaturaDoJogo = tentativa.toString();

                if (meusJogosSalvos.contains(assinaturaDoJogo)) continue;
                if (jogosOficiais.containsKey(assinaturaDoJogo)) continue;

                salvarJogo(historicoGeral, assinaturaDoJogo);

                listaDefinitiva = tentativa;
                somaFinal = somaTotal;
                paresFinal = pares;
                primosFinal = nosPrimos;
                fibonacciFinal = nosFibonacci;
                repetidosFinal = repetidosDoUltimo;
                break;
            }
        }

        String resultadoLimpo = listaDefinitiva.toString().replace("[", "").replace("]", "");
        txtResultado.setText(resultadoLimpo);

        lblSomaPrimos.setText("Soma: " + somaFinal + " / Primos: " + primosFinal);
        lblParesImpares.setText("Pares: " + paresFinal + " / Ímpares: " + (15 - paresFinal));

        String textoRepetidos = "N/A";
        if (maiorNumeroConcurso > 0) {
            textoRepetidos = repetidosFinal + " (Conc. " + maiorNumeroConcurso + ")";
        }
        lblFibRepetidos.setText("Fibonacci: " + fibonacciFinal + " / Repetidos: " + textoRepetidos);

        atualizarContadorTela();

        int novoTotal = meusJogosSalvos.size() + 1;
        String msgExtra = (!dezenasFrias.isEmpty()) ? "\n(Usando análise dos últimos 10 jogos)" : "";
        Toast.makeText(this, "Jogo Inteligente nº " + novoTotal + " Gerado!" + msgExtra, Toast.LENGTH_LONG).show();
    }

    private void salvarJogo(String historicoAntigo, String novoJogo) {
        SharedPreferences.Editor editor = bancoDeDados.edit();
        if (historicoAntigo.isEmpty()) {
            editor.putString("historico_ordenado", novoJogo);
        } else {
            editor.putString("historico_ordenado", historicoAntigo + SEPARADOR + novoJogo);
        }
        editor.apply();
    }

    public void atualizarContadorTela() {
        String historicoGeral = bancoDeDados.getString("historico_ordenado", "");
        int total = historicoGeral.isEmpty() ? 0 : historicoGeral.split(SEPARADOR).length;
        txtContador.setText("Jogos gerados até agora: " + total);
    }

    public void compartilharTexto(String msg) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(intent, "Compartilhar"));
    }

    public void mostrarRedesSociais() {
        String[] opcoes = {"Instagram", "Facebook", "LinkedIn"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conecte-se com Liu:");
        builder.setItems(opcoes, (dialog, which) -> {
            if (which == 0) abrirLink("https://www.instagram.com/laramaoicirapa?igsh=dGhhdTV6cjJuY21k");
            else if (which == 1) abrirLink("https://www.facebook.com/share/1FYhVPJFmt/");
            else if (which == 2) abrirLink("https://www.linkedin.com/in/aparício-amaral-b53451304");
        });
        builder.show();
    }

    public void abrirLink(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public void abrirCadastroOficial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cadastrar Novo Resultado");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText inputConcurso = new EditText(this);
        inputConcurso.setHint("Concurso");
        inputConcurso.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputConcurso);

        final EditText inputData = new EditText(this);
        inputData.setHint("Data (dd/mm/aaaa)");
        inputData.setInputType(InputType.TYPE_CLASS_DATETIME);
        layout.addView(inputData);

        final EditText inputNumeros = new EditText(this);
        inputNumeros.setHint("15 Dezenas (ex: 1 2 3...)");
        inputNumeros.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(inputNumeros);

        builder.setView(layout);
        builder.setPositiveButton("Salvar", (dialog, which) -> {
            processarECadastrar(inputConcurso.getText().toString(), inputData.getText().toString(), inputNumeros.getText().toString());
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    public void processarECadastrar(String concurso, String data, String numerosBrutos) {
        try {
            if (concurso.isEmpty() || data.isEmpty() || numerosBrutos.isEmpty()) return;
            if (DadosOficiais.verificarSeConcursoJaExiste(this, concurso)) {
                new AlertDialog.Builder(this).setTitle("Duplicidade!").setMessage("Concurso " + concurso + " já existe.").setPositiveButton("OK", null).show();
                return;
            }
            String[] partes = numerosBrutos.replace(",", " ").replace("-", " ").trim().split("\\s+");
            if (partes.length != 15) {
                Toast.makeText(this, "Erro: Digite exatamente 15 números.", Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList<Integer> lista = new ArrayList<>();
            for (String p : partes) lista.add(Integer.parseInt(p));
            Collections.sort(lista);
            DadosOficiais.salvarNovoResultado(this, lista.toString(), concurso, data);
            Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erro nos dados.", Toast.LENGTH_SHORT).show();
        }
    }

    public void abrirConferidor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conferir no Seu Histórico");
        final EditText input = new EditText(this);
        input.setHint("Digite os 15 números");
        builder.setView(input);
        builder.setPositiveButton("Verificar", (dialog, which) -> verificarSeJogoExiste(input.getText().toString()));
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    public void verificarSeJogoExiste(String entrada) {
        try {
            String[] partes = entrada.replace(",", " ").replace("-", " ").trim().split("\\s+");
            if (partes.length != 15) {
                Toast.makeText(this, "Digite 15 números!", Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<Integer> nums = new ArrayList<>();
            for (String p : partes) nums.add(Integer.parseInt(p));
            Collections.sort(nums);
            String jogo = nums.toString();
            String historico = bancoDeDados.getString("historico_ordenado", "");
            if (historico.contains(jogo)) {
                new AlertDialog.Builder(this).setTitle("ENCONTRADO!").setMessage("Você já gerou esse jogo.").setPositiveButton("OK", null).show();
            } else {
                new AlertDialog.Builder(this).setTitle("Não encontrado").setMessage("Esse jogo não está no seu histórico.").setPositiveButton("OK", null).show();
            }
        } catch (Exception e) {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarContadorTela();
    }
}