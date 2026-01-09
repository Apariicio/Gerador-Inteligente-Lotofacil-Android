package com.aparicioamaral.quinzenumerosaleatorios;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
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

    TextView txtContador;
    TextView lblSomaPrimos, lblParesImpares, lblFibRepetidos, txtAssinatura, lblCiclo;
    EditText inputFixas;

    // --- TODAS AS 6 CHAVES ---
    Switch switchPares, switchSoma, switchPrimos;
    Switch switchRepetidos, switchFibonacci, switchCiclo;

    Button btnSortear, btnHistorico, btnInserirManual, btnConferir, btnVarredura, btnCadastrarOficial, btnGerenciarManuais;
    GridLayout gridTabuleiro;
    TextView[] bolasTabuleiro = new TextView[26];

    SharedPreferences bancoDeDados;
    private static final String SEPARADOR = "####";
    private List<int[]> cacheMeusJogos = new ArrayList<>();
    private List<DadosConcurso> cacheOficiais = new ArrayList<>();
    private String jogoAtualParaCompartilhar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ocultarBarrasDeNavegacao();

            gridTabuleiro = findViewById(R.id.gridTabuleiro);
            inicializarBolasDoTabuleiro();

            inputFixas = findViewById(R.id.inputFixas);

            // --- Mapeando as 6 Chaves ---
            switchPares = findViewById(R.id.switchPares);
            switchSoma = findViewById(R.id.switchSoma);
            switchPrimos = findViewById(R.id.switchPrimos);
            switchRepetidos = findViewById(R.id.switchRepetidos);
            switchFibonacci = findViewById(R.id.switchFibonacci);
            switchCiclo = findViewById(R.id.switchCiclo);

            // --- Configurando Pintura das Chaves ---
            configurarPinturaChave(switchPares);
            configurarPinturaChave(switchSoma);
            configurarPinturaChave(switchPrimos);
            configurarPinturaChave(switchRepetidos);
            configurarPinturaChave(switchFibonacci);
            configurarPinturaChave(switchCiclo);

            btnSortear = findViewById(R.id.btnSortear);
            btnHistorico = findViewById(R.id.btnHistorico);
            btnConferir = findViewById(R.id.btnConferir);
            btnVarredura = findViewById(R.id.btnVarredura);
            btnCadastrarOficial = findViewById(R.id.btnCadastrarOficial);
            btnGerenciarManuais = findViewById(R.id.btnGerenciarManuais);
            btnInserirManual = findViewById(R.id.btnInserirManual);
            btnInserirManual.setOnClickListener(v -> abrirInserirJogoManual());

            lblSomaPrimos = findViewById(R.id.lblSomaPrimos);
            lblParesImpares = findViewById(R.id.lblParesImpares);
            lblFibRepetidos = findViewById(R.id.lblFibRepetidos);
            lblCiclo = findViewById(R.id.lblCiclo);

            txtContador = findViewById(R.id.txtContador);
            txtAssinatura = findViewById(R.id.txtAssinatura);

            if (txtAssinatura != null) {
                txtAssinatura.setPaintFlags(txtAssinatura.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
                txtAssinatura.setOnClickListener(v -> mostrarRedesSociais());
            }

            gridTabuleiro.setOnClickListener(v -> compartilharJogo());

            bancoDeDados = getSharedPreferences("HistoricoJogos", MODE_PRIVATE);

            lblSomaPrimos.setText("Soma: -- / Primos: --");
            lblParesImpares.setText("Pares: -- / Ímpares: --");
            lblFibRepetidos.setText("Fib: -- / Rep: --");
            lblCiclo.setText("Ciclo: Carregando...");

            btnSortear.setOnClickListener(v -> buscarJogoEquilibrado());
            btnHistorico.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoricoActivity.class)));
            btnConferir.setOnClickListener(v -> abrirConferidor());
            btnVarredura.setOnClickListener(v -> fazerVarreduraRelampago());
            btnCadastrarOficial.setOnClickListener(v -> abrirCadastroOficial());
            btnGerenciarManuais.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoricoManualActivity.class)));

            atualizarContadorTela();
            carregarDadosParaMemoria();
            limparTabuleiro();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Função auxiliar para configurar a pintura inicial e o clique
    private void configurarPinturaChave(Switch chave) {
        pintarChave(chave); // Pinta estado inicial
        chave.setOnCheckedChangeListener((buttonView, isChecked) -> pintarChave(chave));
    }

    private void pintarChave(Switch chave) {
        int corBolinhaOn = Color.parseColor("#4CAF50");
        int corBarraOn   = Color.parseColor("#A5D6A7");
        int corBolinhaOff = Color.parseColor("#ECECEC");
        int corBarraOff   = Color.parseColor("#9E9E9E");

        if (chave.isChecked()) {
            chave.getThumbDrawable().setTint(corBolinhaOn);
            chave.getTrackDrawable().setTint(corBarraOn);
        } else {
            chave.getThumbDrawable().setTint(corBolinhaOff);
            chave.getTrackDrawable().setTint(corBarraOff);
        }
    }

    private void inicializarBolasDoTabuleiro() {
        for (int i = 1; i <= 25; i++) {
            String idName = "num" + (i < 10 ? "0" + i : i);
            int resID = getResources().getIdentifier(idName, "id", getPackageName());
            bolasTabuleiro[i] = findViewById(resID);
            bolasTabuleiro[i].setOnClickListener(v -> compartilharJogo());
        }
    }

    private void limparTabuleiro() {
        for (int i = 1; i <= 25; i++) {
            if (bolasTabuleiro[i] != null) {
                bolasTabuleiro[i].setVisibility(View.VISIBLE);
                bolasTabuleiro[i].setBackgroundResource(R.drawable.bola_apagada);
                bolasTabuleiro[i].setTextColor(Color.parseColor("#999999"));
            }
        }
    }

    private void atualizarTabuleiro(List<Integer> numerosSorteados) {
        limparTabuleiro();
        jogoAtualParaCompartilhar = numerosSorteados.toString();
        for (int i = 1; i <= 25; i++) {
            if (bolasTabuleiro[i] != null) {
                if (numerosSorteados.contains(i)) {
                    bolasTabuleiro[i].setBackgroundResource(R.drawable.bola_selecionada);
                    bolasTabuleiro[i].setTextColor(Color.WHITE);
                }
            }
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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            atualizarContadorTela();
            carregarDadosParaMemoria();
        } catch (Exception e) {}
    }

    private static class DadosConcurso {
        int[] numeros;
        String nomeConcurso;
        public DadosConcurso(int[] n, String nome) {
            this.numeros = n;
            this.nomeConcurso = nome;
        }
    }

    private void carregarDadosParaMemoria() {
        new Thread(() -> {
            try {
                cacheMeusJogos.clear();
                String historico = bancoDeDados.getString("historico_ordenado", "");
                if (!historico.isEmpty()) {
                    String[] jogos = historico.split(SEPARADOR);
                    for (String j : jogos) {
                        if (!j.trim().isEmpty()) {
                            cacheMeusJogos.add(converterStringParaArrayInt(j));
                        }
                    }
                }
                cacheOficiais.clear();
                Map<String, String> oficiaisMap = DadosOficiais.carregarResultadosOficiais(this);
                if (oficiaisMap != null) {
                    for (Map.Entry<String, String> entry : oficiaisMap.entrySet()) {
                        int[] nums = converterStringParaArrayInt(entry.getKey());
                        if (nums.length == 15) {
                            cacheOficiais.add(new DadosConcurso(nums, entry.getValue()));
                        }
                    }
                }
                Collections.sort(cacheOficiais, (o1, o2) -> {
                    int n1 = 0, n2 = 0;
                    try { n1 = Integer.parseInt(o1.nomeConcurso.split(" ")[1]); } catch(Exception e){}
                    try { n2 = Integer.parseInt(o2.nomeConcurso.split(" ")[1]); } catch(Exception e){}
                    return Integer.compare(n1, n2);
                });
                runOnUiThread(() -> {
                    try {
                        List<Integer> faltantes = calcularDezenasDoCiclo();
                        if (!switchCiclo.isChecked()) {
                            lblCiclo.setText("Ciclo: (Filtro Desativado)");
                            lblCiclo.setTextColor(Color.GRAY);
                        } else {
                            if (faltantes.isEmpty()) lblCiclo.setText("Ciclo: Fechado");
                            else lblCiclo.setText("Faltam no Ciclo: " + faltantes.toString());
                            lblCiclo.setTextColor(Color.parseColor("#7C4617"));
                        }
                    } catch (Exception e) {}
                });
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private int[] converterStringParaArrayInt(String str) {
        try {
            String limpa = str.replace("[", "").replace("]", "").replace(",", " ");
            String[] partes = limpa.trim().split("\\s+");
            int[] nums = new int[partes.length];
            for (int i = 0; i < partes.length; i++) {
                nums[i] = Integer.parseInt(partes[i].trim());
            }
            return nums;
        } catch (Exception e) { return new int[0]; }
    }

    private List<Integer> calcularDezenasDoCiclo() {
        Set<Integer> saiuRecentemente = new HashSet<>();
        for (int i = cacheOficiais.size() - 1; i >= 0; i--) {
            int[] nums = cacheOficiais.get(i).numeros;
            for (int n : nums) saiuRecentemente.add(n);
            if (saiuRecentemente.size() == 25) { break; }
        }
        List<Integer> faltantes = new ArrayList<>();
        if (saiuRecentemente.size() < 25 && !saiuRecentemente.isEmpty()) {
            for (int i = 1; i <= 25; i++) {
                if (!saiuRecentemente.contains(i)) faltantes.add(i);
            }
        }
        return faltantes;
    }

    private boolean validarEquilibrioGrade(List<Integer> numeros) {
        int[] linhas = new int[5];
        int[] colunas = new int[5];
        for (int n : numeros) {
            int num = n - 1;
            linhas[num / 5]++;
            colunas[num % 5]++;
        }
        for (int i = 0; i < 5; i++) {
            if (linhas[i] == 0 || linhas[i] == 5) return false;
            if (colunas[i] == 0 || colunas[i] == 5) return false;
        }
        return true;
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

        Map<String, String> oficiaisMap = DadosOficiais.carregarResultadosOficiais(this);

        ArrayList<Integer> numerosDoUltimoConcurso = new ArrayList<>();
        if (!cacheOficiais.isEmpty()) {
            int[] ultimoArr = cacheOficiais.get(cacheOficiais.size() - 1).numeros;
            for(int i : ultimoArr) numerosDoUltimoConcurso.add(i);
        }

        List<Integer> dezenasFrias = calcularDezenasFrias(oficiaisMap);
        List<Integer> dezenasCiclo = calcularDezenasDoCiclo();

        ArrayList<Integer> numerosFixosUsuario = new ArrayList<>();
        if (inputFixas != null) {
            String textoFixos = inputFixas.getText().toString();
            if (!textoFixos.isEmpty()) {
                try {
                    ArrayList<Integer> temp = converterStringParaLista(textoFixos);
                    for (int n : temp) {
                        if (n >= 1 && n <= 25 && !numerosFixosUsuario.contains(n)) {
                            numerosFixosUsuario.add(n);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Erro fixas.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        ArrayList<Integer> numerosDaMoldura = new ArrayList<>();
        Collections.addAll(numerosDaMoldura, 1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24, 25);
        ArrayList<Integer> numerosPrimos = new ArrayList<>();
        Collections.addAll(numerosPrimos, 2, 3, 5, 7, 11, 13, 17, 19, 23);
        ArrayList<Integer> numerosFibonacci = new ArrayList<>();
        Collections.addAll(numerosFibonacci, 1, 2, 3, 5, 8, 13, 21);

        // --- LENDO O ESTADO DAS 6 CHAVES ---
        boolean usarPares = switchPares.isChecked();
        boolean usarSoma = switchSoma.isChecked();
        boolean usarPrimos = switchPrimos.isChecked();
        boolean usarRepetidos = switchRepetidos.isChecked();
        boolean usarFibonacci = switchFibonacci.isChecked();
        boolean usarCiclo = switchCiclo.isChecked();
        // -----------------------------------

        int tentativasLoop = 0;

        while (true) {
            tentativasLoop++;
            if (tentativasLoop > 50000) {
                Toast.makeText(this, "Difícil! Desative algumas chaves.", Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<Integer> tentativa = new ArrayList<>();
            tentativa.addAll(numerosFixosUsuario);

            if (usarCiclo) {
                for (int dCiclo : dezenasCiclo) {
                    if (!tentativa.contains(dCiclo) && tentativa.size() < 15) {
                        if (gerador.nextInt(100) < 70) {
                            tentativa.add(dCiclo);
                        }
                    }
                }
            }

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

            // --- REGRAS ---
            boolean paresOk = !usarPares || (pares >= 6 && pares <= 9);
            boolean somaOk = !usarSoma || (somaTotal >= 165 && somaTotal <= 230);
            boolean primosOk = !usarPrimos || (nosPrimos >= 4 && nosPrimos <= 7);
            boolean fibonacciOk = !usarFibonacci || (nosFibonacci >= 3 && nosFibonacci <= 5);

            boolean repetidosOk = true;
            if (usarRepetidos && !numerosDoUltimoConcurso.isEmpty()) {
                repetidosOk = (repetidosDoUltimo >= 7 && repetidosDoUltimo <= 10);
            }

            boolean molduraOk = (naMoldura >= 8 && naMoldura <= 11);
            boolean gradeOk = validarEquilibrioGrade(tentativa);

            if (paresOk && somaOk && molduraOk && primosOk && fibonacciOk && repetidosOk && gradeOk) {
                String assinaturaDoJogo = tentativa.toString();
                if (meusJogosSalvos.contains(assinaturaDoJogo)) continue;
                if (oficiaisMap != null && oficiaisMap.containsKey(assinaturaDoJogo)) continue;

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

        atualizarTabuleiro(listaDefinitiva);

        // --- MENSAGENS VISUAIS DE STATUS (OFF/ON) ---
        String statusSoma = somaFinal + (usarSoma ? "" : " (Livre)");
        String statusPrimos = primosFinal + (usarPrimos ? "" : " (Livre)");
        lblSomaPrimos.setText("Soma: " + statusSoma + " / Primos: " + statusPrimos);

        String statusPares = paresFinal + (usarPares ? "" : " (Livre)");
        String statusImpares = (15 - paresFinal) + (usarPares ? "" : " (Livre)");
        lblParesImpares.setText("Pares: " + statusPares + " / Ímpares: " + statusImpares);

        String textoConcurso = "N/A";
        if (!cacheOficiais.isEmpty()) {
            String nome = cacheOficiais.get(cacheOficiais.size()-1).nomeConcurso;
            try {
                String[] partes = nome.split(" ");
                if (partes.length > 1) textoConcurso = partes[1];
                else textoConcurso = nome.replaceAll("[^0-9]", "");
            } catch (Exception e){}
        }

        String statusFibo = fibonacciFinal + (usarFibonacci ? "" : " (Livre)");
        String statusRep = repetidosFinal + (usarRepetidos ? "" : " (Livre)");
        lblFibRepetidos.setText("Fibo: " + statusFibo + " / Repe: " + statusRep + " (conc. " + textoConcurso + ")");

        if (!usarCiclo) {
            lblCiclo.setText("Ciclo: (Filtro Desativado)");
            lblCiclo.setTextColor(Color.GRAY);
        } else {
            List<Integer> faltantes = calcularDezenasDoCiclo();
            if (faltantes.isEmpty()) lblCiclo.setText("Ciclo: Fechado");
            else lblCiclo.setText("Faltam no Ciclo: " + faltantes.toString());
            lblCiclo.setTextColor(Color.parseColor("#7C4617"));
        }

        atualizarContadorTela();

        // RECUPERANDO O CONTADOR DE NÚMERO DO JOGO
        int novoTotal = meusJogosSalvos.size() + 1;

        StringBuilder msg = new StringBuilder();
        // Mensagem completa com o contador
        msg.append("Jogo Inteligente nº ").append(novoTotal).append(" Gerado!");

        if (!numerosFixosUsuario.isEmpty()) {
            msg.append("\n* ").append(numerosFixosUsuario.size()).append(" Fixas");
        }

        if (!usarPares) msg.append("\n(OFF) Par Livre/Ímp Livre");
        if (!usarSoma) msg.append("\n(OFF) Soma Livre");
        if (!usarPrimos) msg.append("\n(OFF) Primos Livre");
        if (!usarRepetidos) msg.append("\n(OFF) Repet. Livre");
        if (!usarFibonacci) msg.append("\n(OFF) Fibo Livre");

        if (usarCiclo) {
            if (!dezenasCiclo.isEmpty()) msg.append("\n[Ciclo Ativado]");
        } else {
            msg.append("\n(OFF) Ciclo Livre");
        }

        Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show();
    }

    private void salvarJogo(String historicoAntigo, String novoJogo) {
        SharedPreferences.Editor editor = bancoDeDados.edit();
        if (historicoAntigo.isEmpty()) {
            editor.putString("historico_ordenado", novoJogo);
        } else {
            editor.putString("historico_ordenado", historicoAntigo + SEPARADOR + novoJogo);
        }
        editor.apply();
        cacheMeusJogos.add(converterStringParaArrayInt(novoJogo));
    }

    public void atualizarContadorTela() {
        if (txtContador == null) return;
        String historicoGeral = bancoDeDados.getString("historico_ordenado", "");
        int total = historicoGeral.isEmpty() ? 0 : historicoGeral.split(SEPARADOR).length;
        txtContador.setText("Jogos gerados até agora: " + total);
    }

    public void compartilharJogo() {
        if (jogoAtualParaCompartilhar.isEmpty()) {
            Toast.makeText(MainActivity.this, "Gere um jogo primeiro!", Toast.LENGTH_SHORT).show();
        } else {
            compartilharTexto("Olha esse jogo da Lotofácil: \n" + jogoAtualParaCompartilhar);
        }
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
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {}
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
            carregarDadosParaMemoria();
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

    public void fazerVarreduraRelampago() {
        if (cacheMeusJogos.isEmpty()) {
            Toast.makeText(this, "Carregando dados... Tente em 2 segundos.", Toast.LENGTH_SHORT).show();
            carregarDadosParaMemoria();
            return;
        }

        int qtd11 = 0, qtd12 = 0, qtd13 = 0, qtd14 = 0, qtd15 = 0;
        int totalJogos = cacheMeusJogos.size();
        List<ItemCampeao> listaParaOrdenar = new ArrayList<>();

        for (int i = 0; i < cacheMeusJogos.size(); i++) {
            int[] meuJogo = cacheMeusJogos.get(i);
            int melhorPontuacao = 0;
            String concursoDoRecorde = "";
            int numeroDoConcursoRecorde = 0;

            for (DadosConcurso oficial : cacheOficiais) {
                int acertos = 0;
                for (int m : meuJogo) {
                    for (int o : oficial.numeros) {
                        if (m == o) { acertos++; break; }
                    }
                }
                if (acertos > melhorPontuacao) {
                    melhorPontuacao = acertos;
                    concursoDoRecorde = oficial.nomeConcurso;
                    try {
                        String[] parts = concursoDoRecorde.split(" ");
                        if (parts.length > 1) {
                            numeroDoConcursoRecorde = Integer.parseInt(parts[1]);
                        }
                    } catch (Exception e) { numeroDoConcursoRecorde = 0; }
                }
                if (melhorPontuacao == 15) break;
            }

            if (melhorPontuacao == 11) qtd11++;
            else if (melhorPontuacao == 12) qtd12++;
            else if (melhorPontuacao == 13) qtd13++;
            else if (melhorPontuacao == 14) qtd14++;
            else if (melhorPontuacao == 15) qtd15++;

            if (melhorPontuacao >= 14) {
                String emojiTrofeu = new String(Character.toChars(0x1F3C6));
                String msgFinal = emojiTrofeu + " " + melhorPontuacao + " PONTOS! (Seu Jogo " + (i + 1) + ")\n" +
                        "No " + concursoDoRecorde;
                listaParaOrdenar.add(new ItemCampeao(numeroDoConcursoRecorde, msgFinal));
            }
        }

        Collections.sort(listaParaOrdenar, (item1, item2) -> Integer.compare(item2.concurso, item1.concurso));
        ArrayList<String> detalhesFinais = new ArrayList<>();
        for (ItemCampeao item : listaParaOrdenar) {
            detalhesFinais.add(item.textoFormatado);
        }

        Intent intent = new Intent(this, ResultadoVarreduraActivity.class);
        intent.putExtra("total", totalJogos);
        intent.putExtra("q11", qtd11);
        intent.putExtra("q12", qtd12);
        intent.putExtra("q13", qtd13);
        intent.putExtra("q14", qtd14);
        intent.putExtra("q15", qtd15);
        intent.putStringArrayListExtra("detalhes_campeoes", detalhesFinais);
        startActivity(intent);
    }

    private static class ItemCampeao {
        int concurso;
        String textoFormatado;
        public ItemCampeao(int c, String t) {
            this.concurso = c;
            this.textoFormatado = t;
        }
    }

    private List<Integer> calcularDezenasFrias(Map<String, String> jogosOficiais) {
        List<PacoteJogo> listaOrdenada = new ArrayList<>();
        if (jogosOficiais == null) return new ArrayList<>();

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
            String limpa = numerosStr.replace("[", "").replace("]", "").replace(",", " ");
            String[] partes = limpa.trim().split("\\s+");
            for (String p : partes) {
                if (!p.isEmpty()) lista.add(Integer.parseInt(p.trim()));
            }
        } catch (Exception e) {}
        return lista;
    }

    public void abrirInserirJogoManual() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Proteger Jogo Manual");
        builder.setMessage("Digite os 15 números que você jogou. Eles serão salvos no histórico e o app evitará gerá-los novamente.");

        final EditText input = new EditText(this);
        input.setHint("Ex: 01 02 03 04...");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("SALVAR E PROTEGER", (dialog, which) -> {
            String texto = input.getText().toString();
            processarSalvamentoManual(texto);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void processarSalvamentoManual(String entrada) {
        try {
            String[] partes = entrada.replace(",", " ").replace("-", " ").trim().split("\\s+");
            if (partes.length != 15) {
                Toast.makeText(this, "Erro: Digite exatamente 15 números!", Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList<Integer> numeros = new ArrayList<>();
            for (String p : partes) {
                int n = Integer.parseInt(p);
                if (n < 1 || n > 25) {
                    Toast.makeText(this, "Erro: Número " + n + " inválido (use 1 a 25).", Toast.LENGTH_LONG).show();
                    return;
                }
                if (numeros.contains(n)) {
                    Toast.makeText(this, "Erro: Número " + n + " repetido.", Toast.LENGTH_LONG).show();
                    return;
                }
                numeros.add(n);
            }
            Collections.sort(numeros);
            String jogoFormatado = numeros.toString();
            String historicoGeral = bancoDeDados.getString("historico_ordenado", "");
            if (historicoGeral.contains(jogoFormatado)) {
                Toast.makeText(this, "Atenção: Este jogo JÁ ESTAVA no seu histórico!", Toast.LENGTH_LONG).show();
                return;
            }
            salvarJogo(historicoGeral, jogoFormatado);
            atualizarTabuleiro(numeros);
            atualizarContadorTela();
            Toast.makeText(this, "Jogo salvo Manualmente!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erro: Verifique se digitou apenas números.", Toast.LENGTH_SHORT).show();
        }
    }
}