package ch.epfl.cs107.icoop.actor.Collectable;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Hearth représente des coeurs collectables, rendant des points de vie au joueur
 */
public class Heart extends ICoopCollectable {

    // Durée de leur animation
    final static int  ANIMATION_DURATION = 24;

    // Animation
    final private Animation animation;


    /**
     * Constructeur de coeur
     * @param area (Aire du coeur) non nulle
     * @param position (Coordonnées) non nulle
     */
    public Heart(Area area, DiscreteCoordinates position) {
        
        super(area, Orientation.DOWN, position, false);
        this.animation =  new Animation("icoop/heart", 4, 1, 1, this , 16, 16,
                ANIMATION_DURATION/4, true);
    }

    @Override
    public void drawCollectable(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction){
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
    }

    @Override
    public ICoopItem getInventoryItem() {

        // Retourne null car il n'y a pas
        // d'article d'inventaire correspondant
        return null;
    }
}
