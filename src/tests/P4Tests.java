package tests;

import static org.junit.Assert.*;
import library.*;
import member.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class P4Tests {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception { 
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testBookList() {
		int numBooks = 3;
		int copiesPerBook = 2;
		int booksPerMember = 2;
		LibraryServerImpl server = new LibraryServerImpl(numBooks, copiesPerBook, booksPerMember);
		
		try {
			List<String> bookList = server.getBookListings();
			List<String> availableBookList = server.getAvailableBookListings();
			assertEquals(numBooks,bookList.size());
			assertEquals(numBooks,availableBookList.size());
			//System.out.println("Book list:"+bookList.toString());
			//System.out.println("Available list:"+availableBookList.toString());
		} catch (RemoteException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCheckOut() {
		int numBooks = 3;
		int copiesPerBook = 1;
		int booksPerMember = 2;
		LibraryServerImpl server = new LibraryServerImpl(numBooks, copiesPerBook, booksPerMember);
		
		try {
			ArrayList<MemberImpl> members = createMembers(1, server);
			MemberImpl member = members.get(0);
			
			System.out.println(member.getName());
			System.out.println(server.getBookListings());
			
			assertTrue(member.checkoutBook("Book1"));
			assertTrue(member.checkoutBook("Book2"));
			assertFalse(member.checkoutBook("Book3"));
			
			List<String> bookList = server.getBookListings();
			List<String> availableBookList = server.getAvailableBookListings();
			assertEquals(numBooks,bookList.size());
			assertEquals(1,availableBookList.size());
			System.out.println("Book list:"+bookList.toString());
			System.out.println("Available list:"+availableBookList.toString());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReturn() {
		int numBooks = 3;
		int copiesPerBook = 1;
		int booksPerMember = 2;
		LibraryServerImpl server = new LibraryServerImpl(numBooks, copiesPerBook, booksPerMember);
		
		try {
			ArrayList<MemberImpl> members = createMembers(1, server);
			MemberImpl member = members.get(0);
			
			assertTrue(member.checkoutBook("Book1"));
			assertTrue(member.checkoutBook("Book2"));
			assertFalse(member.checkoutBook("Book3"));
			assertFalse(member.returnBook("Book3"));
			assertTrue(member.returnBook("Book2"));
			assertTrue(member.returnBook("Book1"));
			assertFalse(member.returnBook("Book1"));
			
			List<String> bookList = server.getBookListings();
			List<String> availableBookList = server.getAvailableBookListings();
			assertEquals(numBooks,bookList.size());
			assertEquals(numBooks,availableBookList.size());
			//System.out.println("Book list:"+bookList.toString());
			//System.out.println("Available list:"+availableBookList.toString());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCurrCheckedOutBooks() {
		int numBooks = 3;
		int copiesPerBook = 1;
		int booksPerMember = 2;
		LibraryServerImpl server = new LibraryServerImpl(numBooks, copiesPerBook, booksPerMember);
		
		try {
			ArrayList<MemberImpl> members = createMembers(1, server);
			MemberImpl member = members.get(0);
			
			assertTrue(member.checkoutBook("Book1"));
			assertTrue(member.checkoutBook("Book2"));
			assertEquals(2,member.getBooksCurrCheckedOut().size());
			assertTrue(member.returnBook("Book2"));
			assertEquals(1,member.getBooksCurrCheckedOut().size());
			assertTrue(member.returnBook("Book1"));
			assertEquals(0,member.getBooksCurrCheckedOut().size());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReadBooks() {
		int numBooks = 3;
		int copiesPerBook = 1;
		int booksPerMember = 2;
		LibraryServerImpl server = new LibraryServerImpl(numBooks, copiesPerBook, booksPerMember);
		
		try {
			ArrayList<MemberImpl> members = createMembers(1, server);
			MemberImpl member = members.get(0);
			
			assertTrue(member.checkoutBook("Book1"));
			assertTrue(member.checkoutBook("Book2"));
			assertEquals(0,member.getBooksRead().size());
			assertTrue(member.returnBook("Book2"));
			assertEquals(1,member.getBooksRead().size());
			assertTrue(member.returnBook("Book1"));
			assertEquals(2,member.getBooksRead().size());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMultipleMembers() {
		int numBooks = 3;
		int copiesPerBook = 1;
		int booksPerMember = 2;
		LibraryServerImpl server = new LibraryServerImpl(numBooks, copiesPerBook, booksPerMember);
		
		try {
			ArrayList<MemberImpl> members = createMembers(2, server);
			MemberImpl member1 = members.get(0);
			MemberImpl member2 = members.get(1);
			
			// Try checkout same book
			assertTrue(member1.checkoutBook("Book1"));
			assertFalse(member2.checkoutBook("Book1"));
			
			// Checkout new book
			assertTrue(member2.checkoutBook("Book2"));
			
			// Return and re-checkout
			assertTrue(member1.returnBook("Book1"));
			assertFalse(member2.returnBook("Book1"));
			assertTrue(member2.checkoutBook("Book1"));
			
			assertEquals(2,member2.getBooksCurrCheckedOut().size());
			assertEquals(0,member2.getBooksRead().size());
			
			assertTrue(member2.returnBook("Book1"));
			assertTrue(member2.returnBook("Book2"));
			
			assertEquals(0,member2.getBooksCurrCheckedOut().size());
			assertEquals(2,member2.getBooksRead().size());
			
			// Checkout book already read
			assertTrue(member2.checkoutBook("Book2"));
			assertTrue(member2.returnBook("Book2"));
			assertEquals(3,member2.getBooksRead().size());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMultipleMembersWithMultipleCopies() {
		int numBooks = 3;
		int copiesPerBook = 2;
		int booksPerMember = 2;
		LibraryServerImpl server = new LibraryServerImpl(numBooks, copiesPerBook, booksPerMember);
		
		try {
			ArrayList<MemberImpl> members = createMembers(3, server);
			MemberImpl member1 = members.get(0);
			MemberImpl member2 = members.get(1);
			MemberImpl member3 = members.get(2);
			
			assertEquals(3,server.getAvailableBookListings().size());
			
			// Try checkout same book
			assertTrue(member1.checkoutBook("Book1"));
			assertTrue(member2.checkoutBook("Book1"));
			assertFalse(member3.checkoutBook("Book1"));
			
			assertEquals(2,server.getAvailableBookListings().size());
			
			// Return and re-checkout
			assertTrue(member1.returnBook("Book1"));
			assertTrue(member2.returnBook("Book1"));
			assertTrue(member3.checkoutBook("Book1"));
			
			assertEquals(3,server.getAvailableBookListings().size());
			
			assertTrue(member3.returnBook("Book1"));
			assertEquals(3,server.getAvailableBookListings().size());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private ArrayList<MemberImpl> createMembers(int numMembers, LibraryServerImpl server) throws Exception {
		ArrayList<MemberImpl> members = new ArrayList<MemberImpl>();
		// Initialize members
		for (int i=0;i<numMembers;i++) {
			MemberImpl member = new MemberImpl();
			member.setServer(server);
			boolean registerResult = member.register();
			assertTrue(registerResult);
			members.add(member);
		}
		
		return members;
	}
}
