package ch.epfl.cs107.icoop.actor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;


/**
Représente les portes qui permettent de transiter vers une aire de destination
*/
public class Door extends AreaEntity {

    // Nom de l'aire vers laquelle la porte permet de transiter
    private final String goToAreaName;

    // Positions d'arrivées dans la nouvelle aire, des deux personnages (deux positions différentes)
    private final List<DiscreteCoordinates> futurePositions;

    // Variable qui permet de modéliser les conditions d'ouverture de la porte
    private Logic signal;

    // Coordonnées des autres cells occupées
    private List<DiscreteCoordinates> otherCellsCoordinates;


    /**
     * Constructeur principal des portes
     * @param goToAreaName (Aire où mène la porte)
     * @param signal (Est-elle activée ?)
     * @param futurePositions (Coordonnées), position d'arrivée dans la future map
     * @param ownerArea (Aire où se situe la porte)
     * @param mainCellPosition (Position principale occupée par la porte)
     */
    public Door(String goToAreaName, Logic signal, List<DiscreteCoordinates> futurePositions, Area ownerArea, DiscreteCoordinates mainCellPosition){
        super(ownerArea, Orientation.DOWN, mainCellPosition);
        this.goToAreaName = goToAreaName;
        this.futurePositions = futurePositions;
        this.signal = signal;
        this.otherCellsCoordinates = null;
    }

    /**
     * Autre constructeur avec l'option d'établir les positions des autres cellules que la porte occupent. Le paramètre supplémentaire est : otherCellsPosition
     * @param goToAreaName (Aire où mène la porte)
     * @param signal (Est-elle activée ?)
     * @param futurePositions (Coordonnées), position d'arrivée dans la future map
     * @param ownerArea (Aire où se situe la porte)
     * @param mainCellPosition (Position principale occupée par la porte)
     * @param otherCellsPosition (Autres cellules)
     */
    public Door(String goToAreaName, Logic signal, List<DiscreteCoordinates> futurePositions,
     Area ownerArea, DiscreteCoordinates mainCellPosition, DiscreteCoordinates... otherCellsPosition){
                    this(goToAreaName, signal, futurePositions, ownerArea, mainCellPosition);
                    this.otherCellsCoordinates = Arrays.asList(otherCellsPosition);
    }
                    

     /**
     * Donne une liste composée des coordonnées de la cell principale et des autres cells
     * @return (List of DiscreteCoordinates). Peut être vide mais pas null
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells(){
        // Coordonnée principale
        DiscreteCoordinates mainCellCoords = super.getCurrentMainCellCoordinates();

        // Création de liste des autres coordonnées
        List<DiscreteCoordinates> occupiedCellsCoords = new ArrayList<>();

        // On y ajoute la coordonnée principale, puis les autres
        occupiedCellsCoords.add(mainCellCoords);

        if (otherCellsCoordinates != null) {
            for (DiscreteCoordinates coords : otherCellsCoordinates){
                occupiedCellsCoords.add(coords);
            }
        }
        return occupiedCellsCoords;
    }

    @Override
    public boolean takeCellSpace(){
        return false;
    }

    @Override
    public boolean isCellInteractable(){
        return true;
    }

    @Override
    public boolean isViewInteractable(){
        return false;
    }

     @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction){
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * Getter pour ICoop
     * @return
     */
    public String getDestinationArea() {
        return goToAreaName;
    }

    /**
     * Getter pour ICoop
     * @return
     */
    public List<DiscreteCoordinates> getFuturePositions() {
        return futurePositions;
    }

    /**
     * Getter pour obtenir le signal dans ICoopPlayer
     * @return
     */
    public Logic getSignal() {
        return signal;
    }

    
}
