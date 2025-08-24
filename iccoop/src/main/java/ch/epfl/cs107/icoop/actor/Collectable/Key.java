package ch.epfl.cs107.icoop.actor.Collectable;

import ch.epfl.cs107.icoop.enums.Element;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
Représente les clés élémentaires
*/
public class Key extends ElementalItem {

    // Image de la clé
    final private Sprite sprite;

    @Override
    public boolean isOn() {
        return isCollected();
    }

    @Override
    public boolean isOff() {
        return !isCollected();
    }

    /**
     * Constructeur des clés
     * @param area (Aire) non nulle
     * @param position (Coordonnées) non nulle
     * @param elementalType
     * @param isStockable
     */
    public Key(Area area, DiscreteCoordinates position, Element elementalType) {
        super(area, Orientation.DOWN, position, elementalType, false);

        // Le sprite s'adapte à l'élément
        if (elementalType == Element.FIRE) {
            this.sprite = new Sprite("icoop/key_red", 0.6f, 0.6f, this);
        } else {
            this.sprite = new Sprite("icoop/key_blue", 0.6f, 0.6f, this);
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }


    @Override
    public Element getElement() {
        return elementalType;
    }

    @Override
    public void drawCollectable(Canvas canvas) {
        sprite.draw(canvas);
    }

    @Override
    public ICoopItem getInventoryItem() {
        return elementalType.equals(Element.FIRE) ? ICoopItem.FIREKEY : ICoopItem.WATERKEY;
    }

}
