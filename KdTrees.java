package a05;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.RectHV;

/**
 * Class that implements a symbol table with improvements to enable significantly faster functionality.
 * 
 * @author Hai Le + Jace Parsell
 * 
 * @param <Value>
 */
public class KdTreeST<Value>
{
	private double posInfinity = Double.POSITIVE_INFINITY;
	private double negInfinity = Double.NEGATIVE_INFINITY;
	private Node root, current;
	

	
	private int size;

	/**
	 *  Constructs an empty symbol table of points.
	 */
	public KdTreeST()
	{
		root = null;
		size = 0;
	}

	/**
	 * Checks if the symbol table is empty.
	 * 
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		return this.size() == 0;
	} 

	/**
	 * Gets the number of points in the symbol table.
	 * 
	 * @return the number of points in the symbol table.
	 */
	public int size()
	{
		return this.size;
	}
	
	/**
	 * If the key is not present associates it with the value, otherwise overwrites the value for the key.
	 * 
	 * @param p the <code>Point2D</code> key.
	 * @param val the <code>Value</code> value.
	 */
	public void put(Point2D p, Value val)
	{
		if (this.isEmpty())
		{ // If the symbol table is empty.
			root = new Node(p, val, true); // Initialize the root.
			root.rectangle = new RectHV(negInfinity, negInfinity, posInfinity, posInfinity);
			size = 1;
		} else 
		{ // Add the point
			if (!contains(p)) size++; // If we are not overwriting, increase size.
			current = find(p, root);
			
			if (!current.point.equals(p))
			{ // If the point does not already exist.
				Node child = new Node(p, val, true); // Create the new node.
				
				if (xyCompare(p, current.point, current.vertical) < 0)
				{ // If p is to the left of or below n.

					if (current.vertical)
					{ // If the line of the parent is vertical and the new point to the left of said line.
						// Restrict the rectangle of the child to be less than the x coordinate of the parent point.
						child.rectangle = new RectHV(
								current.rectangle.xmin(),	// Rectangle xmin.
								current.rectangle.ymin(), 	// Rectangle ymin.
								current.point.x(), 			// Rectangle xmax.
								current.rectangle.ymax());	// Rectangle ymax.
					} else
					{ // If the line of the parent is horizontal and the new point is below said line.
						// Assign the rectangle of the child node to be restricted to below the y of the parent point.
						child.rectangle = new RectHV(
								current.rectangle.xmin(), 	// Rectangle xmin.
								current.rectangle.ymin(), 	// Rectangle ymin.
								current.rectangle.xmax(), 	// Rectangle xmax.
								current.point.y());			// Rectangle ymax.
					}
					
					child.vertical = !current.vertical;
					current.leftChild = child;
					
				} else if (xyCompare(p, current.point, current.vertical) >= 0)
				{ // If p is to the right of, equal to, or above n.
					if (current.vertical)
					{ // If the line of the parent is vertical and the new point to the right of or equal to said line.
						// Restrict the rectangle of the child to be greater than the x coordinate of the parent point.
						child.rectangle = new RectHV(
								current.point.x(), 			// Rectangle xmin.
								current.rectangle.ymin(), 	// Rectangle ymin.
								current.rectangle.xmax(), 	// Rectangle xmax.
								current.rectangle.ymax()); 	// Rectangle ymax.
					} else
					{ // If the line of the parent is horizontal and the new point is above said line.
						//Assign the rectangle of the child node to be restricted to above the y of the parent point.
						child.rectangle = new RectHV(
								current.rectangle.xmin(), 	// Rectangle xmin.
								current.point.y(), 			// Rectangle ymin.
								current.rectangle.xmax(), 	// Rectangle xmax.
								current.rectangle.ymax());	// Rectangle ymax.
					}
					child.vertical = !current.vertical;
					current.rightChild = child;
				}
			} else
			{ // If we are over writing a value for key p.
				current.value = val;
			}

		}
	}

	/**
	 * Gets the value of <code>Point2D</code> p, if it exists in the symbol table.
	 * 
	 * @param p - query <code>Point2D</code> point.
	 * @throws NullPointerException if p is null.
	 * @return the value paired to the point if the point exists, null otherwise.
	 */
	public Value get(Point2D p)
	{
		if(p == null) 
		{
			throw new NullPointerException("null points do not exist.");
		}
		if (!this.contains(p))
		{
			return null;
		}
		return find(p, root).value;
	}

