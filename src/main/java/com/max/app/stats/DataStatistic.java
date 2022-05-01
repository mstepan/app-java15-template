package com.max.app.stats;

import java.util.HashMap;
import java.util.Map;

public class DataStatistic {

    private final Map<Character, Integer> freq = new HashMap<>();

    public void add(String data) {
        for (int i = 0; i < data.length(); ++i) {
            char key = data.charAt(i);
            freq.compute(key, (notUsed, cnt) -> cnt == null ? 1 : cnt + 1);
        }
    }

    public double standardDeviation() {
        double diffSquared = 0.0;

        double avg = average();

        for (int singleFreq : freq.values()) {
            double diff = avg - singleFreq;
            diffSquared += (diff * diff);
        }

        return Math.sqrt(diffSquared / distinctElementsCount());
    }

    public double average() {
        return sum() / distinctElementsCount();
    }

    public double sum() {
        double sum = 0.0;

        for (int count : freq.values()) {
            sum += count;
        }
        return sum;
    }


    public int distinctElementsCount() {
        return freq.size();
    }

}
