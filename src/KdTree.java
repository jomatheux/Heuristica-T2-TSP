import algs4.*;

public class KdTree {
    private static final boolean VERTICAL = true;
    private Node root;
    private int size;

    private static class Node {
        private final Point2D p;
        private final boolean orientation;
        private Node lb;
        private Node rt;

        public Node(Point2D p, boolean orientation) {
            this.p = p;
            this.orientation = orientation;
        }
    }

    public KdTree() {
        this.root = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("ponto nulo");
        root = insert(root, p, VERTICAL);
    }

    private Node insert(Node node, Point2D p, boolean orientation) {
        if (node == null) {
            size++;
            return new Node(p, orientation);
        }
        if (node.p.equals(p)) return node;

        double cmp = (orientation == VERTICAL) ? (p.x() - node.p.x()) : (p.y() - node.p.y());

        if (cmp < 0) {
            node.lb = insert(node.lb, p, !orientation);
        } else {
            node.rt = insert(node.rt, p, !orientation);
        }
        return node;
    }
    
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("ponto nulo");
        if (isEmpty()) return null;
        return nearest(root, p, root.p);
    }

    private Point2D nearest(Node node, Point2D queryPoint, Point2D champion) {
        if (node == null) return champion;

        if (queryPoint.distanceSquaredTo(node.p) < queryPoint.distanceSquaredTo(champion)) {
            champion = champion.distanceSquaredTo(queryPoint) < node.p.distanceSquaredTo(queryPoint) ? champion : node.p;
        }

        double cmp = (node.orientation == VERTICAL) ? (queryPoint.x() - node.p.x()) : (queryPoint.y() - node.p.y());
        Node first = (cmp < 0) ? node.lb : node.rt;
        Node second = (cmp < 0) ? node.rt : node.lb;

        champion = nearest(first, queryPoint, champion);

        double distToPartition = (node.orientation == VERTICAL)
            ? (node.p.x() - queryPoint.x()) * (node.p.x() - queryPoint.x())
            : (node.p.y() - queryPoint.y()) * (node.p.y() - queryPoint.y());

        if (distToPartition < queryPoint.distanceSquaredTo(champion)) {
            champion = nearest(second, queryPoint, champion);
        }
        return champion;
    }
}