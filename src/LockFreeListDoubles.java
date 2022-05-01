// Nicholas Tran
// From "Multiprocessor Synchronization and Concurrent Data Structures",
// by Maurice Herlihy and Nir Shavit.
// Copyright 2021 Elsevier Inc. All rights reserved.

import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeListDoubles<T> {
    Node head;

    public LockFreeListDoubles() {
        this.head = new Node(Double.MIN_VALUE);
        Node tail = new Node(Double.MAX_VALUE);
        while (!head.next.compareAndSet(null, tail, false, false)) ;
    }

    public boolean add(T item) {
        double key = (double) item;
        while (true) {
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;
            if (curr.key == key) {
                return false;
            } else {
                Node node = new Node(item);
                node.next = new AtomicMarkableReference(curr, false);
                if (pred.next.compareAndSet(curr, node, false, false)) {
                    return true;
                }
            }
        }
    }

    public boolean remove(T item) {
        double key = (double) item;
        boolean snip;
        while (true) {
            // find predecessor and current entries
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;
            // is the key present?
            if (curr.key != key) {
                return false;
            } else {
                // snip out matching node
                Node succ = curr.next.getReference();
                snip = curr.next.attemptMark(succ, true);
                if (!snip) continue;
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    public boolean contains(T item) {
        double key = (double) item;
        Node curr = head;
        while (curr.key < key) {
            curr = curr.next.getReference();
        }
        return (curr.key == key && !curr.next.isMarked());
    }

    public Window find(Node head, double key) {
        Node pred = null, curr = null, succ = null;
        boolean[] marked = {false}; // is curr marked?
        boolean snip;
        retry:
        while (true) {
            pred = head;
            curr = pred.next.getReference();
            while (true) {
                succ = curr.next.get(marked);
                while (marked[0]) {           // replace curr if marked
                    snip = pred.next.compareAndSet(curr, succ, false, false);
                    if (!snip) continue retry;
                    curr = pred.next.getReference();
                    succ = curr.next.get(marked);
                }
                if (curr.key >= key) return new Window(pred, curr);
                pred = curr;
                curr = succ;
            }
        }
    }

    public double[] fiveLowest() {
        double[] lows = new double[5];
        Node curr = head.next.getReference();

        for (int i = 0; i < 5; i++) {
            lows[i] = (double) curr.item;
            curr = curr.next.getReference();
        }

        return lows;
    }


    public double[] fiveHigest(int len) {
        double[] highs = new double[5];
        Node curr = head.next.getReference();

        while (curr.next.getReference().next.getReference().next.getReference().next.getReference().next.getReference().key != Double.MAX_VALUE) {
            curr = curr.next.getReference();
        }

        for (int i = 0; i < 5; i++) {
            highs[i] = (double) curr.item;
            curr = curr.next.getReference();
        }

        return highs;
    }

    public class Node {
        T item;
        double key;
        AtomicMarkableReference<Node> next;

        Node(T item) {
            this.item = item;
            this.key = (Double) item;
            this.next = new AtomicMarkableReference<Node>(null, false);
        }

        Node(Double key) {
            this.item = null;
            this.key = key;
            this.next = new AtomicMarkableReference<Node>(null, false);
        }
    }

    class Window {
        public Node pred, curr;

        Window(Node myPred, Node myCurr) {
            pred = myPred;
            curr = myCurr;
        }
    }
}