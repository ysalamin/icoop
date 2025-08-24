package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.play.math.DiscreteCoordinates;


/**
 * Classe contenant les positions de spawn
 */
public class SpawnPosition {

    // Position de départ du personnage de feu
    DiscreteCoordinates fireSpawn;

    // Position de départ du personnage d'eau
    DiscreteCoordinates waterSpawn;

    /**
     * Constructeur de SpawnPosition
     * @param fireSpawn (Coordonnées) Spawn du joueur de feu
     * @param waterSpawn (Coordonnées) Spawn du joueur d'eau
     */
    public SpawnPosition(DiscreteCoordinates fireSpawn, DiscreteCoordinates waterSpawn) {
        this.fireSpawn = fireSpawn;
        this.waterSpawn = waterSpawn;
    }

    /**
     * Getter du spawn du joueur rouge
     * @return
     */
    public DiscreteCoordinates getFireSpawn() {
        return fireSpawn;
    }

    /**
     * Getter du spawn du joueur bleu
     * @return
     */
    public DiscreteCoordinates getWaterSpawn() {
        return waterSpawn;
    }


}
