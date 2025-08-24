package ch.epfl.cs107.icoop.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.Collectable.ICoopCollectable;
import ch.epfl.cs107.icoop.enums.Damage;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Représente les explosifs
 */
public class Explosif extends ICoopCollectable implements Interactor{

    // Durée d'animation
    private final static int ANIMATION_DURATION = 24;

    // Deux animations différentes
    private final Animation tickingAnimation;
    private final Animation explosionAnimation;

    // Compteur avant d'exploser
    private float counter;

    // Indications sur l'état de l'explosifs
    private boolean isActivated;
    private boolean isExploding;


    // Gestionnaire d'intéraction
    private final ExplosifInteractionHandler interactionHandler = new ExplosifInteractionHandler();

    /**
     * Constructeur d'explosif
     * @param area (Aire) non nulle
     * @param orientation (Orientation) non nulle
     * @param position (Coordonnées) non nulle
     * @param counter (Compteur) valeure avant qu'il explose
     */
    public Explosif(Area area, Orientation orientation, DiscreteCoordinates position, int counter){
        super(area, orientation, position, true);

        // Désactivé par défaut
        this.isActivated = false;
        this.isExploding = false;
        this.counter = counter;

        // Animation lorsque l'explosif n'a pas encore explosé
        this.tickingAnimation = new Animation("icoop/explosive", 2, 1, 1, this , 16, 16,
        ANIMATION_DURATION/2, true);

        // Animation d'explosion
        this.explosionAnimation = new Animation("icoop/explosion", 7, 1, 1, this , 32, 32,
        ANIMATION_DURATION/7, false);

    }

    /*
     * Active l'explosif
     */
    public void activate(int counter) {
        isActivated = true;
        this.counter = counter;
    }

    /*
     * Fait exploser l'explosif
     */
    public void explode() {
        isExploding = true;
    }
    
    @Override
    public void update(float deltaTime){

        super.update(deltaTime);

        // Tant qu'il est activé, le compteur diminue
        if (isActivated) {
            counter -= deltaTime;
            tickingAnimation.update(deltaTime);
        }

        // Si le counter est négatife on change l'attribut
        // et on commence l'animation d'explosion
        if (counter <= 0) {
            isExploding = true;
            explosionAnimation.update(deltaTime);
        }


        // Lorsque l'animation a eu le temps de s'afficher
        // on peut finalement unregister l'actor
        if (counter <= -1) {
            getOwnerArea().unregisterActor(this);
        }

    }

    
    @Override
    public void drawCollectable(Canvas canvas){

        // Si la bombe n'explose pas on draw la tickingAnimation
        // cela marche aussi si la bombe n'est pas activée car
        // dans ce cas on update simplement pas cette animation
        if (!isExploding){
            tickingAnimation.draw(canvas);
        }

        if (isExploding){
            explosionAnimation.draw(canvas);
        }


    
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells(){
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> neighbourCells = new ArrayList<>();

        // Remplis la liste des cellules voisines avec les 4 cellules directement adjaçantes
        for (Orientation orientation : Orientation.values()) {
            neighbourCells.add(getCurrentMainCellCoordinates().jump(orientation.toVector()));
        }
        return neighbourCells;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        // La bombew veut intéragir à distance seulement si elle explose
        return isExploding;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    @Override
    public boolean takeCellSpace() {
        // Un personnage peut traverser la bombe
        return false;
    }


    /**
     * @return (boolean): true if this is able to have cell interactions
     */
    @Override
    public boolean isCellInteractable() {
        // On peut intéragiravec la bombe si elle n'a pas explosé, ou si elle n'est pas activée
        return (!isExploding && !isActivated);
    }

    @Override
    public boolean isViewInteractable() {
        // Lorsque la bombe n'a pas encore explosé, on peut intéragir avec pour l'activer à distance
        return !isExploding;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        // Fonction par défaut pout le modèle visiteur
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);

    }

    /**
     *  Gestionnaire d'intéraction
     */
    private final class ExplosifInteractionHandler implements ICoopInteractionVisitor {

        
        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {
            // Intéraction avec un rocher : le fait disparaitre si l'explosif explose
            if (isExploding){
                rock.destroy();
            }
        }

        @Override
        public void interactWith(ICoopPlayer player, boolean isCellInteraction) {
            if (isExploding) {
                // Si la bombe explose à côté d'un player, il perd
                // des points de vie
                player.loseHealth(Damage.EXPLOSION);
            }
        }

        @Override
        public void interactWith(Explosif explo, boolean isCellInteraction){
            // Deux explosifs se font exploser entre eux
            if (explo != Explosif.this && !explo.isActivated) {
                explo.activate(1);
            } 
        }

        @Override
        public void interactWith(ElementalWall wall, boolean isCellInteraction) {

            // Détruit les murs avec intéractions à distance et de contact
            if (isExploding) {
                wall.destroy();
            }
        }

    }

    @Override
    public ICoopItem getInventoryItem() {
        return ICoopItem.EXPLOSIVE;
    }
}
