import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdOut;

/**
 * All deque operations must be supported in constant worst-case time.
 * A deque containing N items must use at most 48N + 192 bytes of memory
 * and use space proportional to the number of items currently in the deque.
 * Additionally, your iterator implementation must support each operation
 * (including construction) in constant worst-case time.
 *
 * This only could be provided by resizing array implementation,
 * because dequeue and sample operations require linear time.
 */
public class Deque<Item> implements Iterable<Item> {

    private Node first, last;
    private int size;

    private class Node
    {
        private Item item;
        private Node next;
        private Node prev;
    }

    // construct an empty deque
    public Deque()
    {
        first = null;
        last = first;
        size = 0;
    }

    // is the deque empty?
    public boolean isEmpty()
    {
        return size == 0;
    }

    // return the number of item on the deque
    public int size()
    {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item)
    {
        if (item == null) throw new IllegalArgumentException();
        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;
        first.prev = null;
        size++;
        if (last != null) oldFirst.prev = first;
        else              last = first;
    }

    // add the item to the end
    public void addLast(Item item)
    {
        if (item == null) throw new IllegalArgumentException();
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        last.prev = oldLast;
        size++;
        if (first != null) oldLast.next = last;
        else               first = last;
    }

    // remove and return the item from the front
    public Item removeFirst()
    {
        if (size == 0) throw new NoSuchElementException();
        Item temp = first.item;
        first = first.next;
        size--;
        if (size == 0) last = null;
        else           first.prev = null;
        return temp;
    }

    // remove and return the item from the end
    public Item removeLast()
    {
        if (size == 0) throw new NoSuchElementException();
        Item temp = last.item;
        last = last.prev;
        size--;
        if (size == 0) first = null;
        else           last.next = null;
        return temp;
    }

    private class DequeIterator implements Iterator<Item>
    {
        private Node current = first;
        public boolean hasNext() { return current != null; }
        public void remove()     { throw new UnsupportedOperationException(); }
        public Item next()
        {
            if (current == null) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    // return an iterator over items in order from front to end
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    public static void main(String[] args)
    {
        Deque<String> deque = new Deque<String>();
        deque.addFirst("aa");
        deque.addFirst("bb");
        deque.addFirst("cc");
        deque.addLast("dd");
        StdOut.println(deque.removeFirst());
        StdOut.println(deque.removeFirst());
        StdOut.println(deque.removeFirst());
        StdOut.println(deque.removeLast());
        StdOut.print("size:" + deque.size());
    }
}