	/**
	 * Checks to see if <code>Point2D</code> p exists in the symbol table.
	 * 
	 * @param p - query <code>Point2D</code>. 
	 * @throws NullPointerException if <code>Point2D</code> p is null.
	 * @return true if <code>Point2D</code> p exists, false otherwise.
	 */
	public boolean contains(Point2D p)
	{
		if(p == null) 
		{
			throw new NullPointerException("null points do not exist.");
		}
		current = find(p, root); // Find the Node of the point if it exists.
		if (current.point.equals(p))
			return true;
		return false;

	} 

	/**
	 * Returns an iterable containing all <code>Point2D</code> points in the symbol table in level order.
	 * <p>If the table is empty returns an empty iterable.
	 * 
	 * @return all <code>Point2D</code> points in level order.
	 */
	public Iterable<Point2D> points()
	{
		current = root;
		Queue<Point2D> allPoints = new Queue<>();
		Queue<Node> iteratorHelperQ = new Queue<>();

		if (current != null)
		{
			allPoints.enqueue(current.point);
			while (!iteratorHelperQ.isEmpty())
			{ 
				// Add the children (if they exist) to the Node/Point Queues.
				if (current.leftChild != null) 
				{
					allPoints.enqueue(current.leftChild.point); // Add the Point to the Point Q.
					iteratorHelperQ.enqueue(current.leftChild); // Add the Node to the Node Q.
				}
					
				if (current.rightChild != null) 
				{
					allPoints.enqueue(current.rightChild.point); // Add the Point to the Point Q.
					iteratorHelperQ.enqueue(current.rightChild); // Add the Node to the Node Q.
				}
	
				current = iteratorHelperQ.dequeue(); // Move to the next Node.
				
				if(iteratorHelperQ.isEmpty()) 
				{ // If we have reached the last Node.
					
					// Add the last two possible points.
					if (current.leftChild != null) 
						allPoints.enqueue(current.leftChild.point);
					if (current.rightChild != null) 
						allPoints.enqueue(current.rightChild.point);	
					
					return allPoints; // Return all points in the table in level order.
				}
			}		
		}	
		return allPoints; // Return empty if table is empty.
	}

	/**
	 * Returns the <code>Point2D</code> points that intersect the query <code>RectHV</code> rect.
	 * 
	 * @param rect - the range of points to search for.
	 * @throws NullPointerException if  <code>RectHV</code> rect is null.
	 * @return the range of <code>Point2D</code> points that intersect <code>RectHV</code> rect.
	 * 
	 */
	public Iterable<Point2D> range(RectHV rect)
	{
		if(rect == null) 
		{
			throw new NullPointerException("null rectangles can not contain points.");
		}
		
		current = root; // Start at the root.
		
		Stack<Node> rangeStack = new Stack<>();
		Queue<Point2D> rangeQ = new Queue<>();
		
		rangeStack.push(current);

		while (!rangeStack.isEmpty())
		{ // While we still have Nodes to check.
			if (rect.contains(current.point))
			{ // If their points intersect rect..
				rangeQ.enqueue(current.point);
			}

			// If rect intersects the current rectangle check both children.
			if (rect.intersects(current.rectangle))
			{
				if(current.leftChild != null)
					rangeStack.push(current.leftChild);
				if(current.rightChild != null)
					rangeStack.push(current.rightChild);
			}
			current = rangeStack.pop(); // Move to the next Node.
		}
		return rangeQ; // All points that are inside the rectangle.
	} 

