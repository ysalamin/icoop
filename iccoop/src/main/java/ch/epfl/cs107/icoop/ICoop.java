package ch.epfl.cs107.icoop;


import ch.epfl.cs107.icoop.actor.CenterOfMass;
import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.area.maps.Arena;
import ch.epfl.cs107.icoop.area.maps.Maze;
import ch.epfl.cs107.icoop.area.maps.OrbWay;
import ch.epfl.cs107.icoop.area.maps.Spawn;
import ch.epfl.cs107.icoop.enums.Element;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

/**
 * Classe principale de notre jeu
 */
public class ICoop extends AreaGame implements DialogHandler {

    // Deux joueurs
    private ICoopPlayer player1;
    private ICoopPlayer player2;

    // Tableau regroupant les players
    private ICoopPlayer[] players;

    // Dialogue courant
    private Dialog activeDialog = null;

    @Override
    public String getTitle() {
        return "ICoop";
    }

    /**
     * Add all the ICoop areas
     */
    private void createAreas() {

        // Toutes les aires
        Area mazeArea = new Maze();
        Area orbWayArea = new OrbWay(this);
        Area arenaArea = new Arena();
        Area spawnArea = new Spawn(this, (Logic) mazeArea); // Peut-être mettre en ICoop Area plutot ? jsp

        Area[] areas = new Area[]{mazeArea, orbWayArea, arenaArea, spawnArea};

        for (Area area: areas) {
            addArea(area);

            // Il est important d'appeler le begin sur chaque aire
            // sinon on peut avoir des NullPointerException si on
            // utilise l'instance d'une aire dans le code sans que le
            // player y soit allé une fois
            area.begin(getWindow(), getFileSystem());
        }

    }

    /**
     * @param window (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return true if the game begins properly
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {

        if (super.begin(window, fileSystem)) {
            createAreas();
            initGame();
            return true;
        }
        return false;
    }


    /**
     * Initialise le jeu
     */
    private void initGame() {

        // Le jeu commence dans l'aire spawn

        ICoopArea area = (ICoopArea) setCurrentArea("Spawn", true);
        createPlayers(area);

        // On centre la caméra sur le centre de masse
        setCamera();

        // On met le dialog welcome au début du jeu
        publish(new Dialog("welcome"));
    }

    /**
     * Crée les joueurs 
     * @param area Aire courante
     */
    private void createPlayers(ICoopArea area) {
        // ----- JOUEURS -----

        // Création du joueur 1
        DiscreteCoordinates coords = area.getPlayerSpawnPosition(Element.FIRE);
        player1 =  new ICoopPlayer(area, Orientation.DOWN, coords, Element.FIRE, true);

        // Création du joueur 2
        coords =  area.getPlayerSpawnPosition(Element.WATER);
        player2 = new ICoopPlayer(area, Orientation.DOWN, coords, Element.WATER, false);


        // Register des acteurs
        this.getCurrentArea().registerActor(player1);
        this.getCurrentArea().registerActor(player2);

        players = new ICoopPlayer[2];
        players[0] = player1;
        players[1] = player2;

    }

    /**
     * Ajuste la caméra sur le centre de masse
     */
    private void setCamera() {

        CenterOfMass centerOfMass = new CenterOfMass(player1, player2);
        getCurrentArea().setViewCandidate(centerOfMass);
    }


    @Override
    public void update(float deltaTime) {

        // S'occupe des joueurs changeant de map à l'aide d'une porte
        checkLeavingPlayer();

        // Vérifie que la touche reset ait été pressée
        checkReset();

        // Vérifie que les joueurs aient de la vie
        checkHealth();

        // S'occupe des dialogues : affichage et skip si "next dialog" est appuyée
        checkDialog(deltaTime);

        // Ajustement du scale factor
        ICoopArea currentICoopArea = (ICoopArea) getCurrentArea();
        currentICoopArea.updateScaleFactor(player1, player2);


        super.update(deltaTime);
    }

    @Override
    public void draw() {
        if (activeDialog != null) {
            activeDialog.draw(getWindow());
        }
        super.draw();
    }

    /**
     * Reste la map si l'un des joueurs est mort
     */
    private void checkHealth() {
        for (ICoopPlayer player : players) {
            if (!player.isAlive()) {
                resetMap();
            }
        }
    }


    /**
     * Méthode s'occupant de la partie dialogue
     * @param deltaTime temps entre deux update
     */
    private void checkDialog(float deltaTime){
        Keyboard kbd = getCurrentArea().getKeyboard();

        // Affiche le dialogue s'il y en a un en cours, et pause le jeu
        if (activeDialog != null){
            getCurrentArea().requestPause();
            
            // Check si le joueur veut skip le dialogue
            if (kbd.get(KeyBindings.NEXT_DIALOG).isPressed()){
                activeDialog.update(deltaTime);
            }

            // Enlève le dialogue s'il est terminé
            if (activeDialog.isCompleted()){
                activeDialog = null;
                getCurrentArea().requestResume();
            }
        }
    }

    @Override
    public boolean isDialogActiv() {
        return activeDialog != null;
    }


    @Override
    public void publish(Dialog dialog) {
        this.activeDialog = dialog;
    }

    /**
     * Méthode qui reset le jeu si la touche correspondante est pressée
     */
    private void checkReset() {
        Keyboard keyboard = getCurrentArea().getKeyboard();
        if (keyboard.get(KeyBindings.RESET_AREA).isPressed()) {

            resetMap();

        } else if (keyboard.get(KeyBindings.RESET_GAME).isPressed()) {
            // Ici il suffit de réinitialiser le jeu en entier
            this.begin(getWindow(), getFileSystem());
        }
    }

    /**
     * Réinitialise la map 
     */
    private void resetMap() {
        // On reéinitialise la map
        getCurrentArea().begin(getWindow(), getFileSystem());

        // On remet les joueurs
        createPlayers((ICoopArea) getCurrentArea());

        // Il ne faut pas oublier de remettre la camera centre de masse
        setCamera();

        // On reset la vie
        resetPlayersHealth();
    }

    /**
     * Réinitialise la vie des joeuurs
     */
    private void resetPlayersHealth() {
        player1.resetHealth();
        player2.resetHealth();
    }


    /**
     * Cette fonction check si un des deux players traverse une porte et si c'est le cas les changements
     * de positions
     * et d'areas sont effectués
     */
    private void checkLeavingPlayer() {

        // Pour chacun des joueurs
        for (ICoopPlayer playerEl : players) {

            // Lorsqu'il quitte
            if (playerEl.isLeaving()) {
                
                // Met à jour son attribut
                playerEl.setLeaving(false);

                // Obtient la porte correspondante
                Door door = playerEl.getLeavingDoor();

                // Fait quitter les deux joueurs
                player1.leaveArea();
                player2.leaveArea();

                // Met à jour la map actuelle
                Area areaToGo = setCurrentArea(door.getDestinationArea(), false);

                // Y fait rentrer les joueurs
                player1.enterArea(areaToGo, door.getFuturePositions().get(0));
                player2.enterArea(areaToGo, door.getFuturePositions().get(1));

                // Ajuste la camera
                setCamera();

            }
        }

    }
}