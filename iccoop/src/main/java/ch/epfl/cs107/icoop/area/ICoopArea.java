package ch.epfl.cs107.icoop.area;

import static java.lang.Math.max;

import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.enums.Element;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;


/**
 * Base class for all the areas of ICoop
 */
public abstract class ICoopArea extends Area {

    public final static float DEFAULT_SCALE_FACTOR = 18.f;
    private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;

    // Variable utile pour la fonction update
    // pour garantir un premier affichage
    private boolean hasBeenInitialised;


    /**
     * Area specific callback to initialise the instance
     */
    protected abstract void createArea();

    /**
     * @return the player's spawn position in the area
     */
    public abstract DiscreteCoordinates getPlayerSpawnPosition(Element element);

    /**
     * Callback to initialise the instance of the area
     * @param window (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return true if the area is instantiated correctly, false otherwise
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {


            // On stock l'instance du areaBehavior dans une variable pour, pouvoir l'utiliser dans Arena
            ICoopBehavior areaBehaviorInstance = new ICoopBehavior(window, getTitle());

            setBehavior(areaBehaviorInstance);
            createArea();

            // On crée les obstacles et les cailloux
            areaBehaviorInstance.createActors(this);


            // Initialisation finie
            hasBeenInitialised = false;

            return true;
        }
        return false;
    }


    @Override
    public void update(float deltaTime){

        // Première update pour que quelquechose s'affiche au départ
        if (!hasBeenInitialised) {
            super.update(deltaTime);
            hasBeenInitialised = true;
        }

        // Si l'aire est en pause on update pas
        if (!this.isPaused()) {
            super.update(deltaTime);
        }
    }

    /**
     * Getter for Icoop's scale factor
     * @return Scale factor in both the x-direction and the y-direction
     */
    @Override
    public final float getCameraScaleFactor() {
        return cameraScaleFactor;
    }

    /**
     * Met à jour le scale Factor
     * @param fire
     * @param water
     */
    public void updateScaleFactor(ICoopPlayer fire, ICoopPlayer water){
        float distance = fire.getPosition().sub(water.getPosition()).getLength();
        cameraScaleFactor = (float) max(DEFAULT_SCALE_FACTOR, DEFAULT_SCALE_FACTOR * 0.75 + distance / 2);
    }
 }
