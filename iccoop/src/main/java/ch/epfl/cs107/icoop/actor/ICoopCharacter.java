package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.enums.Damage;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.window.Canvas;

/**
 * ICoopCharacter est une classe de base abstraite représentant un personnage avec gestion de la santé et des dégâts.
 */
public abstract class ICoopCharacter extends MovableAreaEntity {

    // Health attributes
    private final int maxLife;
    private final Health health;

    // Immunity attributes
    private boolean isInvulnerableTemporary;
    private Damage invulnerableDamageType;
    private int invulnerableDuration;
    private static final float IMMUNITY_TIME = 24;
    private float immunityTimer;
    private boolean isImmunityTime;

    /**
     * Constructor for ICoopCharacter
     *
     * @param area        (Area): the area this character belongs to, not null
     * @param orientation (Orientation): the initial orientation of the character
     * @param position    (DiscreteCoordinates): the initial position of the character
     * @param maxLife     (int): the maximum health points for the character
     */
    public ICoopCharacter(Area area, Orientation orientation, DiscreteCoordinates position, int maxLife, boolean isFriendly) {
        super(area, orientation, position);
        this.maxLife = maxLife;
        this.health = new Health(this, Transform.I.translated(0, 1.75f), maxLife, isFriendly);
    }

    /**
     * Resets the character's health to maximum.
     */
    public void resetHealth() {
        health.resetHealth();
    }


    public boolean isImmunityTime() {
        return isImmunityTime;
    }

    public void increaseHealth(int value) {
        health.increase(value);
    }

    /**
     * Fais perdre de la vie
     *
     * @param damage (Damage): the type and amount of damage received
     */
    public void loseHealth(Damage damage) {
        if (damage.equals(invulnerableDamageType) || isImmunityTime) {
            return; // Character is invulnerable to this type of damage
        }

        health.decrease(damage.getDamagePoints());
        startImmunityTimer();
    }

    public void setInvulnerableDamageType(Damage invulnerableDamageType) {
        this.invulnerableDamageType = invulnerableDamageType;
    }

    /**
     * Rend le personnage invulnérable à un type de dégâts spécifique pour une durée déterminée.
     *
     * @param damage
     * @param isTemporary
     * @param duration
     */
    public void becomeInvulnerable(Damage damage, boolean isTemporary, int duration) {
        this.isInvulnerableTemporary = isTemporary;
        this.invulnerableDamageType = damage;
        this.invulnerableDuration = duration;
    }

    /**
     * Démarre le timer d'immunité
     */
    private void startImmunityTimer() {
        isImmunityTime = true;
        immunityTimer = IMMUNITY_TIME;
    }

    /**
     * Met à jour le timer d'immunité
     *
     * @param deltaTime (float):
     */
    protected void updateImmunityTimer(float deltaTime) {
        // ---------Gestion de l'immunité---------
        if (isImmunityTime && immunityTimer > 0) {
            // Décrémente le timer
            immunityTimer -= 1;
        } else {
            // Si la périoide d'immunité est finie, modifie la variable
            isImmunityTime = false;
        }
    }

    /**
     * Getter pour savoir si le character est en vie.
     *
     * @return (boolean): vrai si le player en vie
     */
    public boolean isAlive() {
        return health.isOn() || health.getIntensity() > 0;
    }

    /**
     * Méthode abstraite à implémenter par la sous-classe
     *
     * @param canvas (Canvas)
     */
    protected abstract void drawCharacter(Canvas canvas);

    @Override
    public void draw(Canvas canvas) {
        // Draw health bar
        health.draw(canvas);

        // Draw the character if not immune or flashing during immunity
        if (isImmunityTime) {
            if (immunityTimer % 2 == 0) {
                drawCharacter(canvas);
            }
        } else {
            drawCharacter(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {
        updateImmunityTimer(deltaTime);
        super.update(deltaTime);
    }
}
