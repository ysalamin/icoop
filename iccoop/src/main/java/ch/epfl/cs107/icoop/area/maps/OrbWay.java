package ch.epfl.cs107.icoop.area.maps;

import java.util.Arrays;

import ch.epfl.cs107.icoop.actor.Collectable.Heart;
import ch.epfl.cs107.icoop.actor.Collectable.Orb;
import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.ElementalWall;
import ch.epfl.cs107.icoop.actor.PressurePlate;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.area.SpawnPosition;
import ch.epfl.cs107.icoop.enums.Element;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

/**
 * A class that represent the inital spawn area
 */
public final class OrbWay extends ICoopArea implements Logic {

    // On a besoin d'une variable static car des fois,
    // on veut le spawn sans initialiser l'objet donc
    // le getter ci-dessous ne suffit pas
    public static final SpawnPosition SPAWN_POSITION = new SpawnPosition(
            // FIRE
            new DiscreteCoordinates(1, 12),
            // WATER
            new DiscreteCoordinates(1, 5)
    );

    // Gestionnaire des dialogues
    private final  DialogHandler dialogHandler;

    /**
     * Constructeur de OrbWay
     * @param dialogHandler
     */
    public OrbWay(DialogHandler dialogHandler) {
        this.dialogHandler = dialogHandler;
    }

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition(Element elementType) {
        DiscreteCoordinates coordinates = switch (elementType) {
            case FIRE -> SPAWN_POSITION.getFireSpawn();
            case WATER -> SPAWN_POSITION.getWaterSpawn();
        };

        return coordinates;
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        DiscreteCoordinates fireSpawnReturnCoords = new DiscreteCoordinates(18, 16);
        DiscreteCoordinates waterSpawnReturnCoords = new DiscreteCoordinates(18, 15);

        // ----------------- DOORS ------------------
        Door orbWayDoor1 = new Door(
            "Spawn",                                                                    // Aire vers laquelle la porte emmène
            Logic.TRUE,                                                                              // Toujours open
            Arrays.asList(fireSpawnReturnCoords, waterSpawnReturnCoords), // les deux positions d'arrivées dans OrbWay après avoir pris la porte
            this,                                                                                    // Map actuelle, donc Spawn
            new DiscreteCoordinates(0,14),                                                       // Cellule principale de la porte (une des deux "cases" rouges)
            new DiscreteCoordinates(0,13),
            new DiscreteCoordinates(0,12),                                                       // Autres cellules occupées par les portes
            new DiscreteCoordinates(0,11),      
            new DiscreteCoordinates(0,10)                                                    
            );



        Door orbWayDoor2 = new Door(
            "Spawn",                                                                    // Aire vers laquelle la porte emmène
            Logic.TRUE,                                                                              // Toujours open
            Arrays.asList(fireSpawnReturnCoords, waterSpawnReturnCoords), // les deux positions d'arrivées dans OrbWay après avoir pris la porte
            this,                                                                                    // Map actuelle, donc Spawn
            new DiscreteCoordinates(0,8),                                                        // Cellule principale de la porte (une des deux "cases" rouges)
            new DiscreteCoordinates(0,7),
            new DiscreteCoordinates(0,6),                                                        // Autres cellules occupées par les portes
            new DiscreteCoordinates(0,5),      
            new DiscreteCoordinates(0,4)                                                    
            );
        
        registerActor(orbWayDoor1);
        registerActor(orbWayDoor2);

        // ----------------- ORBS ------------------
        DiscreteCoordinates fireOrbCoord = new DiscreteCoordinates(17, 12);
        DiscreteCoordinates waterOrbCoord = new DiscreteCoordinates(17, 6);
        Orb fireOrb = new Orb(this, fireOrbCoord, Element.FIRE, dialogHandler);
        Orb waterOrb = new Orb(this, waterOrbCoord, Element.WATER, dialogHandler);

        registerActor(fireOrb);
        registerActor(waterOrb);


        // ----------------- PRESSURE PLATES ------------------
        PressurePlate plate1 = new PressurePlate(this, new DiscreteCoordinates(5, 7));
        PressurePlate plate2 = new PressurePlate(this, new DiscreteCoordinates(5, 10));
        registerActor(plate1);
        registerActor(plate2);



        // ----------------- WALLS ------------------
        for (int i = 0; i < 5; i++) {
            registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12, 10+i), Element.FIRE, plate1));
            registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12, 4+i), Element.WATER, plate2));
        }

        // Deux murs de tests
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(7, 6), Element.FIRE));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(7, 12), Element.WATER));

        // ----------------- COEURS ------------------
        DiscreteCoordinates[] heartPositions = {
                new DiscreteCoordinates(8, 4),
                new DiscreteCoordinates(10, 6),
                new DiscreteCoordinates(5, 13),
                new DiscreteCoordinates(10, 11),
        };

        for (DiscreteCoordinates heartPosition : heartPositions) {
            registerActor(new Heart(this, heartPosition));
        }

    }

    @Override
    public String getTitle() {
        return "OrbWay";
    }

    @Override
    public boolean isOn() {
        // Le défi est toujours gagné
        // dans la map orbway
        return true;
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }
}