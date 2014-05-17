package library;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import member.MemberData;

public class LibraryServerImpl implements LibraryServer {

	/**
	 * Constructor for the library server. It is given a number total books to have, number of
	 * copies per book, and maximum books per member. 
	 * Creates a number of Book objects based on numBooks to give them to members when checking them out.  
	 * The server maintains the properties and enforces them for future transactions.
	 * 
	 * @param numBooks
	 * @param copiesPerBook
	 * @param booksPerMember
	 */
	private static int memberNum = 1;
	private int booksPerMember;
	
	public ConcurrentHashMap<Book, Integer> bookList = new ConcurrentHashMap<Book, Integer>();
	public ConcurrentHashMap<String, Integer> memberList = new ConcurrentHashMap<String, Integer>();
	public ConcurrentHashMap<Integer, Set<Book>> checkedOutList = new ConcurrentHashMap<Integer, Set<Book>>();
	public ConcurrentHashMap<String, Book> bookNames = new ConcurrentHashMap<String, Book>();
	
	public LibraryServerImpl(int numBooks, int copiesPerBook, int booksPerMember) {
		// IMPLEMENT THIS
		this.booksPerMember = booksPerMember;
		for(int index = 1; index <= numBooks; index++){
			Book newBook = new Book("Book" + index);
			bookList.put(newBook, copiesPerBook);
			bookNames.put("Book" + index, newBook);
		}
		
	}

	/* (non-Javadoc)
	 * @see library.LibraryServer#registerMember(member.Member)
	 */
	@Override
	public Integer registerMember(MemberData memberdata) throws RemoteException {
		// IMPLEMENT THIS
		if(!memberList.containsKey(memberdata.getName())){
			memberList.put(memberdata.getName(), memberNum);
			checkedOutList.put(memberNum, new HashSet<Book>());
			return memberNum++;
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see library.LibraryServer#checkoutBook(java.lang.String, member.Member)
	 */
	@Override
	public Book checkoutBook(String bookName, MemberData memberdata) throws RemoteException {
		// Checking to see if the member is registered.
		Book request = bookNames.get(bookName);
		int id = memberdata.getMemberId();
		if(!memberList.containsValue(id)){
			return null;
		}
		//Checks if the member already has the book
		if(checkedOutList.get(id).contains(bookName)){
			return null;
		}
		//checks to see if the member has not checked out to many books.
		if(checkedOutList.get(id).size() >= booksPerMember){
			return null;
		}
		//check to see if there are more copies of the book
		if(!(bookList.get(request) > 0)){
			return null;
		}
		//subtracts 1 from the number of books available
		bookList.put(request, bookList.get(request) - 1);
		//adds book to the list of books that are checked out by that member
		checkedOutList.get(id).add(request);
		return request;
	}

	/* (non-Javadoc)
	 * @see library.LibraryServer#returnBook(java.lang.String, member.Member)
	 */
	@Override
	public boolean returnBook(String bookName, MemberData memberdata) throws RemoteException {
		// IMPLEMENT THIS
		Book returnBook = bookNames.get(bookName);
		int id = memberdata.getMemberId();
		if(memberList.containsValue(id)){
			if(checkedOutList.get(id).contains(returnBook)){
				//add 1 to copies of book in stock. Remove book from persons list of checked out books
				bookList.put(returnBook, bookList.get(returnBook) + 1);
				checkedOutList.get(id).remove(returnBook);
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see library.LibraryServer#getBookListings()
	 */
	@Override
	public List<String> getBookListings() throws RemoteException {
		// IMPLEMENT THIS
		List<String> bookListing = new LinkedList<String>();
		for(String bookName : bookNames.keySet()){
			bookListing.add(bookName);
		}
		return bookListing;
	}

	/* (non-Javadoc)
	 * @see library.LibraryServer#getAvailableBookListings()
	 */
	@Override
	public List<String> getAvailableBookListings() throws RemoteException {
		// IMPLEMENT THIS
		List<String> availableBooks = new LinkedList<String>();
		for(Book book : bookList.keySet()){
			if(bookList.get(book) > 0){
				availableBooks.add(book.getName());
			}
		}
		return availableBooks;
	}
	
	public static void main(String[] args) {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		
		try {
			LibraryServer obj = new LibraryServerImpl(4, 2, 2);
			LibraryServer stub = (LibraryServer) UnicastRemoteObject.exportObject(obj, 0);
			Registry registry = LocateRegistry.createRegistry(0);
			registry.rebind("LibraryServer", stub);
			System.out.println("LibraryServer bound in registry on port:"
					+ 0);

		} catch (Exception e) {
			System.out.println("ChatServerImpl err: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
