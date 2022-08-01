package com.ffmoyano.idunn.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AdventurerDto {

    private Long id;
    @NotEmpty(message = "El nombre no puede estar vacío")
    @Size(min=4, max=12, message = "el tamaño del nombre debe ser entre 4 y 12 caracteres")
    private String name;
    @NotEmpty(message = "La clase no puede estar vacía")
    private String characterClass;

    @Min(value = 1, message = "Los puntos de vida deben ser superiores a 0")
    private int hitPoints;
    @NotEmpty(message = "El alineamiento no puede estar vacío")
    private String alignment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

}