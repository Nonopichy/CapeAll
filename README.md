# CapeAll — Fabric / Forge / NeoForge (1.20.2, 1.21.1 e 26.3)

Capas para todos os jogadores, em qualquer servidor vanilla, sem servidor externo: os clientes com o mod
trocam o ID da capa entre si pelas **skin layers** (veja o [README principal](https://github.com/Nonopichy/CapeAll)).

| Minecraft | Fabric | Forge | NeoForge |
|---|---|---|---|
| 1.20.2 | — | ✔ | — |
| 1.21.1 | ✔ (precisa do Fabric API) | ✔ | ✔ |
| 26.3   | ✔ (precisa do Fabric API) | ✔ | ✔ |

Tecla padrão: **I** (dá pra trocar em Controles). O `P` do 1.8.9 virou o atalho vanilla de *Interações sociais*.
Config: `config/capeall.properties` (`selectedCape`, `ticksPerFrame`, `broadcastInterval`).
O protocolo é o mesmo do 1.8.9 (mesmos IDs de capa).

## Build

```
./gradlew build
```

Precisa só de Java 17+ pra rodar o `gradlew`; o resto o Gradle baixa sozinho (JDK 25 pro daemon, 21/25 pra compilar).
Os jars saem em `versions/<minecraft>/<loader>/build/libs/capeall-<loader>-<minecraft>-<versão>.jar`.

## Estrutura

```
core/                       Java puro, sem Minecraft: protocolo, capas, config, layout/cliques do menu
                            + texturas, lang e capeall.mixins.json (iguais em todas as versões)
versions/<mc>/
  version.properties        versões do Minecraft, Java e de cada loader
  common/                   código que muda por versão do Minecraft (vanilla only):
                              CapeAllClient  (implementa Platform: ler/mudar skin layers, chat, tecla)
                              gui/CapeScreen (implementa CapeMenu.Canvas: fill, text, blit, modelo 3D...)
                              mixin/         (getSkin -> capa, tick)
  fabric|forge|neoforge/    só o entrypoint (~15 linhas) + pack.mcmeta no Forge
loaders/                    fabric.mod.json, mods.toml, neoforge.mods.toml (templates compartilhados)
build.gradle                configura todo versions/<mc>/<loader> automaticamente
```

`core` compila sozinho sem Minecraft no classpath — se algo de versão vazar pra lá, o build quebra.
O teste do protocolo (`core/src/test`) roda no `./gradlew build`.

## Nova versão do Minecraft

1. Copie a pasta da versão mais próxima: `versions/26.3` → `versions/<nova>`.
2. Ajuste `version.properties` (Minecraft, Java, Fabric API, Forge, NeoForge).
3. `./gradlew build` e corrija o que quebrar — quase sempre só `common/` (nomes do vanilla mudam) e,
   às vezes, o entrypoint do loader. `core/` não muda.
4. Forge: o `pack.mcmeta` da pasta forge leva o formato de pack da versão.

Loader que não existir pra versão: apague a pasta dele, o `settings.gradle` só inclui as que existem.
Versões até 1.21.x usam mapeamento Mojang + remap no Fabric; 26.x em diante não têm ofuscação.
Forge até 1.20.4 roda com nomes SRG (1.20.6+ já usa os nomes da Mojang): põe `forge_legacy=true` no `version.properties` e o build gera o refmap
do Mixin e reobfusca o jar (o que o ForgeGradle 6 fazia), usando o mapeamento oficial→SRG do próprio ForgeGradle 7.

## Mudanças em relação ao v2.0 (1.8.9)

- Receptor lê cada frame no meio, não na borda: 1 tick de atraso de rede fazia perder ou trocar a capa.
- Preview do menu agora mostra a capa sob o mouse (antes sempre mostrava a selecionada).
- Se desconectar no meio de uma transmissão, as skin layers reais voltam.
- Config em `capeall.properties` (antes `capeall.cfg` do Forge).
