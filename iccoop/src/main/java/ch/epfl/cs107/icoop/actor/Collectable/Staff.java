
package ch.epfl.cs107.icoop.actor.Collectable;


import ch.epfl.cs107.icoop.enums.Element;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Représente les bâtons élémentaires
 */
public class Staff extends ElementalItem {

    // Nombre d'images d'animation
    private final static int ANIMATION_FRAMES = 8;

    // Durée de l'animation
    private final static int  ANIMATION_DURATION = 32;

    // Images
    private final Sprite[] sprites;

    // Animation
    private final Animation animation;

    /**
     * Constructeur des bâtons
     * @param area (Aire) non nulle
     * @param position (Coordonnées) non nulle
     * @param elem (Element)
     */
    public Staff(Area area, DiscreteCoordinates position, Element elem) {
        super(area, Orientation.DOWN, position, elem, true);
        String spriteName = (elem == Element.FIRE) ? "icoop/staff_fire" : "icoop/staff_water";

        // Sprites et Animation
        this.sprites = new Sprite[ANIMATION_FRAMES];
        for (int i = 0; i < ANIMATION_FRAMES; i++) {
            sprites[i] = new RPGSprite(spriteName, 2, 2, this,
                    new RegionOfInterest(
                            i * 32, 0, 32, 32), new Vector(-0.5f, 0)
            );
        }

        this.animation = new Animation(ANIMATION_DURATION / ANIMATION_FRAMES, sprites);

    }

    @Override
    public Element getElement() {
        return elementalType;
    }

    @Override
    public void drawCollectable(Canvas canvas) {
        animation.draw(canvas);
        
    }

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);
        super.update(deltaTime);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public ICoopItem getInventoryItem() {
        return elementalType.equals(Element.FIRE) ? ICoopItem.FIRESTAFF : ICoopItem.WATERSTAFF;
    }
}
