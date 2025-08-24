package ch.epfl.cs107.icoop.actor.Projectiles;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.Explosif;
import ch.epfl.cs107.icoop.actor.Foes.BombFoe;
import ch.epfl.cs107.icoop.actor.Foes.HellSkull;
import ch.epfl.cs107.icoop.actor.Rock;
import ch.epfl.cs107.icoop.enums.Element;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;


// Représente les boules de magie lancées par les bâtons, très similaires aux flammes
public class StaffBall extends Unstoppable {

    // Gestionnaire d'intéraction
    private final StaffBallInteractionHandler interactionHandler = new StaffBallInteractionHandler();

    // Animation
    private final Animation animation;

    // Durée d'animation
    final static int ANIMATION_DURATION = 12;

    // Element de la boule de magie 
    private final Element element;

    /**
     * Constructeur de la boule de magie
     * @param area (Aire) non nulle
     * @param orientation (Orientation) non nulle
     * @param position (Coordonnées) non nulle
     * @param speed (Vitesse)
     * @param maxDistance (Distance max)
     * @param elem (Element)
     */
    public StaffBall(Area area, Orientation orientation, DiscreteCoordinates position, int speed, int maxDistance, Element elem) {
        super(area, orientation, position, speed, maxDistance);

        this.element = elem;

        // Selon l'élément, assigne le spriteName correspondant
        String name = elem == Element.FIRE ? "icoop/magicFireProjectile" : "icoop/magicWaterProjectile";

        this.animation = new Animation(name , 4, 1, 1, this , 32, 32,
        ANIMATION_DURATION/4, true);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        // Aucune intéraction à distance voulue dans tous les cas, nous pouvons retourner une liste vide
        return List.of();
    }

    @Override
    public void draw(Canvas canvas) {
        animation.draw(canvas);
        super.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {

        // Update en permanence de l'animation
        animation.update(deltaTime);

        // Si elle ne peut pas continuer sa course à cause de la case de devant qui ne la laisserait pas rentrer, on stop l'objet
        if (!getOwnerArea().canEnterAreaCells(this, Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector())))) {
            endMovement();
        }

        super.update(deltaTime);
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        // Il n'accepte pas d'intéraction donc on laisse ca vide
    }

    /**
     * Gestionnaire d'intéraction
     */
    private final class StaffBallInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(Explosif explo, boolean isCellInteraction) {
            // Activ l'explosif
            explo.activate(1);
            endMovement();
        }

        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {
            // Détruit le rocher
            rock.destroy();
            endMovement();
        }
        @Override
        public void interactWith(BombFoe foe, boolean isCellInteraction) {
            // Fait perdre des points de vie avec des dégats de l'éléments de la boule de magie
            foe.loseHealth(element.toDamage());
            endMovement();
        }

        @Override
        public void interactWith(HellSkull skull, boolean isCellInteraction) {
            // Si c'est une boule d'eau seulement, fait perdre des points de vie à l'aide de dégat aquatique
            if (element.equals(Element.WATER)){
                skull.loseHealth(element.toDamage());
                endMovement();
            }
        }
    }
}
