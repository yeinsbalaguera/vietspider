package sample;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Book implements Serializable {
  
    public int bookId = 0;
    public String title;
    public String author;
    public String ISBN;
    public String price;
    public Date publishDate;

    public Book() {
    }
    
    public Book(int id) {
      this.bookId = id;
      String value = String.valueOf(Math.random());
      title  = "title." + value + "." + id;
      author  = "author." + value + "." + id;
      ISBN = "ISBN." + value + "." + id;
      price  = "price." + value + "." + id;
      publishDate = Calendar.getInstance().getTime();
    }
    
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(title).append('\n');
      builder.append(author).append('\n');
      builder.append(ISBN).append('\n');
      builder.append(price).append('\n');
      builder.append(publishDate);
      return builder.toString();
    }
}
