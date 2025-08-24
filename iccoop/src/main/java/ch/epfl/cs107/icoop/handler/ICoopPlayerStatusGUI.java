package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.play.engine.actor.Graphics;
import ch.epfl.cs107.play.engine.actor.ImageGraphics;
import ch.epfl.cs107.play.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * A GUI that shows information about the player on the screen.
 */
public class ICoopPlayerStatusGUI implements Graphics {

    private final static int DEPTH = 2000;

    // Joueur lié à l'affichage
    private final ICoopPlayer player;

    // Pour gérer l'affichage à gauche ou à droite
    private final boolean flipped;

    /**
     * Constructeur du GUI
     * @param player (Joueur) Correspond au joueur auquel l'affichage est relié
     * @param flipped (Booléen) Switch ou non l'affichage de côté, pour éviter la superposition
     */
    public ICoopPlayerStatusGUI(ICoopPlayer player, boolean flipped) {
        this.player = player;
        this.flipped = flipped;
    }

    @Override
    public void draw(Canvas canvas) {
        // Compute width, height and anchor
        float width = canvas.getTransform().getX().getX();
        float height = canvas.getTransform().getY().getY();

        float ratio = canvas.getWidth() / (float) canvas.getHeight();
        if (ratio > 1)
            height = width / ratio;
        else
            width = height * ratio;

        Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(flipped ? (-width / 2 + 2) : width / 2, height / 2));

        //Draw selected gear
        ImageGraphics gearDisplay = new ImageGraphics(ResourcePath.getSprite("icoop/gearDisplay"), 1.5f, 1.5f, new RegionOfInterest(0, 0, 32, 32), anchor.add(new Vector(0, height - 1.75f)), 1, DEPTH);
        gearDisplay.draw(canvas);

        // Dessin du sprite du currentItem
        ICoopItem currentItem = player.getCurrentItem();
        if (currentItem != null) {

            ImageGraphics itemSprite = new ImageGraphics(ResourcePath.getSprite(currentItem.getPath()),0.5f, 0.5f,
             new RegionOfInterest(0, 0, 16, 16),anchor.add(new Vector(0.5f, height - 1.25f)),1, DEPTH);
            
             itemSprite.draw(canvas);
        }
        
    }
}
