import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.io.FileInputStream;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 *
 */
public class GraphProcessor {
    private HashMap<Point, List<Point>> vertices;

    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */
    public void initialize(FileInputStream file) throws Exception {
      vertices = new HashMap<>();
      Scanner reader = new Scanner(file);
      int numVertices = reader.nextInt();
      int numEdges = reader.nextInt();
      ArrayList<Point> pts = new ArrayList<>();

      reader.nextLine();

      // Read next "numVertices" lines
      for (int i = 0; i < numVertices; i++) {
        String nextLine = reader.nextLine();
        String[] elements = nextLine.split(" ");
        double latitude = Double.parseDouble(elements[1]);
        double longtidue = Double.parseDouble(elements[2]);
        Point np = new Point(latitude, longtidue);

        vertices.putIfAbsent(np, new ArrayList<>());
        pts.add(np);
      }

      // Read next "numEdges" lines
      for (int i = 0; i < numEdges; i++) {
        String nextt = reader.nextLine();
        String[] arr = nextt.split(" ");
        int startIndex = Integer.parseInt(arr[0]);
        int endIndex = Integer.parseInt(arr[1]);

        Point startingPt = pts.get(startIndex);
        Point endingPt = pts.get(endIndex);

        vertices.get(startingPt).add(endingPt);
        vertices.get(endingPt).add(startingPt);
      }

      reader.close();
    }


    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        Point closest = null;
        double shortestDistance = 999999999;

        for (Point np : vertices.keySet()) {
          if (np.distance(p) < shortestDistance) {
            closest = np;
            shortestDistance = np.distance(p);
          }
        }

        return closest;
    }


    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points, 
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * @param start Beginning point. May or may not be in the graph.
     * @param end Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        double distance = 0.0;
        for (int i = 0; i<route.size()-1; i++) {
            distance += route.get(i).distance(route.get(i+1));
        }
        return distance;
        // TODO Implement routeDistance
    }
    

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        if (vertices.get(p1).contains(p2))
            return true;
        if (!vertices.keySet().contains(p1) || !vertices.keySet().contains(p2))
            return false;
        
        Set<Point> visited = new HashSet<>();
        Map<Point, Point> previous = new HashMap<>();
        Stack<Point> toExplore = new Stack();
        Point current = p1;
        toExplore.add(current);
        visited.add(current);

        while(!toExplore.isEmpty()) {
          current = toExplore.pop();
          for (Point neighbor : vertices.get(current)) {
            if (neighbor.equals(p2))
              return true;
            if (!visited.contains(neighbor)) {
              previous.put(neighbor, current);
              visited.add(neighbor);
              toExplore.push(neighbor);
            }
          }
        }

        return false;
    }

    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * @param start Beginning point.
     * @param end Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route, 
     * either because start is not connected to end or because start equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {
        if (!vertices.keySet().contains(start) || !vertices.keySet().contains(end) || start.equals(end) ||
            connected(start, end) == false) {
            throw new InvalidAlgorithmParameterException("No path between start and end");   
        }

        Map<Point, Double> distance = new HashMap<>();
        List<Point> path = new ArrayList<>();
        Map<Point, Point> previous = new HashMap<>();
        PriorityQueue<Point> toExplore = new PriorityQueue<>();
        Point curr = start;

        distance.put(curr, 0.0);
        toExplore.add(curr);

        while(!toExplore.isEmpty()) {
          curr = toExplore.remove();
          for (Point neighbor : vertices.get(curr)) {
            double weight = curr.distance(neighbor);
            if (!distance.containsKey(neighbor) || distance.get(neighbor) > distance.get(curr) + weight) {
              distance.put(neighbor, distance.get(curr) + weight);
              previous.put(neighbor, curr);
              toExplore.add(neighbor);
            }
          }
        }

        curr = end;

        while(!curr.equals(start)) {
          path.add(curr);
          curr = previous.get(curr);
        }

        path.add(start);
        Collections.reverse(path);
        
        return path;
    }
}