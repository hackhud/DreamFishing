package ua.hackhud.dreamFishing.entities;

public class LakeStatus {
    private double fullness;
    private long nextUpdate;

    public LakeStatus(double fullness, long nextUpdate) {
        this.fullness = fullness;
        this.nextUpdate = nextUpdate;
    }

}
