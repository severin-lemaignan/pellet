// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import java.math.BigInteger;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.GenericIntervalList;
import org.mindswap.pellet.utils.Namespaces;
import org.mindswap.pellet.utils.NumberUtils;
import org.relaxng.datatype.DatatypeException;

import aterm.ATermAppl;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.datetime.BigDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.BigTimeDurationValueType;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.ITimeDurationValueType;


/**
 * @author kolovski
 */
public class XSDDay extends BaseXSDAtomicType implements AtomicDatatype, XSDAtomicType {	
    private static XSDatatype dt = null;
    private static IDateTimeValueType min = null;
    private static IDateTimeValueType max = null;

    static {
        try {
            dt = DatatypeFactory.getTypeByName( "gDay" );
            min = (IDateTimeValueType) dt.createValue( "---01", null );
            max = (IDateTimeValueType) dt.createValue( "---31", null );
        }
        catch( DatatypeException e ) {
            e.printStackTrace();
        }
    }
    
    private static final ValueSpace DAY_VALUE_SPACE = new DayValueSpace();
    
    private static class DayValueSpace extends AbstractDateTimeValueSpace implements ValueSpace {	       
        
		public DayValueSpace() {
            super( min, max, dt );
        }
  
		//return the difference in Days
		public int count(Object start, Object end) {			
            BigDateTimeValueType calendarStart = ((IDateTimeValueType) start).getBigValue();			
			BigDateTimeValueType calendarEnd = ((IDateTimeValueType) end).getBigValue(); 		
			
			return calendarEnd.getDay().intValue() - calendarStart.getDay().intValue() + 1;			
		}		
        
        public Object succ( Object value, int n ) {
            BigInteger bigN = new BigInteger( String.valueOf( n ) );
            ITimeDurationValueType nDays = new BigTimeDurationValueType(
                NumberUtils.INTEGER_ZERO, NumberUtils.INTEGER_ZERO, bigN, 
                NumberUtils.INTEGER_ZERO, NumberUtils.INTEGER_ZERO, NumberUtils.DECIMAL_ZERO );
            IDateTimeValueType s = ((IDateTimeValueType)value).add( nDays );
            
            return s;
        }
	}
    
	public static XSDDay instance = new XSDDay(ATermUtils.makeTermAppl(Namespaces.XSD + "gDay"));

	protected XSDDay(ATermAppl name) {
        super( name, DAY_VALUE_SPACE );
	}

    public BaseXSDAtomicType create( GenericIntervalList intervals ) {
        XSDDay type = new XSDDay( null );
        type.values = intervals;

        return type;
    }

    public AtomicDatatype getPrimitiveType() {
        return instance;
    }	
}