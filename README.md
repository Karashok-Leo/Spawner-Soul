# Spawner Soul · 笼中魄

A standalone Minecraft **1.20.1** mod for **Fabric** and **Forge** by Karashok-Leo.

Current stage: a minimal, shared multi-loader baseline. Gameplay, configuration,
recipes, and compatibility integrations are not implemented yet.

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
