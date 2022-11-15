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
 * Testing GraphProcessor on the family of US data
 * Precursor to autograder, which tests on US data
 * 
 * @author Emily Du
 * @author Havish Malladi
 */

public class TestUSGraphProcessor {
	GraphProcessor usDriver = new GraphProcessor();
	Map<String, Point> usCityLookup;
	String usGraphFile = "/Users/emilydu/Documents/201 Fall 22/p6-routing/data/usa.graph";
	String usCities = "/Users/emilydu/Documents/201 Fall 22/p6-routing/data/uscities.csv";

	/**
     * Tests that driver returns closest point in graph to a given query point
     */
	@BeforeEach
    public void setup() throws Exception {
		usDriver.initialize(usGraphFile);
		usCityLookup = readCities(usCities);
    }

	/**
     * Tests that driver returns a List<Point> corresponding to the shortest path from start to end
     * Accepts alternate paths that are ultimately within 5% of the distance of the true shortest path
     * @throws InvalidAlgorithmParameterException
     */
	@Test
	public void testNearestPoint() {
		String[] froms = new String[]{"Durham NC", "Seattle WA"};
		String[] dests = new String[]{"China TX", "Redmond WA"};

		// includes all possible nearest points within 5% of the closest distance

		List<Point[]> pLookedUpRanges = new ArrayList();
		pLookedUpRanges.add(new Point[] {new Point(35.989709, -78.902124)});
		pLookedUpRanges.add(new Point[] {new Point(47.625719, -122.328043)});
		List<Point[]> qLookedUpRanges = new ArrayList();
		qLookedUpRanges.add(new Point[] {new Point(30.047564, -94.33527)});
		qLookedUpRanges.add(new Point[] {new Point(47.679175, -122.184502), new Point(47.669571, -122.186937)}); 
		

		for (int i = 0; i < froms.length; i++) {
			Point p = usCityLookup.get(froms[i]);
			Point q = usCityLookup.get(dests[i]);
			Point nearP = usDriver.nearestPoint(p);
			Point nearQ = usDriver.nearestPoint(q);
			
			assertTrue(pointInRange(nearP, pLookedUpRanges.get(i)), "Your nearest point isn't a valid point whose (distance from the input) is within 5% of the (distance from the input and the true nearest point)!");
			assertTrue(pointInRange(nearQ, qLookedUpRanges.get(i)), "Your nearest point isn't a valid point whose (distance from the input) is within 5% of the (distance from the input and the true nearest point)!");

		}
	}

	/**
     * Tests that driver returns the distance along a given route represented as a List<Point> input
     * Tests only if .routeDistsance() is correct (i.e. can pass even if .route() is incorect)
     */
	@Test public void testRoute() throws InvalidAlgorithmParameterException {
		// Bellevue WA to Clyde Hill WA
		List<Point> route = usDriver.route(new Point(47.578813, -122.139773), new Point(47.632292, -122.187898));
		List<Point> trueRoute = Arrays.asList(new Point(47.578813, -122.139773), new Point(47.580228, -122.174438), new Point(47.603103, -122.184024), new Point(47.61349, -122.188708), new Point(47.617298, -122.188659), new Point(47.632292, -122.187898));
		
		assertFalse(route == null, "Your route is null!");
		double trueRouteDist = 5.32;
		checkPaths(route, trueRoute, usDriver.routeDistance(route), trueRouteDist);

		assertThrows(InvalidAlgorithmParameterException.class, ()->usDriver.route(new Point(18.399426, -66.071025), new Point(47.625719, -122.328043)));
	}

	/**
     * Tests that driver returns the distance along a given route represented as a List<Point> input
     * Tests only if .routeDistsance() is correct (i.e. can pass even if .route() is incorect)
     */
	@Test 
	public void testRouteDistance() {
		List<Point> route1 = Arrays.asList(new Point(35.9792, -78.9022),  new Point(35.890653, -78.750076), new Point(35.835315, -78.669448), new Point(35.834585, -78.638592), new Point(35.8324, -78.6429));
		List<Point> route2 = Arrays.asList(new Point(35.665571, -77.398172), new Point(35.629082, -77.433207));
		List<List<Point>> routes = Arrays.asList(route1, route2);
		double[] targetDists = new double[] {18.43, 3.20};

		for (int i = 0; i < routes.size(); i++) {
			double routeDist = usDriver.routeDistance(routes.get(i));
			assertTrue(inRange(routeDist, targetDists[i]), 
				"Your route distance is not within rounding error (+/- 0.05) of the actual route distance! This test is designed so that it passes if your .routeDistance() is correct, even if your .route() is incorrect");
		}
	}

	/**
     * Tests that driver returns true if two inputs are connected in the graph
     */
	@Test
	public void testConnected() {
		// San Juan PR to Seattle WA
		assertFalse(usDriver.connected(new Point(18.399426, -66.071025), new Point(47.625719, -122.328043)), 
			"You mistakenly claim two points representing San Juan PR and Seattle WA's nearest points, respectively, are connected. This test is designed if .connected() is correct, even if .nearestPoint() is faulty"); 
		// Durham NC to Raleigh NC
		assertTrue(usDriver.connected(new Point(35.989709, -78.902124), new Point(35.834585, -78.638592)),
		   "You mistakenly claim two points representing Durham NC and Raleigh NC's nearest points, respectively, are not connected. This test is designed if .connected() is correct, even if .nearestPoint() is faulty"); 
	}
 
	// helper method to check if a point's distance to input is within 5% of the true nearest point's distance to input
	private boolean inRange(double res, double target) {
		return (res > target - 0.05 && res < target + 0.05);
	}

	// helper method to check if a route is the true shortest path, or within 5% of the overall distance of the true shortest path
	private static boolean pointInRange(Point result, Point[] ranged) {
		for (Point p : ranged) {
			if (p.equals(result)) return true;
		}

		return false;
	}

	// checks if a range of points contains a certain result point
	private static boolean checkPaths(List<Point> r1, List<Point> r2, double resPathDist, double truePathDist) {
        if (r1.size() == r2.size()) {
            for (int i = 0; i < r1.size(); i++) {
                if (!(r1.get(i).equals(r2.get(i)))) return false;
            }
        } else {
            if (!(resPathDist > 0.95 * truePathDist && resPathDist < 1.05 * truePathDist)) return false;
			if (!(r1.get(0).equals(r2.get(0)) && r1.get(r1.size() - 1).equals(r2.get(r2.size() - 1)))) return false;
        }
        return true;
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