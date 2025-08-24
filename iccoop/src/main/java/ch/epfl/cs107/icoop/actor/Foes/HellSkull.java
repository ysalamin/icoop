package ch.epfl.cs107.icoop.actor.Foes;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.actor.Projectiles.Fire;
import ch.epfl.cs107.icoop.enums.Damage;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;

/** 
 * Représente les crânes de feu lançeurs de flammes
*/
public class HellSkull extends Foe {

    // Durée de feu 
    private float deltaFireTime;

    private final static int ANIMATION_DURATION = 12;
    private static final Orientation[] orders = new Orientation []{ Orientation.UP, Orientation.LEFT , Orientation.DOWN , Orientation.RIGHT};
    private final OrientedAnimation animation;


    private final static float MIN_FIRE_TIME = 1.5f;
    private final static float MAX_FIRE_TIME = 4.f;

    private final HellSkullInteractionHandler interactionHandler = new HellSkullInteractionHandler();


    /**
     * Constructeur des crânes
     * @param area (Aire) non nulle
     * @param orientation (Orientation) non nulle
     * @param position (Coordonnées) non nulle
     */
    public HellSkull(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, new Damage[]{Damage.PHYSICAL, Damage.WATER}, 2);
        this.deltaFireTime = RandomGenerator.getInstance().nextFloat(MIN_FIRE_TIME , MAX_FIRE_TIME);
        this.animation = new OrientedAnimation("icoop/flameskull",
                ANIMATION_DURATION/3, this ,
                new Vector(-0.5f, -0.5f), orders ,
                3, 2, 2, 32, 32, true);

    }


    @Override
    public boolean wantsCellInteraction() {
        // S'il n'est pas mort return true

        return isAlive();
    }

    @Override
    public boolean takeCellSpace() {
        // On peut lui marcher dessus
        return false;
    }


    @Override
    public void drawCharacter(Canvas canvas) {
        animation.draw(canvas);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        // Fonction par défaut pout le modèle visiteur
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);

    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return List.of();
    }


    @Override
    public void update(float deltaTime) {

        super.update(deltaTime);

        // Update de l'animation
        animation.update(deltaTime);


        if (deltaFireTime <= 0) {

            // Position de devant, utile pour savoir si l'on peut y créer une instancew de flamme
            DiscreteCoordinates frontCell = getCurrentMainCellCoordinates().jump(getOrientation().toVector());

            // Lance une flamme si c'est possible
            if (!getOwnerArea().canEnterAreaCells(this, Collections.singletonList(frontCell))) {
                Fire fire = new Fire(
                        getOwnerArea(),
                        getOrientation(),
                        frontCell,
                        1, 100
                );
                getOwnerArea().registerActor(fire);
            }

            // Pour la prochaine flamme, on recrée un temps au hasard
            deltaFireTime = RandomGenerator.getInstance().nextFloat(MIN_FIRE_TIME , MAX_FIRE_TIME);

        } else {
            // Tant que le timer n'est pas fini, on décrémente
            deltaFireTime -= deltaTime;
        }
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    /**
     * Gestionnaire d'intéraction pour le crâne
     */
    private final class HellSkullInteractionHandler implements ICoopInteractionVisitor {
        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            if (isCellInteraction) {
                player.loseHealth(Damage.FIRE);
            }
        }
    }
}
