#### DESIGN ####

For the most part, we remained faithful
to the design proposed in the statement. However, some aspects differ: 

# IcoopCharacter
We created an abstract class called IcoopCharacter to avoid code duplication
between enemies and players. The management of the health bar and damage is therefore better
centralized.

# Use of Items while Moving
To avoid certain display bugs, a player cannot use the useItem key
while moving, so they must be stationary to use their sword or throw balls.

# Elemental Walls
We did not create two subclasses, “Firewall” and “Waterwall.”
We preferred to create a single Wall class and manage its element
using an attribute, which will influence its sprite and interaction rights.

The same organization was used for the “Staffs.”

# Start dialogue
We thought it would be a good idea to publish the first dialogue when the game is initialized
in the initGame method of “ICoop.java,” rather than in a specific map file, in case we
wanted to change the starting map later.

# Fireworks technician
The file states that the fireworks technician moves more slowly in protected mode, but we
preferred to have the fireworks technician stop completely while placing the bomb. Then
he starts moving again. This is also the behavior observed when watching the video
provided in the file.

# Manor door
Instead of creating a Door instance for the manor door, we preferred to create 
a new, different DialogDoor object, as we felt that the two entities were sufficiently different,
 since a DialogDoor is not used to change the air, but instead displays a dialogue.
In addition, we needed remote interactions with the DialogDoor, which a Door does not have.
In fact, in order not to block the dialogue when on the manor door, we test
with remote interactions whether the Player has left so that we can reactivate the dialogue if they return
later.

# Foes 
We added the health bar to the Foes, even though this was not specified in the file.
However, we think this is better for the player, as they can see how much damage is left on the enemy,
and they can recognize enemies better in general, as the bar is red.

# Arena Area
In the final video in the file, the StaffBall balls can break several
rocks at once, but we decided it was better to break only one at a time.

# HellSkull
The statement indicates that HellSkulls must deal fire damage through contact interaction,
and this damage must be identical for all characters. 
We therefore decided to use the same type of damage as walls in order to have cleaner code. 
Walls therefore inflict exactly the same amount of damage as HellSkulls. 
This also means that the fire player, if
they have taken the Orb, is resistant to fire damage from HellSkull.

# Unstoppable class
We preferred to code an abstract class instead of an interface for Unstoppable because we found
it more suitable.
