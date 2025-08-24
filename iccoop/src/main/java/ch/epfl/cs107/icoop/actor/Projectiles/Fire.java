package ch.epfl.cs107.icoop.actor.Projectiles;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.Explosif;
import ch.epfl.cs107.icoop.actor.Foes.Foe;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.enums.Damage;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

// Représente les flammes lancées par les crânes
public class Fire extends Unstoppable {

    // Gestionnaire d'intéraction
    private final FireInteractionHandler interactionHandler = new FireInteractionHandler();

    // Animation
    private final Animation animation = new Animation("icoop/fire", 7, 1, 1, this , 16, 16, 4, true);

    /**
     * Constructeur des flammes
     * @param area (Aire) non nulle
     * @param orientation (Orientation) non nulle
     * @param position (Coordonnées) non nulle
     * @param speed (Vitesse)
     * @param maxDistance (Distance max)
     */
    public Fire(Area area, Orientation orientation, DiscreteCoordinates position, int speed, int maxDistance) {
        super(area, orientation, position, speed, maxDistance);
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
     * Gestionnaire d'intéraction de la flamme
     */
    private final class FireInteractionHandler implements ICoopInteractionVisitor {

        @Override
        public void interactWith(Explosif explo, boolean isCellInteraction) {
            // Active l'explosif
            explo.activate(1);
            endMovement();
        }

        @Override
        public void interactWith(Foe foe, boolean isCellInteraction) {
            // Fait des dégats de feu aux ennemis
            foe.loseHealth(Damage.FIRE);
            endMovement();
        }

        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            // Fait des dégats de feu aux joueurs
            player.loseHealth(Damage.FIRE);
            endMovement();
        }
    }
}
