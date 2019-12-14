package a05;

import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Class that implements a symbol table using a standard RedBlackBST.
 * 
 * @author Hai Le + Jace Parsell
 *
 * @param <Value>
 */
public class PointST<Value>
{

	private RedBlackBST<Point2D, Value> rbBST;
	
	/**
	 * Constructs a new symbol table using a RedBlackBST.
	 */
	public PointST()
	{
		rbBST = new RedBlackBST<>();
	} // construct an empty symbol table of points

	/**
	 * Checks if the symbol table is empty.
	 * 
	 * @return true if empty false otherwise.
	 */
	public boolean isEmpty()
	{
		return rbBST.isEmpty();
	} // is the symbol table empty?

	/**
	 * Gets the number of key value pairs in the symbol table.
	 * @return the number of key value pairs present in the symbol table.
	 */
	public int size()
	{
		return rbBST.size();
	}

	/**
	 * Places the key value pair in the symbol table if it is not already present, otherwise associates a new value to the keu.
	 * 
	 * @param p
	 * @param val
	 * @throws NullPointerException if either argument is null.
	 */
	public void put(Point2D p, Value val)
	{
		if(p == null || val == null) 
		{
			throw new java.lang.NullPointerException("Passed argument is null.");
		}
		rbBST.put(p, val);
	} 

	/**
	 * Returns the value associated with <code>Point2D</code> p.
	 * 
	 * @throws NullPointerException if <code>Point2D</code> p is null.
	 * @param p
	 * @return the value associated with <code>Point2D</code> p.
	 */
	public Value get(Point2D p)
	{
		if(p == null) 
		{
			throw new java.lang.NullPointerException("Passed argument is null.");
		}
		return rbBST.get(p);
	}
	
	/**
	 * Checks to see if <code>Point2D</code> p exists in the symbol table.
	 * 
	 * @throws NullPointerException if <code>Point2D</code> p is null.
	 * @param p
	 * @return true if <code>Point2D</code> p exists, false otherwise.
	 */
	public boolean contains(Point2D p)
	{
		if(p == null) 
		{
			throw new java.lang.NullPointerException("Passed argument is null.");
		}
		return rbBST.contains(p);
	}

	/**
	 * Returns all <code>Point2D</code> points in the symbol table.
	 * 
	 * @return all <code>Point2D</code> points in the symbol table.
	 */
	public Iterable<Point2D> points()
	{
		return rbBST.keys();
	}

	/**
	 * Returns all <code>Point2D</code> points that intersect <code>RectHV</code> rect in the symbol table.
	 * 
	 * @throws NullPointerException if <code>RectHV</code> rect is null.
	 * @return all <code>Point2D</code> points that intersect <code>RectHV</code> rect in the symbol table.
	 */
	public Iterable<Point2D> range(RectHV rect)
	{
		if(rect == null) 
		{
			throw new java.lang.NullPointerException("Passed argument is null.");
		}
		List<Point2D> range = new ArrayList<>();
        for (Point2D point : rbBST.keys()) {
            if (rect.contains(point)) {
                range.add(point);
            }
        }
        return range;
	}

	/**
	 * Returns the <code>Point2D</code> point closest to <code>Point2D</code> p in the symbol table.
	 * 
	 * @throws NullPointerException if <code>Point2D</code> p is null.
	 * @return the <code>Point2D</code> point closest to <code>Point2D</code> p in the symbol table.
	 */
	public Point2D nearest(Point2D p)
	{
		if(p == null) 
		{
			throw new java.lang.NullPointerException("Passed argument is null.");
		}
		
		if(rbBST.isEmpty()) return null;
		
		Point2D champion = new Point2D(0,0);
		for(Point2D k: rbBST.keys()) 
		{
			if(champion == null) champion = k;
			if(k.distanceSquaredTo(p) < champion.distanceSquaredTo(p)) 
			{
				champion = k;
			}
		}
		return champion;
	}

	public static void main(String[] args)
	{
		String filename = "src/res/input1M.txt";//args[0];
        In in = new In(filename);

        // initialize the two data structures with point from standard input
        PointST<Integer> brute = new PointST<Integer>();
        KdTreeST<Integer> kdtree = new KdTreeST<Integer>();
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.put(p, i);
            brute.put(p, i);
        }

      
        int pSTQueries = 0; 
        int kdTQueries = 0;
       
        Stopwatch pSTStart = new Stopwatch();
        while (pSTStart.elapsedTime() < 60) {
            double x = StdRandom.uniform(0,1);
            double y = StdRandom.uniform(0,1);
            Point2D query = new Point2D(x, y);

            brute.nearest(query);
            pSTQueries++;
        
        }
        double pSTTime = pSTStart.elapsedTime();
        Stopwatch kdTStart = new Stopwatch();
        while (kdTStart.elapsedTime() < 60) {
            double x = StdRandom.uniform(0,1);
            double y = StdRandom.uniform(0,1);
            Point2D query = new Point2D(x, y);
            
            kdtree.nearest(query);
            kdTQueries++;
        }
        double kdTTime = pSTStart.elapsedTime();
        
        StdOut.println("PointST queries per sec: " + (pSTQueries/pSTTime));
        StdOut.println("KdTreeST queries per sec: " + (kdTQueries/kdTTime));
	}
	
	
	
	
	
}