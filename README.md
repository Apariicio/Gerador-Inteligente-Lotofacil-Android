🚀 Gerador Inteligente de Números - Lotofácil
🎯 **Versão 4.9.91.75 | Code 75**

Este é um aplicativo Android nativo robusto e de alta performance desenvolvido em Java. Ele funciona como um **"Funil Estatístico" (Sniper)**, projetado para analisar, filtrar e reduzir o universo absoluto de **3.268.760 combinações possíveis** da Lotofácil para um núcleo de elite altamente provável (entre 80.000 e 150.000 jogos), eliminando o "lixo matemático".

O aplicativo utiliza persistência leve de dados (`SharedPreferences`), processamento assíncrono em Threads e conta com um **robô inteligente (Self-Healing)** que busca e corrige automaticamente o banco de dados, garantindo fluidez visual enquanto executa milhões de cruzamentos matemáticos em tempo real.

---

## 📸 CAPTURAS DE TELA DO APP

Abaixo algumas imagens demonstrando as principais telas e funcionalidades do aplicativo:

<img width="1113" height="721" alt="Captura de tela 2026-09-10 193147" src="https://github.com/user-attachments/assets/4cea73e8-1003-49b3-ae8e-2d3676e1853b" />
... Tela principal 👆 último jogo mostrado com Toast.LENGTH_SHORT 👆 turbo 3 gerado 👆
<img width="1123" height="735" alt="Captura de tela 2026-09-10 193200" src="https://github.com/user-attachments/assets/730199a6-8dcc-4a94-81cd-8d48d5e15d38" />
... Menu do gerado da i.a. 👆 app pensando 👆 jogo gerado pela i.a. 👆
<img width="1312" height="650" alt="Captura de tela 2026-09-10 193211" src="https://github.com/user-attachments/assets/69732ffc-4c58-4560-9b7b-4e0130e89857" />
... Menu do app 👆 histórico de jogos oficiais 👆 histórico de jogo criados pelo app 👆 janela para cadastrar jogo oficial 👆




---

## 🛠️ FUNCIONALIDADES PRINCIPAIS

### 🎯 GERAÇÃO INTELIGENTE
- **Sorteio Estratégico Multi-Filtros:** Geração automática baseada em parâmetros estatísticos tradicionais e avançados.
- **Modo Turbo 3x:** Gere 3 jogos instantaneamente em um popup com mini tabuleiros. Botão "Turbo 3x" para geração contínua sem fechar a janela.
- **Navegação Interativa no Tabuleiro:** Setas minimalistas (❮ e ❯) integradas ao volante principal permitem revisar passo a passo todo o seu histórico de jogos gerados na sessão, com atualização imediata do resumo estatístico.
- **Fixação de Dezenas Obrigatórias:** Campo de entrada personalizado com teclado numérico otimizado que força a inclusão de números escolhidos pelo usuário em todos os novos sorteios.
- **Tabuleiro Dinâmico:** Painel visual que acende as dezenas geradas e permite o compartilhamento rápido do jogo via WhatsApp ou redes sociais.

### 🧠 INTELIGÊNCIA ARTIFICIAL (DATA SCIENCE)
- **Super Jogo I.A. com 3 Perfis:**
    - 🛡️ **Conservador:** Foca nas dezenas mais quentes (Padrão Ouro)
    - ⚔️ **Arrojado:** Caçador de 'zebras' e dezenas muito atrasadas
    - 🎯 **Sniper:** Usa a *Lei da Compensação* (analisa o desvio do último sorteio para equilibrar o próximo)
- **Painel de Previsão Estatística:** Analisa 5 fatores (Frequência, Defasagem, Tendência, Correlação e Sazonalidade) para gerar um Top 15 de números recomendados.
- **Manual de Bordo da I.A.:** Botão "ℹ️ Como Funciona?" que explica de forma transparente toda a matemática por trás da inteligência artificial.
- **Motor de Resgate (Plano B):** App à prova de falhas! Se as fixas ou a I.A. entrarem em conflito com os filtros, o 'Plano B' é ativado automaticamente.

### 📊 ANÁLISE E ESTATÍSTICAS
- **Navegador de Resultados Oficiais 🎰:** Um mini-tabuleiro flutuante interativo acessível pelo topo da tela. Viaje no tempo pelos concursos oficiais da Caixa, exibindo instantaneamente as dezenas sorteadas e o resumo estatístico completo (Soma, Pares/Ímpares, Primos, Fibo e Repetidas).
- **Varredura Relâmpago (Backtesting):** Engine de auditoria que cruza todos os jogos gerados no histórico do app contra todos os resultados reais da história da Lotofácil, exibindo um ranking de recordes pessoais com barra de progresso.
- **Gráfico de Frequência de Dezenas:** Painel visual em barras que analisa os últimos 30 concursos e exibe as dezenas mais quentes (🔴 Vermelho) e mais frias (🔵 Azul/Verde).
- **Conferidor Avançado Duplo:** Valida se um jogo já foi sorteado na história oficial ou se já foi salvo anteriormente no histórico do usuário.

### 💾 GESTÃO DE DADOS E SEGURANÇA
- **Backup e Restauração Nativo (SAF):** Exporte e importe todos os seus dados em um arquivo `.json` seguro.
- **Proteção de Jogo Manual:** Permite o cadastro e blindagem de bilhetes físicos jogados na lotérica.
- **Gerenciador de Resultados Oficiais:** Cadastro manual integrado de novos concursos.
- **Limpeza de Cache e Reset de Fábrica:** Opções de manutenção com trava de segurança de dupla confirmação.

