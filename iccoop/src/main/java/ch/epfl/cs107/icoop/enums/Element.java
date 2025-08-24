package ch.epfl.cs107.icoop.enums;

/**
 * Différents éléments disponibles
 */
public enum Element {
    FIRE("icoop/player"),
    WATER("icoop/player2"),
    ;

    // Sprite du player correspondant à l'élément
    private final String spriteName;

    /**
     * Constructeur
     * @param spriteName
     */
    Element(String spriteName) {
        this.spriteName = spriteName;
    }

    // Méthode pour récupérer la valeur associée
    public String getSpriteName() {
        return spriteName;
    }

    // Méthode pour récupérer le Damage associé
    public Damage toDamage(){
        return switch(this){
            case FIRE -> Damage.FIRE;
            case WATER -> Damage.WATER;
        };
    }
}