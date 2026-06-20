🚀 Gerador Inteligente de Números - Lotofácil

Este é um aplicativo Android nativo robusto e de alta performance desenvolvido em Java. Ele funciona como um **"Funil Estatístico" (Sniper)**, projetado para analisar, filtrar e reduzir o universo absoluto de **3.268.760 combinações possíveis** da Lotofácil para um núcleo de elite altamente provável (entre 80.000 e 150.000 jogos), eliminando o "lixo matemático".

O aplicativo utiliza persistência leve de dados (`SharedPreferences`) e processamento assíncrono em Threads para garantir fluidez visual enquanto executa milhões de cruzamentos matemáticos em tempo real.

---

📸 CAPTURAS DE TELA DO APP
Abaixo algumas imagens demonstrando as principais telas e funcionalidades do aplicativo:

<img width="387" height="812" alt="Captura de tela 2026-06-20 171808" src="https://github.com/user-attachments/assets/2833c792-bebf-4846-96b5-60f96b06e2ae" />
<img width="388" height="816" alt="Captura de tela 2026-06-20 171830" src="https://github.com/user-attachments/assets/96d61d11-3038-465e-9945-21885cacf263" />
<img width="392" height="810" alt="Captura de tela 2026-06-20 171852" src="https://github.com/user-attachments/assets/f7886780-8779-493a-b20b-4f861cfb3b25" />
<img width="392" height="815" alt="Captura de tela 2026-06-20 171911" src="https://github.com/user-attachments/assets/12621469-6bcc-446d-900c-12bc376347bc" />
<img width="395" height="821" alt="Captura de tela 2026-06-20 171939" src="https://github.com/user-attachments/assets/5ea3e858-cb2a-46e8-8d4b-a621b70bd360" />
---
## 🛠️ Funcionalidades Principais

* **Sorteio Estratégico Multi-Filtros:** Geração automática baseada em parâmetros estatísticos tradicionais e avançados.
* **Fixação de Dezenas Obligatórias:** Campo de entrada personalizado que força a inclusão de números escolhidos pelo usuário em todos os novos sorteios.
* **Tabuleiro Interativo:** Painel visual dinâmico (grade de 1 a 25) que acende as dezenas geradas e permite o compartilhamento rápido do jogo via WhatsApp ou redes sociais.
* **Varredura Relâmpago (Backtesting):** Engine de auditoria que cruza todos os jogos gerados no histórico do app contra todos os resultados reais da história da Lotofácil, exibindo um ranking de recordes pessoais (*Highlander Mode*).
* **Conferidor Avançado Duplo:** Mecanismo de busca profunda que valida se um jogo qualquer inserido já foi sorteado na história oficial (informando concurso e data) ou se já foi salvo anteriormente no histórico do usuário (informando a posição exata).
* **Proteção de Jogo Manual:** Permite o cadastro e blindagem de bilhetes físicos jogados na lotérica, inserindo-os no histórico e impedindo o app de gerá-los de novo.
* **Gerenciador de Resultados Oficiais:** Cadastro manual integrado de novos concursos para manter a base estatística sempre atualizada sem depender de APIs de terceiros.

---

## 📐 Estrutura Arquitetural do Código

O projeto está organizado de forma modular utilizando práticas de desenvolvimento nativo Android:

### 1. `MainActivity.java` (O Motor Central)
Gerencia a interface gráfica principal, escuta as mudanças de estados das chaves (Switches) e executa o laço crítico de busca probabilística no método `buscarJogoEquilibrado()`. 
* **Ajuste de UI Otimizado:** Implementa um sistema adaptativo para evitar truncamento de strings, organizando métricas curtas no topo (*Fibonacci* e *Ciclo*) e isolando strings extensas na base (*Repetidos* e *Concurso*).
* **Modo Escuro Blindado:** O menu informativo força um container de fundo claro de alto contraste (`#FFFFFF`) garantindo legibilidade do texto escuro sob qualquer tema de sistema operacional.

### 2. `DadosOficiais.java` (A Camada de Persistência e Dados)
Gerencia os dados históricos oficiais estruturados.
* Realiza o *parsing* linear em tempo de execução de um arquivo de texto bruto (`resultados.txt` nos Assets) utilizando tabulações e espaçamentos.
* Mescla em memória os resultados históricos de fábrica com os novos cadastros manuais realizados pelo usuário via `SharedPreferences`.

### 3. `HistoricoActivity.java` (O Log Geral do Usuário)
Apresenta uma lista cronológica invertida (jogos mais recentes no topo) de todos os bilhetes gerados.
* Permite seleção múltipla customizada por clique longo para deleção em lote (*soft deletes* ordenados de forma decrescente para proteção de índices de arrays).
* Possui barra de busca direta com rolagem e foco automático automatizado (*scroll to position*).

### 4. `HistoricoManualActivity.java` (Diretório de Resultados Cadastrados)
Permite gerenciar exclusivamente os resultados oficiais inseridos manualmente pelo usuário através de ordenação dinâmica por número de concurso.

