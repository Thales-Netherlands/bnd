package test;

import org.junit.*;
import static org.junit.Assert.*;

public class TestCase2 {
	static int cntr=0;
	
	@BeforeClass
	public static void beforeClass() {
		cntr++;
		assertEquals(1, cntr);
	}
	@AfterClass
	public static void afterClass() {
		cntr--;
		assertEquals(0, cntr);
	}
	
	@Before
	public void before() {
		cntr+=2;
		assertEquals(3, cntr);
	}
	@After
	public void after() {
		cntr-=2;
		assertEquals(1, cntr);
	}
	
	@Test
	public void m1() {
		System.err.println("All ok");
	}

	@Test
	public void m2() {
		System.err.println("All wrong");
		fail("this one should not be called");
	}

	public void m3() {
		System.err.println("All wrong");
		fail("this one should not be called");
	}

}
