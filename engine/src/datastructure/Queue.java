package datastructure;

public interface Queue<T> {

    T dequeue();

    void enqueue(T t);

    boolean isEmpty();

    int size();
}