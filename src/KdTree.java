import java.util.ArrayList;
import java.util.List;
import algs4.Point2D;
import algs4.RectHV;

public class KdTree {
    private Node root;
    private int size;

    private static class Node {
        private Point2D p;
        private RectHV rect;
        private Node lb;
        private Node rt;
        private boolean isVertical;

        public Node(Point2D p, RectHV rect, boolean isVertical) {
            this.p = p;
            this.rect = rect;
            this.isVertical = isVertical;
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
        if (p == null) throw new IllegalArgumentException("calls insert() with a null key");
        if (contains(p)) return;
        root = insert(root, p, true, 0, 0, 1, 1);
    }

    private Node insert(Node x, Point2D p, boolean isVertical, double xmin, double ymin, double xmax, double ymax) {
        if (x == null) {
            size++;
            return new Node(p, new RectHV(xmin, ymin, xmax, ymax), isVertical);
        }

        if (x.p.equals(p)) return x;

        if (isVertical) {
            if (p.x() < x.p.x()) {
                x.lb = insert(x.lb, p, !isVertical, x.rect.xmin(), x.rect.ymin(), x.p.x(), x.rect.ymax());
            } else {
                x.rt = insert(x.rt, p, !isVertical, x.p.x(), x.rect.ymin(), x.rect.xmax(), x.rect.ymax());
            }
        } else {
            if (p.y() < x.p.y()) {
                x.lb = insert(x.lb, p, !isVertical, x.rect.xmin(), x.rect.ymin(), x.rect.xmax(), x.p.y());
            } else {
                x.rt = insert(x.rt, p, !isVertical, x.rect.xmin(), x.p.y(), x.rect.xmax(), x.rect.ymax());
            }
        }
        return x;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument to contains() is null");
        return contains(root, p, true);
    }

    private boolean contains(Node x, Point2D p, boolean isVertical) {
        if (x == null) return false;
        if (x.p.equals(p)) return true;

        if (isVertical) {
            if (p.x() < x.p.x()) return contains(x.lb, p, !isVertical);
            else return contains(x.rt, p, !isVertical);
        } else {
            if (p.y() < x.p.y()) return contains(x.lb, p, !isVertical);
            else return contains(x.rt, p, !isVertical);
        }
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument to nearest() is null");
        if (isEmpty()) return null;
        return nearest(root, p, root.p);
    }

    private Point2D nearest(Node x, Point2D p, Point2D champion) {
        if (x == null) return champion;

        double distToChampion = champion.distanceSquaredTo(p);
        if (x.rect.distanceSquaredTo(p) >= distToChampion) return champion;

        if (x.p.distanceSquaredTo(p) < distToChampion) {
            champion = x.p;
        }

        if (x.isVertical) {
            if (p.x() < x.p.x()) {
                champion = nearest(x.lb, p, champion);
                champion = nearest(x.rt, p, champion);
            } else {
                champion = nearest(x.rt, p, champion);
                champion = nearest(x.lb, p, champion);
            }
        } else {
            if (p.y() < x.p.y()) {
                champion = nearest(x.lb, p, champion);
                champion = nearest(x.rt, p, champion);
            } else {
                champion = nearest(x.rt, p, champion);
                champion = nearest(x.lb, p, champion);
            }
        }
        return champion;
    }
}