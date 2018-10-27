import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * All queue operations (besides creating an iterator) take
 * constant amortized time (any sequence of M randomized queue
 * operations should take at most cM steps on the worst case,
 * for some constant c).
 *
 * This only could be provided by resizing array implementation,
 * because dequeue and sample operations require linear time.
 */
public class RandomizedQueue<Item> implements Iterable<Item>  {

    private Item[] a; // inserted elements stored as resizing array
    private int size; // number of inserted elements

    // constract an empty randomized queue
    public RandomizedQueue()
    {
        a = (Item[]) new Object[2];
        size = 0;
    }

    // is the queue empty?
    public boolean isEmpty()
    {
        return size == 0;
    }

    // return the number of item on the deque
    public int size()
    {
        return size;
    }

    // resize the underlying array holding the elements
    private void resize(int capacity)
    {
        assert capacity >= size;
        Item[] temp = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++)
            temp[i] = a[i];
        a = temp;
    }

    // add the item
    public void enqueue(Item item)
    {
        if (item == null) throw new IllegalArgumentException();
        if (size == a.length) resize(a.length * 2); // double size of array if necessary
        a[size++] = item; // add item
    }

    // remove and return a random item
    public Item dequeue()
    {
        if (isEmpty()) throw new NoSuchElementException();
        int randIndex = StdRandom.uniform(size);
        Item temp = a[randIndex];
        if (randIndex != size - 1)
            a[randIndex] = a[size - 1];
        a[--size] = null; // avoid loitering
        if (size > 0 && 4 * size == a.length) resize(a.length / 2);
        return temp;
    }

    // return (but don`t remove) a random item
    public Item sample()
    {
        if (size == 0) throw new NoSuchElementException();
        int index = StdRandom.uniform(size);
        return a[index];
    }

    // return an iterator over items in order from front to end
    public Iterator<Item> iterator()
    {
        return new ReverseArrayIterator();
    }

    private class ReverseArrayIterator implements Iterator<Item>
    {
        private final Item[] r;
        private int index = 0;

        public ReverseArrayIterator() {
            r = (Item[]) new Object[size];
            for (int i = 0; i < size; i++)
                r[i] = a[i];
            StdRandom.shuffle(r);
        }
        public boolean hasNext() {
            return index < size;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = r[index++];
            return item;
        }
    }

    public static void main(String[] args) {
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();
        StdOut.println(rq.size());
        rq.enqueue(2);
        StdOut.println(rq.size());
        rq.dequeue();
        StdOut.println(rq.size());
        rq.enqueue(30);
        StdOut.println(rq.size());
        rq.enqueue(9);
        StdOut.println(rq.size());
        rq.dequeue();
        StdOut.println(rq.size());
        rq.dequeue();
        StdOut.println(rq.size());
    }
}
