package ch.epfl.cs107.icoop.actor.Collectable;

import ch.epfl.cs107.icoop.ElementalEntity;
import ch.epfl.cs107.icoop.enums.Element;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;


/**
 * Sous-classe de ICoopCollectable, un ElementalItem est un item ayant un élément associé
 */
public abstract class ElementalItem extends ICoopCollectable implements Logic, ElementalEntity {

    // Element de l'item
    Element elementalType;
    
    /**
     * Constructeur d'un ElementalItem
     * @param area (Aire de l'item) non nulle.
     * @param orientation (Orientation de l'item) non nulle.
     * @param position (Coordonnées) non nulle
     * @param elementalType (Element de l'item)
     * @param isStockable (Peut-il être stocké dans l'inventaire ?)
     */
    public ElementalItem(Area area, Orientation orientation, DiscreteCoordinates position, Element elementalType, boolean isStockable) {
        super(area, orientation, position, isStockable);
        this.elementalType = elementalType;
    }

    @Override
    public boolean isOn() {
        // Retourne true si l'item est collecté
        return isCollected();
    }

    @Override
    public boolean isOff() {
        // Retourne True si l'item n'est pas collecté
        return !isCollected();
    }


    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }


    /**
     * Collecte l'item, après avoir vérifié que son collecteur est du même élément
     * @param entity
     */
    public void collectBy(ElementalEntity entity){
        if (entity.getElement().equals(this.getElement())){
            collect();
        }
    }

}

