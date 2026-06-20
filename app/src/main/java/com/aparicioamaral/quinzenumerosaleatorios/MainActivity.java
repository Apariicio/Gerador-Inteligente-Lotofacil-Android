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
    TextView txtProgressoVarredura;

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
            btnInformacao.setOnClickListener(v -> mostrarInformacoesApp(false));
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
                            String numerosParaMemoria = j;
                            if (j.contains("&DATA&")) {
                                numerosParaMemoria = j.split("&DATA&")[0]; // Arranca a data
                            }
                            cacheMeusJogos.add(converterStringParaArrayInt(numerosParaMemoria));
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
                            limparTabuleiro(); // Aciona as cores assim que os concursos oficiais carregam
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

    public void mostrarInformacoesApp(boolean isAberturaAutomatica) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📖 Guia Completo do App");

        // Mensagem HTML revisada e expandida
        String mensagemHTML =
                "📱 <b>FUNCIONALIDADES PRINCIPAIS</b><br>" +
                        "————————————————————<br>" +
                        "🎯 <b>Gerar Jogo Inteligente:</b> Cria combinações aplicando os filtros ativos (Switches). <b>Ele nunca repete</b> jogos que você já fez ou que já saíram na história oficial.<br><br>" +

                        "🔒 <b>Fixar Números (Campo de Entrada):</b> Digite dezenas obrigatórias que você quer jogar (ex: 01 13 25) e o sistema garantirá que elas sempre entrem no jogo gerado.<br><br>" +

                        "📜 <b>Histórico Geral:</b> Veja todos os jogos que você gerou. <b>Toque no jogo</b> para compartilhar, ou <b>segure</b> para selecionar e apagar vários de uma vez.<br><br>" +

                        "🛡️ <b>Proteger Jogo Manual (Inserir Manual):</b> Salve um jogo que você já fez na lotérica. O app JAMAIS o gerará novamente, evitando que você jogue o mesmo jogo duas vezes.<br><br>" +

                        "🔍 <b>Conferidor de Histórico:</b> Digite 15 números para descobrir se você já gerou esse jogo antes e se ele já foi sorteado pela Caixa.<br><br>" +

                        "⚡ <b>Varredura Relâmpago (Validação):</b> Cruza automaticamente todos os seus jogos salvos contra os concursos oficiais. Você descobre quais jogos teriam feito de <b>11 a 15 pontos</b> em sorteios passados, medindo a eficiência do seu método.<br><br>" +

                        "📥 <b>Cadastrar Oficial / Gerenciar Manuais:</b> Mantenha o banco de dados do app atualizado inserindo novos resultados oficiais da Lotofácil. <b>Toque e segure</b> na lista para deletar cadastros manuais.<br><br>" +

                        "📤 <b>Compartilhar:</b> Toque no tabuleiro de bolas (com um jogo gerado) ou em qualquer jogo no histórico para enviar a combinação por WhatsApp, redes sociais, ou SMS.<br><br>" +

                        "📊 <b>FILTROS OPCIONAIS (SWITCHES) - A LÓGICA POR TRÁS</b><br>" +
                        "————————————————————<br>" +
                        "🧮 <b>Soma:</b> Mantém a soma dos 15 números entre <b>165 e 230</b>, evitando somas muito baixas ou altas.<br>" +
                        "🔢 <b>Par / Ímpar:</b> Equilíbrio de gênero! Exige de <b>6 a 9 pares</b> (e, portanto, 6 a 9 ímpares).<br>" +
                        "🔴 <b>Primos:</b> Exige entre <b>4 e 7 números primos</b> (2,3,5,7,11,13,17,19,23).<br>" +
                        "🌀 <b>Fibonacci:</b> Exige entre <b>3 e 5 números</b> da famosa sequência (1,2,3,5,8,13,21).<br>" +
                        "🔥 <b>Repetidos (Hot Numbers):</b> Exige que <b>7 a 10 números</b> sejam do ÚLTIMO sorteio oficial cadastrado. (Estratégia de números quentes).<br>" +
                        "❄️ <b>Ciclo (Cold Numbers):</b> Dá prioridade (70% de chance) para as dezenas que <b>ainda não saíram no ciclo atual</b> (dezenas atrasadas).<br><br>" +

                        "🛡️ <b>TRAVAS OCULTAS (SEMPRE ATIVAS OU CONTROLADAS PELO ÚLTIMO SWITCH)</b><br>" +
                        "————————————————————<br>" +
                        "🟩 <b>Moldura (Borda):</b> Exige entre <b>8 e 11 números</b> da borda do tabuleiro (1,2,3,4,5,6,10,11,15,16,20,21,22,23,24,25).<br>" +
                        "✖️ <b>Múltiplos de 3:</b> Exige entre <b>3 e 6 números</b> múltiplos de três (3,6,9,12,15,18,21,24).<br>" +
                        "📐 <b>Equilíbrio de Grade:</b> Impede que qualquer linha ou coluna do tabuleiro fique completamente vazia (0) ou completamente cheia (5), forçando uma distribuição uniforme.<br>" +
                        "📏 <b>Trava de Sequência:</b> Bloqueia jogos com <b>8 ou mais números colados</b> em sequência (ex: 1,2,3,4,5,6,7,8). O limite máximo é 7, aumentando a imprevisibilidade.<br>" +
                        "🥶 <b>Dezena Fria:</b> Obriga que o jogo contenha <b>pelo menos 1 número</b> que saiu apenas 3 vezes ou menos nos últimos 10 concursos (equilibrando o jogo).<br>" +
                        "🚫 <b>Anti-Duplicidade Suprema:</b> É a trava mais forte! O gerador DESCARTA imediatamente qualquer jogo que <b>já exista no seu histórico pessoal</b> ou nos <b>+3.000 concursos oficiais</b> da Caixa.<br><br>" +

                        "🧠 <b>O EFEITO 'SNIPER' (A MATEMÁTICA DO FUNIL)</b><br>" +
                        "————————————————————<br>" +
                        "O universo da Lotofácil tem <b>3.268.760 combinações</b>.<br><br>" +
                        "⚙️ Com <b>TODOS OS SWITCHES DESLIGADOS</b> (apenas as travas ocultas e anti-duplicidade ativas), o app já elimina o 'lixo matemático', cortando o universo para <b>cerca de 1.200.000 jogos</b>.<br><br>" +
                        "🎯 Com <b>TODAS AS CHAVES ATIVADAS</b>, o funil fica extremo! Cada filtro sobrepõe o outro, reduzindo drasticamente o mar de combinações para um núcleo de elite de aproximadamente <b>80.000 a 150.000 jogos</b>.<br><br>" +
                        "📈 <b>Conclusão:</b> Quanto mais chaves ligadas, mais 'inteligente' e filtrado é o jogo, aumentando as chances de você estar dentro do grupo de combinações com maior potencial estatístico!" +

                        // ═══════════════════════════════════════════════════════════════
                        // ║         NOVIDADES ADICIONADAS NAS ÚLTIMAS VERSÕES         ║
                        // ═══════════════════════════════════════════════════════════════

                        "<br><br>🆕 <b>NOVAS FUNCIONALIDADES!</b><br>" +
                        "————————————————————<br>" +

                        "🚀 <b>MODO TURBO (GERAÇÃO RÁPIDA DE MÚLTIPLOS JOGOS):</b> Ative o Modo Turbo para gerar <b>3 jogos instantaneamente</b> em um popup dedicado! " +
                        "Dentro do popup, você encontra um <b>botão \"Gerar Turbo\"</b> que permite gerar <b>mais 3 jogos</b> sem fechar a janela, " +
                        "acumulando quantos jogos quiser rapidamente. Perfeito para quem quer várias opções de aposta sem sair da tela principal! " +
                        "Cada jogo gerado no Turbo é automaticamente salvo no histórico.<br><br>" +

                        "📊 <b>Contador de Filtros Ativos:</b> Na tela principal, você vê em tempo real quantos dos 7 filtros estão ativos. " +
                        "Quanto mais filtros, mais restritivo e inteligente é o jogo gerado! <b>0/7</b> = modo livre, <b>7/7</b> = modo sniper.<br><br>" +

                        "🎓 <b>Guia Interativo de Boas-Vindas:</b> Ao abrir o app pela primeira vez, um guia completo é exibido automaticamente. " +
                        "Você pode marcar a opção <b>\"Não mostrar este Guia na próxima vez\"</b> para silenciá-lo. " +
                        "Mas não se preocupe: o guia está sempre disponível no botão ℹ️.<br><br>" +

                        "⏳ <b>Barra de Progresso na Varredura:</b> Agora a Varredura Relâmpago tem uma barra de progresso que mostra em tempo real " +
                        "quantos jogos já foram analisados. Você acompanha o andamento enquanto o app cruza seus jogos com a história oficial.<br><br>" +

                        "🎨 <b>Mapa de Calor das Bolas (Em breve):</b> As bolas do tabuleiro podem ser coloridas com base na frequência de cada número " +
                        "nos últimos 20 concursos, ajudando você a identificar visualmente os números mais quentes (vermelho) e mais frios (azul). " +
                        "Esta funcionalidade está em desenvolvimento e será ativada em breve!";

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
            if (tentativasLoop > 50000) {
                Toast.makeText(this, "Difícil! Desative as 'Travas Ocultas' ou outras chaves.", Toast.LENGTH_LONG).show();
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
        tvTitulo.setTextColor(Color.parseColor("#333333"));
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitulo.setGravity(android.view.Gravity.CENTER);
        tvTitulo.setPadding(30, 40, 30, 40);
        tvTitulo.setBackgroundColor(Color.parseColor("#AB5A7D8E")); // Barra superior cinza
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
            cardJogo.setBackgroundColor(Color.parseColor("#A6F5F5F5"));

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
            txtStats.setTextColor(Color.parseColor("#333333")); // Letras escuras
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
        barraInferior.setBackgroundColor(Color.parseColor("#AB5A7D8E")); // Mantém a sua cor original da barra
        barraInferior.setPadding(50, 35, 50, 35); // Respiro igual para as duas pontas

        // Novo Texto Clicável: TURBO 3x (Alinhado à Esquerda)
        TextView btnNovoTurbo = new TextView(this);
        btnNovoTurbo.setText("⚡ TURBO 3x");
        btnNovoTurbo.setTextSize(16f);
        btnNovoTurbo.setTextColor(Color.parseColor("#333333")); // Mantém o cinza escuro original
        btnNovoTurbo.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams paramsTurbo = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        btnNovoTurbo.setLayoutParams(paramsTurbo);
        btnNovoTurbo.setGravity(android.view.Gravity.START | android.view.Gravity.CENTER_VERTICAL); // Gruda na Esquerda

        // Texto Clicável: FECHAR JOGOS (Alinhado à Direita)
        TextView btnFecharCustom = new TextView(this);
        btnFecharCustom.setText("FECHAR JOGOS ✅");
        btnFecharCustom.setTextSize(16f);
        btnFecharCustom.setTextColor(Color.parseColor("#333333"));
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
}