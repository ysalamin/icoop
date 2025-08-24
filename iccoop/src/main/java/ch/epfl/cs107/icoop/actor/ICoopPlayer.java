package ch.epfl.cs107.icoop.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.ElementalEntity;
import ch.epfl.cs107.icoop.KeyBindings;
import static ch.epfl.cs107.icoop.KeyBindings.BLUE_PLAYER_KEY_BINDINGS;
import static ch.epfl.cs107.icoop.KeyBindings.RED_PLAYER_KEY_BINDINGS;
import ch.epfl.cs107.icoop.actor.Collectable.Heart;
import ch.epfl.cs107.icoop.actor.Collectable.ICoopCollectable;
import ch.epfl.cs107.icoop.actor.Collectable.Key;
import ch.epfl.cs107.icoop.actor.Collectable.Orb;
import ch.epfl.cs107.icoop.actor.Collectable.Staff;
import ch.epfl.cs107.icoop.actor.Foes.BombFoe;
import ch.epfl.cs107.icoop.actor.Foes.HellSkull;
import ch.epfl.cs107.icoop.actor.Projectiles.StaffBall;
import ch.epfl.cs107.icoop.enums.Damage;
import ch.epfl.cs107.icoop.enums.Element;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopInventory;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.icoop.handler.ICoopPlayerStatusGUI;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.areagame.handler.Inventory;
import ch.epfl.cs107.play.areagame.handler.InventoryItem;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import static ch.epfl.cs107.play.math.Orientation.DOWN;
import static ch.epfl.cs107.play.math.Orientation.LEFT;
import static ch.epfl.cs107.play.math.Orientation.RIGHT;
import static ch.epfl.cs107.play.math.Orientation.UP;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

/**
 * A ICoopPlayer is a player for the ICoop game.
 */
public class ICoopPlayer extends ICoopCharacter implements ElementalEntity, Interactor, Inventory.Holder {

    // Element
    private final Element element;

    // Gestionnaire d'interaction
    private final ICoopPlayerInteractionHandler interactionHandler = new ICoopPlayerInteractionHandler();

    // Items et inventaires
    private final ICoopInventory inventory;
    private ICoopItem currentItem;

    // ------------- ANIMATION -----------------
    private OrientedAnimation currentAnimation;

    private final static int MOVE_DURATION = 8;

    private final static int ANIMATION_DURATION = 4;
    private final OrientedAnimation defaultAnimation;

    private final static int SWORD_ANIMATION_DURATION = 2;
    private final OrientedAnimation swordAnimation;

    private final static int STAFF_ANIMATION_DURATION = 2;
    private final OrientedAnimation staffAnimation;
    private final ICoopPlayerStatusGUI statusGui;


    // Touches
    private KeyBindings.PlayerKeyBindings playerKeyBindings;

    // Gestion des portes et passages
    private boolean isLeaving = false;
    private Door leavingDoor = null;

    // Etat
    private  PlayerState playerState = PlayerState.IDLE;

