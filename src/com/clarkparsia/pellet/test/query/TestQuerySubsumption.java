package com.clarkparsia.pellet.test.query;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.clarkparsia.pellet.sparqldl.engine.QueryEngine;
import com.clarkparsia.pellet.sparqldl.engine.QuerySubsumption;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.parser.QueryParser;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Test cases for the class QuerySubsumption
 * 
 * @author Hector Perez-Urbina
 *
 */

public class TestQuerySubsumption {
	String			ont		= "http://owldl.com/ontologies/family.owl";
	String			family	= "http://www.example.org/family#";
	String			prefix	= "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
									+ "PREFIX family: <" + family + ">\r\n" + "SELECT * { ";
	String			suffix	= " }";
	KnowledgeBase	kb;
	QueryParser		parser;
	
	@Before
	public void setUp() {
		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		model.read( ont );
		model.prepare();
		
		kb = ((PelletInfGraph) model.getGraph()).getKB();
		parser = QueryEngine.getParser();
	}

	private Query query(String queryStr) {
		return parser.parse( prefix + queryStr + suffix, kb );
	}

	
	@Test
	public void testIsEquivalentTo() {
		Query[] queries = new Query[2];
		
		queries = example1();
		assertFalse(QuerySubsumption.isEquivalentTo( queries[0], queries[1] ));
		
		queries = example2();
		assertFalse( QuerySubsumption.isEquivalentTo( queries[1], queries[0] ));
		
		queries = example3();
		assertTrue( QuerySubsumption.isEquivalentTo( queries[1], queries[0] ));
		
		queries = example4();
		assertFalse( QuerySubsumption.isEquivalentTo( queries[1], queries[0] ));
	}

	@Test
	public void testIsSubsumedBy() {
		
		Query[] queries = new Query[2];
		
		queries = example1();
		assertTrue( QuerySubsumption.isSubsumedBy( queries[0], queries[1] ));
		assertFalse( QuerySubsumption.isSubsumedBy( queries[1], queries[0] ));
	
		queries = example2();
		assertTrue( QuerySubsumption.isSubsumedBy( queries[0], queries[1] ));
		assertFalse( QuerySubsumption.isSubsumedBy( queries[1], queries[0] ));
		
		queries = example3();
		assertTrue( QuerySubsumption.isSubsumedBy( queries[0], queries[1] ));
		assertTrue( QuerySubsumption.isSubsumedBy( queries[1], queries[0] ));
		
		queries = example4();
		assertTrue( QuerySubsumption.isSubsumedBy( queries[0], queries[1] ));
		assertFalse( QuerySubsumption.isSubsumedBy( queries[1], queries[0] ));
	}

	/**
	 * Simple query subsumption similar to standard concept subsumption. Every
	 * Male is a Person so query 1 is subsumed by query 2. The converse is
	 * obviously not true.
	 */
	private Query[] example1() {
		Query[] queries = new Query[2];
		
		queries[0] = query( "?x a family:Male ." );
		queries[1] = query( "?x a family:Person ." ); 
		
		return queries;
	}

	/**
	 * Another example of subsumption. First query asks for all people married
	 * to Male individuals which is subsumed by the second query which asks for
	 * all Females.
	 */
	public Query[] example2() {
		Query[] queries = new Query[2];
		
		queries[0] = query( "?x family:isMarriedTo ?y . ?y rdf:type family:Male" );
		queries[1] = query( "?x a family:Female ." ); 
		
		return queries;
	}
	
	/**
	 * Example showing query equivalence. The subproperty relation between
	 * hasFather and hasParent properties would normally establish subsumption
	 * in one way but due to cardinality restrictions defined in the ontology
	 * two queries end up being equivalent,
	 */
	public Query[] example3() {
		Query[] queries = new Query[2];
		
		queries[0] = query( "?x family:hasFather ?y . " );
		queries[1] = query( "?x family:hasParent ?y . ?y a family:Male ." ); 
		
		return queries;
	}

	/**
	 * The subsumption in this example holds because of the subproperty relation
	 * between hasBrother and hasSibling.
	 */
	public Query[] example4() {
		
		Query[] queries = new Query[2];
		
		queries[0] = query( "?x a family:Female; family:hasBrother ?y . " );
		queries[1] = query( "?x a family:Female; family:hasSibling ?z ." ); 
		
		return queries;
	}
}
