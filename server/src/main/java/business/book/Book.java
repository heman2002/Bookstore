package business.book;

public class Book {

	private long bookId;
	private String title;
	private String author;

	private String description;
	private int price;

	private int rating;
	private boolean isPublic;

	private boolean isFeatured;
	private long categoryId;

	public Book(long bookId, String title, String author, int price, boolean isPublic, long categoryId, String description, int rating, boolean isFeatured) {
		this.bookId = bookId;
		this.title = title;
		this.author = author;
		this.price = price;
		this.isPublic = isPublic;
		this.categoryId = categoryId;
		this.description = description;
		this.rating = rating;
		this.isFeatured = isFeatured;
	}

	public long getBookId() {
		return bookId;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getDescription() {
		return description;
	}

	public int getPrice() {
		return price;
	}

	public int getRating() {
		return rating;
	}

	public boolean getIsPublic() {
		return isPublic;
	}

	public boolean getIsFeatured() {
		return isFeatured;
	}

	public long getCategoryId() {
		return categoryId;
	}
}
