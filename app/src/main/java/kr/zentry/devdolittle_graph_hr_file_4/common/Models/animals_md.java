package kr.zentry.devdolittle_graph_hr_file_4.common.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class animals_md {
    private animal_md[] animals;

    public List<animal_md> getAnimalsArrays() {
        List<animal_md> ret = new ArrayList<>(Arrays.asList(animals));
        return ret;
    }

    public animal_md[] getAnimals() {
        return animals;
    }

    public void setAnimals(animal_md[] animals) {
        this.animals = animals;
    }
}
