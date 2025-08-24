package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Représente les rochers, un type d'obstacle
 */
public class Rock extends Obstacle {

    // Indique s'il est détruit
    private boolean isDestroyed;

    /**
     * Constructeur du rocher
     * @param area (Aire) non nulle
     * @param orientation (Orientation) non nulle
     * @param position (Coordonnées) non nulle
     */
    public Rock(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, "rock.1");
        this.isDestroyed = false;
    }

    @Override
    public boolean takeCellSpace() {
        return !isDestroyed;
    }

    @Override
    public void draw(Canvas canvas) {
        // Se dessine seulement s'il n'est pas déruit
        if (!isDestroyed) {
            super.draw(canvas);
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction){
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * Détruit le rocher ( change sa variable )
     */
    public void destroy(){
        isDestroyed = true;

        // on l'enlève pour que les projectiles
        // puissent passer à travers
        getOwnerArea().unregisterActor(this);
    }
}
