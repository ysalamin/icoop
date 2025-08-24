package ch.epfl.cs107.icoop.actor.Projectiles;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;


/**
 * Représente de manière abstraite les objets en mouvement inarrêtables
 */
public abstract class Unstoppable extends MovableAreaEntity implements Interactor {

    // Vitesse de déplacement
    private final int speed;

    // Distance restante avant de disparaître
    private int distanceLeft;

    // Variable indiquant si le déplacement est entrain de se faire
    private boolean isTravelling;

    // Durée de movement
    private final int MOVE_DURATION = 8;

    /**
     * Constructeur des "Unstoppable"
     * @param area (Aire) non nulle
     * @param orientation (Orientation) non nulle
     * @param position (Coordonnées) non nulle
     * @param speed (Vitesse)
     * @param maxDistance (Distance max)
     */
    public Unstoppable(Area area, Orientation orientation, DiscreteCoordinates position, int speed, int maxDistance) {
        super(area, orientation, position);
        this.speed = speed;
        this.distanceLeft = maxDistance;
        this.isTravelling = true;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        // Par défaut n'accepte pas les intéractions de contact
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        // Par défaut n'accepte pas les intéractions de contact
        return false;
    }


    @Override
    public boolean wantsCellInteraction() {
        // Veut des intéractions seulement si l'objet est en vol
        return isTravelling;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    /**
     * Arrête le mouvement
     */
    public void endMovement() {
        isTravelling = false;
    }


    @Override
    public void update(float deltaTime) {

        // Disparait lorsqu'il a fini sa course
        if (distanceLeft <= 0 || !isTravelling) {
    
            getOwnerArea().unregisterActor(this);

        } else  {

            // Continue de bouger
            move(MOVE_DURATION / speed);

            // A chaque cycle on diminue d'une unité la distance parcourue
            distanceLeft -= 1;
        }

        super.update(deltaTime);
    }

}