    /**
     * Constructeur du joueur
     * @param owner (Aire) non nulle
     * @param orientation (Orientation) non nulle
     * @param coordinates (Coordonnées) non nulle
     * @param element (Element du joueur)
     * @param flipped (Affichage) switch l'affichage de l'inventaire de côté
     */
    public ICoopPlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates, Element element, boolean flipped) {
        super(owner, orientation, coordinates, 100, true);

        // Element
        this.element = element;


        // Création de l'inventaire
        this.inventory = new ICoopInventory();
        inventory.addPocketItem(ICoopItem.EXPLOSIVE, 1);
        inventory.addPocketItem(ICoopItem.SWORD,1);
        updateCurrentItem();
 

        // Barre d'état
        this.statusGui = new ICoopPlayerStatusGUI(this, flipped);


        // Animations
        Orientation[] orders = {DOWN, RIGHT, UP, LEFT};
        // ------------- AFFICHAGE -----------------

        Vector anchor = new Vector(0, 0);
        defaultAnimation = new OrientedAnimation(element.getSpriteName(), ANIMATION_DURATION, this, anchor, orders,
        4, 1, 2, 16, 32, true);


        Vector swordAnchor = new Vector(-.5f, 0);

        // Pour les animations du staff et de l'épée, l'ordre d'en bas n'est pas correct
        // ce n'est pas le même ordre que la variable du dessous
        Orientation[] itemOrders = {DOWN, UP, RIGHT, LEFT};
        swordAnimation =  new OrientedAnimation(element.getSpriteName()+".sword",
                SWORD_ANIMATION_DURATION , this ,
                swordAnchor, itemOrders, 4, 2, 2, 32, 32);

        String staffAnimationName = (element.equals(Element.FIRE)) ? "icoop/player.staff_fire" : "icoop/player2.staff_water";
        Vector staffAnchor = new Vector(-.5f, -.20f);
        staffAnimation = new OrientedAnimation(staffAnimationName , STAFF_ANIMATION_DURATION , this ,
                staffAnchor, itemOrders, 4, 2, 2, 32, 32);

        // Par défaut, l'animation actuelle est celle par défaut 
        currentAnimation = defaultAnimation;

        // Touches selon l'élément
        switch (element) {
            case FIRE -> playerKeyBindings = RED_PLAYER_KEY_BINDINGS;
            case WATER -> playerKeyBindings = BLUE_PLAYER_KEY_BINDINGS;
        }
    }

    /**
     * Etats possibles pour le player
     */
    private enum PlayerState {
        IDLE,
        SWORD,
        STAFF
    }

    /**
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {

        // Update de la classe ICoopCharacter
        super.update(deltaTime);

        // ---------Gestion du mouvement---------
        Keyboard keyboard = getOwnerArea().getKeyboard();
       
        if(playerState.equals(PlayerState.IDLE)){

            moveIfPressed(Orientation.LEFT, keyboard.get(playerKeyBindings.left()));
            moveIfPressed(Orientation.UP, keyboard.get(playerKeyBindings.up()));
            moveIfPressed(Orientation.RIGHT, keyboard.get(playerKeyBindings.right()));
            moveIfPressed(Orientation.DOWN, keyboard.get(playerKeyBindings.down()));

            if (isDisplacementOccurs()) {
                currentAnimation.update(deltaTime);
            } else {
                currentAnimation.reset();
            }
        }

        // --------- Gestion de l'état et animations associées ---------
        if (!playerState.equals(PlayerState.IDLE)) {
            if (!currentAnimation.isCompleted()) {
                currentAnimation.update(deltaTime);
            } else {

                // Check la touche est encore appuyée
                if (keyboard.get(playerKeyBindings.useItem()).isDown()) {

                    // Recommence l'animation de l'épée si la touche est encore appuyée
                    currentAnimation.reset();
                } else {
                    // Retour à l'état IDLE seulement si aucune action n'est en cours
                    currentAnimation = defaultAnimation;
                    playerState = PlayerState.IDLE;
                }
            }
        
        }

        // ---------Gestion des items---------
        if (keyboard.get(playerKeyBindings.switchItem()).isPressed()) {
            switchItem();
        }
        manageUseItem(keyboard);
    }



    /**
     *  S'occupe de gérer le currentitem en fonction de ce qui est disponible dans l'inventaire
     */
    public void switchItem(){

        // Pour implémenter le concept de circularité, nous crééons un tableau des items (sûrement plus simple mais ça me paraissait naturel)
        List<ICoopItem> itemList = new ArrayList<>();
        for (ICoopItem i : ICoopItem.values()){
            itemList.add(i);
        }

        // On établit l'index de l'item actuel
        int currentIndex = 0;
        for (int i = 0; i < itemList.size(); i++){
            if (itemList.get(i).equals(currentItem)){
                currentIndex = i;
                break;
            }
        }
        
        // On cherche l'index du prochain item qu'il y a de disponible dans l'inventaire
        int nextIndex = (currentIndex + 1) % itemList.size();
        while(!possess(itemList.get(nextIndex))){
            nextIndex = (nextIndex + 1) % itemList.size();

            // Pour éviter la infinite loop qui a causé un bug
            if (nextIndex == currentIndex) {
                currentItem = null;
                return;
            }
        }

        // On update 
        currentItem = itemList.get(nextIndex);
    
    }

    @Override 
    public boolean possess(InventoryItem item) {
        return (item != null && inventory.contains(item));
    }

    /**
     * S'occupe de gérer l'utilisation des items
     * @param kbd instance du Keyboard
     */
    public void manageUseItem(Keyboard kbd){
        
        // Si la touche est pressée 
        if (kbd.get(playerKeyBindings.useItem()).isPressed() && !isDisplacementOccurs()){

            // On récupère la case de devant qui sera utile pour chacun des cas
            DiscreteCoordinates frontCellPosition = getCurrentMainCellCoordinates().jump(getOrientation().toVector());

            // En fonction de l'item actuel 
            switch (currentItem){

                case EXPLOSIVE :
                    // Pose la bombe devant le joueur

                    if (getOwnerArea().canEnterAreaCells(this, Collections.singletonList(frontCellPosition))) {
                        Explosif explo = new Explosif(getOwnerArea(), getOrientation(), frontCellPosition, 3);
                        this.getOwnerArea().registerActor(explo);
                        inventory.removePocketItem(currentItem, 1);
                    }
                    break;

                case SWORD :

                    // on met le player en state SWORD
                    playerState = PlayerState.SWORD;

                    // et on change l'animation
                    currentAnimation = swordAnimation;
                    break;

                case WATERSTAFF:
                    // Si l'élément correspond
                    if (element.equals(Element.WATER)){

                        // Modifie l'animation et l'état du joueur
                        playerState = PlayerState.STAFF;
                        currentAnimation = staffAnimation;

                        // Crée la boule d'eau
                        launchBall(frontCellPosition, Element.WATER);
                    }

                    break;

                case FIRESTAFF:
                    // Si l'élément correspond
                    if (element.equals(Element.FIRE)){

                        // Modifie l'animation et l'état du joueur
                        playerState = PlayerState.STAFF;
                        currentAnimation = staffAnimation;

                        // Crée la boule d'eau
                        launchBall(frontCellPosition, Element.FIRE);
                    }

                    break;

                default :
                    break;

            }

        }
        updateCurrentItem();
    }

    /*
     * Met à jour le current item
     * Utile lors d'une collecte, ou pour la disparition d'un objet utilisé
     */
    private void updateCurrentItem(){

        // Si il n'y a pas d'item actuel, où si le joueur n'en contient aucun, on va effectuer une modifiation
        if (currentItem == null || !possess(currentItem)){
            for (ICoopItem item : ICoopItem.values()){
                if (possess(item)){

                    // Le premier disponible est mis
                    currentItem = item;
                    return;
                }
            }
            // Si il n'y en a aucun, mets null
            currentItem = null;
        }

    }

    /**
     * Lance une boule magique ; de feu ou d'eau
     * @param position Position initiale
     * @param elem element du personnage
     */
    public void launchBall(DiscreteCoordinates position, Element elem){
        playerState = PlayerState.STAFF;
        StaffBall Ball = new StaffBall(getOwnerArea(), getOrientation(), position, 3, 200, elem);
        this.getOwnerArea().registerActor(Ball);
    }

    /**
     * Fonction utilisée pour dessiner le personnage
     */
    @Override
    protected void drawCharacter(Canvas canvas) {
        currentAnimation.draw(canvas);
    }

    @Override
    public void draw(Canvas canvas) {

        // Fonction draw de IcoopCharacter
        super.draw(canvas);

        // Dessin des items
        statusGui.draw(canvas);
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        // On met uniquement la cellule principale
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Retourne true si le joueur est en déplacement
     * @return vrai si le player est en déplacement
     */
    public boolean isMoving() {
        return isDisplacementOccurs();
    }

    /**
     * Orientate and Move this player in the given orientation if the given button is down
     *
     * @param orientation (Orientation): given orientation, not null
     * @param b           (Button): button corresponding to the given orientation, not null
     */
    private void moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }

    /**
     * Leave an area by unregister this player
     */
    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    /**
     * makes the player entering a given area
     * @param area     (Area):  the area to be entered, not null
     * @param position (DiscreteCoordinates): initial position in the entered area, not null
     */
    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }


    /**
     * Retourne l'élément de l'ICoopPlayer (Feu ou eau)
     */
    @Override
    public Element getElement() {
        return this.element;
    }

     @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction){
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * Get this Interactor's curr
     * ent field of view cells coordinates
     * @return (List of DiscreteCoordinates). May be empty but not null
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {

        return Collections.singletonList
        (getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    
    }

    /**@return (boolean): true if this requires cell interaction */
    @Override
    public boolean wantsCellInteraction() {
        // Veut systématiquement toutes les intéractions de contact
        return true;
    }

    /**@return (boolean): true if this requires view interaction */
    @Override
    public boolean wantsViewInteraction() {

        // On veut les intéractions à distance seulement si le joueur est entrain de donné un coup d'épée
        return playerState.equals(PlayerState.SWORD);

    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {

        // Il faut faire de cette manière pour utiliser le handler
        other.acceptInteraction(interactionHandler, isCellInteraction);

    }

    /**
     * Retourne la porte empruntée pour quitter la map
     * @return  boolean
     */
    public Door getLeavingDoor() {
        return leavingDoor;
    }

    /**
     * Ajuste la variable isLeaving, lorsqu'une porte est prise
     * @param leaving boolean qui vrai si le player quitte la map
     */
    public void setLeaving(Boolean leaving) {
        isLeaving = leaving;
    }

    /**
     * Getter pour ICoop.java
     * @return boolean
     */
    public boolean isLeaving() {
        return isLeaving;
    }


    /**
     * Getter de l'item actuel
     * @return IcoopItem
     */
    public ICoopItem getCurrentItem(){
        return currentItem;
    }

    /**
     * Gestionnaire d'interaction
     */
    private final class ICoopPlayerInteractionHandler implements ICoopInteractionVisitor {

        // Interaction avec une porte
        @Override
        public void interactWith(Door door, boolean isCellInteraction) {

            // Vérification des accès
            if (door.getSignal().isOn()) {

                // Mise-à-jour des attributs
                isLeaving = true;
                leavingDoor = door;
            }
            
        }

        // Interaction avec un collectable
        @Override
        public void interactWith(ICoopCollectable item, boolean isCellInteraction){
            if (isCellInteraction && !item.isCollected()){
                item.collect();

                if (item.isStockable()){ 
                    inventory.addPocketItem(item.getInventoryItem(), 1);
                    updateCurrentItem();
                }
            }
        }

        // Interaction avec un explosif
        @Override
        public void interactWith(Explosif explo, boolean isCellInteraction){
            // Interaction à distance only, donc si le joueur presse le bouton pour useitem()
            Keyboard keyboard = getOwnerArea().getKeyboard();

            // Si c'est une intéraction de contact, on prend l'objet
            if (isCellInteraction) {
                
                // Collect l'explo
                interactWith((ICoopCollectable)explo, isCellInteraction);


            } else {
                // Si c'est à distance on active la bombe
                if (keyboard.get(playerKeyBindings.useItem()).isPressed()) {
                    explo.activate(1);
                }
            }
        }

        // Interaction avec une orbe
        @Override
        public void interactWith(Orb orb, boolean isCellInteraction) {

            if (isCellInteraction){

                // On collecte

                orb.collectBy(ICoopPlayer.this);


                // on le rend résistant aux murs
                setInvulnerableDamageType(orb.getDamage());
            }
        }

        // Interaction avec un coeur
        @Override
        public void interactWith(Heart heart, boolean isCellInteraction) {

            // On augmente la vie seulement si c'est pas encore collecté
            if (!heart.isCollected()) {
                // Augmente les points de vie du joueur
                increaseHealth(10);
                interactWith((ICoopCollectable) heart, isCellInteraction);
            }


        }

        // Interaction avec un baton
        @Override
        public void interactWith(Staff staff, boolean isCellInteraction) {

            // Si l'élément correspond et qu'il n'est pas collecté, l'ajoute à l'inventaire et le collecte
            if (isCellInteraction && element == staff.getElement()){
                interactWith((ICoopCollectable) staff, isCellInteraction);
            }
        }

        // Interaction avec un crâne
        @Override
        public void interactWith(HellSkull foe, boolean isCellInteraction) {

            // Se fait seulement lors d'une interaction de contact pendant une attaque avec épée
            if (!isCellInteraction) {

                if (playerState == PlayerState.SWORD) {
                    foe.loseHealth(Damage.PHYSICAL);
                }
            }
        }

        // Interaction avec un artificer
        @Override
        public void interactWith(BombFoe foe, boolean isCellInteraction) {

            // Se fait seulement lors d'une interaction de contact pendant une attaque avec épée
            if (!isCellInteraction) {
    
                if (playerState == PlayerState.SWORD) {
                    foe.loseHealth(Damage.PHYSICAL);
                }
            }
        }

        @Override
        // Interaction avec une clé
        public void interactWith(Key key, boolean isCellInteraction) {
            if (element.equals(key.getElement())){
                interactWith((ICoopCollectable) key, isCellInteraction);
            }  
        }

    }
}