package ch.epfl.cs107.icoop.area.maps;

import java.util.Arrays;

import ch.epfl.cs107.icoop.DialogDoor;
import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.Explosif;
import ch.epfl.cs107.icoop.actor.Rock;
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
public final class Spawn extends ICoopArea {

    // Position de départs
    public static final SpawnPosition SPAWN_POSITION = new SpawnPosition(new DiscreteCoordinates(14, 6), new DiscreteCoordinates(13, 6));

    // Gestionnaire de Dialogue
    private final DialogHandler dialogHandler;

    // Attribut logic de Maze pour pouvoir
    // déverrouiller la porte du manoir
    private Logic mazeLogic;

    /**
     *  Constructeur de Spawn
     */
    public Spawn(DialogHandler dialogHandler, Logic mazeLogic) {
        this.dialogHandler = dialogHandler;
        this.mazeLogic = mazeLogic;
    }


    @Override
    public  DiscreteCoordinates getPlayerSpawnPosition(Element elementType) {
        DiscreteCoordinates coordinates = switch (elementType) {
            case FIRE -> SPAWN_POSITION.getFireSpawn();
            case WATER -> SPAWN_POSITION.getWaterSpawn();
        
        };
        return coordinates;
    }

    @Override
    protected void createArea() {

        // Back et Foregrounds
        registerActor(new Background(this));
        registerActor(new Foreground(this));


        // PORTES
        Door toOrbWayDoor = new Door(
            "OrbWay",                                                                // Aire vers laquelle la porte emmène
            Logic.TRUE,                                                                           // Toujours open
            Arrays.asList(OrbWay.SPAWN_POSITION.getFireSpawn(), OrbWay.SPAWN_POSITION.getWaterSpawn()), // les deux positions d'arrivées dans OrbWay après avoir pris la porte
            this,                                                                                 // Map actuelle, donc Spawn
            new DiscreteCoordinates(19,15),                                                   // Cellule principale de la porte (une des deux "cases" rouges)
            new DiscreteCoordinates(19,16)                                                    // Autre cellule de la porte (l'autre "case" rouge)
            );

        Door toMazeDoor = new Door(
            "Maze",                                                                // Aire vers laquelle la porte emmène
            Logic.TRUE,                                                                           // Toujours open
            Arrays.asList(Maze.SPAWN_POSITION.getFireSpawn(), Maze.SPAWN_POSITION.getWaterSpawn()), // les deux positions d'arrivées dans OrbWay après avoir pris la porte
            this,                                                                                 // Map actuelle, donc Spawn
            new DiscreteCoordinates(4,0),                                                   // Cellule principale de la porte (une des deux "cases" rouges)
            new DiscreteCoordinates(5,0)                                                    // Autre cellule de la porte (l'autre "case" rouge)
            );


        // Porte du manoir
        DialogDoor finalDoor = new DialogDoor(this, new DiscreteCoordinates(6, 11), dialogHandler, mazeLogic);


        // Register des portes
        registerActor(toOrbWayDoor);
        registerActor(toMazeDoor);
        registerActor(finalDoor);



        // Création du rock et de l'explo
        Rock rock = new Rock(this, Orientation.DOWN, new DiscreteCoordinates(10, 10 ) );
        Explosif explo = new Explosif(this, Orientation.DOWN, new DiscreteCoordinates(11, 10), 3);

        registerActor(rock);
        registerActor(explo);
    }

    @Override
    public String getTitle() {
        return "Spawn";
    }



}