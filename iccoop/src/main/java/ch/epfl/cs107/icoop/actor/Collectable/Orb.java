package ch.epfl.cs107.icoop.actor.Collectable;

import ch.epfl.cs107.icoop.ElementalEntity;
import ch.epfl.cs107.icoop.enums.Damage;
import ch.epfl.cs107.icoop.enums.Element;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;


/**
 * Représente les orbes élémentaires
 */
public class Orb extends ElementalItem {

    // Durée de l'animation
    final static int  ANIMATION_DURATION = 24;

    // Nombre d'images de l'animation
    final static int ANIMATION_FRAMES = 6;

    // Animation
    final private Animation animation;

    // Type énuméré (eau ou feu)
    final private OrbType orbType;

    // Images
    final Sprite[] sprites;

    // Indique si un dialogue a été commencé
    private boolean dialogHasBeenStarted;

    // Gestionnaire de dialogues
    final private DialogHandler dialogHandler;

    /**
     * Constructeur d'orbes
     * @param area (Aire) non nulle
     * @param position (Coordonnées) non nulle
     * @param elem (Element) de l'orbe (feu ou eau)
     * @param dialogHandler (Gestionnaire de dialogue)
     */
    public Orb(Area area, DiscreteCoordinates position, Element elem, DialogHandler dialogHandler) {
        super(area, Orientation.DOWN, position, elem, false);

        // Elements
        elementalType = elem;
        this.orbType = (elem.equals(Element.FIRE)) ? OrbType.FIRE : OrbType.WATER;

        // Animation et sprites
        this.sprites = new Sprite[ANIMATION_FRAMES];
        for (int i = 0; i < ANIMATION_FRAMES; i++) {
            sprites[i] = new RPGSprite("icoop/orb", 1, 1, this ,
                    new RegionOfInterest(i * 32, orbType.spriteYDelta , 32, 32));
        }
        this.animation = new Animation(ANIMATION_DURATION / ANIMATION_FRAMES , sprites);

        // Dialogue
        this.dialogHandler = dialogHandler;
    }

    /**
     * Getter de l'enum orbtype 
     * @return (Damage) dégat correspondant à l'obtype
     */
    public Damage getDamage() {
        return orbType.damage;
    }

    @Override
    public void collectBy(ElementalEntity entity) {

        // Si le dialogue n'a pas été commencé, on peut l'ajouter
        if (!dialogHasBeenStarted) {
           dialogHandler.publish(new Dialog(orbType.dialogName));
           dialogHasBeenStarted = true;
        }

        // La collecte se fait uniquement après le dialogue
        if (!dialogHandler.isDialogActiv()){
            super.collectBy(entity);
        }
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

    /**
     * Enum d'éléments possibles pour les orbes, avec Les informations importantes correspondantes
     */
    public enum OrbType {

        // Types feu ou eau
        WATER(0, "orb_water_msg", Damage.WATER),
        FIRE(64, "orb_fire_msg", Damage.FIRE),;

        // Sprite correspondant
        private final int spriteYDelta;

        // Dialogue correspondant
        private final String dialogName;

        // Dégat correspondant
        private final Damage damage;

        /**
         * Constructeur de l'enum OrbType
        */
        OrbType(int spriteYDelta, String dialogName, Damage damage) {
            this.spriteYDelta = spriteYDelta;
            this.dialogName = dialogName;
            this.damage = damage;
        }
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public Element getElement() {
        return elementalType;
    }

    @Override
    public ICoopItem getInventoryItem() {
        return null;
    }
}

