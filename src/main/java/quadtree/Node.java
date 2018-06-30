package quadtree;

public class Node {
    public Point coordinates;
    public int depth;
    public String imgNumb;

    public Node(Point coordinates, String imgNumb) {
        this.coordinates = coordinates;
        this.imgNumb = imgNumb;
        this.depth = this.imgNumb.length();
    }
}
