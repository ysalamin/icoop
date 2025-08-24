package ch.epfl.cs107.icoop;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

/**
 * Représente la porte du manoir qui possède un dialogue
 */
public class DialogDoor extends AreaEntity implements Interactor {

    // Gestionnaire de dialogue basique
    final private DialogHandler dialogHandler;

    // Gestionnaire d'intéraction
    final private DialogDoorHandler interactionHandler = new DialogDoorHandler();

    // Attribut qui représente la clé de la porte
    final private Logic key;

    // Indique si un dialogue a commencé
    private boolean dialogHasBeenStarted;


    /**
     * Constructeur de DialogDoor
     * @param area (Aire) non nulle
     * @param position (Coordonnée) non nulle
     * @param dialogHandler (Gestionnaire de dialogue)
     */
    public DialogDoor(Area area, DiscreteCoordinates position, DialogHandler dialogHandler, Logic key) {
        super(area, Orientation.DOWN, position);
        this.dialogHandler = dialogHandler;
        this.key = key;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(Orientation.DOWN.toVector()));
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * Gestionnaire des des dialogues de portes
     */
    private final class DialogDoorHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {


            // Si la porte voit partir le player ou le voit arriver
            // elle met le dialogue en mode pas commencé
            if (!isCellInteraction) {
                if (player.isMoving()) {
                    dialogHasBeenStarted = false;
                }
            }

            // Si le dialogue n'a pas encore commencé et que le player
            // est sur la porte on peut lancer le dialogue
            if (!dialogHasBeenStarted && isCellInteraction) {
                dialogHasBeenStarted = true;

                Dialog dialog = key.isOn() ? new Dialog("victory") : new Dialog("key_required");

                dialogHandler.publish(dialog);
            }
        }
    }
}
