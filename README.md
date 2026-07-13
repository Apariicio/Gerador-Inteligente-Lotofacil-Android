🚀 Gerador Inteligente de Números - Lotofácil
🎯 **Versão 4.9.91.72 | Code 72**

Este é um aplicativo Android nativo robusto e de alta performance desenvolvido em Java. Ele funciona como um **"Funil Estatístico" (Sniper)**, projetado para analisar, filtrar e reduzir o universo absoluto de **3.268.760 combinações possíveis** da Lotofácil para um núcleo de elite altamente provável (entre 80.000 e 150.000 jogos), eliminando o "lixo matemático".

O aplicativo utiliza persistência leve de dados (`SharedPreferences`), processamento assíncrono em Threads e conta com um **robô inteligente (Self-Healing)** que busca e corrige automaticamente o banco de dados, garantindo fluidez visual enquanto executa milhões de cruzamentos matemáticos em tempo real.

---

## 📸 CAPTURAS DE TELA DO APP

Abaixo algumas imagens demonstrando as principais telas e funcionalidades do aplicativo:

<img width="387" height="812" alt="Tela Principal" src="https://github.com/user-attachments/assets/2833c792-bebf-4846-96b5-60f96b06e2ae" />
<img width="388" height="816" alt="Modo Turbo" src="https://github.com/user-attachments/assets/96d61d11-3038-465e-9945-21885cacf263" />
<img width="392" height="810" alt="Histórico" src="https://github.com/user-attachments/assets/f7886780-8779-493a-b20b-4f861cfb3b25" />
<img width="392" height="815" alt="Varredura" src="https://github.com/user-attachments/assets/12621469-6bcc-446d-900c-12bc376347bc" />
<img width="395" height="821" alt="Gráfico de Frequência" src="https://github.com/user-attachments/assets/5ea3e858-cb2a-46e8-8d4b-a621b70bd360" />

---

## 🛠️ FUNCIONALIDADES PRINCIPAIS

### 🎯 GERAÇÃO INTELIGENTE
- **Sorteio Estratégico Multi-Filtros:** Geração automática baseada em parâmetros estatísticos tradicionais e avançados.
- **Modo Turbo 3x:** Gere 3 jogos instantaneamente em um popup com mini tabuleiros. Botão "Turbo 3x" para geração contínua sem fechar a janela.
- **Fixação de Dezenas Obrigatórias:** Campo de entrada personalizado que força a inclusão de números escolhidos pelo usuário em todos os novos sorteios.
- **Tabuleiro Interativo:** Painel visual dinâmico (grade de 1 a 25) que acende as dezenas geradas e permite o compartilhamento rápido do jogo via WhatsApp ou redes sociais.

### 🧠 INTELIGÊNCIA ARTIFICIAL (DATA SCIENCE)
- **Super Jogo I.A. com 3 Perfis:**
    - 🛡️ **Conservador:** Foca nas dezenas mais quentes (Padrão Ouro)
    - ⚔️ **Arrojado:** Caçador de 'zebras' e dezenas muito atrasadas
    - 🎯 **Sniper:** Usa a *Lei da Compensação* (analisa o desvio do último sorteio para equilibrar o próximo)
- **Painel de Previsão Estatística:** Analisa 5 fatores (Frequência, Defasagem, Tendência, Correlação e Sazonalidade) para gerar um Top 15 de números recomendados.
- **Manual de Bordo da I.A.:** Botão "ℹ️ Como Funciona?" que explica de forma transparente toda a matemática por trás da inteligência artificial.
- **Motor de Resgate (Plano B):** App à prova de falhas! Se as fixas ou a I.A. entrarem em conflito com os filtros, o 'Plano B' é ativado automaticamente.

### 📊 ANÁLISE E ESTATÍSTICAS
- **Varredura Relâmpago (Backtesting):** Engine de auditoria que cruza todos os jogos gerados no histórico do app contra todos os resultados reais da história da Lotofácil, exibindo um ranking de recordes pessoais. **Com barra de progresso em tempo real!**
- **Gráfico de Frequência de Dezenas:** Painel visual em barras (criado do zero, sem bibliotecas pesadas) que analisa os últimos 30 concursos e exibe as dezenas mais quentes (🔴 Vermelho) e mais frias (🔵 Azul/Verde).
- **Conferidor Avançado Duplo:** Mecanismo de busca profunda que valida se um jogo qualquer inserido já foi sorteado na história oficial (informando concurso e data) ou se já foi salvo anteriormente no histórico do usuário (informando a posição exata).

### 💾 GESTÃO DE DADOS E SEGURANÇA
- **Backup e Restauração Nativo (SAF):** Exporte e importe todos os seus dados (Histórico + Resultados Manuais) em um arquivo `.json` seguro, integrado ao gerenciador de arquivos do celular.
- **Proteção de Jogo Manual:** Permite o cadastro e blindagem de bilhetes físicos jogados na lotérica, inserindo-os no histórico e impedindo o app de gerá-los de novo.
- **Gerenciador de Resultados Oficiais:** Cadastro manual integrado de novos concursos para manter a base estatística sempre atualizada.
- **Limpeza de Cache e Reset de Fábrica:** Opções de manutenção com trava de segurança rigorosa.
- **Trava de Segurança nas Exclusões:** Todas as operações destrutivas exigem dupla confirmação.

