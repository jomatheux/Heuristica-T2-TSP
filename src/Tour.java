import algs4.Point2D;
import algs4.StdDraw;
import algs4.StdOut;

public class Tour {

    private static class Node {
        private Point point;
        private Node next;
    }

    private Node start;
    private int count;
    private final boolean useKdTree;
    private KdTree kdTree;

    public Tour() {
        this(false);
    }

    public Tour(boolean useKdTree) {
        this.useKdTree = useKdTree;
        this.start = null;
        this.count = 0;
        if (useKdTree) {
            this.kdTree = new KdTree();
        }
    }

    public Tour(Point a, Point b, Point c, Point d) {
        this();
        insertNearestNaive(a);
        insertNearestNaive(b);
        insertNearestNaive(c);
        insertNearestNaive(d);
    }

    public int size() {
        return count;
    }

    public double length() {
        if (start == null || start.next == start)
            return 0.0;

        double total = 0.0;
        Node current = start;
        do {
            total += current.point.distanceTo(current.next.point);
            current = current.next;
        } while (current != start);
        return total;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (start == null)
            return "(Tour vazio)";

        Node current = start;
        do {
            sb.append(current.point.toString()).append("\n");
            current = current.next;
        } while (current != start);

        return sb.toString();
    }

    public void draw() {
        if (start == null || start.next == start)
            return;

        Node current = start;
        do {
            current.point.drawTo(current.next.point);
            current = current.next;
        } while (current != start);
    }

    public void insertNearest(Point p) {
        if (useKdTree) {
            insertNearestKd(p); // ainda não implementado
        } else {
            insertNearestNaive(p);
        }
    }

    public void insertNearestNaive(Point p) {
        // Se o tour estiver vazio, cria o primeiro nó que aponta para si mesmo.
        if (start == null) {
            start = new Node();
            start.point = p;
            start.next = start;
            count = 1;
            return;
        }

        Node bestPrev = null;
        double minIncrease = Double.POSITIVE_INFINITY;

        Node current = start;
        // Percorre o tour para encontrar o melhor local para inserir o novo ponto.
        do {
            // Calcula o aumento na distância total se 'p' for inserido aqui.
            double oldDist = current.point.distanceTo(current.next.point);
            double newDist = current.point.distanceTo(p) + p.distanceTo(current.next.point);
            double increase = newDist - oldDist;

            // Se este for o menor aumento até agora, salva este local.
            if (increase < minIncrease) {
                minIncrease = increase;
                bestPrev = current;
            }
            current = current.next;
        } while (current != start);

        // Insere o novo nó na melhor posição encontrada.
        Node newNode = new Node();
        newNode.point = p;
        newNode.next = bestPrev.next;
        bestPrev.next = newNode;
        count++;
    }

    public void insertNearestKd(Point p) {
        Point2D p2d = new Point2D(p.x(), p.y());

        // Caso base: se o tour está vazio, apenas insere.
        if (start == null) {
            start = new Node();
            start.point = p;
            start.next = start;
            count = 1;
            kdTree.insert(p2d); // Adiciona o primeiro ponto à árvore
            return;
        }

        // 1. PRIMEIRO, encontra o ponto mais próximo que JÁ ESTÁ no tour.
        Point2D nearestP2D = kdTree.nearest(p2d);
        Point nearestPoint = new Point(nearestP2D.x(), nearestP2D.y());

        // 2. Encontra o nó do ponto mais próximo (nearestNode) e seu predecessor
        // (prevNode).
        Node prevNode = start;
        while (!prevNode.next.point.equals(nearestPoint)) {
            prevNode = prevNode.next;
        }
        Node nearestNode = prevNode.next;

        // 3. Calcula o custo de inserir 'p' ANTES vs DEPOIS do ponto mais próximo.
        double costBefore = prevNode.point.distanceTo(p) + p.distanceTo(nearestNode.point)
                - prevNode.point.distanceTo(nearestNode.point);
        double costAfter = nearestNode.point.distanceTo(p) + p.distanceTo(nearestNode.next.point)
                - nearestNode.point.distanceTo(nearestNode.next.point);

        // 4. AGORA, insere o novo nó na lista encadeada na posição de menor custo.
        Node newNode = new Node();
        newNode.point = p;
        if (costBefore < costAfter) {
            newNode.next = nearestNode;
            prevNode.next = newNode;
        } else {
            newNode.next = nearestNode.next;
            nearestNode.next = newNode;
        }
        count++;

        // 5. POR FIM, adiciona o novo ponto na Kd-Tree para futuras buscas.
        kdTree.insert(p2d);
    }

    // Método de teste (opcional)
    public static void main(String[] args) {
        Tour tour = new Tour();
        tour.insertNearest(new Point(1.0, 1.0));
        tour.insertNearest(new Point(1.0, 4.0));
        tour.insertNearest(new Point(4.0, 4.0));
        tour.insertNearest(new Point(4.0, 1.0));

        StdOut.println("# de pontos = " + tour.size());
        StdOut.println("Comprimento = " + tour.length());
        StdOut.println(tour);

        StdDraw.setXscale(0, 6);
        StdDraw.setYscale(0, 6);
        tour.draw();
    }
}
