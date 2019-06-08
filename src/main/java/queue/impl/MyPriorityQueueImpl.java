package queue.impl;

import queue.MyPriorityQueue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MyPriorityQueueImpl<E, T extends Comparable<T>> implements MyPriorityQueue<E, T> {

    private List<Node> linkedList = new LinkedList<>();

    private class Node {

        E nodeValue;

        T priority;

        Node(E nodeValue, T priority) {
            this.nodeValue = nodeValue;
            this.priority = priority;
        }
    }

    @Override
    public synchronized E pop() {
        if (linkedList.size() != 0) {
            E nodeValue = linkedList.get(0).nodeValue;
            linkedList.remove(linkedList.get(0));
            return nodeValue;
        }
        return null;
    }

    @Override
    public void add(E element, T priority) {
        Node newNode = new Node(element, priority);
        synchronized (this) {
            if (linkedList.size() == 0) {
                linkedList.add(newNode);
            } else if (linkedList.get(0).priority.compareTo(priority) < 0) {
                linkedList.add(0, newNode);
            } else if (linkedList.get(linkedList.size() - 1).priority.compareTo(priority) > 0) {
                linkedList.add(linkedList.size(), newNode);
            } else {
                int i = 0;
                Iterator<Node> iterator = linkedList.iterator();
                while (iterator.hasNext() && iterator.next().priority.compareTo(priority) > 0) {
                    i++;
                }
                linkedList.add(i, newNode);
            }
        }
    }

    @Override
    public synchronized void update(E element, T priority) {
        Optional<Node> optionalNode = linkedList.stream()
                .filter(node -> element.equals(node.nodeValue))
                .findAny();
        optionalNode.ifPresent(node -> {
            linkedList.remove(node);
            add(element, priority);
        });
    }
}