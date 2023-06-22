package business.order;

import api.ApiException;
import business.BookstoreDbException;
import business.JdbcUtils;
import business.book.Book;
import business.book.BookDao;
import business.book.BookForm;
import business.cart.ShoppingCart;
import business.cart.ShoppingCartItem;
import business.customer.Customer;
import business.customer.CustomerDao;
import business.customer.CustomerForm;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.sql.Connection;

public class DefaultOrderService implements OrderService {

	private BookDao bookDao;

	private OrderDao orderDao;

	private LineItemDao lineItemDao;

	private CustomerDao customerDao;

	public void setOrderDao(OrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public void setLineItemDao(LineItemDao lineItemDao) {
		this.lineItemDao = lineItemDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	@Override
	public OrderDetails getOrderDetails(long orderId) {
		Order order = orderDao.findByOrderId(orderId);
		Customer customer = customerDao.findByCustomerId(order.getCustomerId());
		List<LineItem> lineItems = lineItemDao.findByOrderId(orderId);
		List<Book> books = lineItems
				.stream()
				.map(lineItem -> bookDao.findByBookId(lineItem.getBookId()))
				.collect(Collectors.toList());
		return new OrderDetails(order, customer, lineItems, books);
	}

	@Override
    public long placeOrder(CustomerForm customerForm, ShoppingCart cart) {

		validateCustomer(customerForm);
		validateCart(cart);

		try (Connection connection = JdbcUtils.getConnection()) {
			Date date = getDate(
					customerForm.getCcExpiryMonth(),
					customerForm.getCcExpiryYear());
			return performPlaceOrderTransaction(
					customerForm.getName(),
					customerForm.getAddress(),
					customerForm.getPhone(),
					customerForm.getEmail(),
					customerForm.getCcNumber(),
					date, cart, connection);
		} catch (SQLException e) {
			throw new BookstoreDbException("Error during close connection for customer order", e);
		}
	}

	private Date getDate(String monthString, String yearString) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");
		Date date;
		try {
			date = formatter.parse(monthString+"/"+yearString);
		} catch (Exception e) {
			date = new Date();
		}
		return date;
	}

	private long performPlaceOrderTransaction(
			String name, String address, String phone,
			String email, String ccNumber, Date date,
			ShoppingCart cart, Connection connection) {
		try {
			connection.setAutoCommit(false);
			long customerId = customerDao.create(
					connection, name, address, phone, email,
					ccNumber, date);
			long customerOrderId = orderDao.create(
					connection,
					cart.getComputedSubtotal() + cart.getSurcharge(),
					generateConfirmationNumber(), customerId);
			for (ShoppingCartItem item : cart.getItems()) {
				lineItemDao.create(connection, item.getBookId(), customerOrderId, item.getQuantity());
			}
			connection.commit();
			return customerOrderId;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new BookstoreDbException("Failed to roll back transaction", e1);
			}
			return 0;
		}
	}

	private int generateConfirmationNumber() {
		Random random = new Random();
		return random.nextInt(999999999);
	}

	private void validateCustomer(CustomerForm customerForm) {

    	String name = customerForm.getName();

		if (name == null || name.equals("") || name.length() > 45) {
			throw new ApiException.InvalidParameter("Invalid name field");
		}

		String address = customerForm.getAddress();

		if (address == null || address.equals("") || address.length() > 45) {
			throw new ApiException.InvalidParameter("Invalid address field");
		}

		String phone = customerForm.getPhone();

		if (phone==null || phone.equals("")) {
			throw new ApiException.InvalidParameter("Invalid Phone field");
		}

		String phoneRegex
				= "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
				+ "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
				+ "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";

		Pattern phonePattern = Pattern.compile(phoneRegex);
		Matcher phoneMatcher = phonePattern.matcher(phone);
		if (!phoneMatcher.matches()) {
			throw new ApiException.InvalidParameter("Invalid Phone field");
		}

		String email = customerForm.getEmail();
		if(email==null || email.equals("")) {
			throw new ApiException.InvalidParameter("Invalid Email field");
		}
		String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
				+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

		Pattern emailPattern = Pattern.compile(emailRegex);
		Matcher emailMatcher = emailPattern.matcher(email);
		if(!emailMatcher.matches()) {
			throw new ApiException.InvalidParameter("Invalid Email field");
		}

		String ccNumber = customerForm.getCcNumber();
		if (ccNumber == null || ccNumber.equals("")) {
			throw new ApiException.InvalidParameter("Invalid ccNumber field");
		}
		ccNumber = ccNumber.replaceAll("\\D+", "");

		if (ccNumber.length()<14 || ccNumber.length()>16) {
			throw new ApiException.InvalidParameter("Invalid ccNumber field");
		}
		if (expiryDateIsInvalid(customerForm.getCcExpiryMonth(), customerForm.getCcExpiryYear())) {
			throw new ApiException.InvalidParameter("Invalid expiry date");

		}
	}

	private boolean expiryDateIsInvalid(String ccExpiryMonth, String ccExpiryYear) {
		DateTimeFormatter expiryMonthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
		if(ccExpiryMonth.length()==1) {
			ccExpiryMonth = "0"+ccExpiryMonth;
		}
		YearMonth curr = YearMonth.now();
		YearMonth expiry;
		try {
			expiry = YearMonth.parse(ccExpiryMonth+"/"+ccExpiryYear, expiryMonthFormatter);
		} catch (DateTimeParseException e) {
			return true;
		}

		if(curr.isAfter(expiry)) {
			return true;
		}

		return false;

	}

	private void validateCart(ShoppingCart cart) {

		if (cart.getItems().size() <= 0) {
			throw new ApiException.InvalidParameter("Cart is empty.");
		}

		cart.getItems().forEach(item-> {
			if (item.getQuantity() <= 0 || item.getQuantity() > 99) {
				throw new ApiException.InvalidParameter("Invalid quantity");
			}
			Book databaseBook = bookDao.findByBookId(item.getBookId());
			BookForm cartBookForm = item.getBookForm();
			if(cartBookForm.getPrice()!=databaseBook.getPrice()) {
				throw new ApiException.InvalidParameter("Invalid price");
			}
			if(cartBookForm.getCategoryId()!=databaseBook.getCategoryId()) {
				throw new ApiException.InvalidParameter("Invalid category");
			}
		});
	}

}
