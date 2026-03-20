https://legacy.curseforge.com/minecraft/mc-mods/capeall
# CapeAll v2.0 — Capas sem servidor (Só 1.8.9)
<img width="1920" height="991" alt="2026-03-20_03 53 40" src="https://github.com/user-attachments/assets/8e7fab25-f5a4-4653-9a78-c749dd143018" />

> **Capas para todos os jogadores, em qualquer servidor, sem depender de nenhum site ou servidor externo.**

---

## O que mudou na v2.0?

A versão anterior dependia de um site externo (`capeall.000webhostapp.com`) para definir e consultar capas. **Isso não existe mais.**

Agora o CapeAll funciona de forma **100% peer-to-peer (P2P)** — os próprios clientes se comunicam entre si usando um protocolo invisível embutido nas **skin layers** do Minecraft. Nenhum servidor externo, nenhuma API, nenhum site. Funciona em qualquer servidor vanilla sem nenhuma modificação server-side.

### Como funciona?

O Minecraft envia para todos os jogadores próximos um byte que controla quais partes da skin estão visíveis (chapéu, jaqueta, mangas, calças — 7 bits). O CapeAll usa esses bits como um **canal de dados oculto** para transmitir o ID da capa selecionada para os outros jogadores que também possuem o mod.

```
Jogador A (com CapeAll)          Jogador B (com CapeAll)
        │                                  │
        │  ── skin layers codificadas ──►  │
        │                                  │
        │  Decodifica os bits,             │
        │  identifica a capa e exibe       │
        └──────────────────────────────────┘
```

- **Sem servidor** — comunicação direta via pacotes do próprio Minecraft
- **Sem chat** — nada aparece no chat, totalmente invisível
- **Vanilla-friendly** — funciona em qualquer servidor sem plugins
- **Automático** — a capa é transmitida periodicamente a cada 30 segundos

---

## Instalação

