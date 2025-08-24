package ch.epfl.cs107.icoop.area.maps;

import java.util.Arrays;

import ch.epfl.cs107.icoop.actor.Collectable.Heart;
import ch.epfl.cs107.icoop.actor.Collectable.Staff;
import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.ElementalWall;
import ch.epfl.cs107.icoop.actor.Explosif;
import ch.epfl.cs107.icoop.actor.Foes.BombFoe;
import ch.epfl.cs107.icoop.actor.Foes.HellSkull;
import ch.epfl.cs107.icoop.actor.PressurePlate;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.area.SpawnPosition;
import ch.epfl.cs107.icoop.enums.Element;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;

/**
 * Représente la mab labyrinthe
 */
public final class Maze extends ICoopArea implements Logic {

    private Staff fireStaff;
    private Staff waterStaff;

    // Positions de départs
    public static final SpawnPosition SPAWN_POSITION = new SpawnPosition(
           // FIRE
           new DiscreteCoordinates(2, 39),
           // WATER
           new DiscreteCoordinates(3, 39)

    );


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

        // ----------------- DOORS ------------------

        Door arenaDoor = new Door(
                "Arena", Logic.TRUE,
                Arrays.asList(
                        Arena.SPAWN_POSITION.getFireSpawn(),
                        Arena.SPAWN_POSITION.getWaterSpawn()
                ),
                this,
                new DiscreteCoordinates(19, 7),
                new DiscreteCoordinates(19, 6)
                );

        registerActor(arenaDoor);

        // ----------------- PRESSURE PLATES ------------------
        PressurePlate firstPP = new PressurePlate(this, new DiscreteCoordinates(6, 33));
        registerActor(firstPP);
        PressurePlate secondPP = new PressurePlate(this, new DiscreteCoordinates(9, 25));
        registerActor(secondPP);

        // ----------------- WALLS ------------------
        // Attention aux paramètres
        // si le mur ne prend pas de paramètre de type LOGIC, il est considéré comme
        // toujours actif
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(4,35), Element.WATER));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(4,36), Element.WATER));

        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(6,35), Element.FIRE, firstPP)); //pression 4
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(7,36), Element.FIRE, firstPP)); //pression4

        registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(2,34), Element.FIRE));
        registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(3,34), Element.FIRE));        

        registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(5,24), Element.WATER));
        registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(6,24), Element.WATER));

        registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(8,21), Element.FIRE, secondPP)); //composant7
        
        registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(8,4), Element.WATER));
        registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(13,4), Element.FIRE));


        // ----------------- COEURS ------------------
        DiscreteCoordinates[] heartPositions = {
                new DiscreteCoordinates(15, 18),
                new DiscreteCoordinates(16, 19),
                new DiscreteCoordinates(14, 19),
                new DiscreteCoordinates(14, 17),
        };

        for (DiscreteCoordinates heartPosition : heartPositions) {
            registerActor(new Heart(this, heartPosition));
        }

        // ----------------- Explosifs ---------------
        registerActor(new Explosif(this, Orientation.DOWN, new DiscreteCoordinates(6, 25), 50));

        // ----------------- Skulls ---------------

        DiscreteCoordinates[] skullCoordinates =  {
                new DiscreteCoordinates(12, 33),
                new DiscreteCoordinates(12, 31),
                new DiscreteCoordinates(12, 29),
                new DiscreteCoordinates(12, 27),
                new DiscreteCoordinates(12,25),
                new DiscreteCoordinates(10, 33),
                new DiscreteCoordinates(10, 32),
                new DiscreteCoordinates(10, 30),
                new DiscreteCoordinates(10, 28),
                new DiscreteCoordinates(10, 26)
        };

        for (DiscreteCoordinates skullCoordinate : skullCoordinates) {
            registerActor(new HellSkull(this, Orientation.RIGHT, skullCoordinate));
        }

        // ----------------- Artificiers ---------------
        DiscreteCoordinates[] bombFoesCoordinates =  {
                new DiscreteCoordinates(5, 15),
                new DiscreteCoordinates(6, 17),
                new DiscreteCoordinates(10, 17),
                new DiscreteCoordinates(5, 14),
        };

        for (DiscreteCoordinates bombFoeCoord : bombFoesCoordinates) {
            registerActor(new BombFoe(this, bombFoeCoord));
        }

        // ----------------- Staffs ---------------

        fireStaff = new Staff(this, new DiscreteCoordinates(13, 2), Element.FIRE);
        waterStaff = new Staff(this, new DiscreteCoordinates(8, 2), Element.WATER);
        registerActor(fireStaff);
        registerActor(waterStaff);


    }

    @Override
    public String getTitle() {
        return "Maze";
    }

    @Override
    public boolean isOn() {
        return new And(fireStaff, waterStaff).isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }
}