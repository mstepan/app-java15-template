package com.max.app.geeks;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Geeks1 {


    public static void main(String[] args) {

        TreeNode first = new TreeNode(9, List.of(new TreeNode(3), new TreeNode(-1)));

        TreeNode second = new TreeNode(-13, List.of(new TreeNode(1), new TreeNode(-3)));

        TreeNode tree = new TreeNode(5, List.of(first, second));


        TreeNode maxAvgSubtree = findSubtreeWithMaxAverage(tree);

        System.out.println(maxAvgSubtree);

        System.out.println("Geeks1 done...");
    }


    /**
     * post-order N-ary tree traversal
     * <p>
     * time: O(N)
     * spac: O(N)
     */
    static TreeNode findSubtreeWithMaxAverage(TreeNode root) {
        Objects.requireNonNull(root);

        Map<TreeNode, Stat> processedNodes = new IdentityHashMap<>();

        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();

            if (cur.isLeaf()) {
                processedNodes.put(cur, new Stat(cur));
            }
            else {
                if (processedNodes.containsKey(cur.firstChild())) {
                    processedNodes.put(cur, combineStat(cur, processedNodes));
                }
                else {
                    stack.push(cur);
                    for (TreeNode child : cur.children) {
                        stack.push(child);
                    }
                }
            }
        }

        Optional<Stat> maybeMaxStat = processedNodes.values().stream().max(Stat.AVG_ASC);

        assert maybeMaxStat.isPresent();

        System.out.println(processedNodes);

        return maybeMaxStat.get().node;
    }

    private static Stat combineStat(TreeNode cur, Map<TreeNode, Stat> processedNodes) {
        int combinedSum = cur.value;
        int size = 1;

        for (TreeNode child : cur.children) {
            Stat childStat = processedNodes.get(child);
            combinedSum += childStat.sum;
            size += childStat.size;
        }
        return new Stat(cur, combinedSum, size);
    }

    private static class Stat {

        private static final Comparator<Stat> AVG_ASC = Comparator.comparingDouble(Stat::avg);

        final TreeNode node;
        final int sum;
        final int size;

        Stat(TreeNode node, int sum, int size) {
            this.node = node;
            this.sum = sum;
            this.size = size;
        }

        Stat(TreeNode node) {
            this.node = node;
            this.sum = node.value;
            this.size = 1;
        }

        double avg() {
            return (double) sum / size;
        }

        @Override
        public String toString() {
            return "(val: " + node.value + ", avg: " + avg() + ")";
        }

    }
}
