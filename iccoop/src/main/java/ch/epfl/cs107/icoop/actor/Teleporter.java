package ch.epfl.cs107.icoop.actor;

import java.util.List;

import ch.epfl.cs107.icoop.actor.Collectable.Key;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Représente les téléporteurs, une spécification des portes
 */
public class Teleporter extends Door implements Logic {

    // Image
    private final RPGSprite sprite;

    private final Key fireKey;
    private final Key waterKey;


     /**
     * Constructeur principal des portes
     * @param goToAreaName (Aire où mène la porte)
     * @param signal (Est-elle activée ?)
     * @param futurePositions (Coordonnées), position d'arrivée dans la future map
     * @param ownerArea (Aire où se situe la porte)
     * @param mainCellPosition (Position principale occupée par la porte)
     */
    public Teleporter(String goToAreaName, Logic signal, List<DiscreteCoordinates> futurePositions, Area ownerArea, DiscreteCoordinates mainCellPosition, Key fireKey, Key waterKey) {
        super(goToAreaName, signal, futurePositions, ownerArea, mainCellPosition);
        this.sprite = new RPGSprite("shadow", 1, 1, this , new RegionOfInterest(0, 0, 32,
                32));
        this.fireKey = fireKey;
        this.waterKey = waterKey;
    }

    @Override
    public void draw(Canvas canvas) {
        if (isOn()) {
            sprite.draw(canvas);
            super.draw(canvas);
        }
    }

    @Override
    public boolean isCellInteractable() {
        return new And(fireKey, waterKey).isOn();
    }

    @Override
    public boolean isOn() {
        return new And(fireKey, waterKey).isOn();
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }
}
