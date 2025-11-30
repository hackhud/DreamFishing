package ua.hackhud.dreamFishing.service;

import ua.hackhud.dreamFishing.entities.Lake;

public class LakeFullnessService {

    public void consumeFullness(Lake lake) {
        lake.getLakeStatus().decreaseFullness(calculateRandomFullnessDecrease());
    }

    private double calculateRandomFullnessDecrease() {
        return Math.random() * 0.05D;
    }

}
