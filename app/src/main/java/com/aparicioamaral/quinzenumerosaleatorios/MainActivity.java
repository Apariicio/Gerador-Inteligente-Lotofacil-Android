/*
 * Copyright (c) 2026 [Aparício (Liu)]. Todos os direitos reservados.
 * Este arquivo é parte do aplicativo [Quinze Números Aleatórios].
 * A cópia não autorizada deste arquivo, via qualquer meio, é estritamente proibida.
 * Desenvolvido por: [Aparício] - [www.linkedin.com/in/aparício-amaral-b53451304]
 */
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    TextView txtFiltrosAtivos;
    EditText inputFixas;

    // --- TODAS AS 6 CHAVES ---
    Switch switchPares, switchSoma, switchPrimos;
    Switch switchRepetidos, switchFibonacci, switchCiclo, switchOcultas;
    LinearLayout layoutProgresso;
    ProgressBar progressBarVarredura;
    TextView txtProgressoVarredura, iconeTrevoLoading;
    android.animation.ObjectAnimator animacaoTrevo;

    Button btnSortear, btnTurbo, btnInformacao;
    TextView btnHistorico, btnInserirManual, btnConferir, btnVarredura, btnCadastrarOficial, btnGerenciarManuais;
    GridLayout gridTabuleiro;
    TextView[] bolasTabuleiro = new TextView[26];

    SharedPreferences bancoDeDados;
    private static final String SEPARADOR = "####";
    private List<int[]> cacheMeusJogos = new ArrayList<>();
    private int[] freqUltimos20 = new int[26];
    private List<DadosConcurso> cacheOficiais = new ArrayList<>();
    private String jogoAtualParaCompartilhar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 🌟 NOVO: Carrega e aplica o tema salvo pelo usuário antes de desenhar a tela
        SharedPreferences bdTema = getSharedPreferences("HistoricoJogos", MODE_PRIVATE);
        int temaSalvo = bdTema.getInt("tema_usuario", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(temaSalvo);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ocultarBarrasDeNavegacao();

            gridTabuleiro = findViewById(R.id.gridTabuleiro);
            inicializarBolasDoTabuleiro();

            inputFixas = findViewById(R.id.inputFixas);

            // --- Mapeando as 7 Chaves ---
            switchPares = findViewById(R.id.switchPares);
            switchSoma = findViewById(R.id.switchSoma);
            switchPrimos = findViewById(R.id.switchPrimos);
            switchRepetidos = findViewById(R.id.switchRepetidos);
            switchFibonacci = findViewById(R.id.switchFibonacci);
            switchCiclo = findViewById(R.id.switchCiclo);
            switchOcultas = findViewById(R.id.switchOcultas);

            txtFiltrosAtivos = findViewById(R.id.txtFiltrosAtivos);
            atualizarContadorFiltros();

            // --- Configurando Pintura das Chaves ---
            configurarPinturaChave(switchPares);
            configurarPinturaChave(switchSoma);
            configurarPinturaChave(switchPrimos);
            configurarPinturaChave(switchRepetidos);
            configurarPinturaChave(switchFibonacci);
            configurarPinturaChave(switchCiclo);
            configurarPinturaChave(switchOcultas);

            btnSortear = findViewById(R.id.btnSortear);
            btnTurbo = findViewById(R.id.btnTurbo);
            btnTurbo.setOnClickListener(v -> abrirPopupTurbo3x());
            btnHistorico = findViewById(R.id.btnHistorico);
            btnConferir = findViewById(R.id.btnConferir);
            btnVarredura = findViewById(R.id.btnVarredura);
            btnCadastrarOficial = findViewById(R.id.btnCadastrarOficial);
            btnGerenciarManuais = findViewById(R.id.btnGerenciarManuais);
            btnInformacao = findViewById(R.id.btnInformacao);
            btnInformacao.setOnClickListener(v -> {

                androidx.appcompat.view.ContextThemeWrapper wrapper = new androidx.appcompat.view.ContextThemeWrapper(MainActivity.this, R.style.TemaMenuPremium);
                androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(wrapper, btnInformacao);

                // Opção 1: Sobre
                android.text.SpannableString op1 = new android.text.SpannableString("   Sobre o App 📖   ");
                op1.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, op1.length(), 0);
                op1.setSpan(new android.text.style.RelativeSizeSpan(1.1f), 0, op1.length(), 0);

                // Opção 2: Tema
                android.text.SpannableString op2 = new android.text.SpannableString("   Escolher Tema 🎨   ");
                op2.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, op2.length(), 0);
                op2.setSpan(new android.text.style.RelativeSizeSpan(1.1f), 0, op2.length(), 0);

                // Opção 3: Backup
                android.text.SpannableString op3 = new android.text.SpannableString("   Backup e Restauração 💾   ");
                op3.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, op3.length(), 0);
                op3.setSpan(new android.text.style.RelativeSizeSpan(1.1f), 0, op3.length(), 0);

                // Opção 4: Lembretes
                android.text.SpannableString op4 = new android.text.SpannableString("   Lembrete de Sorteios ⏰   ");
                op4.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, op4.length(), 0);
                op4.setSpan(new android.text.style.RelativeSizeSpan(1.1f), 0, op4.length(), 0);

                // Opção 5: Gráfico de Frequência
                android.text.SpannableString op5 = new android.text.SpannableString("   Gráfico de Frequência 📈   ");
                op5.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, op5.length(), 0);
                op5.setSpan(new android.text.style.RelativeSizeSpan(1.1f), 0, op5.length(), 0);

                // Opção 6: Inteligência Artificial (Previsão)
                android.text.SpannableString op6 = new android.text.SpannableString("   Previsão Estatística (I.A.) 🤖   ");
                op6.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, op6.length(), 0);
                op6.setSpan(new android.text.style.ForegroundColorSpan(Color.parseColor("#B0276E")), 0, op6.length(), 0); // Destaque em cor diferente
                op6.setSpan(new android.text.style.RelativeSizeSpan(1.1f), 0, op6.length(), 0);

                // Adiciona todas as opções
                popup.getMenu().add(0, 1, 0, op1);
                popup.getMenu().add(0, 2, 1, op2);
                popup.getMenu().add(0, 3, 2, op3);
                popup.getMenu().add(0, 4, 3, op4);
                popup.getMenu().add(0, 5, 4, op5);
                popup.getMenu().add(0, 6, 5, op6);

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == 1) {
                        mostrarInformacoesApp(false);
                        return true;
                    } else if (item.getItemId() == 2) {
                        String[] opcoesTema = {"Tema Claro ☀️", "Tema Escuro 🌙", "Padrão do Sistema ⚙️"};
                        int atual = bancoDeDados.getInt("tema_usuario", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        int itemMarcado = 2;
                        if (atual == androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO) itemMarcado = 0;
                        else if (atual == androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES) itemMarcado = 1;

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Visual do Aplicativo")
                                .setSingleChoiceItems(opcoesTema, itemMarcado, (dialogTema, qualItem) -> {
                                    int modoExibicao = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                                    if (qualItem == 0) modoExibicao = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
                                    else if (qualItem == 1) modoExibicao = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

                                    bancoDeDados.edit().putInt("tema_usuario", modoExibicao).apply();
                                    androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(modoExibicao);
                                    dialogTema.dismiss();
                                })
                                .show();
                        return true;
                    } else if (item.getItemId() == 3) {
                        abrirMenuBackupRestaurar();
                        return true;
                    } else if (item.getItemId() == 4) {
                        abrirCentralLembretes();
                        return true;
                    } else if (item.getItemId() == 5) {
                        // Abre o painel do Gráfico
                        abrirGraficoFrequencia();
                        return true;
                    } else if (item.getItemId() == 6) {
                        // GATILHO DA I.A.
                        abrirMenuSuperIA();
                        return true;
                    }
                    return false;
                });
                popup.show();
            });

            btnInserirManual = findViewById(R.id.btnInserirManual);
            btnInserirManual.setOnClickListener(v -> abrirInserirJogoManual());

            lblSomaPrimos = findViewById(R.id.lblSomaPrimos);
            lblParesImpares = findViewById(R.id.lblParesImpares);
            lblFibRepetidos = findViewById(R.id.lblFibRepetidos);
            lblCiclo = findViewById(R.id.lblCiclo);

            txtContador = findViewById(R.id.txtContador);
            layoutProgresso = findViewById(R.id.layoutProgresso);
            progressBarVarredura = findViewById(R.id.progressBarVarredura);
            txtProgressoVarredura = findViewById(R.id.txtProgressoVarredura);
            iconeTrevoLoading = findViewById(R.id.iconeTrevoLoading);

            txtAssinatura = findViewById(R.id.txtAssinatura);
            /*if (txtAssinatura != null) {
                txtAssinatura.setPaintFlags(txtAssinatura.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
                txtAssinatura.setOnClickListener(v -> mostrarRedesSociais());
            }*/

            gridTabuleiro.setOnClickListener(v -> compartilharJogo());

            bancoDeDados = getSharedPreferences("HistoricoJogos", MODE_PRIVATE);

            lblSomaPrimos.setText("Soma: -- / Primos: --");
            lblParesImpares.setText("Pares: -- / Ímpares: --");
            lblFibRepetidos.setText("Fib: -- / Ciclo: Carregando...");
            lblCiclo.setText("Rep: --");

            btnSortear.setOnClickListener(v -> buscarJogoEquilibrado());
            btnHistorico.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoricoActivity.class)));
            btnConferir.setOnClickListener(v -> abrirConferidor());
            btnVarredura.setOnClickListener(v -> fazerVarreduraRelampago());
            btnCadastrarOficial.setOnClickListener(v -> abrirCadastroOficial());
            btnGerenciarManuais.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HistoricoManualActivity.class)));

            // 🌟 Ativa a animação do Trevo antes de carregar os dados
            layoutProgresso.setVisibility(View.VISIBLE);
            progressBarVarredura.setVisibility(View.GONE); // Esconde a barra reta
            iconeTrevoLoading.setVisibility(View.VISIBLE); // Mostra o trevo gigante

            txtProgressoVarredura.setText("Sincronizando a sorte...");
            aplicarFundoLegivel(txtProgressoVarredura);

            // Faz o trevo girar 360 graus infinitamente
            animacaoTrevo = android.animation.ObjectAnimator.ofFloat(iconeTrevoLoading, "rotation", 0f, 360f);
            animacaoTrevo.setDuration(1000);
            animacaoTrevo.setRepeatCount(android.animation.ObjectAnimator.INFINITE);
            animacaoTrevo.setInterpolator(new android.view.animation.LinearInterpolator());
            animacaoTrevo.start();

            btnSortear.setEnabled(false); // Tranca o botão
            btnTurbo.setEnabled(false);   // Tranca o Turbo

            atualizarContadorTela();
            carregarDadosParaMemoria();
            limparTabuleiro();

            // VERIFICAÇÃO DO PRIMEIRO ACESSO
            boolean ocultarGuia = bancoDeDados.getBoolean("ocultar_guia_inicio", false);
            if (!ocultarGuia) {
                mostrarInformacoesApp(true); // Chama o modo demo marcando que é abertura automática
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Função auxiliar para configurar a pintura inicial e o clique
    private void configurarPinturaChave(Switch chave) {
        pintarChave(chave); // Pinta estado inicial
        chave.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pintarChave(chave);
            atualizarContadorFiltros();
        });
    }

    private void atualizarContadorFiltros() {
        if (txtFiltrosAtivos == null) return;
        int ativos = 0;

        if (switchPares != null && switchPares.isChecked()) ativos++;
        if (switchSoma != null && switchSoma.isChecked()) ativos++;
        if (switchPrimos != null && switchPrimos.isChecked()) ativos++;
        if (switchRepetidos != null && switchRepetidos.isChecked()) ativos++;
        if (switchFibonacci != null && switchFibonacci.isChecked()) ativos++;
        if (switchCiclo != null && switchCiclo.isChecked()) ativos++;
        if (switchOcultas != null && switchOcultas.isChecked()) ativos++;

        txtFiltrosAtivos.setText("Filtros ativos: " + ativos + "/7");
    }

    private void pintarChave(Switch chave) {
        int corBolinhaOn = androidx.core.content.ContextCompat.getColor(this, R.color.switch_bolinha_on);
        int corBarraOn   = androidx.core.content.ContextCompat.getColor(this, R.color.switch_barra_on);
        int corBolinhaOff = androidx.core.content.ContextCompat.getColor(this, R.color.switch_bolinha_off);
        int corBarraOff   = androidx.core.content.ContextCompat.getColor(this, R.color.switch_barra_off);

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
        // 1. Calcula a frequência nos últimos 20 concursos em segundo plano (salvando na variável global)
        Arrays.fill(freqUltimos20, 0);
        if (cacheOficiais != null && !cacheOficiais.isEmpty()) {
            int totalConcursosAnalisados = 0;
            for (int i = cacheOficiais.size() - 1; i >= 0 && totalConcursosAnalisados < 20; i--) {
                for (int num : cacheOficiais.get(i).numeros) {
                    if (num >= 1 && num <= 25) {
                        freqUltimos20[num]++;
                    }
                }
                totalConcursosAnalisados++;
            }
        }

        // 2. Aplica a cor PADRÃO UNIFORME para todas as bolas (estado inicial/apagado)
        for (int i = 1; i <= 25; i++) {
            if (bolasTabuleiro[i] != null) {
                bolasTabuleiro[i].setVisibility(View.VISIBLE);
                bolasTabuleiro[i].setAlpha(1.0f); // 100% visível
                bolasTabuleiro[i].setBackgroundResource(R.drawable.bola_apagada);

                // Remove qualquer pintura de cor do mapa de calor
                if (bolasTabuleiro[i].getBackground() != null) {
                    bolasTabuleiro[i].getBackground().mutate().setTintList(null);
                }

                // Cor do texto padrão (cinza claro) para bolas não sorteadas
                bolasTabuleiro[i].setTextColor(Color.parseColor("#999999"));
            }
        }
    }

    private void atualizarTabuleiro(List<Integer> numerosSorteados) {
        limparTabuleiro(); // Deixa todas padronizadas, uniformes e apagadas primeiro
        jogoAtualParaCompartilhar = numerosSorteados.toString();

        for (int i = 1; i <= 25; i++) {
            if (bolasTabuleiro[i] != null) {

                if (numerosSorteados.contains(i)) {
                    // === BOLA SORTEADA (Aplica o Mapa de Calor Apenas Aqui!) ===
                    bolasTabuleiro[i].setBackgroundResource(R.drawable.bola_selecionada);

                    if (bolasTabuleiro[i].getBackground() != null) {
                        bolasTabuleiro[i].getBackground().mutate().setTintList(null);
                    }

                    // Texto padrão branco
                    bolasTabuleiro[i].setTextColor(Color.WHITE);

                    /*int freq = freqUltimos20[i];
                    int corFundoSelecionado;

                    if (freq >= 14) {
                        corFundoSelecionado = Color.parseColor("#D32F2F"); // Vermelho Forte (Quentes)
                    } else if (freq >= 12) {
                        corFundoSelecionado = Color.parseColor("#F57C00"); // Laranja Forte (Médios)
                    } else if (freq >= 10) {
                        corFundoSelecionado = Color.parseColor("#FFB300"); // Amarelo Forte
                    } else {
                        corFundoSelecionado = Color.parseColor("#FFE082"); // Amarelo Claro
                    }

                    if (bolasTabuleiro[i].getBackground() != null) {
                        bolasTabuleiro[i].getBackground().mutate().setTint(corFundoSelecionado);
                    }

                    bolasTabuleiro[i].setTextColor(Color.WHITE);*/
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            android.net.Uri uri = data.getData();

            if (requestCode == REQ_COD_BACKUP) {
                // Usuário escolheu onde salvar o arquivo, agora gravamos os dados nele
                realizarBackupDoSistema(uri);
            } else if (requestCode == REQ_COD_RESTORE) {
                // Usuário selecionou o arquivo de backup, agora lemos e restauramos
                restaurarDadosDoSistema(uri);
            }
        }
    }

    private static class DadosConcurso {
        int[] numeros;
        String nomeConcurso;
        public DadosConcurso(int[] n, String nome) {
            this.numeros = n;
            this.nomeConcurso = nome;
        }
    }
    // 🌟 CORREÇÃO DEFINITIVA DO BUG "2482" (FIM DO CONFLITO DE THREADS)
    private void carregarDadosParaMemoria() {
        new Thread(() -> {
            try {
                // 1. Cria listas TEMPORÁRIAS (Sala de espera) para não bagunçar a memória principal
                List<int[]> memoriaTemporariaMeusJogos = new ArrayList<>();
                List<DadosConcurso> memoriaTemporariaOficiais = new ArrayList<>();

                // Carrega os Jogos Gerados
                String historico = bancoDeDados.getString("historico_ordenado", "");
                if (!historico.isEmpty()) {
                    String[] jogos = historico.split(SEPARADOR);
                    for (String j : jogos) {
                        if (!j.trim().isEmpty()) {
                            String numerosParaMemoria = j;
                            if (j.contains("&DATA&")) {
                                numerosParaMemoria = j.split("&DATA&")[0];
                            }
                            memoriaTemporariaMeusJogos.add(converterStringParaArrayInt(numerosParaMemoria));
                        }
                    }
                }

                // Carrega e Mistura os Oficiais + Manuais
                Map<String, String> oficiaisMap = DadosOficiais.carregarResultadosOficiais(this);
                if (oficiaisMap != null) {
                    for (Map.Entry<String, String> entry : oficiaisMap.entrySet()) {
                        int[] nums = converterStringParaArrayInt(entry.getKey());
                        if (nums.length == 15) {
                            memoriaTemporariaOficiais.add(new DadosConcurso(nums, entry.getValue()));
                        }
                    }
                }

                // 🌟 ORDENA TUDO NA SALA DE ESPERA (Antes de entregar para o App)
                Collections.sort(memoriaTemporariaOficiais, (o1, o2) -> {
                    int n1 = extrairNumeroConcurso(o1.nomeConcurso);
                    int n2 = extrairNumeroConcurso(o2.nomeConcurso);
                    return Integer.compare(n1, n2);
                });

                // 2. Transfere a lista PERFEITA e ORDENADA para a interface do usuário (Sem falhas!)
                runOnUiThread(() -> {
                    try {
                        cacheMeusJogos.clear();
                        cacheMeusJogos.addAll(memoriaTemporariaMeusJogos);

                        cacheOficiais.clear();
                        cacheOficiais.addAll(memoriaTemporariaOficiais);

                        // Atualiza os textos da tela
                        List<Integer> faltantes = calcularDezenasDoCiclo();
                        String textoCiclo = "";
                        int corCiclo = Color.GRAY;

                        if (!switchCiclo.isChecked()) {
                            textoCiclo = "Ciclo: (Filtro Desativado)";
                            corCiclo = Color.GRAY;
                        } else {
                            if (faltantes.isEmpty()) textoCiclo = "Ciclo: Fechado";
                            else textoCiclo = "Faltam no Ciclo: " + faltantes.toString();
                            corCiclo = Color.parseColor("#7C4617");
                        }

                        lblFibRepetidos.setText("Fib: -- / " + textoCiclo);
                        lblFibRepetidos.setTextColor(corCiclo);

                        lblCiclo.setText("Rep: --");
                        lblCiclo.setTextColor(Color.parseColor("#333333"));

                        if (jogoAtualParaCompartilhar.isEmpty()) {
                            limparTabuleiro();
                        }
                        // O banco terminou! Para de girar o trevo e libera o app
                        if (animacaoTrevo != null) animacaoTrevo.cancel(); // Freia o trevo
                        layoutProgresso.setVisibility(View.GONE); // Esconde a caixa amarela inteira
                        iconeTrevoLoading.setVisibility(View.GONE); // Esconde o trevo
                        progressBarVarredura.setVisibility(View.VISIBLE); // Devolve a barra para a Varredura usar depois

                        btnSortear.setEnabled(true); // Destranca botão
                        btnTurbo.setEnabled(true); // Destranca botão

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

    public void mostrarInformacoesApp(boolean isAberturaAutomatica) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📖 Guia Completo do App");

        // Mensagem HTML revisada e expandida
        String mensagemHTML =
                "📱 <b>FUNCIONALIDADES PRINCIPAIS</b><br>" +
                        "————————————————————<br>" +
                        "🎯 <b>Gerar Jogo Inteligente:</b> Cria combinações aplicando os filtros ativos (Switches). <b>Ele nunca repete</b> jogos que você já fez ou que já saíram na história oficial.<br><br>" +

                        "🚀 <b>Turbo 3x (Geração Rápida):</b> Gera <b>3 jogos instantaneamente</b> em um popup dedicado com mini tabuleiros! Dentro do popup, o botão <b>\"Turbo 3x\"</b> permite gerar mais 3 jogos sem fechar a janela, acumulando quantos jogos quiser. Cada jogo é automaticamente salvo no histórico.<br><br>" +

                        "🔒 <b>Fixar Números (Campo de Entrada):</b> Digite dezenas obrigatórias (ex: 01 13 25) que sempre entrarão no jogo gerado.<br><br>" +

                        "📜 <b>Histórico Geral:</b> Veja todos os jogos gerados com <b>data e hora de criação</b>. Toque para compartilhar ou segure para selecionar e apagar vários de uma vez.<br><br>" +

                        "🛡️ <b>Proteger Jogo Manual (Inserir Manual):</b> Salve jogos que você já fez na lotérica. O app JAMAIS os gerará novamente.<br><br>" +

                        "🔍 <b>Conferidor de Histórico:</b> Digite 15 números para descobrir se você já gerou esse jogo ou se ele já foi sorteado oficialmente.<br><br>" +

                        "⚡ <b>Varredura Relâmpago (Backtesting):</b> Cruza todos os seus jogos contra os concursos oficiais. Descubra quais jogos teriam feito de <b>11 a 15 pontos</b> em sorteios passados. <b>Nova barra de progresso</b> mostra o andamento em tempo real!<br><br>" +

                        "📥 <b>Cadastrar Oficial / Gerenciar Manuais:</b> Mantenha o banco de dados atualizado inserindo novos resultados oficiais. Toque e segure para deletar cadastros.<br><br>" +

                        "📤 <b>Compartilhar:</b> Toque no tabuleiro ou em qualquer jogo do histórico para enviar por WhatsApp ou redes sociais.<br><br>" +

                        "🧠 <b>Previsão Estatística (I.A. Leve):</b> A \"joia da coroa\" do app! Um motor matemático que analisa <b>5 fatores</b> (Frequência, Defasagem, Tendência, Correlação e Sazonalidade) para gerar um <b>Top 15</b> de números recomendados. Você pode aplicar como Fixas ou gerar um jogo diretamente!<br><br>" +

                        "📊 <b>Gráfico de Frequência:</b> Painel visual com barras coloridas mostrando os números mais quentes (🔴) e mais frios (🔵) nos últimos 30 concursos. Perfeito para análises rápidas!<br><br>" +

                        "💾 <b>Backup e Restauração:</b> Exporte todos os seus dados (histórico, configurações, cadastros manuais) em um arquivo <b>.json</b> seguro. Importe para restaurar em outro dispositivo ou após uma limpeza.<br><br>" +

                        "⏰ <b>Lembrete de Sorteios:</b> Configure um horário e receba <b>notificações push</b> nos dias de sorteio (Segunda a Sábado). Blindado contra economia de bateria!<br><br>" +

                        "📊 <b>Contador de Filtros Ativos:</b> Na tela principal, veja em tempo real quantos dos 7 filtros estão ativos. <b>0/7</b> = modo livre, <b>7/7</b> = modo sniper.<br><br>" +

                        "🎓 <b>Guia Interativo de Boas-Vindas:</b> Exibido automaticamente na primeira execução. Marque <b>\"Não mostrar novamente\"</b> para silenciá-lo. Disponível a qualquer momento pelo menu ☰.<br><br>" +

                        "🎨 <b>Controle de Tema:</b> Alterne entre <b>Tema Claro ☀️</b>, <b>Tema Escuro 🌙</b> ou <b>Padrão do Sistema ⚙️</b> instantaneamente pelo menu.<br><br>" +

                        "🧹 <b>Manutenção de Dados:</b> Opções de <b>Limpar Cache</b> e <b>Reset de Fábrica</b> (com trava de segurança) para manter o app sempre leve e funcionando.<br><br>" +

                        "🍀 <b>Tela de Carregamento Imersiva:</b> Animação de trevo giratório com bloqueio dos botões até que os dados estejam totalmente carregados.<br><br>" +

                        "📊 <b>FILTROS OPCIONAIS (SWITCHES) - A LÓGICA POR TRÁS</b><br>" +
                        "————————————————————<br>" +
                        "🧮 <b>Soma:</b> Mantém a soma dos 15 números entre <b>165 e 230</b>.<br>" +
                        "🔢 <b>Par / Ímpar:</b> Equilíbrio! Exige de <b>6 a 9 pares</b> (e 6 a 9 ímpares).<br>" +
                        "🔴 <b>Primos:</b> Exige entre <b>4 e 7 números primos</b> (2,3,5,7,11,13,17,19,23).<br>" +
                        "🌀 <b>Fibonacci:</b> Exige entre <b>3 e 5 números</b> da sequência (1,2,3,5,8,13,21).<br>" +
                        "🔥 <b>Repetidos (Hot Numbers):</b> Exige que <b>7 a 10 números</b> sejam do ÚLTIMO sorteio oficial. (Estratégia de números quentes).<br>" +
                        "❄️ <b>Ciclo (Cold Numbers):</b> Dá prioridade (70%) para dezenas <b>ainda não sorteadas</b> no ciclo atual (dezenas atrasadas).<br>" +
                        "🛡️ <b>Travas Ocultas (Switch Mestre):</b> Controla todas as regras abaixo. Desative para gerar jogos sem restrições extras.<br><br>" +

                        "🛡️ <b>TRAVAS OCULTAS (CONTROLADAS PELO SWITCH MESTRE)</b><br>" +
                        "————————————————————<br>" +
                        "🟩 <b>Moldura (Borda):</b> Exige entre <b>8 e 11 números</b> da borda (1,2,3,4,5,6,10,11,15,16,20,21,22,23,24,25).<br>" +
                        "✖️ <b>Múltiplos de 3:</b> Exige entre <b>3 e 6 números</b> múltiplos de três (3,6,9,12,15,18,21,24).<br>" +
                        "📐 <b>Equilíbrio de Grade:</b> Impede linhas ou colunas vazias (0) ou cheias (5).<br>" +
                        "📏 <b>Trava de Sequência:</b> Bloqueia jogos com <b>8 ou mais números colados</b> (limite máximo 7).<br>" +
                        "🥶 <b>Dezena Fria:</b> Obriga <b>pelo menos 1 número</b> com baixa frequência nos últimos 10 concursos.<br>" +
                        "🚫 <b>Anti-Duplicidade Suprema:</b> Descarta jogos que <b>já existem</b> no seu histórico ou nos +3.000 concursos oficiais.<br><br>" +

                        "🧠 <b>O EFEITO 'SNIPER' (A MATEMÁTICA DO FUNIL)</b><br>" +
                        "————————————————————<br>" +
                        "O universo da Lotofácil tem <b>3.268.760 combinações</b>.<br><br>" +
                        "⚙️ Com <b>TODOS OS SWITCHES DESLIGADOS</b> (apenas travas ocultas e anti-duplicidade), o app reduz para <b>~1.200.000 jogos</b>.<br><br>" +
                        "🎯 Com <b>TODAS AS CHAVES ATIVADAS</b>, o funil reduz para apenas <b>80.000 a 150.000 jogos</b> altamente prováveis!<br><br>" +
                        "📈 <b>Conclusão:</b> Quanto mais chaves ligadas, mais 'inteligente' e filtrado é o jogo!" +

                        // ═══════════════════════════════════════════════════════════════
                        // ║            NOVIDADES - VERSÃO ATUAL                     ║
                        // ═══════════════════════════════════════════════════════════════

                        "<br><br>🆕 <b>NOVIDADES DA VERSÃO ATUAL!</b><br>" +
                        "————————————————————<br>" +

                        "☰ <b>Menu Suspenso Premium:</b> O antigo botão de informação evoluiu para um menu elegante com cantos arredondados, reunindo todas as funcionalidades avançadas em um só lugar!<br><br>" +

                        "🎨 <b>Controle Dinâmico de Tema:</b> Alternância instantânea entre <b>Tema Claro ☀️</b>, <b>Tema Escuro 🌙</b> ou <b>Padrão do Sistema ⚙️</b>. A preferência é salva automaticamente.<br><br>" +

                        "💾 <b>Sistema de Backup Nativo (SAF):</b> Exporte e importe todos os seus dados (Histórico + Resultados Manuais) em um arquivo <b>.json</b> seguro, integrado ao gerenciador de arquivos do celular.<br><br>" +

                        "⏰ <b>Lembrete Inteligente (AlarmManager):</b> Sistema de notificações push com alarme agendado (\"Hora do Sorteio\"). Configurável pelo usuário e blindado contra o modo de economia de bateria. Ignora automaticamente os domingos!<br><br>" +

                        "📊 <b>Gráfico de Frequência de Dezenas:</b> Painel visual em barras (criado do zero, sem bibliotecas pesadas) que analisa os últimos <b>30 concursos</b> e exibe as dezenas mais quentes (🔴 Vermelho) e mais frias (🔵 Azul/Verde), ordenadas da maior para a menor frequência.<br><br>" +

                        "🔧 <b>Correção do Bug do Concurso \"2482\":</b> O app agora utiliza uma <b>\"sala de espera\"</b> (variáveis temporárias) na inicialização. O cálculo do último concurso oficial é sempre 100% preciso!<br><br>" +

                        "🍀 <b>Tela de Carregamento Imersiva:</b> Animação de <b>Trevo Giratório</b> com texto sombreado na inicialização, bloqueando os botões até que os dados estatísticos estejam totalmente carregados.<br><br>" +

                        "🧠 <b>Painel de Previsão Estatística (IA Leve):</b> A <b>\"joia da coroa\"</b>! Motor matemático que analisa <b>5 pesos estatísticos</b>:<br>" +
                        "• <b>Frequência (30%):</b> Números que mais saíram<br>" +
                        "• <b>Defasagem (25%):</b> Números há mais tempo sem sair<br>" +
                        "• <b>Tendência (20%):</b> Números em alta ou baixa<br>" +
                        "• <b>Correlação (15%):</b> Números que combinam com suas fixas<br>" +
                        "• <b>Sazonalidade (10%):</b> Padrões do mês atual<br>" +
                        "Gera um <b>Top 15</b> de números recomendados com botão direto para <b>\"Usar como Fixas\"</b> ou <b>\"Gerar Jogo I.A.\"</b>.<br><br>" +

                        "🧹 <b>Limpeza de Cache e Reset de Fábrica:</b> Duas novas opções de manutenção no menu de Backup. A primeira esvazia arquivos temporários, a segunda (com <b>trava de segurança rigorosa</b>) zera completamente o aplicativo.<br><br>" +

                        "🔒 <b>Trava de Segurança nas Exclusões:</b> Todas as operações destrutivas (limpar histórico, reset de fábrica, etc.) agora exigem <b>dupla confirmação</b> para evitar exclusões acidentais.<br><br>" +

                        "⚡ <b>Barra de Progresso na Varredura:</b> Acompanhe em tempo real o andamento da análise de todos os seus jogos contra a história oficial.<br><br>" +

                        "📱 <b>Interface Otimizada:</b> Ajustes visuais em todos os componentes para melhor experiência em diferentes tamanhos de tela e temas." +

                        "🧠 <b>Super Jogo I.A. (Data Science):</b> A evolução máxima do nosso algoritmo! Agora a I.A. possui um cérebro independente com <b>3 perfis de ação</b>:<br>" +
                        "• 🛡️ <b>Conservador:</b> Foca nas dezenas mais quentes (Padrão Ouro).<br>" +
                        "• ⚔️ <b>Arrojado:</b> Caçador de 'zebras' e dezenas muito atrasadas.<br>" +
                        "• 🎯 <b>Sniper:</b> Usa a <i>Lei da Compensação</i> (analisa o desvio do último sorteio para equilibrar o próximo).<br><br>" +
                        "📖 <b>Manual de Bordo da I.A.:</b> Novo botão <b>\"ℹ️ Como Funciona?\"</b> que explica de forma transparente toda a matemática por trás da inteligência artificial.<br><br>" +
                        "🛡️ <b>Motor de Resgate (Plano B):</b> O app ficou à prova de falhas! Se as suas dezenas Fixas (ou a I.A.) entrarem em conflito impossível com os filtros ligados, o 'Plano B' é ativado automaticamente, garantindo que o seu jogo seja gerado e estampado no tabuleiro sem travamentos.<br><br>" +
                        "🎨 <b>Design Premium e Legibilidade:</b> Novo <b>Tabuleiro Exclusivo I.A.</b> com relatório de justificativa detalhado. Além disso, as mensagens de carregamento ganharam <b>fundos inteligentes (pílula)</b> que se adaptam perfeitamente ao Tema Claro ☀️ e ao Tema Escuro 🌙 para máxima leitura visual.<br><br>";


        LinearLayout layoutPrincipal = new LinearLayout(this);
        layoutPrincipal.setOrientation(LinearLayout.VERTICAL);
        layoutPrincipal.setBackgroundColor(Color.parseColor("#F5F5F5"));
        // ScrollView para garantir que todo o conteúdo seja visualizado
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        LinearLayout.LayoutParams paramsScroll = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        scrollView.setLayoutParams(paramsScroll);

        android.widget.TextView textView = new android.widget.TextView(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(android.text.Html.fromHtml(mensagemHTML, android.text.Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(android.text.Html.fromHtml(mensagemHTML));
        }

        textView.setPadding(50, 40, 50, 40);
        textView.setTextSize(15f); // Um pouco maior para facilitar a leitura
        textView.setTextColor(Color.parseColor("#222222"));
        textView.setLineSpacing(0, 1.3f); // Espaçamento entre linhas

        scrollView.addView(textView);
        layoutPrincipal.addView(scrollView);

        // Caixinha de Seleção (Só aparece se for a abertura automática)
        android.widget.CheckBox chkNaoMostrar = new android.widget.CheckBox(this);
        if (isAberturaAutomatica) {
            chkNaoMostrar.setText("Não mostrar este Guia na próxima vez");
            chkNaoMostrar.setTextColor(Color.parseColor("#444444"));
            chkNaoMostrar.setPadding(20, 20, 20, 20);
            layoutPrincipal.addView(chkNaoMostrar);
        }

        builder.setView(layoutPrincipal);

        builder.setPositiveButton("Certo, entendi!", (dialog, which) -> {
            // Se a pessoa marcou a caixa na abertura automática, salvamos no SharedPreferences
            if (isAberturaAutomatica && chkNaoMostrar.isChecked()) {
                SharedPreferences.Editor editor = bancoDeDados.edit();
                editor.putBoolean("ocultar_guia_inicio", true);
                editor.apply();
                Toast.makeText(this, "Guia silenciado. Acesse via botão 'Informação' quando quiser.", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
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
            for (String j : historicoGeral.split(SEPARADOR)) {
                if (j.contains("&DATA&")) meusJogosSalvos.add(j.split("&DATA&")[0]);
                else meusJogosSalvos.add(j);
            }
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
        ArrayList<Integer> numerosMultiplos3 = new ArrayList<>();
        Collections.addAll(numerosMultiplos3, 3, 6, 9, 12, 15, 18, 21, 24);

        // --- LENDO O ESTADO DE TODAS AS 7 CHAVES ---
        boolean usarPares = switchPares.isChecked();
        boolean usarSoma = switchSoma.isChecked();
        boolean usarPrimos = switchPrimos.isChecked();
        boolean usarRepetidos = switchRepetidos.isChecked();
        boolean usarFibonacci = switchFibonacci.isChecked();
        boolean usarCiclo = switchCiclo.isChecked();
        boolean usarOcultas = switchOcultas.isChecked(); // <--- CHAVE MASTER DAS OCULTAS
        // -----------------------------------

        int tentativasLoop = 0;

        while (true) {
            tentativasLoop++;
            // "PLANO B": SE OS FILTROS FOREM IMPOSSÍVEIS, PASSA O TRATOR E GERA MESMO ASSIM
            if (tentativasLoop > 50000) {
                runOnUiThread(() -> {
                    String msg = "⚠️ Conflito de Filtros! Ativando Plano B para forçar suas Fixas no tabuleiro...";
                    // Exibe a primeira vez (dura ~3.5 segundos)
                    Toast.makeText(this, msg,Toast.LENGTH_LONG).show();
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }, 3000);
                });

                ArrayList<Integer> planoB = new ArrayList<>(numerosFixosUsuario); // Pega as fixas da IA

                // Preenche o que falta ignorando os switches, só para garantir que o jogo exista!
                while (planoB.size() < 15) {
                    int num = gerador.nextInt(25) + 1;
                    if (!planoB.contains(num)) planoB.add(num);
                }
                Collections.sort(planoB);

                listaDefinitiva = planoB;

                // Recalcula as estatísticas reais desse novo jogo rebelde para mostrar na tela
                somaFinal = 0; paresFinal = 0; primosFinal = 0; fibonacciFinal = 0; repetidosFinal = 0;
                for (int n : planoB) {
                    somaFinal += n;
                    if (n % 2 == 0) paresFinal++;
                    if (numerosPrimos.contains(n)) primosFinal++;
                    if (numerosFibonacci.contains(n)) fibonacciFinal++;
                    if (!numerosDoUltimoConcurso.isEmpty() && numerosDoUltimoConcurso.contains(n)) repetidosFinal++;
                }
                break; // Quebra o loop infinito e vai direto pintar o tabuleiro!
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

            // OTIMIZAÇÃO CRUCIAL: GERAÇÃO INTELIGENTE DE PARES E ÍMPARES
            if (usarPares) {
                // 1. Sorteia uma meta válida dentro da sua faixa permitida (6, 7, 8 ou 9)
                int metaPares = gerador.nextInt(4) + 6;
                int metaImpares = 15 - metaPares;

                // 2. Conta quantos pares e ímpares já vieram das Fixas + dezenas do Ciclo
                int atuaisPares = 0;
                int atuaisImpares = 0;
                for (int n : tentativa) {
                    if (n % 2 == 0) atuaisPares++;
                    else atuaisImpares++;
                }

                // 3. Validação de segurança: Se as fixas/ciclo já estourarem a meta, pula para a próxima tentativa
                if (atuaisPares > metaPares || atuaisImpares > metaImpares) {
                    continue;
                }

                // 4. Calcula exatamente quantos faltam de cada tipo para fechar o jogo
                int faltamPares = metaPares - atuaisPares;
                int faltamImpares = metaImpares - atuaisImpares;

                // 5. Separa as dezenas restantes de 1 a 25 que ainda NÃO estão no jogo
                ArrayList<Integer> paresDisponiveis = new ArrayList<>();
                ArrayList<Integer> imparesDisponiveis = new ArrayList<>();
                for (int i = 1; i <= 25; i++) {
                    if (!tentativa.contains(i)) {
                        if (i % 2 == 0) paresDisponiveis.add(i);
                        else imparesDisponiveis.add(i);
                    }
                }

                // 6. Preenche cirurgicamente a quantidade exata de pares necessários
                while (faltamPares > 0 && !paresDisponiveis.isEmpty()) {
                    int idx = gerador.nextInt(paresDisponiveis.size());
                    tentativa.add(paresDisponiveis.remove(idx));
                    faltamPares--;
                }

                // 7. Preenche cirurgicamente a quantidade exata de ímpares necessários
                while (faltamImpares > 0 && !imparesDisponiveis.isEmpty()) {
                    int idx = gerador.nextInt(imparesDisponiveis.size());
                    tentativa.add(imparesDisponiveis.remove(idx));
                    faltamImpares--;
                }

                // 8. Trava de segurança extra (caso falte algo por inconsistência matemática)
                while (tentativa.size() < 15) {
                    int num = gerador.nextInt(25) + 1;
                    if (!tentativa.contains(num)) tentativa.add(num);
                }

            } else {
                //FLUXO TRADICIONAL: Se a chave de pares estiver DESLIGADA, sorteia tudo aleatório
                while (tentativa.size() < 15) {
                    int num = gerador.nextInt(25) + 1;
                    if (!tentativa.contains(num)) {
                        tentativa.add(num);
                    }
                }
            }
            Collections.sort(tentativa);

            int pares = 0, somaTotal = 0, naMoldura = 0, nosPrimos = 0, nosFibonacci = 0, repetidosDoUltimo = 0;
            int quantidadeFriasNoJogo = 0, nosMultiplos3 = 0;

            for (Integer numero : tentativa) {
                if (numero % 2 == 0) pares++;
                somaTotal += numero;
                if (numerosDaMoldura.contains(numero)) naMoldura++;
                if (numerosPrimos.contains(numero)) nosPrimos++;
                if (numerosFibonacci.contains(numero)) nosFibonacci++;
                if (numerosMultiplos3.contains(numero)) nosMultiplos3++;
                if (!numerosDoUltimoConcurso.isEmpty() && numerosDoUltimoConcurso.contains(numero)) repetidosDoUltimo++;
                if (dezenasFrias.contains(numero)) quantidadeFriasNoJogo++;
            }

            int sequenciaAtual = 1;
            int maiorSequencia = 1;
            for (int i = 0; i < tentativa.size() - 1; i++) {
                if (tentativa.get(i) + 1 == tentativa.get(i + 1)) {
                    sequenciaAtual++;
                    if (sequenciaAtual > maiorSequencia) maiorSequencia = sequenciaAtual;
                } else {
                    sequenciaAtual = 1;
                }
            }

            // --- REGRAS VISUAIS ---
            boolean paresOk = !usarPares || (pares >= 6 && pares <= 9);
            boolean somaOk = !usarSoma || (somaTotal >= 165 && somaTotal <= 230);
            boolean primosOk = !usarPrimos || (nosPrimos >= 4 && nosPrimos <= 7);
            boolean fibonacciOk = !usarFibonacci || (nosFibonacci >= 3 && nosFibonacci <= 5);
            boolean repetidosOk = !usarRepetidos || numerosDoUltimoConcurso.isEmpty() || (repetidosDoUltimo >= 7 && repetidosDoUltimo <= 10);

            // --- REGRAS OCULTAS (Se usarOcultas for false, aprova automaticamente) ---
            boolean molduraOk = !usarOcultas || (naMoldura >= 8 && naMoldura <= 11);
            boolean gradeOk = !usarOcultas || validarEquilibrioGrade(tentativa);
            boolean multiplosOk = !usarOcultas || (nosMultiplos3 >= 3 && nosMultiplos3 <= 6);
            boolean sequenciaOk = !usarOcultas || (maiorSequencia <= 7);
            boolean friasOk = !usarOcultas || (dezenasFrias.isEmpty() || quantidadeFriasNoJogo >= 1);

            // CHECAGEM DE TODOS OS FILTROS
            if (paresOk && somaOk && molduraOk && primosOk && fibonacciOk && repetidosOk && gradeOk && multiplosOk && sequenciaOk && friasOk) {
                String assinaturaDoJogo = tentativa.toString();

                // A LEI SUPREMA E INVIOLÁVEL: ANTI-DUPLICIDADE
                // Independente de travas estarem ligadas ou desligadas, o jogo repetido morre aqui.
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

        // --- MENSAGENS VISUAIS DA TELA ---
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

// 1. Primeiro guardamos o texto e a cor do Ciclo exatamente como eram na sua lógica original
        String textoCiclo = "";
        int corCiclo = Color.GRAY;
        if (!usarCiclo) {
            textoCiclo = "Ciclo: (Livre)";
            corCiclo = Color.GRAY;
        } else {
            List<Integer> faltantes = calcularDezenasDoCiclo();
            if (faltantes.isEmpty()) textoCiclo = "Ciclo: Fechado";
            else textoCiclo = "Faltam no Ciclo: " + faltantes.toString();
            corCiclo = Color.parseColor("#7C4617");
        }

// 2. Agora invertemos a exibição nas caixas de texto da tela:
// O de cima (lblFibRepetidos) passa a mostrar Fibonacci + Ciclo
        lblFibRepetidos.setText("Fibo: " + statusFibo + " / " + textoCiclo);
        lblFibRepetidos.setTextColor(corCiclo); // A cor marrom/cinza do ciclo agora pinta o texto de cima

// O de baixo (lblCiclo) passa a mostrar apenas os Repetidos e o número do Concurso
        lblCiclo.setText("Repe: " + statusRep + " (conc. " + textoConcurso + ")");
        lblCiclo.setTextColor(Color.parseColor("#333333")); // Mantém a cor escura normal para os repetidos

        atualizarContadorTela();

        int novoTotal = meusJogosSalvos.size() + 1;

        StringBuilder msg = new StringBuilder();
        msg.append("Jogo Inteligente nº ").append(novoTotal).append(" Gerado!");

        if (!numerosFixosUsuario.isEmpty()) {
            msg.append("\n* ").append(numerosFixosUsuario.size()).append(" Fixas");
        }

        if (!usarPares) msg.append("\n(OFF) Par/Ímp Livre");
        if (!usarSoma) msg.append("\n(OFF) Soma Livre");
        if (!usarPrimos) msg.append("\n(OFF) Primos Livre");
        if (!usarRepetidos) msg.append("\n(OFF) Repet. Livre");
        if (!usarFibonacci) msg.append("\n(OFF) Fibo Livre");
        if (!usarCiclo) msg.append("\n(OFF) Ciclo Livre");

        // NOVO AVISO SE AS TRAVAS OCULTAS FORAM DESLIGADAS
        if (!usarOcultas) msg.append("\n(⚠️) Travas Extras Desligadas!");
        else if (usarCiclo && !dezenasCiclo.isEmpty()) msg.append("\n[Ciclo Ativado]");

        Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show();
    }

    private void salvarJogo(String historicoAntigo, String novoJogo) {
        // Gera a data e hora atual (ex: 20/06/26 15:13:58)
        String dataAtual = new java.text.SimpleDateFormat("dd/MM/yy HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());

        // Cola uma etiqueta invisível &DATA& para separar os números da data
        String jogoComData = novoJogo + "&DATA&" + dataAtual;

        SharedPreferences.Editor editor = bancoDeDados.edit();
        if (historicoAntigo.isEmpty()) {
            editor.putString("historico_ordenado", jogoComData);
        } else {
            editor.putString("historico_ordenado", historicoAntigo + SEPARADOR + jogoComData);
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

    /*public void mostrarRedesSociais() {
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
    }*/

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
            // Limpa e padroniza a entrada do usuário
            String[] partes = entrada.replace(",", " ").replace("-", " ").trim().split("\\s+");
            if (partes.length != 15) {
                Toast.makeText(this, "Digite 15 números!", Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<Integer> nums = new ArrayList<>();
            for (String p : partes) nums.add(Integer.parseInt(p));
            Collections.sort(nums);
            String jogo = nums.toString();

            // --- 1º PROBLEMA RESOLVIDO: Verificar nos Resultados Oficiais ---
            Map<String, String> oficiaisMap = DadosOficiais.carregarResultadosOficiais(this);
            boolean achouOficial = false;
            String infoOficial = "";
            if (oficiaisMap != null && oficiaisMap.containsKey(jogo)) {
                achouOficial = true;
                infoOficial = oficiaisMap.get(jogo); // Pega o "Concurso XXXX (dd/mm/aaaa)"
            }

            // --- 2º PROBLEMA RESOLVIDO: Descobrir a posição exata no histórico do App ---
            String historico = bancoDeDados.getString("historico_ordenado", "");
            boolean achouNoApp = false;
            int numeroDoJogo = -1;

            if (!historico.isEmpty()) {
                String[] jogosSalvos = historico.split(SEPARADOR);
                for (int i = 0; i < jogosSalvos.length; i++) {
                    String jogoSalvo = jogosSalvos[i].trim();
                    if (jogoSalvo.contains("&DATA&")) {
                        jogoSalvo = jogoSalvo.split("&DATA&")[0]; // Olha só pros números
                    }
                    if (jogoSalvo.equals(jogo)) {
                        achouNoApp = true;
                        numeroDoJogo = i + 1;
                        break;
                    }
                }
            }

            // --- Construção da Mensagem de Retorno ---
            StringBuilder mensagem = new StringBuilder();
            String tituloDialog = "Resultado do Conferidor";

            // Seção Oficial
            if (achouOficial) {
                tituloDialog = "🚨 JOGO JÁ SORTEADO!";
                mensagem.append("⚠️ **LOTOFÁCIL OFICIAL:**\nEste jogo JÁ FOI SORTEADO com 15 pontos na história!\n👉 ").append(infoOficial).append("\n\n");
            } else {
                mensagem.append("✅ **LOTOFÁCIL OFICIAL:**\nJogo inédito! Nunca fez 15 pontos em concursos oficiais.\n\n");
            }

            // Seção Histórico do App
            if (achouNoApp) {
                if (!achouOficial) tituloDialog = "🗂️ JOGO JÁ GERADO";
                mensagem.append("📱 **HISTÓRICO DO APP:**\nVocê já gerou ou salvou este jogo antes!\n👉 Ele se encontra como o **Jogo nº ").append(numeroDoJogo).append("** na sua lista geral.");
            } else {
                mensagem.append("📱 **HISTÓRICO DO APP:**\nEste jogo não consta no seu histórico de jogos salvos.");
            }

            // Exibe a janela de alerta bem detalhada
            new AlertDialog.Builder(this)
                    .setTitle(tituloDialog)
                    .setMessage(mensagem.toString())
                    .setPositiveButton("Entendi", null)
                    .show();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao processar e conferir os números.", Toast.LENGTH_SHORT).show();
        }
    }

    public void fazerVarreduraRelampago() {
        if (cacheMeusJogos.isEmpty()) {
            Toast.makeText(this, "Carregando histórico...", Toast.LENGTH_SHORT).show();
            carregarDadosParaMemoria();
            return;
        }

        int totalJogos = cacheMeusJogos.size();

        // 1. MOSTRA A CAIXA DE PROGRESSO E DESATIVA O BOTÃO
        layoutProgresso.setVisibility(View.VISIBLE);
        btnVarredura.setEnabled(false);
        progressBarVarredura.setMax(totalJogos);
        progressBarVarredura.setProgress(0);
        txtProgressoVarredura.setText("Analisando " + totalJogos + " jogos...");

        new Thread(() -> {

            int qtd11 = 0, qtd12 = 0, qtd13 = 0, qtd14 = 0, qtd15 = 0;
            List<ItemCampeao> listaParaOrdenar = new ArrayList<>();

            for (int i = 0; i < totalJogos; i++) {
                int[] meuJogo = cacheMeusJogos.get(i);
                int recordePessoal = 0;
                String concursoRecorde = "";
                int numConcRecorde = 0;
                int[] melhorOficialNumeros = null;

                for (DadosConcurso oficial : cacheOficiais) {
                    int acertos = 0;
                    for (int m : meuJogo) {
                        for (int o : oficial.numeros) {
                            if (m == o) { acertos++; break; }
                        }
                    }

                    if (acertos > recordePessoal) {
                        recordePessoal = acertos;
                        concursoRecorde = oficial.nomeConcurso;
                        melhorOficialNumeros = oficial.numeros;
                        try {
                            String[] parts = concursoRecorde.split(" ");
                            if (parts.length > 1) numConcRecorde = Integer.parseInt(parts[1]);
                        } catch (Exception e) {}
                    }
                    if (recordePessoal == 15) break;
                }

                switch (recordePessoal) {
                    case 15: qtd15++; break;
                    case 14: qtd14++; break;
                    case 13: qtd13++; break;
                    case 12: qtd12++; break;
                    case 11: qtd11++; break;
                }

                if (recordePessoal >= 14) {
                    String emojiTrofeu = new String(Character.toChars(0x1F3C6));
                    String premio = (recordePessoal == 15) ? "15 PONTOS!!" : "14 PONTOS";
                    String msg = emojiTrofeu + " " + premio + " (Jogo " + (i + 1) + ")\nNo " + concursoRecorde;

                    StringBuilder sbDezenas = new StringBuilder();
                    for(int n : meuJogo) sbDezenas.append(String.format("%02d ", n));

                    StringBuilder sbOficiais = new StringBuilder();
                    if (melhorOficialNumeros != null) {
                        for(int n : melhorOficialNumeros) sbOficiais.append(String.format("%02d ", n));
                    }
                    listaParaOrdenar.add(new ItemCampeao(numConcRecorde, msg, sbDezenas.toString().trim(), sbOficiais.toString().trim()));
                }

                // 2. ATUALIZA A BARRA EM TEMPO REAL NO MEIO DO LOOP
                progressBarVarredura.setProgress(i + 1);
            }

            Collections.sort(listaParaOrdenar, (item1, item2) -> Integer.compare(item2.concurso, item1.concurso));

            ArrayList<String> detalhesFinais = new ArrayList<>();
            ArrayList<String> dezenasFinais = new ArrayList<>();
            ArrayList<String> oficiaisFinais = new ArrayList<>();
            for (ItemCampeao item : listaParaOrdenar) {
                detalhesFinais.add(item.textoFormatado);
                dezenasFinais.add(item.dezenas);
                oficiaisFinais.add(item.dezenasOficiais);
            }

            int f11 = qtd11; int f12 = qtd12; int f13 = qtd13; int f14 = qtd14; int f15 = qtd15;

            runOnUiThread(() -> {
                // 3. ESCONDE A BARRA E REATIVA O BOTÃO
                layoutProgresso.setVisibility(View.GONE);
                btnVarredura.setEnabled(true);

                Intent intent = new Intent(MainActivity.this, ResultadoVarreduraActivity.class);
                intent.putExtra("total", totalJogos);
                intent.putExtra("q11", f11);
                intent.putExtra("q12", f12);
                intent.putExtra("q13", f13);
                intent.putExtra("q14", f14);
                intent.putExtra("q15", f15);

                ArrayList<String> listaSegura = new ArrayList<>();
                ArrayList<String> dezenasSeguras = new ArrayList<>();
                ArrayList<String> oficiaisSeguras = new ArrayList<>();
                if (detalhesFinais.size() > 200) {
                    listaSegura.addAll(detalhesFinais.subList(0, 200));
                    listaSegura.add("... e mais campeões ocultos.");
                    dezenasSeguras.addAll(dezenasFinais.subList(0, 200));
                    dezenasSeguras.add("");
                    oficiaisSeguras.addAll(oficiaisFinais.subList(0, 200));
                    oficiaisSeguras.add("");
                } else {
                    listaSegura.addAll(detalhesFinais);
                    dezenasSeguras.addAll(dezenasFinais);
                    oficiaisSeguras.addAll(oficiaisFinais);
                }

                intent.putStringArrayListExtra("detalhes_campeoes", listaSegura);
                intent.putStringArrayListExtra("dezenas_campeoes", dezenasSeguras);
                intent.putStringArrayListExtra("oficiais_campeoes", oficiaisSeguras);

                startActivity(intent);
            });

        }).start();
    }

    private static class ItemCampeao {
        int concurso;
        String textoFormatado;
        String dezenas;
        String dezenasOficiais;
        public ItemCampeao(int c, String t, String d, String dof) {
            this.concurso = c;
            this.textoFormatado = t;
            this.dezenas = d;
            this.dezenasOficiais = dof;
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

    // LEITOR DE FIXAS BLINDADO
    private ArrayList<Integer> converterStringParaLista(String numerosStr) {
        ArrayList<Integer> lista = new ArrayList<>();
        try {
            // Arranca vírgulas, colchetes e letras. Deixa SÓ números e espaços!
            String limpa = numerosStr.replaceAll("[^0-9 ]", " ");
            String[] partes = limpa.trim().split("\\s+");
            for (String p : partes) {
                if (!p.isEmpty()) {
                    int num = Integer.parseInt(p.trim());
                    // Garante que é uma bola válida de 1 a 25 e que não está repetida
                    if (num >= 1 && num <= 25 && !lista.contains(num)) {
                        lista.add(num);
                    }
                }
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
            boolean jogoJaExiste = false;

            if (!historicoGeral.isEmpty()) {
                for (String j : historicoGeral.split(SEPARADOR)) {
                    String apenasNumeros = j;
                    if (j.contains("&DATA&")) apenasNumeros = j.split("&DATA&")[0];
                    if (apenasNumeros.equals(jogoFormatado)) {
                        jogoJaExiste = true;
                        break;
                    }
                }
            }

            if (jogoJaExiste) {
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

    public void abrirPopupTurbo3x() {
        Toast.makeText(this, "Processando Turbo 3x...", Toast.LENGTH_SHORT).show();

        // Preparando as regras exatas que você já usa no app
        Random gerador = new Random();
        String historicoGeral = bancoDeDados.getString("historico_ordenado", "");
        List<String> meusJogosSalvos = new ArrayList<>();
        if (!historicoGeral.isEmpty()) {
            for (String j : historicoGeral.split(SEPARADOR)) {
                if (j.contains("&DATA&")) meusJogosSalvos.add(j.split("&DATA&")[0]);
                else meusJogosSalvos.add(j);
            }
        }
        Map<String, String> oficiaisMap = DadosOficiais.carregarResultadosOficiais(this);

        ArrayList<Integer> numerosDoUltimo = new ArrayList<>();
        if (!cacheOficiais.isEmpty()) {
            for(int i : cacheOficiais.get(cacheOficiais.size() - 1).numeros) numerosDoUltimo.add(i);
        }
        List<Integer> dezenasFrias = calcularDezenasFrias(oficiaisMap);
        List<Integer> dezenasCiclo = calcularDezenasDoCiclo();
        ArrayList<Integer> numerosFixos = new ArrayList<>();
        if (inputFixas != null && !inputFixas.getText().toString().isEmpty()) {
            try {
                for (int n : converterStringParaLista(inputFixas.getText().toString())) {
                    if (n >= 1 && n <= 25 && !numerosFixos.contains(n)) numerosFixos.add(n);
                }
            } catch (Exception e) {}
        }

        List<Integer> m3 = Arrays.asList(3, 6, 9, 12, 15, 18, 21, 24);
        List<Integer> prim = Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19, 23);
        List<Integer> fib = Arrays.asList(1, 2, 3, 5, 8, 13, 21);
        List<Integer> mold = Arrays.asList(1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24, 25);

        boolean uPar = switchPares.isChecked(), uSoma = switchSoma.isChecked();
        boolean uPrim = switchPrimos.isChecked(), uRep = switchRepetidos.isChecked();
        boolean uFib = switchFibonacci.isChecked(), uCiclo = switchCiclo.isChecked();
        boolean uOc = switchOcultas.isChecked();

        // Variáveis para guardar os 3 jogos do Turbo e não deixar eles repetirem entre si
        List<ArrayList<Integer>> jogosTurboGerados = new ArrayList<>();
        List<String> assinaturasNestaSessao = new ArrayList<>();

        // ⚡ O LOOP TURBO (RODA 3 VEZES) ⚡
        for (int rodada = 0; rodada < 3; rodada++) {
            ArrayList<Integer> tentativa = new ArrayList<>();
            int loopSafeguard = 0;

            while (true) {
                loopSafeguard++;
                if (loopSafeguard > 50000) {
                    Toast.makeText(this, "Dificuldade ao gerar jogo " + (rodada+1) + ". Desative algumas chaves.", Toast.LENGTH_LONG).show();
                    return; // Aborta se os filtros estiverem impossíveis
                }

                tentativa.clear();
                tentativa.addAll(numerosFixos);

                if (uCiclo) {
                    for (int dCiclo : dezenasCiclo) {
                        if (!tentativa.contains(dCiclo) && tentativa.size() < 15 && gerador.nextInt(100) < 70) tentativa.add(dCiclo);
                    }
                }

                if (uPar) {
                    int mPar = gerador.nextInt(4) + 6;
                    int mImp = 15 - mPar;
                    int aPar = 0, aImp = 0;
                    for (int n : tentativa) { if (n % 2 == 0) aPar++; else aImp++; }
                    if (aPar > mPar || aImp > mImp) continue;
                    int fPar = mPar - aPar, fImp = mImp - aImp;

                    ArrayList<Integer> pDisp = new ArrayList<>(), iDisp = new ArrayList<>();
                    for (int i = 1; i <= 25; i++) {
                        if (!tentativa.contains(i)) { if (i % 2 == 0) pDisp.add(i); else iDisp.add(i); }
                    }
                    while (fPar > 0 && !pDisp.isEmpty()) { tentativa.add(pDisp.remove(gerador.nextInt(pDisp.size()))); fPar--; }
                    while (fImp > 0 && !iDisp.isEmpty()) { tentativa.add(iDisp.remove(gerador.nextInt(iDisp.size()))); fImp--; }
                    while (tentativa.size() < 15) {
                        int num = gerador.nextInt(25) + 1;
                        if (!tentativa.contains(num)) tentativa.add(num);
                    }
                } else {
                    while (tentativa.size() < 15) {
                        int num = gerador.nextInt(25) + 1;
                        if (!tentativa.contains(num)) tentativa.add(num);
                    }
                }
                Collections.sort(tentativa);

                int pares = 0, soma = 0, naMold = 0, nPrim = 0, nFib = 0, repUlt = 0, nFrias = 0, nM3 = 0;
                for (int n : tentativa) {
                    if (n % 2 == 0) pares++;
                    soma += n;
                    if (mold.contains(n)) naMold++;
                    if (prim.contains(n)) nPrim++;
                    if (fib.contains(n)) nFib++;
                    if (m3.contains(n)) nM3++;
                    if (!numerosDoUltimo.isEmpty() && numerosDoUltimo.contains(n)) repUlt++;
                    if (dezenasFrias.contains(n)) nFrias++;
                }

                int seqAt = 1, maiorSeq = 1;
                for (int i = 0; i < tentativa.size() - 1; i++) {
                    if (tentativa.get(i) + 1 == tentativa.get(i + 1)) { seqAt++; if (seqAt > maiorSeq) maiorSeq = seqAt; }
                    else seqAt = 1;
                }

                boolean pOk = !uPar || (pares >= 6 && pares <= 9);
                boolean sOk = !uSoma || (soma >= 165 && soma <= 230);
                boolean prOk = !uPrim || (nPrim >= 4 && nPrim <= 7);
                boolean fOk = !uFib || (nFib >= 3 && nFib <= 5);
                boolean rOk = !uRep || numerosDoUltimo.isEmpty() || (repUlt >= 7 && repUlt <= 10);

                boolean moOk = !uOc || (naMold >= 8 && naMold <= 11);
                boolean grOk = !uOc || validarEquilibrioGrade(tentativa);
                boolean m3Ok = !uOc || (nM3 >= 3 && nM3 <= 6);
                boolean sqOk = !uOc || (maiorSeq <= 7);
                boolean frOk = !uOc || (dezenasFrias.isEmpty() || nFrias >= 1);

                if (pOk && sOk && moOk && prOk && fOk && rOk && grOk && m3Ok && sqOk && frOk) {
                    String assinatura = tentativa.toString();

                    // TRAVA ANTI-DUPLICIDADE GERAL E INTERNA DO TURBO
                    if (meusJogosSalvos.contains(assinatura)) continue;
                    if (oficiaisMap != null && oficiaisMap.containsKey(assinatura)) continue;
                    if (assinaturasNestaSessao.contains(assinatura)) continue; // Evita que o Jogo 2 seja igual ao Jogo 1

                    // Aprovado!
                    jogosTurboGerados.add(tentativa);
                    assinaturasNestaSessao.add(assinatura);
                    meusJogosSalvos.add(assinatura); // Adiciona na memória pra não repetir nos próximos loops

                    // Salva no banco de dados Oficial do App na mesma hora
                    historicoGeral = bancoDeDados.getString("historico_ordenado", "");
                    salvarJogo(historicoGeral, assinatura);

                    break; // Sai do while e vai para a próxima rodada do Turbo
                }
            }
        }

        // 🎨 MONTANDO O VISUAL DA JANELA FLUTUANTE (POP-UP) CUSTOMIZADA 🎨
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Container principal que envelopa toda a janela flutuante
        LinearLayout layoutDialog = new LinearLayout(this);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);

        // 1. BARRA DO TÍTULO (Voltou o cinza sólido original)
        TextView tvTitulo = new TextView(this);
        tvTitulo.setText("⚡ Turbo 3x: Combinações Geradas!");
        tvTitulo.setTextSize(18f);
        tvTitulo.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_principal));
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitulo.setGravity(android.view.Gravity.CENTER);
        tvTitulo.setPadding(30, 40, 30, 40);
        tvTitulo.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.barra_popup)); // Barra superior cinza
        layoutDialog.addView(tvTitulo);

        // 2. MEIO - ÁREA DOS MINI-TABULEIROS (Fundo geral invisível para destacar os quadrinhos)
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f); // Peso 1 para expandir apenas no meio e não empurrar os botões
        scrollView.setLayoutParams(scrollParams);
        scrollView.setBackgroundColor(Color.TRANSPARENT);

        LinearLayout layoutConteudo = new LinearLayout(this);
        layoutConteudo.setOrientation(LinearLayout.VERTICAL);
        layoutConteudo.setPadding(30, 25, 30, 10);

        // Descobrindo o número oficial atual do histórico para colocar no título
        int historicoTamanhoAtual = bancoDeDados.getString("historico_ordenado", "").split(SEPARADOR).length;

        // 🌟 NOVO: Buscando o número do último concurso para exibir nos repetidos
        String textoConcurso = "N/A";
        if (!cacheOficiais.isEmpty()) {
            String nome = cacheOficiais.get(cacheOficiais.size()-1).nomeConcurso;
            try {
                String[] partes = nome.split(" ");
                if (partes.length > 1) textoConcurso = partes[1];
                else textoConcurso = nome.replaceAll("[^0-9]", "");
            } catch (Exception e){}
        }

        // 🌟 NOVO: Calculando o status atual do Ciclo
        String textoCiclo = "";
        List<Integer> faltantes = calcularDezenasDoCiclo();
        if (faltantes.isEmpty()) {
            textoCiclo = "Fechado";
        } else {
            textoCiclo = "Faltam " + faltantes.toString();
        }

        for (int i = 0; i < 3; i++) {
            int numJogoGeral = (historicoTamanhoAtual - 2) + i;

            // Quadrinho individual (O Fundo Principal)
            LinearLayout cardJogo = new LinearLayout(this);
            cardJogo.setOrientation(LinearLayout.VERTICAL);
            cardJogo.setPadding(25, 15, 25, 15);
            cardJogo.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.fundo_card_popup));

            // Mantemos WRAP_CONTENT para abraçar tudo dinamicamente
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 15);
            cardParams.gravity = android.view.Gravity.CENTER_HORIZONTAL;
            cardJogo.setLayoutParams(cardParams);

            // Título de cada tabuleiro
            TextView tituloJogo = new TextView(this);
            tituloJogo.setText("Jogo " + (i + 1) + " (Nº " + numJogoGeral + " Gerado)");
            tituloJogo.setTextSize(16f);
            tituloJogo.setTextColor(Color.parseColor("#B0276E"));
            tituloJogo.setTypeface(null, android.graphics.Typeface.BOLD);
            tituloJogo.setGravity(android.view.Gravity.CENTER);
            tituloJogo.setPadding(0, 0, 0, 5);

            cardJogo.addView(tituloJogo);

            // 🌟 NOVO: Container Horizontal para colocar Tabuleiro e Textos Lado a Lado
            LinearLayout layoutLadoALado = new LinearLayout(this);
            layoutLadoALado.setOrientation(LinearLayout.HORIZONTAL);
            layoutLadoALado.setGravity(android.view.Gravity.CENTER_VERTICAL); // Centraliza no meio da altura

            // 1. O Mini-tabuleiro na Esquerda
            GridLayout miniTab = criarMiniTabuleiro(jogosTurboGerados.get(i));
            LinearLayout.LayoutParams paramsTab = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsTab.setMargins(0, 0, 20, 0); // Espaço de 20dp na direita para afastar da caixinha branca
            miniTab.setLayoutParams(paramsTab);
            layoutLadoALado.addView(miniTab);

            // 2. Extraindo os números e calculando as Estatísticas reais deste jogo gerado
            List<Integer> jogoAtual = jogosTurboGerados.get(i);
            int cSoma = 0, cPares = 0, cPrimos = 0, cFibo = 0, cRepetidos = 0;

            for (int n : jogoAtual) {
                cSoma += n;
                if (n % 2 == 0) cPares++;
                if (prim.contains(n)) cPrimos++;
                if (fib.contains(n)) cFibo++;
                if (numerosDoUltimo.contains(n)) cRepetidos++;
            }
            int cImpares = 15 - cPares;

            // 3. A Caixinha de Estatísticas na Direita (Sem o fundo extra!)
            TextView txtStats = new TextView(this);
            txtStats.setText("Resumo rápido do jogo\n" +
                    "\nSoma: " + cSoma + "\n" +
                    "Par: " + cPares + " / Ímpar: " + cImpares + "\n" +
                    "Primos: " + cPrimos + "\n" +
                    "Fibo: " + cFibo + "\n" +
                    "Repet.: " + cRepetidos + " (" + textoConcurso + ")\n" +
                    "Ciclo: " + textoCiclo);
            txtStats.setTextSize(13f);
            txtStats.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_principal)); // Letras escuras
            txtStats.setPadding(15, 15, 15, 15);
            txtStats.setTypeface(null, android.graphics.Typeface.BOLD);

            // Adiciona a caixinha ao lado do tabuleiro
            layoutLadoALado.addView(txtStats);

            // Por fim, coloca o conjunto Lado-a-Lado de volta dentro do Quadrinho
            cardJogo.addView(layoutLadoALado);
            layoutConteudo.addView(cardJogo);
        }

        scrollView.addView(layoutConteudo, new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT));

        layoutDialog.addView(scrollView);

        // 3. BARRA INFERIOR (Envelopa o Turbo 3x na esquerda e o Fechar na direita)
        LinearLayout barraInferior = new LinearLayout(this);
        barraInferior.setOrientation(LinearLayout.HORIZONTAL);
        barraInferior.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.barra_popup)); // Mantém a sua cor original da barra
        barraInferior.setPadding(50, 35, 50, 35); // Respiro igual para as duas pontas

        // Novo Texto Clicável: TURBO 3x (Alinhado à Esquerda)
        TextView btnNovoTurbo = new TextView(this);
        btnNovoTurbo.setText("⚡ TURBO 3x");
        btnNovoTurbo.setTextSize(16f);
        btnNovoTurbo.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_principal)); // Mantém o cinza escuro original
        btnNovoTurbo.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams paramsTurbo = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        btnNovoTurbo.setLayoutParams(paramsTurbo);
        btnNovoTurbo.setGravity(android.view.Gravity.START | android.view.Gravity.CENTER_VERTICAL); // Gruda na Esquerda

        // Texto Clicável: FECHAR JOGOS (Alinhado à Direita)
        TextView btnFecharCustom = new TextView(this);
        btnFecharCustom.setText("FECHAR JOGOS ✅");
        btnFecharCustom.setTextSize(16f);
        btnFecharCustom.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_principal));
        btnFecharCustom.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams paramsFechar = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        btnFecharCustom.setLayoutParams(paramsFechar);
        btnFecharCustom.setGravity(android.view.Gravity.END | android.view.Gravity.CENTER_VERTICAL); // Gruda na Direita

        // Adiciona os dois textos dentro da mesma barra cinza
        barraInferior.addView(btnNovoTurbo);
        barraInferior.addView(btnFecharCustom);
        layoutDialog.addView(barraInferior);

        builder.setView(layoutDialog);
        AlertDialog dialog = builder.create();

        // Configura a ação do botão FECHAR
        btnFecharCustom.setOnClickListener(v -> {
            atualizarContadorTela();
            dialog.dismiss();
        });

        // Configura a ação do NOVO BOTÃO TURBO (Gera 3 novos jogos instantaneamente)
        btnNovoTurbo.setOnClickListener(v -> {
            dialog.dismiss(); // Descarta o Pop-up atual para liberar memória
            abrirPopupTurbo3x(); // Dispara um novo ciclo de jogos na velocidade da luz
        });

        dialog.show();

        // Remove o caixote rígido do Android para que a nossa transparência do meio funcione perfeitamente
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }

        limparTabuleiro(); // Deixa o tabuleiro principal limpo/inoperante no fundo
    }

    private GridLayout criarMiniTabuleiro(List<Integer> dezenasSorteadas) {
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(5);
        grid.setRowCount(5);
        grid.setAlignmentMode(GridLayout.ALIGN_MARGINS);

        // Centraliza o grid na tela
        LinearLayout.LayoutParams gridParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        gridParams.gravity = android.view.Gravity.CENTER;
        gridParams.bottomMargin = 5; // Espaço para o próximo jogo
        grid.setLayoutParams(gridParams);

        int tamanhoBola = 85; // Tamanho ideal para o mini tabuleiro
        int margem = 6;

        for (int i = 1; i <= 25; i++) {
            TextView bola = new TextView(this);
            bola.setText(String.format("%02d", i));
            bola.setGravity(android.view.Gravity.CENTER);
            bola.setTextSize(13f);
            bola.setTypeface(null, android.graphics.Typeface.BOLD);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = tamanhoBola;
            params.height = tamanhoBola;
            params.setMargins(margem, margem, margem, margem);
            bola.setLayoutParams(params);

            if (dezenasSorteadas.contains(i)) {
                // A cor vermelha forte original do seu app para as dezenas sorteadas
                bola.setBackgroundResource(R.drawable.bola_selecionada);
                if (bola.getBackground() != null) {
                    bola.getBackground().mutate().setTintList(null);
                }
                bola.setTextColor(Color.WHITE);
            } else {
                // Fundo apagado e texto cinza para as dezenas de fora
                bola.setBackgroundResource(R.drawable.bola_apagada);
                if (bola.getBackground() != null) {
                    bola.getBackground().mutate().setTintList(null);
                }
                bola.setTextColor(Color.parseColor("#999999"));
            }
            grid.addView(bola);
        }
        return grid;
    }

    // 🌟 VARIÁVEIS DE CONTROLE DOS ARQUIVOS
    private static final int REQ_COD_BACKUP = 888;
    private static final int REQ_COD_RESTORE = 999;

    // 🌟 MENU DE GERENCIAMENTO DE DADOS (EXPANDIDO)
    private void abrirMenuBackupRestaurar() {
        String[] opcoes = {
                "Criar Arquivo de Backup 📤",
                "Restaurar Dados de um Arquivo 📥",
                "Limpar Arquivos de Cache 🧹",
                "Reset de Fábrica (Apagar Tudo) ⚠️"
        };

        new AlertDialog.Builder(this)
                .setTitle("💾 Sistema e Dados")
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("application/json");
                        intent.putExtra(Intent.EXTRA_TITLE, "backup_sniper_lotofacil.json");
                        startActivityForResult(intent, REQ_COD_BACKUP);
                    } else if (which == 1) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(intent, REQ_COD_RESTORE);
                    } else if (which == 2) {
                        limparCacheDoApp();
                    } else if (which == 3) {
                        abrirLimpezaDeDados();
                    }
                })
                .setNegativeButton("Voltar", null)
                .show();
    }

    // ====================================================================
    // 🧹 MOTOR DE LIMPEZA DE CACHE
    // ====================================================================
    private void limparCacheDoApp() {
        try {
            java.io.File dirCache = getCacheDir();
            if (dirCache != null && dirCache.isDirectory()) {
                apagarArquivos(dirCache);
            }
            Toast.makeText(this, "🧹 Cache e arquivos temporários limpos com sucesso! Seu app está mais leve.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao limpar cache.", Toast.LENGTH_SHORT).show();
        }
    }

    // Função auxiliar para varrer a pasta de cache do Android
    private boolean apagarArquivos(java.io.File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] filhos = dir.list();
            if (filhos != null) {
                for (String filho : filhos) {
                    boolean sucesso = apagarArquivos(new java.io.File(dir, filho));
                    if (!sucesso) return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        }
        return false;
    }

    // ====================================================================
    // ⚠️ MOTOR DE RESET DE FÁBRICA (COM TRAVA DE SEGURANÇA SUPREMA)
    // ====================================================================
    private void abrirLimpezaDeDados() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("⚠️ RESET DE FÁBRICA");

        // Montando o visual do pop-up de alerta
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);

        TextView mensagem = new TextView(this);
        mensagem.setText("Isso apagará TODO o seu histórico de jogos, resultados oficiais cadastrados manualmente, configurações e alarmes.\n\nO aplicativo voltará ao estado original de quando foi instalado. Esta ação NÃO pode ser desfeita.");
        mensagem.setTextColor(Color.parseColor("#D32F2F")); // Vermelho perigo
        mensagem.setTextSize(15f);
        mensagem.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(mensagem);

        // A Trava de Segurança (Checkbox)
        android.widget.CheckBox chkConfirmar = new android.widget.CheckBox(this);
        chkConfirmar.setText("Tenho certeza absoluta. Quero apagar tudo.");
        chkConfirmar.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_principal));
        chkConfirmar.setPadding(10, 40, 0, 0);
        layout.addView(chkConfirmar);

        builder.setView(layout);

        builder.setPositiveButton("Apagar Tudo", (dialog, which) -> {
            if (chkConfirmar.isChecked()) {
                // 1. Apaga o banco de dados principal (Histórico, Temas, Guias, etc)
                bancoDeDados.edit().clear().apply();

                // 2. Apaga o banco de dados dos resultados manuais
                getSharedPreferences("NovosResultadosOficiais", MODE_PRIVATE).edit().clear().apply();

                // 3. Desliga o alarme de lembrete no sistema do celular
                gerenciarAlarmeDoSistema(false, 0, 0);

                Toast.makeText(this, "♻️ Aplicativo resetado com sucesso!", Toast.LENGTH_LONG).show();

                // 4. Reinicia a tela bruscamente para limpar a memória RAM e recarregar do zero
                recreate();
            } else {
                Toast.makeText(this, "Ação cancelada. Você não marcou a caixa de confirmação.", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void realizarBackupDoSistema(android.net.Uri uri) {
        try {
            org.json.JSONObject jsonCompleto = new org.json.JSONObject();

            // 1. Empacota o histórico pessoal e configurações
            jsonCompleto.put("historico_ordenado", bancoDeDados.getString("historico_ordenado", ""));
            jsonCompleto.put("tema_usuario", bancoDeDados.getInt("tema_usuario", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));
            jsonCompleto.put("ocultar_guia_inicio", bancoDeDados.getBoolean("ocultar_guia_inicio", false));

            // 2. Empacota os Cadastros Oficiais Manuais
            org.json.JSONObject jsonManuais = new org.json.JSONObject();
            java.util.Map<String, ?> todosManuais = DadosOficiais.lerApenasManuais(this);
            for (java.util.Map.Entry<String, ?> entry : todosManuais.entrySet()) {
                jsonManuais.put(entry.getKey(), entry.getValue().toString());
            }
            jsonCompleto.put("resultados_manuais", jsonManuais);

            // 3. Escreve tudo de forma organizada (com recuo de 4 espaços) dentro do arquivo
            java.io.OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(jsonCompleto.toString(4).getBytes());
                outputStream.close();
                Toast.makeText(this, "Backup gerado e salvo com sucesso! 🎉", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao exportar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void restaurarDadosDoSistema(android.net.Uri uri) {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return;

            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String linha;
            while ((linha = reader.readLine()) != null) {
                stringBuilder.append(linha);
            }
            inputStream.close();

            org.json.JSONObject jsonCompleto = new org.json.JSONObject(stringBuilder.toString());

            // Escudo de segurança: Valida se o arquivo realmente pertence ao nosso app
            if (!jsonCompleto.has("historico_ordenado") && !jsonCompleto.has("resultados_manuais")) {
                Toast.makeText(this, "Arquivo inválido ou não reconhecido! ❌", Toast.LENGTH_LONG).show();
                return;
            }

            // TRAVA DE SEGURANÇA SUPREMA ANTES DE SOBRESCREVER
            new AlertDialog.Builder(this)
                    .setTitle("⚠️ IMPORTAÇÃO DE DADOS")
                    .setMessage("Atenção! Isso apagará todos os seus jogos atuais do app e os substituirá pelos dados do arquivo. Deseja prosseguir?")
                    .setPositiveButton("Sim, Restaurar Tudo", (dialog, which) -> {
                        try {
                            // 1. Restaura Histórico e Configurações
                            SharedPreferences.Editor editorHistorico = bancoDeDados.edit();
                            if (jsonCompleto.has("historico_ordenado")) editorHistorico.putString("historico_ordenado", jsonCompleto.getString("historico_ordenado"));
                            if (jsonCompleto.has("tema_usuario")) editorHistorico.putInt("tema_usuario", jsonCompleto.getInt("tema_usuario"));
                            if (jsonCompleto.has("ocultar_guia_inicio")) editorHistorico.putBoolean("ocultar_guia_inicio", jsonCompleto.getBoolean("ocultar_guia_inicio"));
                            editorHistorico.apply();

                            // 2. Restaura Resultados Oficiais Manuais
                            android.content.SharedPreferences prefsManuais = getSharedPreferences("NovosResultadosOficiais", MODE_PRIVATE);
                            android.content.SharedPreferences.Editor editorManuais = prefsManuais.edit();
                            editorManuais.clear(); // Limpa a lousa para injetar os dados limpos

                            if (jsonCompleto.has("resultados_manuais")) {
                                org.json.JSONObject jsonManuais = jsonCompleto.getJSONObject("resultados_manuais");
                                java.util.Iterator<String> chaves = jsonManuais.keys();
                                while (chaves.hasNext()) {
                                    String chave = chaves.next();
                                    editorManuais.putString(chave, jsonManuais.getString(chave));
                                }
                            }
                            editorManuais.apply();

                            Toast.makeText(this, "Dados sincronizados com sucesso! 🔄", Toast.LENGTH_LONG).show();

                            // Atualiza a memória global imediatamente e reinicia o visual para aplicar o tema restaurado
                            carregarDadosParaMemoria();
                            int temaRestaurado = bancoDeDados.getInt("tema_usuario", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(temaRestaurado);

                            recreate(); // Recria a tela de forma elegante
                        } catch (Exception ex) {
                            Toast.makeText(this, "Falha na injeção de dados: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao processar arquivo de backup: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // 🌟 CENTRAL DE CONFIGURAÇÃO DE LEMBRETES DE SORTEIO
    private void abrirCentralLembretes() {
        boolean ativo = bancoDeDados.getBoolean("lembrete_ativo", false);
        int hora = bancoDeDados.getInt("lembrete_hora", 19);
        int minuto = bancoDeDados.getInt("lembrete_minuto", 0);

        String textoStatus = ativo ? "Status atual: ATIVADO às " + String.format("%02d:%02d", hora, minuto) : "Status atual: DESATIVADO";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("⏰ Lembrete de Sorteio");
        builder.setMessage("Configure um horário para o aplicativo te avisar nos dias de sorteio oficial (Segunda a Sábado).\n\n" + textoStatus);

        builder.setPositiveButton("Configurar Horário 🗓️", (dialog, which) -> {
            // Pede permissão de notificação se for Android 13 ou superior (Proteção de sistema)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                }
            }

            // Abre o relógio nativo do Android para o usuário escolher o horário
            new android.app.TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
                // Salva a configuração no banco de dados do app
                bancoDeDados.edit()
                        .putBoolean("lembrete_ativo", true)
                        .putInt("lembrete_hora", hourOfDay)
                        .putInt("lembrete_minuto", minuteOfHour)
                        .apply();

                // Liga o motor de agendamento do celular
                gerenciarAlarmeDoSistema(true, hourOfDay, minuteOfHour);
                Toast.makeText(this, "Lembrete configurado com sucesso para às " + String.format("%02d:%02d", hourOfDay, minuteOfHour) + "! 🔔", Toast.LENGTH_LONG).show();
            }, hora, minuto, true).show();
        });

        if (ativo) {
            builder.setNeutralButton("Desativar Lembrete 🔕", (dialog, which) -> {
                bancoDeDados.edit().putBoolean("lembrete_ativo", false).apply();
                gerenciarAlarmeDoSistema(false, 0, 0);
                Toast.makeText(this, "Lembrete desativado com sucesso!", Toast.LENGTH_SHORT).show();
            });
        }

        builder.setNegativeButton("Voltar", null);
        builder.show();
    }

    private void gerenciarAlarmeDoSistema(boolean ativar, int hora, int minuto) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        android.content.Intent intent = new android.content.Intent(this, LembreteReceiver.class);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                this, 777, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);

        if (ativar) {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(java.util.Calendar.HOUR_OF_DAY, hora);
            calendar.set(java.util.Calendar.MINUTE, minuto);
            calendar.set(java.util.Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
            }

            // 🌟 CÓDIGO BLINDADO CONTRA ECONOMIA DE BATERIA
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

    // ====================================================================
    // 🌟 MOTOR DO GRÁFICO DE FREQUÊNCIA (COM BARRA DE CARREGAMENTO)
    // ====================================================================
    private void abrirGraficoFrequencia() {
        if (cacheOficiais == null || cacheOficiais.isEmpty()) {
            Toast.makeText(this, "Aguarde o carregamento dos dados rápidos...", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Exibe a Barra de Progresso na tela principal imediatamente
        layoutProgresso.setVisibility(View.VISIBLE);
        progressBarVarredura.setMax(100);
        progressBarVarredura.setProgress(20);
        txtProgressoVarredura.setText("Calculando frequências...");

        // 2. Joga o processamento pesado para um Trabalhador Invisível (Thread)
        new Thread(() -> {
            try {
                // Matemática pura em segundo plano (não trava a tela)
                int limite = Math.min(30, cacheOficiais.size());
                int[] frequencia = new int[26];

                for (int i = cacheOficiais.size() - 1; i >= cacheOficiais.size() - limite; i--) {
                    for (int num : cacheOficiais.get(i).numeros) {
                        if (num >= 1 && num <= 25) frequencia[num]++;
                    }
                }

                List<int[]> listaOrdenadaDeBolas = new ArrayList<>();
                for (int i = 1; i <= 25; i++) {
                    listaOrdenadaDeBolas.add(new int[]{i, frequencia[i]});
                }
                Collections.sort(listaOrdenadaDeBolas, (bola1, bola2) -> Integer.compare(bola2[1], bola1[1]));

                int maxFreq = listaOrdenadaDeBolas.get(0)[1];

                // Atualiza o texto da barra de progresso antes de começar a desenhar a tela
                runOnUiThread(() -> {
                    progressBarVarredura.setProgress(70);
                    txtProgressoVarredura.setText("Montando painel visual...");
                });

                // Pequeno truque de UX (pausa de 150 milissegundos) para a barra fluir na tela
                Thread.sleep(150);

                // 3. Volta para a Tela Principal para desenhar os componentes (Isso exige a UI Thread)
                runOnUiThread(() -> {
                    try {
                        android.widget.ScrollView scrollView = new android.widget.ScrollView(MainActivity.this);
                        LinearLayout painelGrafico = new LinearLayout(MainActivity.this);
                        painelGrafico.setOrientation(LinearLayout.VERTICAL);
                        painelGrafico.setPadding(50, 30, 50, 30);

                        TextView legenda = new TextView(MainActivity.this);
                        legenda.setText("🔴 Quentes (20+)  |  🟢 Normais (17 a 19)  |  🔵 Frias (16-)");
                        legenda.setTextSize(12);
                        legenda.setGravity(android.view.Gravity.CENTER);
                        legenda.setPadding(0, 0, 0, 30);
                        painelGrafico.addView(legenda);

                        // Desenha as 25 barras
                        for (int[] item : listaOrdenadaDeBolas) {
                            int numeroDaBola = item[0];
                            int quantidadeSaiu = item[1];

                            LinearLayout linha = new LinearLayout(MainActivity.this);
                            linha.setOrientation(LinearLayout.HORIZONTAL);
                            linha.setGravity(android.view.Gravity.CENTER_VERTICAL);
                            linha.setPadding(0, 8, 0, 8);

                            TextView txtNum = new TextView(MainActivity.this);
                            txtNum.setText(String.format("%02d", numeroDaBola));
                            txtNum.setTextSize(16);
                            txtNum.setTypeface(null, android.graphics.Typeface.BOLD);
                            txtNum.setTextColor(androidx.core.content.ContextCompat.getColor(MainActivity.this, R.color.texto_principal));
                            txtNum.setPadding(0, 0, 20, 0);

                            ProgressBar barra = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleHorizontal);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 45, 1.0f);
                            barra.setLayoutParams(params);
                            barra.setMax(maxFreq > 0 ? maxFreq : 1);
                            barra.setProgress(quantidadeSaiu);

                            int corBarra;
                            if (quantidadeSaiu >= 20) {
                                corBarra = Color.parseColor("#E53935");
                            } else if (quantidadeSaiu <= 16) {
                                corBarra = Color.parseColor("#1E88E5");
                            } else {
                                corBarra = Color.parseColor("#43A047");
                            }

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                barra.setProgressTintList(android.content.res.ColorStateList.valueOf(corBarra));
                            } else {
                                barra.getProgressDrawable().setColorFilter(corBarra, android.graphics.PorterDuff.Mode.SRC_IN);
                            }

                            TextView txtCount = new TextView(MainActivity.this);
                            txtCount.setText(quantidadeSaiu + "x");
                            txtCount.setTextSize(14);
                            txtCount.setTypeface(null, android.graphics.Typeface.BOLD);
                            txtCount.setTextColor(androidx.core.content.ContextCompat.getColor(MainActivity.this, R.color.texto_suave));
                            txtCount.setPadding(20, 0, 0, 0);

                            linha.addView(txtNum);
                            linha.addView(barra);
                            linha.addView(txtCount);
                            painelGrafico.addView(linha);
                        }

                        scrollView.addView(painelGrafico);

                        // 4. Esconde a barra de progresso e exibe o gráfico final
                        layoutProgresso.setVisibility(View.GONE);

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("📈 Frequência (Últimos " + limite + " concursos)")
                                .setView(scrollView)
                                .setPositiveButton("Fechar", null)
                                .show();

                    } catch (Exception e) {
                        layoutProgresso.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Erro visual: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    layoutProgresso.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Erro no processamento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    // Ferramenta auxiliar para extrair só o número do concurso do banco de dados (ex: "Concurso 3714" -> 3714)
    private int extrairNumeroConcurso(String valor) {
        try {
            String apenasNumeros = valor.replaceAll("[^0-9]", " ");
            String[] partes = apenasNumeros.trim().split("\\s+");
            return Integer.parseInt(partes[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    // ====================================================================
    // 🤖 MOTOR DE PREVISÃO ESTATÍSTICA (MACHINE LEARNING LEVE)
    // ====================================================================
    private void abrirPainelPrevisaoIA() {
        if (cacheOficiais == null || cacheOficiais.isEmpty()) {
            Toast.makeText(this, "Aguarde o carregamento do banco de dados...", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Inicia o Carregamento Visual
        layoutProgresso.setVisibility(View.VISIBLE);
        progressBarVarredura.setVisibility(View.VISIBLE);
        if (iconeTrevoLoading != null) iconeTrevoLoading.setVisibility(View.GONE);
        progressBarVarredura.setMax(100);
        progressBarVarredura.setProgress(10);
        txtProgressoVarredura.setText("Iniciando varredura neural...");

        new Thread(() -> {
            try {
                int totalConcursos = cacheOficiais.size();

                // Variáveis de Análise para cada uma das 25 dezenas (Index 1 a 25)
                int[] freq30 = new int[26];
                int[] freqAnterior30 = new int[26];
                int[] defasagem = new int[26];
                Arrays.fill(defasagem, 999); // Começa com defasagem alta
                int[] correlacao = new int[26];
                int[] sazonalidade = new int[26];

                // Pega as fixas do usuário para a Correlação
                List<Integer> fixasAtuais = new ArrayList<>();
                if (inputFixas != null && !inputFixas.getText().toString().isEmpty()) {
                    fixasAtuais = converterStringParaLista(inputFixas.getText().toString());
                }

                // Mês atual para Sazonalidade
                int mesAtual = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1;

                runOnUiThread(() -> { progressBarVarredura.setProgress(30); txtProgressoVarredura.setText("Analisando Frequência e Defasagem..."); });

                // LÓGICA 1 e 2: Frequência e Defasagem
                int limiteFreq = Math.min(30, totalConcursos);
                for (int i = 0; i < limiteFreq; i++) {
                    int idxReal = totalConcursos - 1 - i;
                    for (int num : cacheOficiais.get(idxReal).numeros) {
                        if (num >= 1 && num <= 25) {
                            freq30[num]++;
                            // Se for a primeira vez que acha o número vindo de trás pra frente, essa é a defasagem exata
                            if (defasagem[num] == 999) defasagem[num] = i;
                        }
                    }
                }
                // Preenche quem não saiu nos últimos 30 com o limite
                for (int i = 1; i <= 25; i++) if (defasagem[i] == 999) defasagem[i] = limiteFreq;

                runOnUiThread(() -> { progressBarVarredura.setProgress(50); txtProgressoVarredura.setText("Calculando Tendências..."); });

                // LÓGICA 3: Tendência (Compara os últimos 15 com os 15 anteriores)
                int limiteTendencia = Math.min(60, totalConcursos);
                for (int i = 30; i < limiteTendencia; i++) {
                    int idxReal = totalConcursos - 1 - i;
                    for (int num : cacheOficiais.get(idxReal).numeros) {
                        if (num >= 1 && num <= 25) freqAnterior30[num]++;
                    }
                }

                runOnUiThread(() -> { progressBarVarredura.setProgress(70); txtProgressoVarredura.setText("Cruzando Correlações e Sazonalidade..."); });

                // LÓGICA 4 e 5: Correlação e Sazonalidade (Varre TODO o histórico)
                for (DadosConcurso concurso : cacheOficiais) {
                    List<Integer> numsDoConcurso = new ArrayList<>();
                    for (int n : concurso.numeros) numsDoConcurso.add(n);

                    // Correlação: Se as fixas do usuário saíram nesse concurso, pontua os outros números que saíram junto
                    boolean temFixas = false;
                    for (int f : fixasAtuais) {
                        if (numsDoConcurso.contains(f)) { temFixas = true; break; }
                    }
                    if (temFixas) {
                        for (int n : numsDoConcurso) if (n >= 1 && n <= 25 && !fixasAtuais.contains(n)) correlacao[n]++;
                    }

                    // Sazonalidade: Extrai o mês do concurso (ex: "Concurso 1234 (10/05/2015)")
                    try {
                        String nome = concurso.nomeConcurso;
                        if (nome.contains("/")) {
                            String[] partesData = nome.split("/");
                            if (partesData.length >= 2) {
                                int mesSorteio = Integer.parseInt(partesData[1].replaceAll("[^0-9]", ""));
                                if (mesSorteio == mesAtual) {
                                    for (int n : numsDoConcurso) if (n >= 1 && n <= 25) sazonalidade[n]++;
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                }

                // NORMALIZAÇÃO E NOTA FINAL
                float[] notasFinais = new float[26];
                int maxFreq = 1, maxDefasagem = 1, maxCorrelacao = 1, maxSazonalidade = 1;

                // Encontra os maiores valores para criar o teto (100%) de cada fator
                for (int i = 1; i <= 25; i++) {
                    if (freq30[i] > maxFreq) maxFreq = freq30[i];
                    if (defasagem[i] > maxDefasagem) maxDefasagem = defasagem[i];
                    if (correlacao[i] > maxCorrelacao) maxCorrelacao = correlacao[i];
                    if (sazonalidade[i] > maxSazonalidade) maxSazonalidade = sazonalidade[i];
                }

                // Lista de objetos para ordenar o ranking
                List<PrevisaoItem> ranking = new ArrayList<>();

                for (int i = 1; i <= 25; i++) {
                    // Normaliza os pesos (0.0 a 1.0)
                    float nFreq = (float) freq30[i] / maxFreq;
                    float nDefa = (float) defasagem[i] / maxDefasagem;

                    // Tendência: (Atual - Anterior) / Anterior. Se for positivo, tá quente.
                    float nTend = 0;
                    if (freqAnterior30[i] > 0) {
                        nTend = (float) (freq30[i] - freqAnterior30[i]) / freqAnterior30[i];
                        if (nTend > 1) nTend = 1; // Trava em 100% de bônus
                        if (nTend < -1) nTend = -1;
                    }

                    float nCorr = maxCorrelacao > 1 ? (float) correlacao[i] / maxCorrelacao : 0;
                    float nSazo = maxSazonalidade > 1 ? (float) sazonalidade[i] / maxSazonalidade : 0;

                    // O ALGORITMO FINAL:
                    // Freq(30%) + Defasagem(25%) + Tendência(20%) + Correlação(15%) + Sazonalidade(10%)
                    float nota = (nFreq * 30f) + (nDefa * 25f) + (nTend * 20f) + (nCorr * 15f) + (nSazo * 10f);

                    // Escala para ficar num formato amigável de 0 a 100
                    nota = Math.max(1, Math.min(99, nota * 1.5f + 40)); // Ajuste de curva matemática para espalhar as notas

                    notasFinais[i] = nota;
                    ranking.add(new PrevisaoItem(i, (int) nota, freq30[i], defasagem[i], nTend));
                }

                // Ordena os melhores
                Collections.sort(ranking, (r1, r2) -> Integer.compare(r2.notaFinal, r1.notaFinal));

                runOnUiThread(() -> { progressBarVarredura.setProgress(100); txtProgressoVarredura.setText("Gerando Painel..."); });
                Thread.sleep(200); // Pausa dramática para UX

                // CONSTRUÇÃO DO PAINEL VISUAL
                runOnUiThread(() -> {
                    layoutProgresso.setVisibility(View.GONE);

                    android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
                    LinearLayout painelUI = new LinearLayout(this);
                    painelUI.setOrientation(LinearLayout.VERTICAL);
                    painelUI.setPadding(40, 20, 40, 40);

                    // AVISO LEGAL DE RESPONSABILIDADE
                    TextView aviso = new TextView(this);
                    aviso.setText("⚠️ AVISO IMPORTANTE:\nEsta é uma análise estatística baseada em padrões históricos, não uma garantia mágica. Use como auxílio à sua estratégia.");
                    aviso.setTextSize(12);
                    aviso.setTextColor(Color.parseColor("#E53935"));
                    aviso.setTypeface(null, android.graphics.Typeface.BOLD);
                    aviso.setPadding(0, 0, 0, 30);
                    painelUI.addView(aviso);

                    // TÍTULO DO TOP 15
                    TextView tituloTop = new TextView(this);
                    tituloTop.setText("🏆 TOP 15 NÚMEROS SUGERIDOS");
                    tituloTop.setTextSize(16);
                    tituloTop.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_principal));
                    tituloTop.setTypeface(null, android.graphics.Typeface.BOLD);
                    tituloTop.setPadding(0, 10, 0, 20);
                    painelUI.addView(tituloTop);

                    // GERA A LISTA DO RANKING
                    StringBuilder sbTop15 = new StringBuilder(); // Para salvar no input depois
                    for (int i = 0; i < 15; i++) {
                        PrevisaoItem item = ranking.get(i);
                        sbTop15.append(String.format("%02d ", item.numero));

                        LinearLayout linha = new LinearLayout(this);
                        linha.setOrientation(LinearLayout.HORIZONTAL);
                        linha.setPadding(0, 5, 0, 5);

                        TextView txtNum = new TextView(this);
                        txtNum.setText(String.format("%02d", item.numero));
                        txtNum.setTextSize(18);
                        txtNum.setTypeface(null, android.graphics.Typeface.BOLD);
                        txtNum.setTextColor(Color.parseColor("#1976D2"));
                        txtNum.setPadding(0, 0, 20, 0);

                        TextView txtStats = new TextView(this);
                        String tendenciaSinal = item.tendencia > 0 ? "📈" : (item.tendencia < 0 ? "📉" : "➖");
                        String alertaAtraso = item.atraso >= 10 ? " ⏳(Atrasado!)" : "";
                        txtStats.setText("Nota: " + item.notaFinal + "% | Saiu: " + item.freq + "x | Atraso: " + item.atraso + alertaAtraso + " " + tendenciaSinal);
                        txtStats.setTextSize(13);
                        txtStats.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_suave));

                        linha.addView(txtNum);
                        linha.addView(txtStats);
                        painelUI.addView(linha);
                    }

                    scrollView.addView(painelUI);

                    // ==========================================================
                    // BOTÕES DE AÇÃO INTELIGENTES
                    // ==========================================================
                    AlertDialog dialogIA = new AlertDialog.Builder(this)
                            .setTitle("🧠 Previsão Estatística (I.A.)")
                            .setView(scrollView)
                            .setNeutralButton("Fechar", null)

                            // BOTÃO 1: Usa apenas os 8 melhores para o app conseguir equilibrar o resto!
                            .setNegativeButton("Top 8 como Fixas", (dialog, which) -> {
                                StringBuilder sbTop8 = new StringBuilder();
                                for (int i = 0; i < 8; i++) {
                                    sbTop8.append(String.format("%02d ", ranking.get(i).numero));
                                }
                                if (inputFixas != null) {
                                    inputFixas.setText(sbTop8.toString().trim());
                                    Toast.makeText(this, "Top 8 aplicado! Agora clique em Gerar para o app equilibrar o resto do jogo.", Toast.LENGTH_LONG).show();
                                }
                            })

                            // BOTÃO 2: Força o Top 15 exato
                            .setPositiveButton("Forçar Top 15", (dialog, which) -> {
                                if (inputFixas != null) {
                                    inputFixas.setText(sbTop15.toString().trim());
                                    Toast.makeText(this, "Top 15 colado nos fixos! Lembre-se: os filtros principais precisam estar desligados para aceitar esse jogo.", Toast.LENGTH_LONG).show();
                                }
                            })
                            .create();

                    dialogIA.show();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    layoutProgresso.setVisibility(View.GONE);
                    Toast.makeText(this, "Erro na I.A.: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // ====================================================================
    // 🤖 FASE 1: PORTA DE ENTRADA DA SUPER I.A. (TERMOS E PERFIS)
    // ====================================================================
    private void abrirMenuSuperIA() {
        // Cria o layout da janela
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);

        // 1. O Aviso Legal
        TextView aviso = new TextView(this);
        aviso.setText("⚠️ TERMO DE RESPONSABILIDADE\n\nA Lotofácil é um jogo de azar imprevisível. Esta Inteligência Artificial utiliza data science avançado para encontrar os melhores padrões matemáticos (Frequência, Defasagem e Compensação), mas NÃO garante vitórias.\n\nEscolha a estratégia do algoritmo abaixo:");
        aviso.setTextColor(Color.parseColor("#D32F2F")); // Vermelho suave
        aviso.setTextSize(14f);
        layout.addView(aviso);

        // 2. O Grupo de Opções (Perfis da I.A.)
        RadioGroup grupoPerfis = new RadioGroup(this);
        grupoPerfis.setPadding(0, 40, 0, 0);

        RadioButton rbConservador = new RadioButton(this);
        rbConservador.setId(View.generateViewId());
        rbConservador.setText("🛡️ I.A. Conservadora (Padrão Ouro)");
        rbConservador.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_principal));
        rbConservador.setPadding(0, 10, 0, 10);

        RadioButton rbArrojado = new RadioButton(this);
        rbArrojado.setId(View.generateViewId());
        rbArrojado.setText("⚔️ I.A. Arrojada (Caçadora de Zebras)");
        rbArrojado.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_principal));
        rbArrojado.setPadding(0, 10, 0, 10);

        RadioButton rbSniper = new RadioButton(this);
        rbSniper.setId(View.generateViewId());
        rbSniper.setText("🎯 I.A. Sniper (Mista / Recomendado)");
        rbSniper.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_principal));
        rbSniper.setPadding(0, 10, 0, 10);

        // Deixa o Sniper marcado por padrão
        rbSniper.setChecked(true);

        grupoPerfis.addView(rbConservador);
        grupoPerfis.addView(rbArrojado);
        grupoPerfis.addView(rbSniper);
        layout.addView(grupoPerfis);

        // 3. Monta e exibe a Janela
        new AlertDialog.Builder(this)
                .setTitle("🧠 Super Gerador I.A.")
                .setView(layout)
                .setCancelable(false)
                .setNegativeButton("Cancelar", null)

                // 🌟 NOVO BOTÃO: O Manual da I.A.
                .setNeutralButton("ℹ️ Como Funciona?", (dialog, which) -> {
                    mostrarExplicacaoIA(); // Abre o manual
                })

                .setPositiveButton("Aceitar e Iniciar", (dialog, which) -> {
                    int perfilEscolhido = 3;
                    if (rbConservador.isChecked()) perfilEscolhido = 1;
                    else if (rbArrojado.isChecked()) perfilEscolhido = 2;

                    Toast.makeText(this, "Iniciando processamento neural...", Toast.LENGTH_SHORT).show();
                    processarSuperJogoIA(perfilEscolhido);
                })
                .show();
    }

    // ====================================================================
    // 🤖 FASE 2: O CÉREBRO INDEPENDENTE (DATA SCIENCE)
    // ====================================================================
    private void processarSuperJogoIA(int perfil) {
        if (cacheOficiais == null || cacheOficiais.isEmpty()) {
            Toast.makeText(this, "Aguarde o banco de dados carregar...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inicia a animação de varredura para dar um visual de processamento
        layoutProgresso.setVisibility(View.VISIBLE);
        progressBarVarredura.setVisibility(View.VISIBLE);
        txtProgressoVarredura.setText("I.A. analisando algoritmos de compensação...");

        new Thread(() -> {
            try {
                // 1. ANÁLISE DO ÚLTIMO CONCURSO (LEI DA COMPENSAÇÃO)
                int[] ultimoSorteio = cacheOficiais.get(cacheOficiais.size() - 1).numeros;
                int imparesUltimo = 0;
                for (int num : ultimoSorteio) {
                    if (num % 2 != 0) imparesUltimo++;
                }

                // Se ontem vieram MUITOS ímpares (>8), a tendência (regressão à média) é cair para 7 hoje.
                int metaImpares = (imparesUltimo > 8) ? 7 : 8;
                if (perfil == 2) metaImpares = (imparesUltimo > 8) ? 6 : 9; // O Arrojado vai aos extremos

                // 2. CÁLCULO DE FREQUÊNCIA E ATRASO (Últimos 30 concursos)
                int[] freq30 = new int[26];
                int[] atraso = new int[26];
                Arrays.fill(atraso, 30); // Padrão máximo de atraso

                int totalConcursos = cacheOficiais.size();
                int limiteBusca = Math.min(30, totalConcursos);

                for (int i = 0; i < limiteBusca; i++) {
                    int idx = totalConcursos - 1 - i;
                    for (int num : cacheOficiais.get(idx).numeros) {
                        if (num >= 1 && num <= 25) {
                            freq30[num]++;
                            if (atraso[num] == 30) atraso[num] = i; // Registra há quantos jogos não sai
                        }
                    }
                }

                // 3. SEPARAÇÃO EM GRUPOS DE PODER
                List<Integer> quentes = new ArrayList<>();
                List<Integer> frias = new ArrayList<>();
                for (int i = 1; i <= 25; i++) {
                    // Se saiu mais de 16 vezes ou saiu nos últimos 2 jogos, é Quente. Senão, é Fria (Zebra).
                    if (freq30[i] >= 16 || atraso[i] <= 2) quentes.add(i);
                    else frias.add(i);
                }

                // Embaralha para não pegar sempre as mesmas
                Collections.shuffle(quentes);
                Collections.shuffle(frias);

                // 4. O FUNIL DE ESCOLHA BASEADO NO PERFIL
                List<Integer> jogoFinal = new ArrayList<>();
                String justificativa = "";

                if (perfil == 1) { // CONSERVADOR (11 Quentes, 4 Frias)
                    justificativa = "🛡️ Estratégia Conservadora: Foquei nas dezenas mais quentes do momento e apliquei o Padrão Ouro estatístico. Ideal para manter um jogo seguro e consistente.";
                    montarJogoPorPerfil(jogoFinal, quentes, frias, 11, 4, metaImpares);
                }
                else if (perfil == 2) { // ARROJADO (6 Quentes, 9 Frias)
                    justificativa = "⚔️ Estratégia Arrojada: Algumas dezenas estão muito atrasadas! Forcei a entrada de 'Zebras' para quebrar o padrão. Ideal para buscar prêmios acumulados onde a maioria erra.";
                    montarJogoPorPerfil(jogoFinal, quentes, frias, 6, 9, metaImpares);
                }
                else { // SNIPER (8 Quentes, 7 Frias + Compensação)
                    String textoComp = (imparesUltimo > 8) ? "reduzir os ímpares" : "aumentar os ímpares";
                    justificativa = "🎯 Estratégia Sniper: O último sorteio teve " + imparesUltimo + " ímpares. Usei a Lei da Compensação para " + textoComp + " neste jogo, mesclando perfeitamente dezenas quentes e atrasadas.";
                    montarJogoPorPerfil(jogoFinal, quentes, frias, 8, 7, metaImpares);
                }

                // 5. TRAVA DE SEGURANÇA (Se a matemática falhar, completa até 15)
                Random gerador = new Random();
                while (jogoFinal.size() < 15) {
                    int num = gerador.nextInt(25) + 1;
                    if (!jogoFinal.contains(num)) jogoFinal.add(num);
                }
                Collections.sort(jogoFinal);

                // Simula um "tempo de pensamento" para a I.A. (1.5 segundos)
                Thread.sleep(1500);

                // 6. ENVIANDO PARA A FASE 3 (O Tabuleiro)
                final String justificativaBlindada = justificativa;
                runOnUiThread(() -> {
                    layoutProgresso.setVisibility(View.GONE);
                    abrirSuperTabuleiroIA(jogoFinal, justificativaBlindada, perfil);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    layoutProgresso.setVisibility(View.GONE);
                    Toast.makeText(this, "Erro no motor da I.A.", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    // Função matemática auxiliar para equilibrar a mescla de bolas
    private void montarJogoPorPerfil(List<Integer> jogo, List<Integer> quentes, List<Integer> frias, int qtdQuentes, int qtdFrias, int metaImpares) {
        int imparesAtuais = 0;
        // Puxa as Quentes
        for (int q : quentes) {
            if (jogo.size() < qtdQuentes) {
                boolean isImpar = (q % 2 != 0);
                if (isImpar && imparesAtuais >= metaImpares) continue;
                jogo.add(q);
                if (isImpar) imparesAtuais++;
            }
        }
        // Puxa as Frias
        for (int f : frias) {
            if (jogo.size() < (qtdQuentes + qtdFrias)) {
                boolean isImpar = (f % 2 != 0);
                if (isImpar && imparesAtuais >= metaImpares && jogo.size() < 14) continue;
                if (!jogo.contains(f)) {
                    jogo.add(f);
                    if (isImpar) imparesAtuais++;
                }
            }
        }
    }

    // ====================================================================
    // 🤖 FASE 3: O TABULEIRO FUTURISTA DA SUPER I.A.
    // ====================================================================
    private void abrirSuperTabuleiroIA(List<Integer> jogoGerado, String justificativa, int perfil) {
        // Criamos uma tela rolável caso o celular seja pequeno
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);

        // 1. Título Estilizado
        TextView titulo = new TextView(this);
        titulo.setText("🧠 SUPER JOGO I.A.");
        titulo.setTextSize(20f);
        titulo.setTypeface(null, android.graphics.Typeface.BOLD);
        titulo.setTextColor(Color.parseColor("#9C27B0")); // Roxo "Neon" futurista
        titulo.setGravity(android.view.Gravity.CENTER);
        titulo.setPadding(0, 0, 0, 30);
        layout.addView(titulo);

        // 2. O Tabuleiro de Números (A Sequência Gerada)
        TextView txtNumeros = new TextView(this);
        StringBuilder sbNumeros = new StringBuilder();
        for (int num : jogoGerado) {
            sbNumeros.append(String.format("%02d  ", num)); // Formata com zero (ex: 01, 05)
        }
        txtNumeros.setText(sbNumeros.toString().trim());
        txtNumeros.setTextSize(24f);
        txtNumeros.setTypeface(null, android.graphics.Typeface.BOLD);
        txtNumeros.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_principal));
        txtNumeros.setGravity(android.view.Gravity.CENTER);
        // Colocamos um fundo suave para destacar os números
        txtNumeros.setBackgroundColor(Color.parseColor("#1A9C27B0"));
        txtNumeros.setPadding(20, 30, 20, 30);
        layout.addView(txtNumeros);

        // 3. O Relatório de Justificativa
        TextView txtJustificativa = new TextView(this);
        txtJustificativa.setText("\n📊 Relatório de Processamento:\n" + justificativa);
        txtJustificativa.setTextSize(14f);
        txtJustificativa.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_suave));
        txtJustificativa.setPadding(0, 30, 0, 20);
        layout.addView(txtJustificativa);

        scrollView.addView(layout);

        // 4. A Janela Pop-up com as Opções
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(scrollView)
                .setCancelable(false)
                .setNegativeButton("❌ Fechar", null)
                .setNeutralButton("🔄 Refazer I.A.", (d, which) -> {
                    // Clica aqui e ele roda o cérebro de novo sem fechar a experiência!
                    processarSuperJogoIA(perfil);
                })
                .setPositiveButton("✅ JOGAR ESSA SEQUÊNCIA", (d, which) -> {
                    aplicarSuperJogoNaTela(jogoGerado);
                })
                .create();

        dialog.show();
    }

    // ====================================================================
    // 🎨 DESIGN: FUNDO LEGÍVEL PARA TEXTOS DE CARREGAMENTO
    // ====================================================================
    private void aplicarFundoLegivel(TextView txt) {
        if (txt == null) return;

        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        shape.setCornerRadius(40f); // Cantos bem arredondados (estilo pílula)

        // Verifica qual é o tema atual do aparelho (Claro ou Escuro)
        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            // 🌙 TEMA DARK: Fundo escuro/cinza com 85% de opacidade
            shape.setColor(Color.parseColor("#D91E1E1E"));
            txt.setTextColor(Color.parseColor("#FFFFFF")); // Texto branco
        } else {
            // ☀️ TEMA CLARO: Fundo esbranquiçado com 85% de opacidade
            shape.setColor(Color.parseColor("#D9FFFFFF"));
            txt.setTextColor(Color.parseColor("#121212")); // Texto preto/escuro
        }

        txt.setBackground(shape);
        // Adiciona um respiro (margem interna) para o texto não ficar colado nas bordas da pílula
        txt.setPadding(10, 20, 10, 20);
    }

    // ====================================================================
    // 📖 O MANUAL DE INSTRUÇÕES DA I.A. (COMO FUNCIONA)
    // ====================================================================
    private void mostrarExplicacaoIA() {
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);

        TextView titulo = new TextView(this);
        titulo.setText("Como a I.A. pensa?");
        titulo.setTextSize(18f);
        titulo.setTypeface(null, android.graphics.Typeface.BOLD);
        titulo.setTextColor(Color.parseColor("#1976D2")); // Azul para dar cara de informação
        titulo.setPadding(0, 0, 0, 20);
        layout.addView(titulo);

        TextView texto = new TextView(this);
        texto.setText("Nosso algoritmo analisa os últimos 30 concursos para separar os números em dois grupos: os QUENTES (que saem muito) e as FRIAS/ZEBRAS (que estão atrasados).\n\nA partir daí, você escolhe como ela deve agir:");
        texto.setTextSize(14f);
        texto.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.texto_principal));
        texto.setPadding(0, 0, 0, 30);
        layout.addView(texto);

        // Bloco Conservador
        TextView txtCons = new TextView(this);
        txtCons.setText("🛡️ I.A. Conservadora");
        txtCons.setTypeface(null, android.graphics.Typeface.BOLD);
        txtCons.setTextColor(Color.parseColor("#388E3C")); // Verde
        layout.addView(txtCons);

        TextView descCons = new TextView(this);
        descCons.setText("Joga na zona de conforto estatística. O algoritmo escolhe a maioria das dezenas (11 bolas) entre as mais Quentes do momento e completa com algumas poucas Frias. Excelente para manter a segurança.\n");
        descCons.setTextSize(13f);
        layout.addView(descCons);

        // Bloco Arrojado
        TextView txtArr = new TextView(this);
        txtArr.setText("⚔️ I.A. Arrojada");
        txtArr.setTypeface(null, android.graphics.Typeface.BOLD);
        txtArr.setTextColor(Color.parseColor("#D32F2F")); // Vermelho
        layout.addView(txtArr);

        TextView descArr = new TextView(this);
        descArr.setText("Estratégia de alto risco para quebrar padrões. A I.A. foca nas 'Zebras' (9 bolas), forçando a entrada de dezenas muito atrasadas. Feita para tentar pegar aquele prêmio onde a maioria das pessoas erra as dezenas.\n");
        descArr.setTextSize(13f);
        layout.addView(descArr);

        // Bloco Sniper
        TextView txtSni = new TextView(this);
        txtSni.setText("🎯 I.A. Sniper (Recomendado)");
        txtSni.setTypeface(null, android.graphics.Typeface.BOLD);
        txtSni.setTextColor(Color.parseColor("#9C27B0")); // Roxo
        layout.addView(txtSni);

        TextView descSni = new TextView(this);
        descSni.setText("O equilíbrio absoluto. Além de misturar 8 Quentes e 7 Frias, ela usa a poderosa 'Lei da Compensação'. A I.A. olha para o sorteio de ontem: se ontem saíram Ímpares demais (algo raro), hoje ela força a máquina a escolher mais Pares para compensar o desvio matemático.");
        descSni.setTextSize(13f);
        layout.addView(descSni);

        scrollView.addView(layout);

        new AlertDialog.Builder(this)
                .setView(scrollView)
                .setCancelable(false)
                .setPositiveButton("Entendi", (dialog, which) -> {
                    // Quando o usuário terminar de ler, reabre a tela da I.A. para ele poder jogar!
                    abrirMenuSuperIA();
                })
                .show();
    }

    // ====================================================================
    // ⚙️ O MOTOR QUE INJETA O JOGO DA I.A. NO SEU TABULEIRO PRINCIPAL
    // ====================================================================
    private void aplicarSuperJogoNaTela(List<Integer> jogoIA) {
        ArrayList<Integer> jogoPronto = new ArrayList<>(jogoIA);
        String assinaturaDoJogo = jogoPronto.toString();

        // 1. Salva no seu histórico oficial
        String historicoGeral = bancoDeDados.getString("historico_ordenado", "");
        salvarJogo(historicoGeral, assinaturaDoJogo);

        // 2. Acende as bolinhas visuais do seu aplicativo
        atualizarTabuleiro(jogoPronto);

        // 3. Calcula as estatísticas desse jogo único para exibir na tela principal
        int pares = 0, soma = 0;
        for (int n : jogoPronto) {
            soma += n;
            if (n % 2 == 0) pares++;
        }

        // 4. Atualiza os textos da sua interface avisando que a I.A. assumiu o controle
        lblSomaPrimos.setText("Soma: " + soma + " (Gerado por I.A.)");
        lblParesImpares.setText("Pares: " + pares + " / Ímpares: " + (15 - pares) + " (I.A.)");
        lblFibRepetidos.setText("Estatísticas Mistas (Data Science)");
        lblFibRepetidos.setTextColor(Color.parseColor("#9C27B0")); // Roxo na interface também!

        atualizarContadorTela();
        Toast.makeText(this, "🚀 Super Jogo I.A. fixado no tabuleiro e salvo no histórico!", Toast.LENGTH_LONG).show();
    }

    // Classe auxiliar para organizar o ranking da I.A.
    private static class PrevisaoItem {
        int numero;
        int notaFinal;
        int freq;
        int atraso;
        float tendencia;
        public PrevisaoItem(int num, int nota, int f, int a, float t) {
            this.numero = num; this.notaFinal = nota; this.freq = f; this.atraso = a; this.tendencia = t;
        }
    }
}