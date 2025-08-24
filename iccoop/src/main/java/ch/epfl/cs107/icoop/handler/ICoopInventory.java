package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.play.areagame.handler.Inventory;

/**
 * Inventaire de notre jeu, ne contenant qu'une seule poche
 */
public class ICoopInventory extends Inventory {

    public ICoopInventory(){
        super("OnlyPocket");
    }

}
