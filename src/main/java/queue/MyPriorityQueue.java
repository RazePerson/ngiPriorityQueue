package queue;

/**
 * A thread safe priority queue that provides the possibility to use user-defined priorities.
 *
 * @param <E> the type of elements in the queue
 * @param <T> the type of the priority
 */
public interface MyPriorityQueue<E, T> {

    /**
     * Gets the element with the biggest priority and removes it from the queue.
     * The complexity is O(1).
     *
     * @return the element with the biggest priority
     */
    E pop();

    /**
     * Adds a new element with the specified priority which has to be comparable.
     * The complexity is O(n).
     *
     * @param element  the type of the element
     * @param priority the type of the priority
     */
    void add(E element, T priority);

    /**
     * Updates a certain element's priority and reorders the queue.
     * The complexity is O(n).
     *
     * @param element  the type of the element
     * @param priority the type of the priority
     */
    void update(E element, T priority);
}
