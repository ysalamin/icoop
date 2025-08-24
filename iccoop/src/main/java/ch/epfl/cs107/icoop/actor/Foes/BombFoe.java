package ch.epfl.cs107.icoop.actor.Foes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.Explosif;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.enums.Damage;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import static ch.epfl.cs107.play.math.Orientation.DOWN;
import static ch.epfl.cs107.play.math.Orientation.LEFT;
import static ch.epfl.cs107.play.math.Orientation.RIGHT;
import static ch.epfl.cs107.play.math.Orientation.UP;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;


/**
 * Représente les artificiers, c-à-d les ennemis lançants des explosifs
 */
public class BombFoe extends Foe {

    // Etat de l'artificer
    private State state;

    // Animation actuelle
    private  OrientedAnimation currentAnimation;

    // Animations de protection et de non-protection
    private final OrientedAnimation nonProtectedAnimation;
    private final OrientedAnimation protectedAnimation;

    // Durée de l'animation
    private final int ANIMATION_DURATION = 32;

    // Distance de vue élargie
    private static final int EXTENDED_VIEW_DISTANCE = 8;

    // Gestionnaire d'intéractions
    private final BombFoeInteractionHandler interactionHandler = new BombFoeInteractionHandler();

    // Compteur du temps où il ne fait rien
    private int inactionCounter = 0;

    // Valeur limite de temps où il ne fait rien
    private static final int MAX_INACTION_STEPS = 24;

    // Compteur de temps où il se cache derrière son bouclier
    private int hideCounter;

    // Joueur ciblé
    private ICoopPlayer targetedPlayer;

   /**
    * Constructeur des artificiers
    * @param area (Aire) non nulle
    * @param position (Coordonnées) non nulle
    */
    public BombFoe(Area area, DiscreteCoordinates position) {
        super(area, DOWN, position, new Damage[]{Damage.FIRE, Damage.PHYSICAL}, 5);

        // Etat d'inaction par défaut
        this.state = State.IDLE;

        // Animations
        Vector anchor = new Vector(-0.5f, 0);
        Orientation[] orders = {DOWN, RIGHT, UP, LEFT};

        this.nonProtectedAnimation = new OrientedAnimation("icoop/bombFoe", ANIMATION_DURATION/3,
                this , anchor, orders, 4, 2, 2, 32, 32,
                true);

        this.protectedAnimation = new OrientedAnimation("icoop/bombFoe.protecting",
                ANIMATION_DURATION/3,this , anchor, orders, 4, 2, 2, 32, 32,
                false);

        this.currentAnimation = nonProtectedAnimation;


    }

    @Override
    public void drawCharacter(Canvas canvas) {

        switch (state) {
            case ATTACK, IDLE -> currentAnimation = nonProtectedAnimation;
            case HIDE -> currentAnimation = protectedAnimation;
        }

        currentAnimation.draw(canvas);
    }


    /**
     * Type énuméré des différentes états que peuvent prendre les artificiers
     */
    private enum State {
        IDLE(2),
        ATTACK(6),
        HIDE(1),
        ;

        // Facteur de vitesse
        private final int speedFactor;

        /**
         * Constructeur des énums
         * @param speedFactor
         */
        State(int speedFactor) {
            this.speedFactor = speedFactor;
        }

        public int getSpeedFactor() {
            return speedFactor;
        }
    }

