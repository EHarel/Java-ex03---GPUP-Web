package datastructure;

public class QueueLinkedList<T> implements Queue<T> {
    QueueNode oldestNode;
    QueueNode youngestNode;
    int size = 0;

    /**
     *
     * @return null if queue is empty
     */
    @Override
    public T dequeue() {
        if (this.youngestNode == null) { // datastructure.Queue is empty
            return null;
        }

        QueueNode<T> temp = this.oldestNode;
        this.oldestNode = this.oldestNode.getNext();
        size--;

        if (this.oldestNode == null) {
            this.youngestNode = null;
        }

        return temp.getValue();
    }

    @Override
    public void enqueue(T newValue) {
        QueueNode<T> temp = new QueueNode<>(newValue, null);

        if (this.oldestNode == null) { // Empty queue
            this.oldestNode = this.youngestNode = temp;
            return;
        }

        this.youngestNode.setNext(temp);
        this.youngestNode = temp;
        size++;
    }

    @Override
    public boolean isEmpty() {
        return (oldestNode == null);
    }

    private class QueueNode<T> {
        private T value;
        private QueueNode<T> nextNode;

        public QueueNode(T initValue, QueueNode<T> next) {
            this.value = initValue;
            this.nextNode = next;
        }

        public QueueNode<T> getNext() {
            return nextNode;
        }

        public T getValue() {
            return value;
        }

        public void setNext(QueueNode<T> next) {
            this.nextNode = next;
        }
    }

    @Override
    public int size() {
        int size = 0;

        QueueNode<T> currNode = oldestNode;

        while (currNode != null) {
            size++;
            currNode = currNode.getNext();
        }

        return size;
    }
}