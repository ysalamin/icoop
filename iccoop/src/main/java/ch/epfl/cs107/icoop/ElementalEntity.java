package ch.epfl.cs107.icoop;

import ch.epfl.cs107.icoop.enums.Element;

/**
Interface caractérisant les entités pouvant avoir un éléments 
*/
public interface ElementalEntity {

    /* Retourne l'élément de l'entité */
    Element getElement();
}