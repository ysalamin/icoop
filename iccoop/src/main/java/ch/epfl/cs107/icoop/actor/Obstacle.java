package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Représente les obstacles
 */
public class Obstacle extends AreaEntity {

    private String spriteName = "rock.2";
    private Sprite sprite;

    /**
     * Constructeur par défaut
     * @param area (Aire) non nulle
     * @param orientation (Orientation) non nulle
     * @param position (Coordonnées) non nulle
     */
    public Obstacle(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        createSprite();
    }

    /**
     * Constructeur qui précise le spriteName
     * @param area (Aire) non nulle
     * @param orientation (Orientation) non nulle
     * @param position (Coordonnées) non nulle
     * @param spriteName nom de la sprite
     */
    public Obstacle(Area area, Orientation orientation, DiscreteCoordinates position, String spriteName) {
        this(area, orientation, position);
        this.spriteName = spriteName;
        createSprite();
    }

    /** Crée une sprite */
    private void createSprite() {
        sprite = new Sprite(spriteName, 1f, 1f, this);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    // Par défaut le rock est non traversable
    @Override
    public boolean takeCellSpace() {
        return true;
    }

    // Accepte tout type d'interaction
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    // Accepte tout type d'intéraction
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    // On draw la sprite par défaut
    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
        super.draw(canvas);
    }
    
    // Accepte tout type d'intéraction
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction){
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
