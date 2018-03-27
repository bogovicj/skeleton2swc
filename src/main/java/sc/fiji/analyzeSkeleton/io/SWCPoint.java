/* -*- mode: java; c-basic-offset: 8; indent-tabs-mode: t; tab-width: 8 -*- */

/* Copyright 2006, 2007, 2008, 2009, 2010, 2011 Mark Longair */

/*
  This file is part of the ImageJ plugin "Simple Neurite Tracer".

  The ImageJ plugin "Simple Neurite Tracer" is free software; you
  can redistribute it and/or modify it under the terms of the GNU
  General Public License as published by the Free Software
  Foundation; either version 3 of the License, or (at your option)
  any later version.

  The ImageJ plugin "Simple Neurite Tracer" is distributed in the
  hope that it will be useful, but WITHOUT ANY WARRANTY; without
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  PARTICULAR PURPOSE.  See the GNU General Public License for more
  details.

  In addition, as a special exception, the copyright holders give
  you permission to combine this program with free software programs or
  libraries that are released under the Apache Public License.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package sc.fiji.analyzeSkeleton.io;

import java.io.PrintWriter;
import java.util.ArrayList;

import tracing.PointInImage;

public class SWCPoint implements Comparable< SWCPoint >
{
	ArrayList< SWCPoint > nextPoints;
	SWCPoint previousPoint;
	int id, type, previous;
	double x, y, z, radius;

	public SWCPoint( final int id, final int type, final double x, final double y,
			final double z, final double radius, final int previous )
	{
		nextPoints = new ArrayList<>();
		this.id = id;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.previous = previous;
	}

	public PointInImage getPointInImage()
	{
		return new PointInImage( x, y, z );
	}

	public int getId()
	{
		return id;
	}

	public int getType()
	{
		return type;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getZ()
	{
		return z;
	}

	public double getRadius()
	{
		return radius;
	}

	public int getPrevious()
	{
		return previous;
	}

	public void addNextPoint( final SWCPoint p )
	{
		if ( !nextPoints.contains( p ) )
			nextPoints.add( p );
	}

	public void setPreviousPoint( final SWCPoint p )
	{
		previousPoint = p;
	}

	@Override
	public String toString()
	{
		return "SWCPoint [" + id + "] " + type + " " + "(" + x + "," + y + "," + z + ") "
				+ "radius: " + radius + ", " + "[previous: " + previous + "]";
	}

	@Override
	public int compareTo( final SWCPoint o )
	{
		final int oid = o.id;
		return (id < oid) ? -1 : ((id > oid) ? 1 : 0);
	}

	public void println( final PrintWriter pw )
	{
		pw.println( "" + id + " " + type + " " + x + " " + y + " " + z + " " + radius
				+ " " + previous );
	}
}
