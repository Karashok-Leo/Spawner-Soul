# Third-party notices

The Spawner Soul project remains **LGPL-2.1-only**, under [LICENSE](LICENSE).
The dependencies below retain their own licenses; the project license does not
relicense them. Existing template and texture attribution is in [README.md](README.md).

## NightConfig 3.6.4

- Author: TheElectronWill and contributors.
- Modules: `com.electronwill.night-config:core:3.6.4` and
  `com.electronwill.night-config:toml:3.6.4`.
- License: GNU Lesser General Public License, version 3.0. Its accompanying
  GNU General Public License, version 3.0, is also supplied.
- Verbatim texts: [LGPLv3](LICENSES/NightConfig-LGPL-3.0.txt) and
  [GPLv3](LICENSES/NightConfig-GPL-3.0.txt).

The LGPL text is copied unchanged from the
[NightConfig v3.6.4 license](https://github.com/TheElectronWill/night-config/blob/v3.6.4/LICENSE).
The GPL text is the standard
[GNU GPL version 3](https://www.gnu.org/licenses/gpl-3.0.txt), copied verbatim
from the build environment's system license text.
The [published core POM](https://repo.maven.apache.org/maven2/com/electronwill/night-config/core/3.6.4/core-3.6.4.pom)
identifies the library's LGPLv3 license.

### Packaging

The Fabric build includes `core` and `toml` as separate nested jars. Their library
code is unmodified and is not shaded or relocated. Fabric Loom adds loader
metadata to those jars; they are not byte-identical to the upstream archives.
The Forge build does not bundle another copy: Forge **1.20.1-47.2.30**
[supplies both modules at version 3.6.4](https://maven.minecraftforge.net/net/minecraftforge/fmlloader/1.20.1-47.2.30/fmlloader-1.20.1-47.2.30.pom).

### Source and replacement

The exact library source and build files are available at
[tag v3.6.4](https://github.com/TheElectronWill/night-config/tree/v3.6.4)
([source archive](https://github.com/TheElectronWill/night-config/archive/refs/tags/v3.6.4.tar.gz)).
The published module sources are also available for
[core](https://repo.maven.apache.org/maven2/com/electronwill/night-config/core/3.6.4/core-3.6.4-sources.jar)
and [toml](https://repo.maven.apache.org/maven2/com/electronwill/night-config/toml/3.6.4/toml-3.6.4-sources.jar).

You may modify the library and rebuild/relink Spawner Soul against an
interface-compatible version. Point the Gradle dependencies at your modified
artifacts and rebuild using the project source and instructions in README.
Fabric's replacement libraries must retain or regenerate their loader metadata.
On Forge, the shared replacement must also be compatible with Forge itself.
No additional restriction on library modification or reverse engineering for
debugging such modifications is imposed by this project.

Distributors of binaries must keep these notices and both license texts with
the binaries, and provide the corresponding library source and application
source/build files with the access required by those licenses. Preserve access
to the exact source version when publishing a release.