    @Override
    public boolean wantsCellInteraction() {
        // Ne veut pas d'intéraction de contact
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        // Veut des intéractions à distance en IDLE ou en ATTACK
        return state.equals(State.IDLE) || state.equals(State.ATTACK);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        // Fonction par défaut pout le modèle visiteur
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {

        // Création de la liste des cellules visible, qui sera remplie ensuite.
        List<DiscreteCoordinates> fieldOfViewCells = new ArrayList<>();

        // Coordonnées et orientation
        DiscreteCoordinates currentPosition = getCurrentMainCellCoordinates();
        Vector orientationVector = getOrientation().toVector();


        if (state.name().equals(State.ATTACK.name())) {

            // En mode attaque, il ne voit que l'unique cellule en face de lui
            fieldOfViewCells.add(currentPosition.jump(orientationVector));

        } else {

            // Dans les autres modes, il voit un ensemble constant de cellules en face de lui
            for (int i = 1; i <= EXTENDED_VIEW_DISTANCE; i++) {
                fieldOfViewCells.add(currentPosition.jump(orientationVector.mul(i)));
            }
        }
        return fieldOfViewCells;

    }



    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    @Override
    public void update(float deltaTime) {

        super.update(deltaTime);

        currentAnimation.update(deltaTime);

        // Si l'artificier est en période d'immunité, il est remis en IDL avec un temps d'inaction nul
        if (isImmunityTime()) {
            state = State.IDLE;
            inactionCounter = 0;
        }

        // Ne fait absolument rien si en mode inactif
         if (inactionCounter < MAX_INACTION_STEPS) {
            inactionCounter += 1;

        } else {

             // Si il n'est pas inactif, intéressons nous à son état
             switch (state) {

                 // En état IDLE, il ne fait rien d'autre que de se déplacer// de façon aléatoire
                 case IDLE -> {
                     randomMove();
                 }
                 // En état d'attaque, il se déplace de façon ciblée
                 case ATTACK -> {
                     targetedMove();
                 }

                 case HIDE -> {
                     // En mode protégé, l'artificier oublie sa cible
                     if (targetedPlayer != null) {
                         targetedPlayer = null;
                     }

                     if (hideCounter >= 0) {

                         // petite incohérence entre les vidéos de demo et l'énoncé:
                         // mais l'assistant m'a dit de ne pas me préoccuper de ça et
                         // donc s'il ne bouge pas dans la vidéo je peux faire la même
                         // chose, j'ai donc commenté le randomMove()
                         // l'artificier reste donc inactif après avoir posé la bombe au lieu de
                         // bouger lentement comme dans l'énoncé

                         // randomMove();

                         hideCounter -= 1;
                     } else {

                        // Si le counter touche à sa fin, l'artificier revient en état inactif
                        state = State.IDLE;
                        
                        // Reset de son animation pour la prochaine attaque
                        protectedAnimation.reset();
                     }
                }
            }
        }
    }


    /**
     * Fonction gérant le mouvement aléatoire des artificiers
     */
    private void randomMove() {

        // Assure qu'aucun déplacement n'est déjà en cours
        if (!isDisplacementOccurs()) {

            // Probabilité arbitraire qu'il change d'orientation
            double changeOrientationProbability = 0.4;

            // Si la probabilité est passée, lui change l'orientation pour une orientation aléatoire
            if (RandomGenerator.getInstance().nextDouble() < changeOrientationProbability) {
                int randomIndex = RandomGenerator.getInstance().nextInt(Orientation.values().length);
                orientate(Orientation.values()[randomIndex]);
            }

            // Appelle la fonction pour qu'il bouge
            move(ANIMATION_DURATION / state.speedFactor);

            // N'est plus en inaction, le compteur est donc remis à zéro
            inactionCounter = 0;
        }
    }

    /**
     * Fonction qui gère le mouvement ciblé d'un artificier envers un joueur
     */
    private void targetedMove() {

        // Facteur de vitesse 
        int localSpeedFactor;

        // S'assure qu'une cible est bien sélectionnée
        if (targetedPlayer == null) {
            return;
        }

        // Distance (sous forme de vecteur) entre l'artificier et sa cible
        Vector v = targetedPlayer.getPosition().sub(this.getPosition());

        // Composantes du vecteur distance
        float deltaX = v.x;
        float deltaY = v.y;

        // Défini la nouvelle orientation en fonction des composantes du vecteur distance
        Orientation orientation;
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            orientation = Orientation.fromVector(new Vector(deltaX, 0));
        } else {
            orientation = Orientation.fromVector(new Vector(0, deltaY));
        }

        // si le changement d’orientation n’a pas pu se faire, un pas de déplacement à vitesse
        //rapide aura lieu.
        if (orientation != null) {
            orientate(orientation);
            localSpeedFactor = 1;
        } else {
            // sinon on double la vitesse
            localSpeedFactor = 2;
        }

        // Distance scalaire entre le joueur et l'artificier (Sous forme de float cette fois)
        float distancePlayerFoe = DiscreteCoordinates.distanceBetween(getCurrentMainCellCoordinates(), targetedPlayer.getCurrentMainCellCoordinates());

        // Avance si la distance est plus grande que 2
        if (distancePlayerFoe > 2) {

            move(localSpeedFactor * ANIMATION_DURATION / state.getSpeedFactor());

        } 
        
        // Lance une bombe sinon
        else {

            // Si l'artificier est assez proche, il largue une bombre
            DiscreteCoordinates frontCell = getCurrentMainCellCoordinates().jump(getOrientation().toVector());

            // On vérifie si la cell est libre pour poser la bombe
            if (getOwnerArea().canEnterAreaCells(this, Collections.singletonList(frontCell))) {
                Explosif explosif = new Explosif(getOwnerArea(), DOWN, frontCell, 2);
                getOwnerArea().registerActor(explosif);

                // La bombe est tout de suite activée
                explosif.activate(1);

                // On met le counter à jour
                hideCounter = RandomGenerator.getInstance().nextInt(72, 120);

                // L'artificier passe en mode protégé
                state = State.HIDE;
            }
        }
    }

    /**
     * Gestionnaire d'intéraction de l'artificier
     */
    private class BombFoeInteractionHandler implements ICoopInteractionVisitor {

        // Il n'intéragit qu'avec les joueurs
        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            // Si il voit un player dans son champ de vision il se met en mode attaque
            state = State.ATTACK;
            targetedPlayer = player;
        }
    }
}
