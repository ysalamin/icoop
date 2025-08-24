package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Représente les plaques de pressions
 */
public class PressurePlate extends AreaEntity implements Logic, Interactor {

    // Images
    private final static String SPRITE_NAME = "GroundPlateOff";
    private final Sprite sprite;

    // Indique si un joueur marche sur la plaque
    private boolean isPressed;

    // Joueur marchant sur la plaque
    private ICoopPlayer currentPlayer;

    // Gestionnaire d'intéraction
    private final PressurePlateInteractionHandler interactionHandler = new PressurePlateInteractionHandler();

    /**
     * Constructeur
     * @param area (Aire) non nulle
     * @param position (Coordonnées) non  nulle
     */
    public PressurePlate(Area area, DiscreteCoordinates position) {
        super(area, Orientation.DOWN, position);
        this.isPressed = false;
        this.sprite = new Sprite(SPRITE_NAME, 1f, 1f, this);

        // Par défaut il n y a pas de player sur la plaque de pression.
        this.currentPlayer = null;
    }

    @Override
    public boolean isOn() {
        // La plaque est activée si elle n'est
        // pas pressée
        return !isPressed;
    }

    @Override
    public boolean isOff() {
        // elle est désactivée lorsqu'elle
        // est pressée
        return isPressed;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        // Il y a seulement la cell principale
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        // Le joueur doit pouvoir marcher dessus
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        // La plaque s'active si le joueur est directement dessus
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        // Pas d'intéraction à distance, il faut marcher sur la plaque uniquement
        return false;
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return List.of();
    }

    @Override
    public boolean wantsCellInteraction(){
        return true;
    }

    @Override
    public boolean wantsViewInteraction(){
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction){
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    @Override
    public void update(float deltaTime) {

        // Si le player en memoire n'est plus sur la position ou si il est null on remet le isPressed sur false
        if (currentPlayer == null) {
            isPressed = false;
        } else if (!currentPlayer.getCurrentCells().equals(this.getCurrentCells())) {
            isPressed = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }


    /**
     * Gestionnaire d'intéraction
     */
    private final class PressurePlateInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            // Associee le jour à l'attribut, et indique qu'elle est pressée
            PressurePlate.this.currentPlayer = player;
            isPressed = true;
        }
        }
}