	/**
	 * Returns the closest point to p that exists in the symbol table.
	 * 
	 * @param p - the query <code>Point2D</code> point.
	 * @return the <code>Point2d</code> closest to <code>Point2D</code> p.
	 * @throws NullPointerException if <code>Point2D</code> p is null.
	 */
	public Point2D nearest(Point2D p)
	{
		if(p == null) 
		{
			throw new NullPointerException("null points do not exist.");
		}
		
		// Initialize champion to be the point that is as close as possible to where the point would be if it were in the symbol table.
		Point2D champion = find(p, root).point;

		double cmp;
		
		Stack<Node> nearestStack = new Stack<>();
		nearestStack.push(root);
		
		while(!nearestStack.isEmpty()) 
		{
			current = nearestStack.pop();
			
			// Check if we found a closer point.
			if (current.point.distanceSquaredTo(p) < champion.distanceSquaredTo(p))
			{
				champion = current.point;
			}
			
			cmp = xyCompare(current.point, p, current.vertical);
			if(cmp < 0)
			{ // If the current point is below or to the left of the search point.
				if(current.vertical)
				{// If to the left.
					if(current.leftChild != null) 
					{ // If the leftChild exists and could contain a closer point.
						if(current.leftChild.rectangle.distanceSquaredTo(p) < champion.distanceSquaredTo(p));
							nearestStack.push(find(p, current.leftChild)); // Check the left child for potential points.
					}
				}	
				else
				{// If below.
					if(current.rightChild != null) 
					{ // If the rightChild exists and could contain a closer point. 
						if(current.rightChild.rectangle.distanceSquaredTo(p) < champion.distanceSquaredTo(p));
							nearestStack.push(find(p, current.rightChild)); // Check the right child for potential points.
					}
					
				}
					
			}else
			{ // If the current point is above or to the right of the search point.
				if(current.vertical)
				{ // If to the right.
					if(current.rightChild != null) 
					{ // If the rightChild exists and could contain a closer point.
						if(current.rightChild.rectangle.distanceSquaredTo(p) < champion.distanceSquaredTo(p));
							nearestStack.push(find(p, current.rightChild)); // Check the right child for potential points.
					}
				}	
				else
				{ // If above.
					if(current.leftChild != null) 
					{ // If the leftChild exists and could contain a closer point.
						if(current.leftChild.rectangle.distanceSquaredTo(p) < champion.distanceSquaredTo(p));
							nearestStack.push(find(p, current.leftChild)); // Check the left child for potential points.
					}	
				}	
			}	
		}
			
		return champion;
	}	
			
	
	public static void main(String[] args) throws Exception
	{
		
		
//		KDTreeSTTest kd = new KDTreeSTTest();
//		kd.setUp();
//		kd.testIsEmpty();
//		kd.testPut();
//		kd.testKdTreeST();
//		kd.testPoints();
//		kd.testContains();
//		kd.testNearest();
//		kd.testGet();
		
		
	}

	/**
	 * Node class for implementing a symbol table.
	 * 
	 * @author Hai Le + Jace Parsell
	 *
	 */
	private class Node
	{
		private Point2D point;
		private Value value;
		private RectHV rectangle;
		private boolean vertical = true;
		private Node leftChild = null;
		private Node rightChild = null;

		/**
		 * Creates a <code>Node</code> object containing the qualities specified.
		 * 
		 * @param p <code>Point2D</code> to be assigned.
		 * @param val <code>Value</code> to be assigned.
		 * @param vertical <code>boolean</code> whether or not the <code>Node</code> represents a vertical line.
		 */
		public Node(Point2D p, Value val, boolean vertical)
		{
			this.point = p;
			this.value = val;
			this.vertical = vertical;
		}
	}

	/**
	 * Compares the two points based on if the Node is a vertical line or not.
	 * If vertical compares based off of x values.
	 * If !vertical compares based off of y values.
	 * 
	 * @param childPoint
	 * @param parentPoint
	 * @param boolean true if the parent Node is a vertical line, false otherwise.
	 * 
	 * @return negative if the childPoint is less than the parentPoint, positive otherwise.
	 */
	private double xyCompare(Point2D childPoint, Point2D parentPoint, boolean vertical)
	{
		if (vertical)
			return (childPoint.x() - parentPoint.x());
		else
			return (childPoint.y() - parentPoint.y());
	}
	
	/**
	 * Searches the symbol table for <code>Point2D</code> p, if p is not present returns the parent of where p would be.
	 * 
	 * @param p the Point to search for.
	 * @param current the Node to start from.
	 * @return <code>Point2D</code> p if it exists,  otherwise returns what would be the parent <code>Node</code> of p. 
	 */
	private Node find(Point2D p, Node current)
	{
		while(true) 
		{
			if (current.point.equals(p))
			{ // If we found the point.
				return current; // return the Node with point p.
			}	
			
			if (xyCompare(p, current.point, current.vertical) < 0)
			{ // If p would be the left child.
				if (current.leftChild != null)
				{ // If the left child exists.
					current = current.leftChild;
				}else
				{ // If the left child is null but is as close as possible to where p would be	
					return current;
				}
			}
			else if (xyCompare(p, current.point, current.vertical) >= 0)
			{ // If p would be the right child.
				if (current.rightChild != null)
				{ // If the right child exists.
					current = current.rightChild;
				}else
				{ // If the right child is null but is as close as possible to where p would be
					return current;
				}
			}			
		}
	}
	
	
}
