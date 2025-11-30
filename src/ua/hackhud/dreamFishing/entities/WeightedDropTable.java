package ua.hackhud.dreamFishing.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedDropTable {

    private static final Random RANDOM = new Random();
    private static final double MODIFIER_STRENGTH = 0.5;
    private static final double MIN_WEIGHT_FACTOR = 0.3;

    private final List<DropItem> items;
    private double[] cumulativeWeights;
    private double totalWeight;

    public WeightedDropTable(List<DropItem> items) {
        this.items = new ArrayList<>(items);
        recalcWeights();
    }

    public DropItem getRandomDrop() {
        if (items.isEmpty()) {
            return null;
        }

        double r = RANDOM.nextDouble() * totalWeight;
        int index = binarySearch(r);
        return items.get(index);
    }

    public DropItem getRandomDropWithFullness(double fullnessPercent) {
        if (items.isEmpty()) {
            return null;
        }

        double normalizedFullness = clampFullness(fullnessPercent / 100.0);
        if (normalizedFullness == 0.0D || items.size() == 1) {
            return getRandomDrop();
        }

        int itemCount = items.size();
        double adjustedTotalWeight = 0.0D;
        double[] adjustedCumWeights = new double[itemCount];

        for (int index = 0; index < itemCount; index++) {
            adjustedTotalWeight += calculateAdjustedWeight(index, itemCount, normalizedFullness);
            adjustedCumWeights[index] = adjustedTotalWeight;
        }

        if (adjustedTotalWeight <= 0.0D) {
            return getRandomDrop();
        }

        double randomValue = RANDOM.nextDouble() * adjustedTotalWeight;
        int index = binarySearch(randomValue, adjustedCumWeights);
        return items.get(index);
    }

    private int binarySearch(double value) {
        return binarySearch(value, cumulativeWeights);
    }

    private int binarySearch(double value, double[] weights) {
        int left = 0;
        int right = weights.length - 1;
        while (left < right) {
            int mid = (left + right) / 2;
            if (value < weights[mid]) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    public List<DropItem> getItems() {
        return items;
    }

    public void addDrop(DropItem drop) {
        if (drop == null) {
            return;
        }
        items.add(drop);
        recalcWeights();
    }

    private void recalcWeights() {
        cumulativeWeights = new double[items.size()];
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getWeight();
            cumulativeWeights[i] = total;
        }
        totalWeight = total;
    }

    private double calculateAdjustedWeight(int index, int itemCount, double normalizedFullness) {
        double baseWeight = Math.max(0.0D, items.get(index).getWeight());
        if (normalizedFullness == 0.0D || itemCount <= 1) {
            return baseWeight;
        }

        double position = (double) index / (itemCount - 1);
        double rarityBias = 2 * position - 1;

        double weightFactor = 1 + MODIFIER_STRENGTH * normalizedFullness * rarityBias;
        weightFactor = Math.max(MIN_WEIGHT_FACTOR, weightFactor);

        return baseWeight * weightFactor;
    }

    private double clampFullness(double value) {
        if (value < 0.0D) return 0.0D;
        if (value > 1.0D) return 1.0D;
        return value;
    }
}
