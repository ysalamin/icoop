package ch.epfl.cs107.icoop.actor.Foes;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.ICoopCharacter;
import ch.epfl.cs107.icoop.enums.Damage;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/*
 * Représente de manière abstraite les ennemis, se déplaçant sur une grille et pouvant intéragir et recevoir des intéractions
*/
public abstract class Foe extends ICoopCharacter implements Interactor {

    // Vulnérabilité
    private final Damage[] vulnerabilityList;

    // Animations
    private static final int ANIMATION_DURATION = 24;
    private final Animation deathAnimation;
    private double animationCounter = ANIMATION_DURATION;

    /**
     * Constructeur d'ennemi
     * @param area (Aire) non nulle
     * @param orientation (Orientation) non nulle
     * @param position (Coordonnées) non nulle
     * @param vulnerabilityList (Liste des faiblesses), peut être vide mais non nulle
     */
    public Foe(Area area, Orientation orientation, DiscreteCoordinates position, Damage[] vulnerabilityList, int MAX_LIFE) {
        super(area, orientation, position, MAX_LIFE, false);
        this.vulnerabilityList = vulnerabilityList;
        this.deathAnimation = new Animation("icoop/vanish", 7, 2, 2, this , 32, 32, new
                Vector(-0.5f, 0f), ANIMATION_DURATION/7, false);
    }

    @Override
    public void loseHealth(Damage damage) {

        // teste la liste de vulnérabilité avant de faire les dégat
        for (Damage vulnerableDamage : vulnerabilityList) {
            if (damage.equals(vulnerableDamage)) {
                super.loseHealth(damage);
            }
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public boolean takeCellSpace() {
        // Prend seulement de la place vivant
        return isAlive();
    }

    @Override
    public boolean isCellInteractable() {
        // Accepte les intéractions de contact par défaut
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        // Accepte les intéractions à distance par défaut
        return true;
    }

    @Override
    public boolean wantsCellInteraction() {
        // Demande des intéractions de contact par défaut
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        // Demande des intéractions à distance par défaut
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }


    @Override
    public void update(float deltaTime) {

        // Si les pv sont = 0 ou < 0
        if (!isAlive()) {
            animationCounter -= 1.5;
            deathAnimation.update(deltaTime);
        }

        if (animationCounter < 0) {
            // A la fin de l'animation
            // On unregister l'actor
            getOwnerArea().unregisterActor(this);
        }

        super.update(deltaTime);
    }


    @Override
    public void draw(Canvas canvas) {
        // Tant qu'il n'est pas mort, l'animation se dessine
        if (!isAlive()) {
            deathAnimation.draw(canvas);
        } else {
            super.draw(canvas);
        }
    }

 }


