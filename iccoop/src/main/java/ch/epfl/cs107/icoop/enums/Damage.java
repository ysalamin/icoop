package ch.epfl.cs107.icoop.enums;

/**
 * Différentes dégats pouvant être infligés
 */
public enum Damage {

    // Type de dégats / points de dégats associés
    EXPLOSION(50),
    FIRE(20),
    WATER(20),
    PHYSICAL(1)
        ;

    private final int damagePoints;

    /**
     * Constructeur
     * @param damagePoints (Points de dégats)
     */
    Damage(int damagePoints) {
        this.damagePoints = damagePoints;
    }

    /**
     * Getter des points de dégats
     * @return
     */
    public int getDamagePoints() {
        return damagePoints;
    }

}