package ua.hackhud.dreamFishing.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedDropTable {

    private final List<DropItem> items;
    private double[] cumWeights;
    private double totalWeight;
    private static final Random RANDOM = new Random();

    public WeightedDropTable(List<DropItem> items) {
        this.items = new ArrayList<>(items);
        this.cumWeights = new double[items.size()];

        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getWeight();
            cumWeights[i] = total;
        }
        this.totalWeight = total;
    }

    public DropItem getRandomDrop() {
        if (items.isEmpty()) return null;
        double r = RANDOM.nextDouble() * totalWeight;
        int index = binarySearch(r);
        return items.get(index);
    }

    private int binarySearch(double value) {
        int left = 0, right = cumWeights.length - 1;
        while (left < right) {
            int mid = (left + right) / 2;
            if (value < cumWeights[mid]) right = mid;
            else left = mid + 1;
        }
        return left;
    }

    public List<DropItem> getItems() {
        return items;
    }

    public void addDrop(DropItem drop) {
        if (drop == null) return;
        items.add(drop);
        recalcWeights();
    }

    private void recalcWeights() {
        cumWeights = new double[items.size()];
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getWeight();
            cumWeights[i] = total;
        }
        totalWeight = total;
    }
}
