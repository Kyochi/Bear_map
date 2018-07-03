import quadtree.Node;
import quadtree.Point;
import quadtree.QuadTree;

import java.util.*;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    public final static int MAXDEPTH = 7;
    private QuadTree quadTree;
    public Rasterer(String imgRoot) {
        quadTree = new QuadTree(Rasterer.MAXDEPTH, MapServer.ROOT_ULLON, MapServer.ROOT_ULLAT, MapServer.ROOT_LRLON, MapServer.ROOT_LRLAT);
    }

    private boolean rastering( Map<Double, List<Node>> sortedByLat, Point queryUlPoint, Point queryLrPoint, double userWidth) {
        Node root = this.quadTree.getRoot();
        Queue<Node> bfsQueue = new LinkedList<>();
        List<Node> nodeMatched = new ArrayList<>();
        boolean isQueryValid = QuadTree.rectanglesOverLap(queryUlPoint, root.getuLCoordinates(),
                queryLrPoint, root.getlRCoordinates());
        double queryLonDPP = QuadTree.computelonDPP(queryLrPoint.longitude, queryUlPoint.longitude, userWidth);
        if (isQueryValid) bfsQueue.offer(root);
        while (!bfsQueue.isEmpty()) {
            Node cur = bfsQueue.poll();
            if (cur == null) continue;

            boolean overlap = QuadTree.rectanglesOverLap(queryUlPoint, cur.getuLCoordinates(),
                    queryLrPoint, cur.getlRCoordinates());
            if (overlap && (cur.getLonDPP() <= queryLonDPP || cur.getDepth()==MAXDEPTH)) {
                if (!sortedByLat.containsKey(cur.getuLCoordinates().latitude)) {
                    List<Node> nodeList = new ArrayList<>();
                    nodeList.add(cur);
                    sortedByLat.put(cur.getuLCoordinates().latitude,nodeList);
                }
                else{
                    List<Node> nodeList = sortedByLat.get(cur.getuLCoordinates().latitude);
                    nodeList.add(cur);
                    sortedByLat.put(cur.getuLCoordinates().latitude, nodeList);
                }
                continue;
            }
            if (overlap) {
                for (int i = 0; i < QuadTree.QTCHILDSNODECONSTANT; i++) {
                    bfsQueue.offer(cur.getChilds()[i]);
                }
            }

        }
        if (sortedByLat.isEmpty()) return false;

        return true;
    }



    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     *                    Can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     *                    forget to set this to true! <br>
     * @see #
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        System.out.println(params);
        Map<Double, List<Node>> sortedByLat = new TreeMap<>(Comparator.reverseOrder());
        List<List<String>> mapLists = new ArrayList<>();
        boolean success = this.rastering(sortedByLat, new Point(params.get("ullon"), params.get("ullat")),
                new Point(params.get("lrlon"), params.get("lrlat")),
                params.get("w"));

        Map<String, Object> results = new HashMap<>();
        Point rasterUl = null;
        Point rasterLr = null;
        String[][] mapToSend = null;
        if (success) {
            // Should be more efficient
            for (Map.Entry<Double, List<Node>> latitudeNode : sortedByLat.entrySet()) {
                if (rasterLr == null) rasterUl = latitudeNode.getValue().get(0).getuLCoordinates();
                List<String> line = new ArrayList<>();
                for (Node node : latitudeNode.getValue()) {
                    line.add(node.getImgNumb());
                    rasterLr = node.getlRCoordinates();
                }
                mapLists.add(line);
            }
            mapToSend = new String[mapLists.size()][mapLists.get(0).size()];
            for (int i = 0; i != mapToSend.length; i++) {
                for (int j = 0; j != mapToSend[i].length; j++) {
                    mapToSend[i][j] = "img/" + mapLists.get(i).get(j) + ".png";
                }
            }
        }
        if (rasterLr == null || rasterUl == null) {
            results.put("raster_ul_lon", "");
            results.put("raster_ul_lat", "");
            results.put("raster_lr_lon", "");
            results.put("raster_lr_lat", "");
            results.put("depth", "");
        }
        else {
            results.put("render_grid", mapToSend);
            results.put("raster_ul_lon", rasterUl.longitude);
            results.put("raster_ul_lat", rasterUl.latitude);
            results.put("raster_lr_lon", rasterLr.longitude);
            results.put("raster_lr_lat", rasterLr.latitude);
            results.put("depth", mapLists.get(0).get(0).length());
        }
        results.put("query_success", success);
        return results;
    }

}
