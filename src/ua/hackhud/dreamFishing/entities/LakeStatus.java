package ua.hackhud.dreamFishing.entities;

public class LakeStatus {
    private double fullness;
    private long nextUpdate;

    public LakeStatus(double fullness, long nextUpdate) {
        this.fullness = fullness;
        this.nextUpdate = nextUpdate;
    }

    public double getFullness() {
        return fullness;
    }

    public void setFullness(double fullness) {
        this.fullness = fullness;
    }

    public long getNextUpdate() {
        return nextUpdate;
    }

    public void setNextUpdate(long nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public void decreaseFullness(double decrease) {
        fullness = Math.max(0.0D, fullness - decrease);
    }
}