### 5. `ResultadoVarreduraActivity.java` (O Painel Estatístico)
Dashboard analítico focado em processar grandes volumes de dados. Exibe em formato gráfico-textual o desempenho histórico e distribui medalhas/troféus para os jogos campeões que atingiram 14 ou 15 pontos em simulações passadas.

---

## 📊 Regras Estatísticas Aplicadas

O coração lógico do algoritmo opera através de uma verificação em camadas consecutivas dentro de um laço de repetição condicional limitado a 50.000 iterações por clique para prevenir congelamentos (*ANR - Application Not Responding*):

### 🎛️ Filtros Opcionais (Painel de Switches)
1.  **Par / Ímpar:** Limita a combinação a proporções equilibradas (entre 6 e 9 dezenas pares).
2.  **Soma:** Restringe o somatório de todas as 15 dezenas no intervalo hiper-frequente de **165 a 230**.
3.  **Primos:** Valida a presença obrigatória de **4 a 7** números primos (2, 3, 5, 7, 11, 13, 17, 19, 23).
4.  **Fibonacci:** Limita o jogo a conter entre **3 e 5** dezenas da sequência (1, 2, 3, 5, 8, 13, 21).
5.  **Repetidos:** Analisa dinamicamente o sorteio anterior e exige a repetição de **7 a 10** dezenas (padrão estatístico mais comum).
6.  **Ciclo da Lotofácil:** Prioriza matematicamente com **70% de probabilidade** a escolha de dezenas que ainda não saíram no ciclo vigente para acelerar a convergência do fechamento.

### 🛡️ Filtros Fixos Ocultos (A Lei Suprema)
Independentemente dos filtros visíveis ativados, o motor de geração obriga o jogo a seguir restrições severas de simetria de grade:
* **Moldura da Grade:** Obriga de **8 a 11** dezenas localizadas nas bordas do bilhete.
* **Múltiplos de 3:** Exige entre **3 e 6** números múltiplos de três.
* **Equilíbrio Geométrico:** Avalia linhas e colunas do volante, descartando jogos que deixem qualquer fileira completamente vazia (0) ou cheia (5).
* **Trava de Sequência Sequencial:** Descarta bilhetes artificiais ou viciados contendo 8 ou mais dezenas sequenciais coladas (limite máximo de 7).
* **Inclusão de Dezena Fria:** Monitora os últimos 10 concursos reais e força a entrada de pelo menos 1 número considerado de baixa frequência (que saiu 3 vezes ou menos).
* **Anti-Duplicidade Absoluta:** Verifica em tempo real as bases locais de dados e destrói instantaneamente qualquer jogo repetido gerado que já exista no seu histórico ou que já tenha premiado com 15 pontos no passado.

---

## 🆕 Novas Funcionalidades (Última Versão)

| Funcionalidade | Descrição |
|----------------|-----------|
| **🚀 Modo Turbo** | Gera 3 jogos instantaneamente em popup dedicado com botão "Gerar Turbo" para geração contínua sem fechar a janela |
| **📊 Contador de Filtros Ativos** | Indicador visual em tempo real mostrando quantos dos 7 filtros estão ativos (0/7 a 7/7) |
| **🎓 Guia Interativo de Boas-Vindas** | Tutorial automático na primeira execução com opção "Não mostrar novamente" |
| **⏳ Barra de Progresso na Varredura** | Feedback visual do andamento da análise em tempo real |
| **📅 Histórico com Data/Hora** | Registro temporal de cada jogo gerado para rastreamento de estratégias |
| **🛡️ Trava de Segurança na Exclusão** | Confirmação em duas etapas para operações destrutivas no histórico |
| **🔒 Blindagem de Tela no Guia** | Container de fundo claro com alto contraste garantindo legibilidade em qualquer tema |
| **📱 Interface Otimizada** | Ajustes visuais para melhor experiência em diferentes tamanhos de tela |

---

## 🔧 Requisitos Técnicos

- **Android SDK:** API 21 (Android 5.0 Lollipop) ou superior
- **Linguagem:** Java 8+
- **Persistência:** SharedPreferences
- **Assets:** Arquivo `resultados.txt` contendo a base histórica oficial da Lotofácil
- **Arquitetura:** Single Activity com múltiplas Activities de suporte (Histórico, Varredura, Turbo)

---

## 📱 Compatibilidade

O aplicativo foi testado e funciona em:
- ✅ Dispositivos Android 5.0 (Lollipop) a Android 14 (Upside Down Cake)
- ✅ Telas de 4.7" a 7" (Smartphones e Tablets)
- ✅ Modo Retrato e Paisagem (suporte básico)
- ✅ Temas Claro e Escuro (com adaptação forçada no guia)

---

## 🚀 Futuras Melhorias (Roadmap)

- [ ] Exportação/Importação de dados (Backup completo)
- [ ] Gráficos de frequência dos números gerados
- [ ] Notificações de novos concursos
- [ ] Modo Concentração (tela limpa com apenas o tabuleiro)
- [ ] Efeito de "Revelação" das bolas no sorteio

---

## 📝 Licença

Este projeto é de uso pessoal e educacional. Todos os direitos reservados.

---

## 👨‍💻 Desenvolvedor

Desenvolvido por Liu Amaral
