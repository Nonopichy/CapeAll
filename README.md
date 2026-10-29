https://legacy.curseforge.com/minecraft/mc-mods/capeall
# CapeAll v2.1 — Capas sem servidor (1.8.9, 1.20.2, 1.21.1 e 26.3)
<img width="1920" height="991" alt="2026-03-20_03 53 40" src="https://github.com/user-attachments/assets/8e7fab25-f5a4-4653-9a78-c749dd143018" />

> **Capas para todos os jogadores, em qualquer servidor, sem depender de nenhum site ou servidor externo.**

---

## O que mudou na v2.1?

A v2.0 só rodava no Forge 1.8.9. A **v2.1 leva o CapeAll para o Minecraft moderno**: **Forge 1.20.2** e **Fabric, Forge e NeoForge** no **1.21.1** e **26.3**.

O protocolo é o mesmo da v2.0, com os mesmos IDs de capa, então jogadores em loaders diferentes (por exemplo Fabric e NeoForge na mesma versão do Minecraft) veem as capas uns dos outros.

- **Tecla I** — no Minecraft moderno o P abre *Interações sociais*; a tecla pode ser trocada em Controles
- **Recepção mais confiável** — cada frame do protocolo é lido no meio, então atraso de rede não perde nem troca a capa
- **Preview de verdade** — o modelo 3D do menu mostra a capa que está sob o mouse
- **Skin intacta** — se você desconectar no meio de uma transmissão, suas skin layers reais voltam
- **Config nova** — `config/capeall.properties`, igual em todos os loaders

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
3. **Requisito:** o loader da sua versão do Minecraft