1. Baixe o mod em [Releases](https://github.com/Nonopichy/CapeAll/releases)
2. Coloque o `.jar` na pasta `mods/` do seu Minecraft
3. **Requisito:** Minecraft Forge 1.8.9

> Não é mais necessário acessar nenhum site. Tudo é configurado dentro do jogo.

---

## Como usar

### Escolhendo sua capa

1. Entre em qualquer servidor
2. Pressione **P** para abrir o menu do CapeAll
3. Escolha sua capa na grade — o modelo 3D mostra o preview em tempo real
4. Clique para selecionar — pronto, sua capa é salva e transmitida automaticamente

### O Menu

O menu possui três áreas:

| Seção | Descrição |
|---|---|
| **Preview do Jogador** | Modelo 3D do seu personagem com a capa. Arraste para rotacionar. |
| **Grade de Capas** | 61 capas disponíveis. Passe o mouse para ver o nome, clique para selecionar. |
| **Lista de Jogadores** | Jogadores próximos que usam o CapeAll, com suas cabeças e capas detectadas. |

---

## Capas disponíveis (61)

<details>
<summary>Clique para ver a lista completa</summary>

| # | Nome | # | Nome | # | Nome |
|---|---|---|---|---|---|
| 0 | `15` | 21 | `founde` | 42 | `prisma` |
| 1 | `1cake` | 22 | `hp` | 43 | `realms` |
| 2 | `2011` | 23 | `japtrans` | 44 | `scrolls` |
| 3 | `2012` | 24 | `migrator` | 45 | `snowman` |
| 4 | `2013` | 25 | `mojang` | 46 | `spade` |
| 5 | `2015` | 26 | `mojira` | 47 | `squid` |
| 6 | `2016` | 27 | `noc` | 48 | `studios` |
| 7 | `360` | 28 | `nyan` | 49 | `tc2010` |
| 8 | `awesom` | 29 | `oalblack` | 50 | `test` |
| 9 | `bday` | 30 | `oblack` | 51 | `tik` |
| 10 | `blonk` | 31 | `oblue` | 52 | `tpan` |
| 11 | `bun1` | 32 | `ocyan` | 53 | `trans` |
| 12 | `bun2` | 33 | `ogray` | 54 | `turtle` |
| 13 | `bun3` | 34 | `ogreen` | 55 | `ty2011` |
| 14 | `cherry` | 35 | `opurple` | 56 | `un1` |
| 15 | `chitrans` | 36 | `ored` | 57 | `un2` |
| 16 | `classic` | 37 | `ostand` | 58 | `un3` |
| 17 | `cobalt` | 38 | `owhite` | 59 | `valentin` |
| 18 | `customer` | 39 | `oyellow` | 60 | `vanilla` |
| 19 | `db` | 40 | `ppride` | — | — |
| 20 | `dev00` | 41 | `vete` | — | — |

</details>

---

## Protocolo Skin Layer — Como funciona por baixo

Para quem tem curiosidade técnica:

```
┌─────────────────────────────────────────────────┐
│  Protocolo de Transmissão (7 bits por frame)    │
├─────────────────────────────────────────────────┤
│                                                 │
│  1. PRE_SYNC   → 0000000 (limpa o estado)       │
│  2. SYNC       → 1111111 (sinal de início)      │
│  3. HEADER     → canal + tamanho dos dados      │
│  4. DATA       → bits do ID da capa             │
│  5. END        → 1111111 (sinal de fim)          │
│  6. RESTORE    → restaura as skin layers reais  │
│                                                 │
│  Cada frame = 1 tick do servidor (~50ms)         │
│  Transmissão completa ≈ 1-2 segundos            │
│  Broadcast a cada 30 segundos                   │
│                                                 │
└─────────────────────────────────────────────────┘
```

Os outros clientes com CapeAll monitoram as skin layers dos jogadores próximos. Quando detectam a sequência `SYNC → HEADER → DATA → END`, decodificam o ID da capa e a exibem no modelo do jogador.

---

## Configuração

O arquivo de configuração fica em `.minecraft/config/capeall.cfg`:

| Opção | Padrão | Descrição |
|---|---|---|
| `selectedCape` | `mojang` | Capa selecionada |
| `ticksPerFrame` | `5` | Ticks entre cada frame do protocolo |
| `broadcastInterval` | `600` | Ticks entre cada broadcast (600 = 30s) |

---

## Comparação: v1.0 vs v2.0

| | v1.0 (antiga) | v2.0 (atual) |
|---|---|---|
| **Dependência** | Site externo obrigatório | Nenhuma |
| **Configuração** | Via navegador | Dentro do jogo (tecla P) |
| **Funcionamento** | Consulta HTTP ao servidor | P2P via skin layers |
| **Disponibilidade** | Depende do site estar online | Sempre funciona |
| **Servidor** | Precisava de internet extra | Funciona offline entre jogadores |
| **Preview** | Não tinha | Modelo 3D em tempo real |
| **Detecção** | Não mostrava outros jogadores | Lista jogadores com o mod |

---

## Código Fonte

- Branch principal: [capeall-1.8.9](https://github.com/Nonopichy/CapeAll/tree/capeall-1.8.9)
- Forge 1.8.9 + SpongePowered Mixins
- Licença: LGPL v2.1

---

## FAQ

**Outros jogadores sem o mod veem alguma coisa estranha?**
> Durante a transmissão (~1-2 segundos a cada 30s), as partes da skin do jogador podem piscar brevemente. Fora isso, tudo normal.

**Funciona em servidores com anti-cheat?**
> Sim. O mod apenas altera as camadas visíveis da skin, que é um recurso vanilla do Minecraft. Nenhum pacote customizado é enviado.

**Preciso de internet?**
> Não para o mod em si. Apenas a conexão normal com o servidor Minecraft.

**Posso usar capas personalizadas?**
> As 61 capas incluídas são texturas embutidas no mod. Para adicionar novas, coloque a textura em `assets/minecraft/textures/cape/` e registre no `CapeRegistry.java`.

---

*Feito por [Nonopichy](https://github.com/Nonopichy)*
