package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.play.engine.actor.Dialog;

/**
 * Interface permettant à ceux qui l'implémentent de publier des dialogues
 */
public interface DialogHandler {

    /**
     * Publie un dialogue
     * @param dialog
     */
    void publish(Dialog dialog);

    /**
     * Retoune true si un dialogue est actuellement actif
     * @return
     */
    boolean isDialogActiv();
}