| Minecraft | Loader | Arquivo |
|---|---|---|
| 1.8.9 | Forge | `capeall-2.0.jar` ([v2.0](https://github.com/Nonopichy/CapeAll/releases/tag/2.0)) |
| 1.20.2 | Forge | `capeall-forge-1.20.2-2.1.jar` |
| 1.21.1 | Fabric (+ [Fabric API](https://modrinth.com/mod/fabric-api)), Forge ou NeoForge | `capeall-<loader>-1.21.1-2.1.jar` |
| 26.3 | Fabric (+ [Fabric API](https://modrinth.com/mod/fabric-api)), Forge ou NeoForge | `capeall-<loader>-26.3-2.1.jar` |

> Não é mais necessário acessar nenhum site. Tudo é configurado dentro do jogo.

---

## Como usar

### Escolhendo sua capa

1. Entre em qualquer servidor
2. Pressione **I** para abrir o menu do CapeAll (no 1.8.9 é **P**)
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
| 0 | `15` | 21 | `founde` | 42 | `realms` |
| 1 | `1cake` | 22 | `hp` | 43 | `scrolls` |
| 2 | `2011` | 23 | `japtrans` | 44 | `snowman` |
| 3 | `2012` | 24 | `migrator` | 45 | `spade` |
| 4 | `2013` | 25 | `mojang` | 46 | `squid` |
| 5 | `2015` | 26 | `mojira` | 47 | `studios` |
| 6 | `2016` | 27 | `noc` | 48 | `tc2010` |
| 7 | `360` | 28 | `nyan` | 49 | `test` |
| 8 | `awesom` | 29 | `oalblack` | 50 | `tik` |
| 9 | `bday` | 30 | `oblack` | 51 | `tpan` |
| 10 | `blonk` | 31 | `oblue` | 52 | `trans` |
| 11 | `bun1` | 32 | `ocyan` | 53 | `turtle` |
| 12 | `bun2` | 33 | `ogray` | 54 | `ty2011` |
| 13 | `bun3` | 34 | `ogreen` | 55 | `un1` |
| 14 | `cherry` | 35 | `opurple` | 56 | `un2` |
| 15 | `chitrans` | 36 | `ored` | 57 | `un3` |
| 16 | `classic` | 37 | `ostand` | 58 | `valentin` |
| 17 | `cobalt` | 38 | `owhite` | 59 | `vanilla` |
| 18 | `customer` | 39 | `oyellow` | 60 | `vete` |
| 19 | `db` | 40 | `ppride` | — | — |
| 20 | `dev00` | 41 | `prisma` | — | — |

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
│  2. SYNC       → 1010101 (sinal de início)      │
│  3. HEADER     → canal + tamanho dos dados      │
│  4. DATA       → bits do ID da capa             │
│  5. END        → 1111111 (sinal de fim)         │
│  6. RESTORE    → restaura as skin layers reais  │
│                                                 │
│  Cada frame = 5 ticks do jogo (~250ms)          │
│  Transmissão completa ≈ 1-2 segundos            │
│  Broadcast a cada 30 segundos                   │
│                                                 │
└─────────────────────────────────────────────────┘
```

Os outros clientes com CapeAll monitoram as skin layers dos jogadores próximos. Quando detectam a sequência `SYNC → HEADER → DATA → END`, decodificam o ID da capa e a exibem no modelo do jogador.

Na v2.1 cada frame é lido no meio, e não no primeiro tick: um atraso de rede de 1 ou 2 ticks não troca nem perde mais a capa.

---

## Configuração

O arquivo de configuração fica em `.minecraft/config/capeall.properties` (no 1.8.9, `capeall.cfg`):

| Opção | Padrão | Descrição |
|---|---|---|
| `selectedCape` | `mojang` | Capa selecionada |
| `ticksPerFrame` | `5` | Ticks entre cada frame do protocolo (3 a 10) |
| `broadcastInterval` | `600` | Ticks entre cada broadcast (600 = 30s, de 100 a 2400) |

---

## Comparação: v1.0 vs v2.0 vs v2.1

| | v1.0 (antiga) | v2.0 | v2.1 (atual) |
|---|---|---|---|
| **Dependência** | Site externo obrigatório | Nenhuma | Nenhuma |
| **Configuração** | Via navegador | Dentro do jogo (tecla P) | Dentro do jogo (tecla I) |
| **Funcionamento** | Consulta HTTP ao servidor | P2P via skin layers | P2P via skin layers (mesmo protocolo) |
| **Disponibilidade** | Depende do site estar online | Sempre funciona | Sempre funciona |
| **Servidor** | Precisava de internet extra | Funciona offline entre jogadores | Funciona offline entre jogadores |
| **Preview** | Não tinha | Modelo 3D em tempo real | Modelo 3D com a capa sob o mouse |
| **Detecção** | Não mostrava outros jogadores | Lista jogadores com o mod | Lista jogadores com o mod |
| **Versões** | 1.8.9 e 1.12.2 | 1.8.9 | 1.20.2, 1.21.1 e 26.3 |
| **Loaders** | Forge | Forge | Fabric, Forge e NeoForge |

---

## Código Fonte

- Branch 1.8.9: [capeall-1.8.9](https://github.com/Nonopichy/CapeAll/tree/capeall-1.8.9) — Forge 1.8.9 + SpongePowered Mixins
- Branch 1.20.2 / 1.21.1 / 26.3: [capeall-multiversion](https://github.com/Nonopichy/CapeAll/tree/capeall-multiversion) — Fabric, Forge e NeoForge + Mixins, com o núcleo compartilhado entre as versões
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
> As 61 capas incluídas são texturas embutidas no mod. Para adicionar novas, coloque a textura em `assets/minecraft/textures/cape/` e registre no `CapeRegistry.java` (na v2.1 os dois ficam em `core/`).

**Jogadores em loaders diferentes se veem?**
> Sim. Fabric, Forge e NeoForge usam o mesmo protocolo e os mesmos IDs de capa; basta estarem na mesma versão do Minecraft.

**Por que a tecla mudou de P para I?**
> No Minecraft moderno o P abre *Interações sociais*. Se preferir outra tecla, troque em Controles.

---

*Feito por [Nonopichy](https://github.com/Nonopichy)*
