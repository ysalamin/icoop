package ch.epfl.cs107.icoop.actor.Collectable;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
* Représente les items ramassables de notre jeu
*/
public abstract class ICoopCollectable extends CollectableAreaEntity {

    // Indique si l'objet est stockable dans l'inventaire
    private final boolean isStockable;

/**
 * Constructeur des ICoopCollectables
 * @param area (Aire des collectables) non nulle
 * @param orientation (Orientation) non nulle
 * @param position (Coordonnées) non nulle
 * @param isStockable (Est-il stockable dans l'inventaire ?)
 */
    public ICoopCollectable(Area area, Orientation orientation, DiscreteCoordinates position, boolean isStockable) {
        super(area, orientation, position);
        this.isStockable = isStockable; 
    }

    
    /**
     * Getter de la stocabilité
     * @return (boolean) vrai si l'item est stockable
     */
    public boolean isStockable(){
        return isStockable;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        // Fonction par défaut pout le modèle visiteur
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);

    }

    /**
     * Donne l'article d'inventaire associé aux objets collectables
     * @return
     */
    public abstract ICoopItem getInventoryItem();

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        // La seule Cell est la principale
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isCollected()) {
            drawCollectable(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {

        // Désenregistre l'item si il est collecté
        if (isCollected() ) {
            getOwnerArea().unregisterActor(this);
        }
    }

    public abstract void  drawCollectable(Canvas canvas);

    
    @Override
    public boolean takeCellSpace() {
        // Par défaut un Collectable est traversable.
        return false;
    }

    
    @Override
    public boolean isCellInteractable() {
        // Uniquement des intéractions de contact à ce niveau
        return true;
    }

    
    @Override
    public boolean isViewInteractable() {
        // Uniquement des intéractions de contact à ce niveau
        return false;
    }

}
