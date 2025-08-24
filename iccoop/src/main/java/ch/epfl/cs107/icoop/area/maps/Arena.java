package ch.epfl.cs107.icoop.area.maps;

import java.util.Arrays;

import ch.epfl.cs107.icoop.actor.Collectable.Key;
import ch.epfl.cs107.icoop.actor.Teleporter;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.area.SpawnPosition;
import ch.epfl.cs107.icoop.enums.Element;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

/**
 * Représente la map finale : l'arène !
 */
public class Arena extends ICoopArea implements Logic {

    private Teleporter teleporter;

    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        // Clés
        Key fireKey = new Key(this, new DiscreteCoordinates(9, 16), Element.FIRE);
        Key waterKey = new Key(this, new DiscreteCoordinates(9, 4), Element.WATER);

        registerActor(fireKey);
        registerActor(waterKey);

        // Portail
        teleporter = new Teleporter(
                "Spawn", Logic.TRUE, Arrays.asList(Spawn.SPAWN_POSITION.getFireSpawn(), Spawn.SPAWN_POSITION.getWaterSpawn()), this,
                new DiscreteCoordinates(10, 11),
                fireKey,
                waterKey
        );
        registerActor(teleporter);

    }


    public final static SpawnPosition SPAWN_POSITION = new SpawnPosition(
            new DiscreteCoordinates(4, 5),
            new DiscreteCoordinates(14, 15)
    );

    @Override
    public DiscreteCoordinates getPlayerSpawnPosition(Element element) {
        if (element == Element.FIRE) {
            return SPAWN_POSITION.getFireSpawn();
        }
        return SPAWN_POSITION.getWaterSpawn();
    }

    @Override
    public String getTitle() {
        return "Arena";
    }

    @Override
    public boolean isOn() {
        return teleporter.isOn();
    }

    @Override
    public boolean isOff() {
        return false;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);




    }
}
