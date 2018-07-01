package quadtree;

public class Node {
    private static double PIXELS = 256.0;
    private Point uLCoordinates;
    private Point lRCoordinates;
    private int depth;
    private String imgNumb;

    private double lonDPP;

    private Node[] childs;

    public Node(Point uLCoord, Point lRCoord, String imgNumb) {
        this.uLCoordinates = uLCoord;
        this.lRCoordinates = lRCoord;
        this.lonDPP = Node.PIXELS;
        this.imgNumb = imgNumb;
        lonDPP = QuadTree.computelonDPP(lRCoordinates.longitude, uLCoordinates.longitude, Node.PIXELS);
        this.depth = this.imgNumb.length();
        this.childs = new Node[4];
    }

    public double getLonDPP() {
        return lonDPP;
    }

    public Node[] getChilds() {
        return childs;
    }

    public Point getuLCoordinates() {
        return uLCoordinates;
    }

    public void setuLCoordinates(Point uLCoordinates) {
        this.uLCoordinates = uLCoordinates;
    }

    public Point getlRCoordinates() {
        return lRCoordinates;
    }

    public void setlRCoordinates(Point lRCoordinates) {
        this.lRCoordinates = lRCoordinates;
    }

    public int getDepth() {

        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getImgNumb() {
        return imgNumb;
    }

    public void setImgNumb(String imgNumb) {
        this.imgNumb = imgNumb;
    }
}