### 🔄 ATUALIZAÇÃO E SINCRONIZAÇÃO
- **Busca Automática de Concursos Faltantes (Robô Curador):** O app verifica e baixa automaticamente os concursos oficiais faltantes (CAIXA) em segundo plano!
- **Robô Self-Healing:** Sistema que monitora a integridade do banco de dados e corrige inconsistências.
- **Botão de Atualização Global:** Sincronize manualmente com a CAIXA com um único toque (🔄).

### 🎨 DESIGN E EXPERIÊNCIA
- **Menu Suspenso Premium:** Elegância com cantos arredondados reunindo todas as funcionalidades avançadas.
- **Controle Dinâmico de Tema:** Alternância instantânea entre Tema Claro ☀️, Tema Escuro 🌙 ou Padrão do Sistema ⚙️.
- **Tela de Carregamento Imersiva:** Animação de Trevo Giratório com texto sombreado inteligente (pílula adaptativa).

---

## 📐 ESTRUTURA ARQUITETURAL DO CÓDIGO

O projeto está organizado de forma modular utilizando práticas de desenvolvimento nativo Android:

### 1. `MainActivity.java` (O Motor Central)
Gerencia a interface gráfica principal, escuta as mudanças de estados das chaves e executa o laço crítico de busca probabilística no método `buscarJogoEquilibrado()`.
- **Motor de Carregamento Unificado:** Sistema centralizado (`exibirCarregamento()`).
- **Navegador de Jogos e Resultados:** Gestão de indexação local para revisitar bilhetes gerados ou consultar a base de dados da CAIXA.
- **Sistema de IA Modular:** Implementação com 3 perfis e motor de previsão.

### 2. `DadosOficiais.java` (A Camada de Persistência e Dados)
Gerencia os dados históricos oficiais estruturados, mesclando em memória os resultados históricos de fábrica com novos cadastros/downloads.

### 3. `HistoricoActivity.java` e `HistoricoManualActivity.java`
Telas que apresentam o log geral de bilhetes do usuário e resultados manuais, com suporte a deleção em lote via clique longo.

### 4. `ResultadoVarreduraActivity.java` (O Painel Estatístico)
Dashboard analítico focado em processar grandes volumes de dados de *backtesting*.

### 5. `LembreteReceiver.java` (Sistema de Notificações)
BroadcastReceiver que gerencia notificações push agendadas, com filtro inteligente para dias úteis.

---

## 📊 REGRAS ESTATÍSTICAS APLICADAS

O coração lógico do algoritmo opera através de uma verificação em camadas consecutivas (limitado a 50.000 iterações de segurança):

### 🎛️ Filtros Opcionais (Painel de Switches)
1.  **Soma:** Restringe o somatório entre **165 e 230**.
2.  **Primos:** Valida de **4 a 7** números primos.
3.  **Par / Ímpar:** Limita entre **6 e 9** pares.
4.  **Fibonacci:** Limita entre **3 e 5** dezenas.
5.  **Repetidos:** Exige repetição de **7 a 10** dezenas do sorteio anterior.
6.  **Ciclo da Lotofácil:** Prioriza matematicamente (**70% de chance**) dezenas não sorteadas no ciclo.
7.  **Travas Ocultas (Mestre):** Controla regras extras fixas.

### 🛡️ Filtros Fixos Ocultos
* **Moldura:** De **8 a 11** dezenas nas bordas.
* **Múltiplos de 3:** Entre **3 e 6** números.
* **Equilíbrio Geométrico:** Impede fileiras totalmente vazias (0) ou cheias (5).
* **Trava de Sequência:** Bloqueia 8 ou mais dezenas coladas.
* **Dezena Fria:** Força a entrada de pelo menos 1 número de baixa frequência.
* **Anti-Duplicidade Absoluta:** Destrói instantaneamente jogos que já existam no histórico local ou oficial.

---

## 🆕 NOVIDADES DA VERSÃO 4.9.91.75

| Funcionalidade | Descrição |
|----------------|-----------|
| **🎰 Navegador de Resultados** | Galeria interativa para explorar todo o histórico de concursos oficiais e estatísticas. |
| **◀▶ Navegação no Tabuleiro** | Setas integradas ao volante principal para rever seus jogos gerados passo a passo. |
| **⌨️ Teclado Otimizado** | Campo de Fixas com layout numérico inteligente para digitação rápida. |
| **☰ Menu Suspenso Premium** | Menu elegante reunindo todas as funcionalidades avançadas. |
| **🌐 Sincronização Inteligente** | Robô de cura automática e botão de atualização global da Caixa. |
| **🤖 Super Jogo I.A.** | Evolução estatística com 3 perfis e Painel Top 15. |
| **🎨 Design e UX** | Tema Claro/Escuro fluido, pílulas de status adaptativas e barra de progresso real. |
| **🛡️ Segurança de Dados** | Backup nativo via SAF, Limpeza de Cache, Reset de Fábrica com trava de segurança. |

---

## 🔧 REQUISITOS TÉCNICOS

- **Android SDK:** API 29 (Android 10) ou superior
- **Linguagem:** Java 11+
- **Persistência:** SharedPreferences
- **Arquitetura:** Single Activity com múltiplas Activities de suporte

---

## 📝 LICENÇA E AUTORIA

Este projeto é de uso pessoal e educacional. O algoritmo de Inteligência Artificial, os motores lógicos (Plano B), o sistema Self-Healing e a arquitetura de análise estatística presentes neste repositório são propriedades intelectuais exclusivas.

**Criado, arquitetado e desenvolvido por Aparício (Liu)**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/aparício-amaral-b53451304)

**Nenhuma parte deste projeto pode ser comercializada, clonada ou republicada sem autorização expressa.**

---

⭐ Se você gostou deste aplicativo, considere dar uma estrela no repositório!

📦 **Code:** 75
📱 **Versão:** 4.9.91.75 
📅 **Última Atualização:** Setembro 2026
