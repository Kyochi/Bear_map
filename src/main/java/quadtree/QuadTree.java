package quadtree;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private final static int QTCHILDSNODECONSTANT = 4;

    private Node root;
    private int maxDepth;

    public QuadTree(int maxDepth, double ulLongRoot, double ulLatRoot, double lrLongRoot, double lrLatRoot) {
        this.maxDepth = maxDepth;
        root = new Node(new Point(ulLongRoot, ulLatRoot), new Point(lrLongRoot, lrLatRoot), "root");
        this.build(root, 0 , new StringBuilder(""));
    }

    private void build(Node current, int depth, StringBuilder num) {
        if (depth+1 > maxDepth) return;
        String img = num.toString();
        for (int i = 1 ; i <= QTCHILDSNODECONSTANT; i++) {
            StringBuilder imgBuilder = new StringBuilder(img);
            imgBuilder.append(i);
            List<Point> points = computeUlLrFromParentNode(current, i);
            Node child = new Node(points.get(0),points.get(1), imgBuilder.toString());
            current.getChilds()[i-1] = child;
            this.build(child, depth+1, imgBuilder);
        }
    }

    public Node getRoot() {
        return root;
    }

    public static double computelonDPP(double lrLong, double ulLong, int weight) {
        return (lrLong-ulLong)/weight;
    }

    public static List<Point> computeUlLrFromParentNode(Node parent, int quadChildNumb) {
        List<Point> points = new ArrayList<>();
        Point ul;
        Point lr;
        if (quadChildNumb == 1) {
            ul = new Point(parent.getuLCoordinates().longitude, parent.getuLCoordinates().latitude);
            lr = new Point((parent.getlRCoordinates().longitude + parent.getuLCoordinates().longitude)/2,
                    (parent.getuLCoordinates().latitude + parent.getlRCoordinates().latitude)/2);
        }
        else if (quadChildNumb == 2) {
            ul = new Point((parent.getlRCoordinates().longitude + parent.getuLCoordinates().longitude)/2,
                    parent.getuLCoordinates().latitude);
            lr = new Point(parent.getlRCoordinates().longitude, (parent.getuLCoordinates().latitude + parent.getlRCoordinates().latitude)/2);
        }
        else if (quadChildNumb == 3) {
            ul = new Point(parent.getuLCoordinates().longitude, (parent.getuLCoordinates().latitude + parent.getlRCoordinates().latitude)/2);
            lr = new Point((parent.getlRCoordinates().longitude + parent.getuLCoordinates().longitude)/2,
                    parent.getlRCoordinates().latitude);
        }
        else if (quadChildNumb == 4) {
            ul = new Point((parent.getlRCoordinates().longitude + parent.getuLCoordinates().longitude)/2,
                    (parent.getuLCoordinates().latitude + parent.getlRCoordinates().latitude)/2);
            lr = new Point(parent.getlRCoordinates().longitude, parent.getlRCoordinates().latitude);
        }
        else return points;
        points.add(ul);
        points.add(lr);
        return points;
    }
}
