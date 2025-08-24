#### CONCEPTION ####

Nous sommes restés pour la plus grande partie du temps fidèle
à la conception proposée par l'énoncé. Cependant, certains aspects diffèrent : 

# IcoopCharacter
Nous avons créé une classe abstraite IcoopCharacter pour éviter la duplication de code
des ennemis et des players. La gestion de la barre de vie est des dégâts est donc mieux
centralisée.

# Utilisation d'Item en déplacement
Pour éviter certains bugs d'affichage, un joueur ne peux pas utiliser la touche useItem
en déplacement, il doit donc être à l'arrêt pour utiliser son épée ou lancer des boules.

# Elemental Walls
Nous n'avons pas créé deux sous classes "Firewall" et "Waterwall".
Nous avons préferé crée qu'une seule classe Wall, et gérer son élément à
l'aide d'un attribut, qui influencera son sprite et ses droits d'interactions.

Cette même organisation a été utilisée pour les "Staffs"

# Dialogue de début
Il nous a semblé bon de publier le premier dialog à l'initialisation du jeu
dans la méthode initGame de "ICoop.java", et non pas dans le fichier d'une map spécifique, au cas où nous
souhaiterions changer de carte de départ plus tard.

# Artificier
Dans le dossier, il est écrit que l'artificier avance moins vite en mode protégé, mais nous
avons préféré que l'artificier s'arrête complétement le temps de poser et la bombe. Ensuite
il se remet à bouger. C'est aussi le comportement que l'on observe quand on regarde la vidéo
fournie du dossier.

# Porte du manoir
Au lieu de créer une instance Door pour la porte du manoir, nous avons préféré créer 
un nouvel objet DialogDoor différent, en effet, nous avons jugé que les deux entités divergeaient
suffisamment, car une DialogDoor ne sert pas à changer d'air, mais affiche un dialogue à la place.
De plus nous avons eu besoin des intéractions à distance avec la DialogDoor alors qu'une Door n'en a pas.
En effet, pour ne pas bloquer le dialogue lorsqu'on est sur la porte du manoir, nous testons
avec des intéractions à distance si le Player est parti pour pouvoir réactiver le dialogue s'il revient
par la suite.

# Foes 
Nous avons ajouté la barre de vie sur les Foes alors que ceci n'était pas indiqués dans le dossier.
Mais nous trouvons cela préférable pour le joueur, car il peut savoir combien de dégats il reste sur l'ennemi,
et il peut mieux reconnaitre les ennemis en général, car la barre est rouge.

# Aire Arena
Dans la vidéo finale du dossier, les boules des StaffBall permettent de casser plusieurs
rochers à la fois, nous avons jugé préférable de n'en casser qu'um seul à la fois.

# HellSkull
Dans l'énoncé, il est indiqué que les HellSkull doivent faire des dommages de feu par intéraction
de contact et ces dommages doivent être identiques pour tous les personnages. 
Nous avons donc décidé de mettre le même type de dommage que les mûrs pour avoir un code plus propre. 
Les murs infligent donc exactement le même nombre de dommages que les HellSkulls. 
Cela signifie également que le joueur feu, s'il
a pris l'Orb, est résistant aux dommages de feu du HellSkull.

# Unstoppable classe
Nous avons préféré coder une classe abstraite au lieu d'une interface pour le Unstoppable car nous trouvions
cela plus adapté.