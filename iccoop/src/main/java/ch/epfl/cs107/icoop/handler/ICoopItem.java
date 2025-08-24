package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.play.areagame.handler.InventoryItem;

/**
 * Enum des différents modèles d'articles d'inventaires
 */
public enum ICoopItem implements InventoryItem {

    SWORD("Sword", 0, "icoop/sword.icon"),
    FIREKEY("FireKey", 0,  "icoop/key_blue"),
    WATERKEY("WaterKey", 0, "icoop/key_red"),
    FIRESTAFF("FireStaff", 0, "icoop/staff_fire.icon"),
    WATERSTAFF("WaterStaff", 0, "icoop/staff_water.icon"),
    EXPLOSIVE("Explosive", 0, "icoop/explosive");

    // Nom de l'item
    private final String name;

    // Poche dans laquelle il sera contenu (0 ici car qu'une seule poche est disponible)
    private final int pocketId;

    // Path du sprite
    private final String path;

    /**
     * Constructeur de l'enum
     * @param name (Nom de l'item)
     * @param pocketId (Numéro de poche)
     * @param path (Chemin d'accès)
     */
    ICoopItem(String name, int pocketId, String path){
        this.name = name;
        this.pocketId = pocketId;
        this.path = path;
    }

    /** @return (int): pocket id, the item is referred to */
    @Override
    public int getPocketId(){
        return pocketId;
    }

    /** @return (String): name of the item, not null */
    @Override
    public String getName(){
        return name;
    }

   /** @return (String): name of the item, not null */
   public String getPath(){
        return path;
}

    
}
