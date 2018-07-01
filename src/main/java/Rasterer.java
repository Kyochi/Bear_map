import quadtree.QuadTree;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    public final static int MAXDEPTH = 3;
    private QuadTree quadTree;
    public Rasterer(String imgRoot) {
        quadTree = new QuadTree(Rasterer.MAXDEPTH, MapServer.ROOT_ULLON, MapServer.ROOT_ULLAT, MapServer.ROOT_LRLON, MapServer.ROOT_LRLAT);
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
        String[][] map = new String[2][2];
        map[0][0] = "img/"+quadTree.getRoot().getChilds()[0].getImgNumb()+".png";
        map[0][1] = "img/"+quadTree.getRoot().getChilds()[1].getImgNumb()+".png";
        map[1][0] = "img/"+quadTree.getRoot().getChilds()[2].getImgNumb()+".png";
        map[1][1] = "img/"+quadTree.getRoot().getChilds()[3].getImgNumb()+".png";
        Map<String, Object> results = new HashMap<>();
        results.put("render_grid", map);
        results.put("raster_ul_lon", MapServer.ROOT_ULLON);
        results.put("raster_ul_lat", MapServer.ROOT_ULLAT);
        results.put("raster_lr_lon", MapServer.ROOT_LRLON);
        results.put("raster_lr_lat", MapServer.ROOT_LRLAT);
        results.put("depth", 1);
        results.put("query_success", true);

        System.out.println("Since you haven't implemented getMapRaster, nothing is displayed in "
                           + "your browser.");
        return results;
    }

}
