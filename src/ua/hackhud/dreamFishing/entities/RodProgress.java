package ua.hackhud.dreamFishing.entities;

public class RodProgress {
    private final int current;
    private final int needed;

    public RodProgress(int current, int needed) {
        this.current = current;
        this.needed = needed;
    }

    public static RodProgress empty() {
        return new RodProgress(0, 0);
    }

    public RodProgress increment() {
        return new RodProgress(current + 1, needed);
    }

    public boolean isComplete() {
        return current >= needed;
    }

    public int getCurrent() {
        return current;
    }

    public int getNeeded() {
        return needed;
    }
}