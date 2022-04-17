package com.max.app.geeks;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * https://www.geeksforgeeks.org/minimize-refills-to-reach-end-of-path/
 * <p>
 * Given an integer target which represents the total distance to be covered by a car on a straight road. Given another
 * array, station[] of size N representing petrol pumps where ith petrol pump is station[i][0] position away from the start
 * and has station[i][1] amount of fuel. The car has an infinite petrol capacity and starts with M amount of fuel. The task
 * is to find the minimum number of times the car has to stop for refueling to reach the end when it uses one unit of fuel
 * for moving one unit distance.
 * <p>
 * Note: If it reaches ith station with 0 fuel left, it can refuel from that petrol pump and all the fuel from a petrol pump
 * can be transferred to the car.
 * <p>
 * Use greedy approach to find min numbers of refuels.
 * Greedy approach works here, b/c car fuel capacity is UNLIMITED.
 */
public class MinRefuelsCountToReachTargetDistance {

    public static void main(String[] args) {

        GasStation[] stations = new GasStation[] {
            new GasStation(10, 60), new GasStation(20, 30), new GasStation(30, 30), new GasStation(60, 40)
        };

        final int initialFuel = 10;
        final int targetDistance = 100;
        int refuelsCnt = finMinRefuelsCount(stations, initialFuel, targetDistance);

        System.out.printf("refuelsCnt: %d%n", refuelsCnt);

        System.out.println("Maine done...");
    }

    /*
     * time: O(N*lgN)
     * space: O(N)
     */
    public static int finMinRefuelsCount(GasStation[] stations, int initialFuel, int targetDistance) {

        // time: O(N*lgN)
        Arrays.sort(stations, GasStation.DISTANCE_ASC);
        int refuelsCnt = 0;

        // space: O(N)
        PriorityQueue<GasStation> maxHeap = new PriorityQueue<>(GasStation.FUEL_DESC);
        int curDistance = initialFuel;
        int index = 0;

        // time: O(N*lgN), because we potentially need to insert to max-heap N times (and potentially delete N times)
        while (curDistance < targetDistance) {

            for (; index < stations.length; ++index) {
                GasStation gasStation = stations[index];
                if (curDistance < gasStation.distance()) {
                    break;
                }
                maxHeap.add(gasStation);
            }

            if (maxHeap.isEmpty()) {
                return -1;
            }

            curDistance += maxHeap.poll().fuel;
            ++refuelsCnt;
        }

        return refuelsCnt;
    }

    public static record GasStation(int distance, int fuel) {
        private static Comparator<GasStation> DISTANCE_ASC = Comparator.comparing(GasStation::distance);
        private static Comparator<GasStation> FUEL_DESC = Comparator.comparing(GasStation::fuel).reversed();

    }


}
