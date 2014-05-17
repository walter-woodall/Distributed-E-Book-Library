package member;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import library.*;

public class MemberImpl implements Member{

	/**
	 * Default constructor of the member client. Initializes variables. 
	 * You may add other constructors if you need.
	 * 
	 */
	private static int nameCount = 0;
	private MemberData data;
	private LibraryServer server;
	public MemberImpl() {
		// IMPLEMENT THIS
		this.data = new MemberData("Member" + nameCount++,new LinkedList<Book>(),null,new LinkedList<String>());
	}

	/* (non-Javadoc)
	 * @see member.Member#getName()
	 */
	public String getName() throws RemoteException{
		// IMPLEMENT THIS
		return data.getName();
	}

	/* (non-Javadoc)
	 * @see member.Member#register()
	 */
	public boolean register() throws RemoteException{
		// IMPLEMENT THIS
		int id = server.registerMember(data);
		if( id != 0){
			data.setMemberId(id);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see member.Member#checkoutBook(java.lang.String)
	 */
	public boolean checkoutBook(String bookName) throws RemoteException{
		// IMPLEMENT THIS
		Book request = server.checkoutBook(bookName, data);
		if(request != null){
			data.getBooksCurrCheckedOut().add(request);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see member.Member#returnBook(java.lang.String)
	 */
	public boolean returnBook(String bookName) throws RemoteException{
		// IMPLEMENT THIS
		if(server.returnBook(bookName, data)){
			//removes the book from members list of books checked out
			for(Book book : data.getBooksCurrCheckedOut()){
				if(book.getName().equals(bookName)){
					data.getBooksCurrCheckedOut().remove(book);
				}
			}
			//adds book to members list of books read
			data.getBooksRead().add(bookName);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see member.Member#getServer()
	 */
	public LibraryServer getServer() throws RemoteException{
		// IMPLEMENT THIS
		return this.server;
	}

	/* (non-Javadoc)
	 * @see member.Member#setServer(library.LibraryServer)
	 */
	public void setServer(LibraryServer server) throws RemoteException {
		// IMPLEMENT THIS
		this.server = server;
	}

	/* (non-Javadoc)
	 * @see member.Member#getBooksCheckedOut()
	 */
	public List<Book> getBooksCurrCheckedOut() throws RemoteException{
		// IMPLEMENT THIS
		
		return data.getBooksCurrCheckedOut();
	}

	/* (non-Javadoc)
	 * @see member.Member#getBooksRead()
	 */
	public List<String> getBooksRead() throws RemoteException {
		// IMPLEMENT THIS
		return data.getBooksRead();
	}
	
	public static void main(String[] args) {
		
	}

}
