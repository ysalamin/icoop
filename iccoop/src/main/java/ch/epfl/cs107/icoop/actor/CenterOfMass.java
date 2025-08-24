package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;

/**
 * Représente le centre de masse de plusieurs joueurs
 */
public class CenterOfMass implements Actor {

    // Liste de joueurs dont on veut tenir compte pour le centre de masse
    private final Actor[] actors;

    public CenterOfMass(Actor actor, Actor... restOfActors) {
        this.actors = new Actor[restOfActors.length + 1];
        actors[0] = actor;
        System.arraycopy(restOfActors, 0, this.actors, 1, restOfActors.length);
    }

    @Override
    public Vector getPosition() {
        Vector position = Vector.ZERO;
        for (Actor actor : actors) {
            position = position.add(actor.getPosition());
        }
        return position.mul(1f / actors.length);
    }

    @Override
    public Transform getTransform() {
        return Transform.I.translated(getPosition());
    }

    @Override
    public Vector getVelocity() {
        Vector velocity = Vector.ZERO;
        for (Actor actor : actors) {
            velocity = velocity.add(actor.getVelocity());
        }
        return velocity.mul(1f / actors.length);
    }
}
