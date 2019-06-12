package queue.impl;

import queue.MyPriorityQueue;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

public class MyPriorityQueueImpl<E, T extends Comparable<T>> implements MyPriorityQueue<E, T> {

    private ConcurrentSkipListMap<Node, T> concurrentSkipListMap = new ConcurrentSkipListMap<>();

    private class Node implements Comparable<Node> {

        E nodeValue;

        T priority;

        Node(E nodeValue, T priority) {
            this.nodeValue = nodeValue;
            this.priority = priority;
        }

        @Override
        public int compareTo(Node o) {
            if (o.nodeValue.equals(nodeValue)) {
                return 0;
            }
            if (o.priority.compareTo(priority) != 0) {
                return o.priority.compareTo(priority);
            }
            return 1;
        }
    }

    @Override
    public E pop() {
        Node nodeToReturn = concurrentSkipListMap.keySet().pollFirst();
        if (nodeToReturn != null) {
            return nodeToReturn.nodeValue;
        }
        return null;
    }

    @Override
    public void add(E element, T priority) {
        Node newNode = new Node(element, priority);
        concurrentSkipListMap.put(newNode, priority);
    }

    @Override
    public void update(E element, T priority) {
        Optional<Node> optionalNode = concurrentSkipListMap.keySet().stream()
                .filter(node -> element.equals(node.nodeValue))
                .findAny();

        optionalNode.ifPresent(node -> {
            concurrentSkipListMap.remove(node);
            add(element, priority);
        });
    }
}