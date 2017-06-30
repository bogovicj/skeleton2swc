package sc.fiji.analyzeSkeleton.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import sc.fiji.analyzeSkeleton.Edge;
import sc.fiji.analyzeSkeleton.Graph;
import sc.fiji.analyzeSkeleton.Point;
import sc.fiji.analyzeSkeleton.Skeleton2Swc;
import sc.fiji.analyzeSkeleton.SwcIO;
import sc.fiji.analyzeSkeleton.Vertex;

public class SwcAsGraph
{

	public static Graph load( File f )
	{
		ArrayList< SWCPoint > swcPts = SwcIO.loadSWC( f );
		int N = swcPts.size();

		Graph g = new Graph();

		// find branch points

		ArrayList< Integer > branchAndTipIds = new ArrayList< Integer >();
		// first count the number of children each point has
		// only points with #children > 1 are branch points
		int[] numChildren = new int[ N ];
		for ( SWCPoint pt : swcPts )
		{
			int prev = pt.getPrevious();
			if ( prev >= 0 )
				numChildren[ prev ]++;
		}
		for ( int i = 0; i < N; i++ )
		{
			if ( numChildren[ i ] > 1 || numChildren[ i ] == 0
					|| swcPts.get( i ).getPrevious() < 0 )
				branchAndTipIds.add( swcPts.get( i ).getId() );
		}

//		System.out.println( Arrays.toString( numChildren ) );
//		System.out.println( branchAndTipIds );

		HashMap< Integer, Vertex > vertices = new HashMap< Integer, Vertex >();

		int i = 0;
		boolean first = true;
		ArrayList< Point > slab = new ArrayList< Point >();
		Vertex lastVertex = null;
		double length = 0;
		// assumes that all "slabs" are adjacent in the list
		for ( SWCPoint swcPt : swcPts )
		{
			Point p = new Point( (int) swcPt.getX(), (int) swcPt.getY(),
					(int) swcPt.getZ() );
			// System.out.println( "p: " + swcPt );
			// System.out.println( "pid: " + swcPt.getId() );
			// System.out.println( "is id branch or tip : " +
			// branchAndTipIds.contains( swcPt.getId() ));

			if ( branchAndTipIds.contains( swcPt.getId() ) )
			{

				// make the vertex
				Vertex v = new Vertex();
				v.addPoint( p );
				g.addVertex( v );
				vertices.put( i, v );

				// Add an edge

				// No Edge if this is the first vertex though
				if ( first )
				{
					g.setRoot( v );
					first = false;
				} else
				{
					// If we don't already know the vertex opposite this edge,
					// find it
					if ( lastVertex == null )
						lastVertex = vertices.get( swcPt.getPrevious() );

					// make the edge and add it
					Edge e = new Edge( lastVertex, v, slab, length );
					v.setPredecessor( e );
					g.addEdge( e );

					// reset
					lastVertex = null;
					slab = new ArrayList< Point >();
					length = 0;
				}
			} else
			{
				if ( slab == null )
				{
					slab = new ArrayList< Point >();
				}

				if ( lastVertex == null )
				{
					lastVertex = vertices.get( swcPt.getPrevious() );
				}
				slab.add( p );
			}
			length++;

			i++;
		}
		return g;
	}

	public static void main( String[] args )
	{
		String f = "/groups/saalfeld/home/bogovicj/swc/test.swc";
		String outname = "/groups/saalfeld/home/bogovicj/swc/test_out.swc";

		Graph g = SwcAsGraph.load( new File( f ) );
		System.out.println( g );

		ArrayList< tracing.SWCPoint > swcPts = Skeleton2Swc.graphToSwc( g );
		System.out.println( "  num points of output : " + swcPts.size() );

		System.out.println( "Exporting to " + outname );
		try
		{
			final PrintWriter pw = new PrintWriter(
					new OutputStreamWriter( new FileOutputStream( outname ), "UTF-8" ) );
			Skeleton2Swc.flushSWCPoints( swcPts, pw );
		} catch ( final IOException ioe )
		{
			System.err.println( "Saving to " + outname + " failed" );
		}
	}
}
