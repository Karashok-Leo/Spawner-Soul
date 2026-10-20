package karashokleo.spawner_soul.spawner;

/** Persistent state added only to vanilla spawner block entities and their subclasses. */
public interface SpawnerState {

    int spawnerSoul$getRemainingSpawns();

    void spawnerSoul$setRemainingSpawns(int remainingSpawns);
}