### 🔄 ATUALIZAÇÃO E SINCRONIZAÇÃO
- **Busca Automática de Concursos Faltantes (Robô Curador):** O app verifica e baixa automaticamente os concursos oficiais faltantes diretamente da fonte oficial (CAIXA). Tudo em segundo plano!
- **Robô Self-Healing:** Sistema inteligente que monitora a integridade do banco de dados e corrige automaticamente qualquer inconsistência.
- **Botão de Atualização Global:** Sincronize manualmente com a CAIXA a qualquer momento com um único toque.

### 🎨 DESIGN E EXPERIÊNCIA
- **Menu Suspenso Premium:** Menu elegante com cantos arredondados reunindo todas as funcionalidades avançadas.
- **Controle Dinâmico de Tema:** Alternância instantânea entre Tema Claro ☀️, Tema Escuro 🌙 ou Padrão do Sistema ⚙️.
- **Tela de Carregamento Imersiva:** Animação de Trevo Giratório com texto sombreado, bloqueando os botões até que os dados estejam carregados.
- **Contador de Filtros Ativos:** Indicador visual em tempo real mostrando quantos dos 7 filtros estão ativos (0/7 a 7/7).
- **Guia Interativo de Boas-Vindas:** Tutorial automático na primeira execução com opção "Não mostrar novamente".

---

## 📐 ESTRUTURA ARQUITETURAL DO CÓDIGO

O projeto está organizado de forma modular utilizando práticas de desenvolvimento nativo Android:

### 1. `MainActivity.java` (O Motor Central)
Gerencia a interface gráfica principal, escuta as mudanças de estados das chaves (Switches) e executa o laço crítico de busca probabilística no método `buscarJogoEquilibrado()`.
- **Motor de Carregamento Unificado:** Sistema centralizado (`exibirCarregamento()`) que gerencia trevo, barra de progresso e textos com fundos adaptativos.
- **Sistema de IA Modular:** Implementação completa com 3 perfis, motor de previsão e manual de bordo.
- **Robô Curador Integrado:** Inicialização automática em segundo plano para manter a base de dados sempre atualizada.
- **Botão de Sincronização Global:** Atualização manual forçada com a API da CAIXA.

### 2. `DadosOficiais.java` (A Camada de Persistência e Dados)
Gerencia os dados históricos oficiais estruturados.
- Realiza o *parsing* linear em tempo de execução de um arquivo de texto bruto (`resultados.txt` nos Assets).
- Mescla em memória os resultados históricos de fábrica com os novos cadastros manuais realizados pelo usuário via `SharedPreferences`.
- Suporte para deleção seletiva de cadastros manuais.

### 3. `HistoricoActivity.java` (O Log Geral do Usuário)
Apresenta uma lista cronológica invertida (jogos mais recentes no topo) de todos os bilhetes gerados com **data e hora de criação**.
- Permite seleção múltipla customizada por clique longo para deleção em lote.
- Possui barra de busca direta com rolagem e foco automático.

### 4. `HistoricoManualActivity.java` (Diretório de Resultados Cadastrados)
Permite gerenciar exclusivamente os resultados oficiais inseridos manualmente pelo usuário através de ordenação dinâmica por número de concurso.

### 5. `ResultadoVarreduraActivity.java` (O Painel Estatístico)
Dashboard analítico focado em processar grandes volumes de dados. Exibe em formato gráfico-textual o desempenho histórico e distribui medalhas/troféus para os jogos campeões que atingiram 14 ou 15 pontos em simulações passadas.

### 6. `LembreteReceiver.java` (Sistema de Notificações)
BroadcastReceiver que gerencia notificações push agendadas, com filtro inteligente para ignorar domingos e reagendamento automático diário.

---

## 📊 REGRAS ESTATÍSTICAS APLICADAS

O coração lógico do algoritmo opera através de uma verificação em camadas consecutivas dentro de um laço de repetição condicional limitado a 50.000 iterações por clique para prevenir congelamentos (*ANR*):

### 🎛️ Filtros Opcionais (Painel de Switches)
1.  **Par / Ímpar:** Limita a combinação a proporções equilibradas (entre 6 e 9 dezenas pares).
2.  **Soma:** Restringe o somatório de todas as 15 dezenas no intervalo hiper-frequente de **165 a 230**.
3.  **Primos:** Valida a presença obrigatória de **4 a 7** números primos (2, 3, 5, 7, 11, 13, 17, 19, 23).
4.  **Fibonacci:** Limita o jogo a conter entre **3 e 5** dezenas da sequência (1, 2, 3, 5, 8, 13, 21).
5.  **Repetidos:** Analisa dinamicamente o sorteio anterior e exige a repetição de **7 a 10** dezenas (padrão estatístico mais comum).
6.  **Ciclo da Lotofácil:** Prioriza matematicamente com **70% de probabilidade** a escolha de dezenas que ainda não saíram no ciclo vigente.
7.  **Travas Ocultas (Switch Mestre):** Controla a ativação/desativação de todas as regras de filtro fixo.

