# Spawner Soul · 笼中魄

A standalone Minecraft **1.20.1** mod for **Fabric** and **Forge** by Karashok-Leo.

Current stage: core spawner counts, soul interactions, and reloadable configuration.
Recipes, recipe types, third-party UI integrations, and publishing are not implemented.
Install the mod on **both client and server**; the server decides gameplay results.

## Core behavior

- Each successfully added primary spawner entity costs one remaining spawn; riding
  passengers do not cost extra (the vanilla spawn-attempt unit). Failed attempts
  and cancelled entity joins cost nothing. New spawners start at **20**; **-1** is infinite.
- Exhausted spawners remain visibly empty by default; configuration can destroy them
  instead. Empty cages can be refilled with a soul.
- Normal survival-player mining with a suitable tool drops one **Spawner Soul / 笼中魄**
  carrying the entity type and all remaining spawns. Empty/invalid cages drop no soul.
  Creative breaking never produces a soul. No soul or cage crafting is supplied.
- Use a soul on a spawner to replace its count (default) or add counts for the same
  entity. Different entities may replace the old entity and count by default, but
  never inherit the old count. Empty cages always accept a valid soul.
- Successful use consumes one soul, except in creative mode. Rejected, invalid,
  overflowing, or no-change uses consume nothing. In ADD mode, infinity absorbs
  finite counts; replacing infinity with a finite count is allowed by default.
- Spawn eggs retain vanilla entity changes, but do not replenish/reset the count.
  The scope is vanilla spawner block entities and subclasses reusing their behavior;
  minecart spawners and unrelated custom implementations are not changed.

## Configuration

One instance-wide file, `config/spawner_soul.toml`, is created on first startup only
when absent. These are the only settings:

```toml
defaultSpawnCount = 20               # -1 = infinite; otherwise >= 0
exhaustionBehavior = "KEEP_EMPTY"     # KEEP_EMPTY or DESTROY
dropsEnabled = true
dropOnlyPlayerMining = true          # false also permits normal destruction loot
sameEntityBehavior = "REPLACE"       # REPLACE or ADD
allowDifferentEntityReplacement = true
allowInfiniteToFinite = true
```

Run `/spawner_soul reload` with permission level **2** (or from the server console)
after editing. The complete candidate is parsed, mapped to a fresh data object,
and validated before replacing the current snapshot. Invalid syntax, unknown keys,
wrong types, unknown enum names, and invalid counts reject the whole reload. Failure
retains the old in-memory settings and **never rewrites your edited file**. Reload
also does not recreate a deleted file. Invalid existing files at startup are logged,
left untouched, and use in-memory defaults until a successful reload.

Reloading default counts does not change initialized spawners. Changing the exhaustion
policy does not immediately remove existing empty cages. Broad drop mode follows actual
normal destruction paths, not `/loot ... mine` queries; destruction with drops disabled
still drops nothing. The drop-only-player option means survival, not creative/adventure.

NightConfig **3.6.4** is intentionally shared with Forge 1.20.1's bundled version.
Fabric includes separate nested core/TOML libraries; see [third-party notices](THIRD_PARTY_NOTICES.md).

## Development

Use JDK **17**. The Gradle wrapper is included, and both loaders use Parchment
**1.20.1 / 2023.09.03** mappings. Gradle can provision additional toolchains
required by its build plugins; the mod bytecode still targets Java 17.

```sh
./gradlew build
```

On Windows, use `gradlew.bat build`. Loader-specific jars are written to
`fabric/build/libs/` and `forge/build/libs/`; the `common` jar is not a playable mod.

- `common`: shared code and resources, without loader-specific APIs.
- `fabric`: Fabric entry point, platform implementation, and metadata.
- `forge`: Forge entry point, platform implementation, and metadata.

Development clients can be started with `./gradlew :fabric:runClient` or
`./gradlew :forge:runClient`. Runtime data and build outputs are not committed.

## License and attribution

This project uses [LGPL 2.1](LICENSE), copied unchanged from
[Spell Dimension Mod](https://github.com/Karashok-Leo/Spell-Dimension-Mod).
The soul texture is also reused unchanged from that project:
`src/main/resources/assets/spell-dimension/textures/item/spawner_soul.png`
(at commit `e0ebecccdea63f0b26935b3baee6c271084aa401`).

The build and platform skeleton are based on
[MultiLoader Template, branch 1.20.1](https://github.com/jaredlll08/MultiLoader-Template/tree/1.20.1)
(at commit `d6b81d85d63566cbe5e67fd2f246f1441cccf686`). Its original
[CC0 1.0 license](LICENSES/MultiLoader-Template-CC0-1.0.txt) is preserved.
