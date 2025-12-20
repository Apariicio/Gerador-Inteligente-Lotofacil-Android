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
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView txtResultado, txtContador;

    // MUDANÇA: USANDO 3 TEXTVIEWS COMBINADOS
    TextView lblSomaPrimos, lblParesImpares, lblFibRepetidos, txtAssinatura;

    Button btnSortear, btnCompartilhar, btnHistorico, btnConferir, btnVarredura, btnCadastrarOficial, btnGerenciarManuais;
    SharedPreferences bancoDeDados;

    private static final String SEPARADOR = "####";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MODO TELA CHEIA
        ocultarBarrasDeNavegacao();

        txtResultado = findViewById(R.id.txtResultado);
        btnSortear = findViewById(R.id.btnSortear);
        btnCompartilhar = findViewById(R.id.btnCompartilhar);
        btnHistorico = findViewById(R.id.btnHistorico);
        btnConferir = findViewById(R.id.btnConferir);
        btnVarredura = findViewById(R.id.btnVarredura);
        btnCadastrarOficial = findViewById(R.id.btnCadastrarOficial);
        btnGerenciarManuais = findViewById(R.id.btnGerenciarManuais);

        // LIGANDO OS NOVOS COMPONENTES
        lblSomaPrimos = findViewById(R.id.lblSomaPrimos);
        lblParesImpares = findViewById(R.id.lblParesImpares);
        lblFibRepetidos = findViewById(R.id.lblFibRepetidos);

        txtContador = findViewById(R.id.txtContador);
        txtAssinatura = findViewById(R.id.txtAssinatura);

        txtAssinatura.setPaintFlags(txtAssinatura.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);

        bancoDeDados = getSharedPreferences("HistoricoJogos", MODE_PRIVATE);

        txtResultado.setText("Clique para buscar...");

        // TEXTOS INICIAIS
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

        for (Map.Entry<String, String> entry : jogosOficiais.entrySet()) {
            String info = entry.getValue();
            try {
                String[] partes = info.split(" ");
                if (partes.length > 1) {
                    int numConcurso = Integer.parseInt(partes[1]);
                    if (numConcurso > maiorNumeroConcurso) {
                        maiorNumeroConcurso = numConcurso;
                        String numerosStr = entry.getKey().replace("[", "").replace("]", "").replace(" ", "");
                        String[] numsArr = numerosStr.split(",");
                        numerosDoUltimoConcurso.clear();
                        for (String n : numsArr) numerosDoUltimoConcurso.add(Integer.parseInt(n.trim()));
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

            for (Integer numero : tentativa) {
                if (numero % 2 == 0) pares++;
                somaTotal += numero;
                if (numerosDaMoldura.contains(numero)) naMoldura++;
                if (numerosPrimos.contains(numero)) nosPrimos++;
                if (numerosFibonacci.contains(numero)) nosFibonacci++;
                if (!numerosDoUltimoConcurso.isEmpty() && numerosDoUltimoConcurso.contains(numero)) {
                    repetidosDoUltimo++;
                }
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

            if (paresOk && somaOk && molduraOk && primosOk && fibonacciOk && repetidosOk) {
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

        // --- ATUALIZAÇÃO NO NOVO FORMATO AGRUPADO ---
        lblSomaPrimos.setText("Soma: " + somaFinal + " / Primos: " + primosFinal);
        lblParesImpares.setText("Pares: " + paresFinal + " / Ímpares: " + (15 - paresFinal));

        String textoRepetidos = "N/A";
        if (maiorNumeroConcurso > 0) {
            textoRepetidos = String.valueOf(repetidosFinal);
        }
        lblFibRepetidos.setText("Fibonacci: " + fibonacciFinal + " / Repetidos: " + textoRepetidos);

        atualizarContadorTela();

        int novoTotal = meusJogosSalvos.size() + 1;
        Toast.makeText(this, "Jogo Inteligente nº " + novoTotal + " Gerado!", Toast.LENGTH_LONG).show();
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

    public void fazerVarreduraGeral() {
        String historico = bancoDeDados.getString("historico_ordenado", "");
        if (historico.isEmpty()) {
            Toast.makeText(this, "Histórico vazio.", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] meusJogos = historico.split(SEPARADOR);
        Map<String, String> oficiais = DadosOficiais.carregarResultadosOficiais(this);
        ArrayList<String> conflitos = new ArrayList<>();

        for (int i = 0; i < meusJogos.length; i++) {
            if (oficiais.containsKey(meusJogos[i])) {
                conflitos.add("JOGO Nº " + (i + 1) + "\nSaiu no: " + oficiais.get(meusJogos[i]) + "\nSeq: " + meusJogos[i]);
            }
        }

        if (!conflitos.isEmpty()) {
            Intent intent = new Intent(this, ResultadoVarreduraActivity.class);
            intent.putStringArrayListExtra("lista_erros", conflitos);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this).setTitle("Tudo Limpo!").setMessage("Nenhum jogo seu já foi sorteado.").setPositiveButton("OK", null).show();
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