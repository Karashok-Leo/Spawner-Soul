package karashokleo.spawner_soul.item;

/** Both loaders register this single shared item instance during their registration phase. */
public final class ModItems {

    public static final SpawnerSoulItem SPAWNER_SOUL = new SpawnerSoulItem();

    private ModItems() {
    }
}