### 🛡️ Filtros Fixos Ocultos (Controlados pelo Switch Mestre)
* **Moldura da Grade:** Obriga de **8 a 11** dezenas localizadas nas bordas do bilhete.
* **Múltiplos de 3:** Exige entre **3 e 6** números múltiplos de três.
* **Equilíbrio Geométrico:** Avalia linhas e colunas do volante, descartando jogos que deixem qualquer fileira completamente vazia (0) ou cheia (5).
* **Trava de Sequência Sequencial:** Descarta bilhetes artificiais contendo 8 ou mais dezenas sequenciais coladas (limite máximo de 7).
* **Inclusão de Dezena Fria:** Monitora os últimos 10 concursos reais e força a entrada de pelo menos 1 número de baixa frequência (3 vezes ou menos).
* **Anti-Duplicidade Absoluta:** Verifica em tempo real as bases locais de dados e destrói instantaneamente qualquer jogo repetido gerado que já exista no seu histórico ou que já tenha premiado com 15 pontos no passado.

---

## 🆕 NOVIDADES DA VERSÃO 4.9.91.72

| Funcionalidade | Descrição |
|----------------|-----------|
| **☰ Menu Suspenso Premium** | Menu elegante com cantos arredondados reunindo todas as funcionalidades avançadas |
| **🔄 Botão de Atualização Global** | Sincronize manualmente com a CAIXA a qualquer momento |
| **🌐 Busca Automática de Concursos** | Robô Curador que baixa concursos faltantes automaticamente |
| **🤖 Robô Self-Healing** | Sistema de autocorreção do banco de dados |
| **🎨 Design Adaptativo** | Cores refinadas para Tema Claro e Escuro |
| **🛡️ Motor de Resgate (Plano B)** | App à prova de falhas para geração de jogos |
| **📊 Gráfico de Frequência** | Painel visual em barras sem bibliotecas pesadas |
| **🍀 Tela de Carregamento Imersiva** | Trevo giratório com fundo inteligente (pílula adaptativa) |
| **⚡ Barra de Progresso** | Feedback visual em tempo real na varredura |
| **🔒 Trava de Segurança** | Dupla confirmação em todas as exclusões |
| **🧠 Manual de Bordo da I.A.** | Explicação transparente da matemática da IA |
| **📱 Interface Otimizada** | Ajustes visuais para melhor experiência em diferentes telas |

---

## 🔧 REQUISITOS TÉCNICOS

- **Android SDK:** API 29 (Android 10) ou superior
- **Linguagem:** Java 11+
- **Persistência:** SharedPreferences
- **Assets:** Arquivo `resultados.txt` contendo a base histórica oficial da Lotofácil
- **Arquitetura:** Single Activity com múltiplas Activities de suporte
- **Permissões:** POST_NOTIFICATIONS, SCHEDULE_EXACT_ALARM, USE_EXACT_ALARM, WAKE_LOCK

---

## 📱 COMPATIBILIDADE

O aplicativo foi testado e funciona em:
- ✅ Dispositivos Android 10 (API 29) a Android 14 (API 34)
- ✅ Telas de 4.7" a 7" (Smartphones e Tablets)
- ✅ Modo Retrato (principal) e Paisagem (suporte básico)
- ✅ Temas Claro, Escuro e Padrão do Sistema

---

## 🚀 FUTURAS MELHORIAS (ROADMAP)

- [ ] Widget na Tela Inicial
- [ ] Modo Concentração (tela limpa com apenas o tabuleiro)
- [ ] Efeito de "Revelação" das bolas no sorteio
- [ ] Exportação de Relatórios em PDF
- [ ] Análise de Correlação entre Números

---

## 📝 LICENÇA

Este projeto é de uso pessoal e educacional. Todos os direitos reservados.

---

## 👨‍💻 DESENVOLVEDOR

**Criado, arquitetado e desenvolvido por Aparício (Liu)**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/aparício-amaral-b53451304)

---

## ©️ AUTORIA E DIREITOS AUTORAIS

Criado, arquitetado e desenvolvido por **Aparício (Liu)**.

O algoritmo de Inteligência Artificial, os motores lógicos (Plano B), o sistema Self-Healing e a arquitetura de análise estatística presentes neste repositório são propriedades intelectuais exclusivas.

**Nenhuma parte deste projeto pode ser comercializada, clonada ou republicada sem autorização expressa.**

---

## ⭐ APOIE O PROJETO

Se você gostou deste aplicativo, considere dar uma estrela ⭐ no repositório e compartilhar com outros entusiastas da Lotofácil!

---

📱 **Versão:** 4.9.91.72
📦 **Code:** 72
📅 **Última Atualização:** Julho 2026