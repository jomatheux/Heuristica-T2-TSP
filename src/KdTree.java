import algs4.Point2D;
import algs4.RectHV;

public class KdTree {

    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    private Node root;
    private int size;

    private static class Node {
        private final Point2D p;
        private final RectHV rect;
        private Node lb;
        private Node rt;
        private final boolean orientation;

        Node(Point2D p, RectHV rect, boolean orientation) {
            this.p = p;
            this.rect = rect;
            this.orientation = orientation;
        }
    }

    public KdTree() {
        root = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("ponto nulo");
        if (root == null) {
            root = new Node(p, new RectHV(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY), VERTICAL);
            size = 1;
            return;
        }
        root = insert(root, p);
    }

    private Node insert(Node node, Point2D p) {
        if (node == null) return null;

        if (node.p.equals(p)) return node;

        if (node.orientation == VERTICAL) {
            if (p.x() < node.p.x()) {
                if (node.lb == null) {
                    RectHV r = new RectHV(node.rect.xmin(), node.rect.ymin(), node.p.x(), node.rect.ymax());
                    node.lb = new Node(p, r, HORIZONTAL);
                    size++;
                } else {
                    node.lb = insert(node.lb, p);
                }
            } else {
                if (node.rt == null) {
                    RectHV r = new RectHV(node.p.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
                    node.rt = new Node(p, r, HORIZONTAL);
                    size++;
                } else {
                    node.rt = insert(node.rt, p);
                }
            }
        } else { // horizontal split
            if (p.y() < node.p.y()) {
                if (node.lb == null) {
                    RectHV r = new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.p.y());
                    node.lb = new Node(p, r, VERTICAL);
                    size++;
                } else {
                    node.lb = insert(node.lb, p);
                }
            } else {
                if (node.rt == null) {
                    RectHV r = new RectHV(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.rect.ymax());
                    node.rt = new Node(p, r, VERTICAL);
                    size++;
                } else {
                    node.rt = insert(node.rt, p);
                }
            }
        }
        return node;
    }

    public Point2D nearest(Point2D query) {
        if (query == null) throw new IllegalArgumentException("ponto nulo");
        if (isEmpty()) return null;
        return nearest(root, query, root.p);
    }

    private Point2D nearest(Node node, Point2D query, Point2D champion) {
        if (node == null) return champion;

        double bestDist = query.distanceSquaredTo(champion);
        double nodeDist = query.distanceSquaredTo(node.p);
        if (nodeDist < bestDist) {
            champion = node.p;
            bestDist = nodeDist;
        }

        Node first = node.lb;
        Node second = node.rt;

        if (node.lb != null && node.rt != null) {
            double distLb = node.lb.rect.distanceSquaredTo(query);
            double distRt = node.rt.rect.distanceSquaredTo(query);
            if (distLb < distRt) {
                first = node.lb; second = node.rt;
            } else {
                first = node.rt; second = node.lb;
            }
        }

        if (first != null && first.rect.distanceSquaredTo(query) < bestDist) {
            champion = nearest(first, query, champion);
            bestDist = query.distanceSquaredTo(champion);
        }

        if (second != null && second.rect.distanceSquaredTo(query) < bestDist) {
            champion = nearest(second, query, champion);
        }

        return champion;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("ponto nulo");
        return contains(root, p);
    }

    private boolean contains(Node node, Point2D p) {
        if (node == null) return false;
        if (node.p.equals(p)) return true;
        if (node.orientation == VERTICAL) {
            if (p.x() < node.p.x()) return contains(node.lb, p);
            else return contains(node.rt, p);
        } else {
            if (p.y() < node.p.y()) return contains(node.lb, p);
            else return contains(node.rt, p);
        }
    }
}