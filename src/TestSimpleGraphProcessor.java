import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.security.InvalidAlgorithmParameterException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.*;

/**
 * Testing GraphProcessor on the family of simple data
 * Intended to help debug with simple tests
 * 
 * @author Emily Du
 * @author Havish Malladi
 */

public class TestSimpleGraphProcessor {
	GraphProcessor simpleDriver = new GraphProcessor();
	Map<String, Point> simpleCityLookup;
	String simpleGraphFile = "/Users/emilydu/Documents/201 Fall 22/p6-route/data/simple.graph";
	String simpleCities = "/Users/emilydu/Documents/201 Fall 22/p6-route/data/simplecities.csv";

    // Setup to initialize driver before tests
	@BeforeEach
    public void setup() throws Exception {
		simpleDriver.initialize(simpleGraphFile);
		simpleCityLookup = readCities(simpleCities);
    }

    /**
     * Tests that driver returns closest point in graph to a given query point
     */
	@Test
	public void testNearestPoint() {
		String[] froms = new String[]{"A A", "B B", "H H", "K K", "L L"};

		// includes all possible nearest points within 5% of the closest distance
        List<Point[]> pLookedUpRanges = new ArrayList();
		pLookedUpRanges.add(new Point[] {new Point(2, -1)});
        pLookedUpRanges.add(new Point[] {new Point(2, 0)});
        pLookedUpRanges.add(new Point[] {new Point(-1, -1)});
        pLookedUpRanges.add(new Point[] {new Point(2, -1), new Point(1, -1)});
        pLookedUpRanges.add(new Point[] {new Point(-1, -1), new Point(0, 0)});
		

		for (int i = 0; i < froms.length; i++) {
			Point p = simpleCityLookup.get(froms[i]);
			Point nearP = simpleDriver.nearestPoint(p);
            
			assertTrue(pointInRange(nearP, pLookedUpRanges.get(i)), "Your nearest point isn't a valid point whose (distance from the input) is within 5% of the (distance from the input and the true nearest point)!");
		}
	}

    /**
     * Tests that driver returns a List<Point> corresponding to the shortest path from start to end
     * Accepts alternate paths that are ultimately within 5% of the distance of the true shortest path
     * @throws InvalidAlgorithmParameterException
     */
	@Test public void testRoute() throws InvalidAlgorithmParameterException {
		// A to F
		List<Point> route = simpleDriver.route(new Point(2, -1), new Point(1, 1));
		List<Point> trueRoute1 = Arrays.asList(new Point(2, -1), new Point(2, 0), new Point(1, 1));
        assertFalse(route == null, "Your route is null!");
        double trueRouteDist = 166.93;
		assertTrue(checkPaths(route, trueRoute1, simpleDriver.routeDistance(route), trueRouteDist), "Your route was not close to the true shortest path between the start and ending points!");

        // D to J (1, -1) to (-1, 1)
        assertThrows(InvalidAlgorithmParameterException.class, ()->simpleDriver.route(new Point(1, -1), new Point(-1, 1)));
	
	}

    /**
     * Tests that driver returns the distance along a given route represented as a List<Point> input
     * Tests only if .routeDistsance() is correct (i.e. can pass even if .route() is incorect)
     */
	@Test 
	public void testRouteDistance() {
		List<Point> route1 = Arrays.asList(new Point(1, 0), new Point(1, 1), new Point(2, 1)); // E to C
        List<Point> route2 = Arrays.asList(new Point(2, 0), new Point(1, 0)); // B to E
        List<Point> route3 = Arrays.asList(new Point(0, 0), new Point(-1, 1));
		List<List<Point>> routes = Arrays.asList(route1, route2, route3);
		double[] targetDists = new double[] {138.331, 69.17, 97.82};

		for (int i = 0; i < routes.size(); i++) {
			double routeDist = simpleDriver.routeDistance(routes.get(i));
			assertTrue(inRange(routeDist, targetDists[i]),
				"Your route distance is not within rounding error (+/- 0.05) of the actual route distance! This test is designed so that it passes if your .routeDistance() is correct, even if your .route() is incorrect");
		}
	}

    /**
     * Tests that driver returns true if two inputs are connected in the graph
     */
	@Test
	public void testConnected() {
		// A to J
		assertFalse(simpleDriver.connected(new Point(2, -1), new Point(-1, 1)), 
			"You mistakenly claim two points representing San Juan PR and Seattle WA's nearest points, respectively, are connected. This test is designed if .connected() is correct, even if .nearestPoint() is faulty"); 
		// G to H
		assertTrue(simpleDriver.connected(new Point(0, 0), new Point(-1, -1)),
		   "You mistakenly claim two points representing Durham NC and Raleigh NC's nearest points, respectively, are not connected. This test is designed if .connected() is correct, even if .nearestPoint() is faulty"); 
	}
 
    // helper method to check if a point's distance to input is within 5% of the true nearest point's distance to input
	private static boolean inRange(double resPathDist, double truePathDist) {
		return (resPathDist > 0.95 * truePathDist && resPathDist < 1.05 * truePathDist);
	}

    // helper method to check if a route is the true shortest path, or within 5% of the overall distance of the true shortest path
    private static boolean checkPaths(List<Point> r1, List<Point> r2, double resPathDist, double truePathDist) {
        if (r1.size() == r2.size()) {
            for (int i = 0; i < r1.size(); i++) {
                if (!(r1.get(i).equals(r2.get(i)))) return false;
            }
        } else {
            if (!inRange(resPathDist, truePathDist)) return false;
			if (!(r1.get(0).equals(r2.get(0)) && r1.get(r1.size() - 1).equals(r2.get(r2.size() - 1)))) return false;
        }
        return true;
    }

    // checks if a range of points contains a certain result point
    private static boolean pointInRange(Point result, Point[] ranged) {
		for (Point p : ranged) {
			if (p.equals(result)) return true;
		}

		return false;
	}

    // reads cities from a file
	private static Map<String, Point> readCities(String fileName) throws FileNotFoundException {
		Scanner reader = new Scanner(new File(fileName));
		Map<String, Point> cityLookup = new HashMap<>(); 
		while (reader.hasNextLine()) {
			try {
				String[] info = reader.nextLine().split(",");
				cityLookup.put(info[0] + " " + info[1], 
				new Point(Double.parseDouble(info[2]),
				Double.parseDouble(info[3])));
			} catch(Exception e) {
				continue;    
			}
		}
		return cityLookup;
	}
}