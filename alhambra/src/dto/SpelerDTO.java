package dto;

import utils.Kleur;

public record SpelerDTO(
    String gebruikersnaam,
    int geboortejaar,
    Kleur kleur,
    int zetStenen
) {
    
}