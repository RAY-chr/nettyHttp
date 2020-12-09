package netty.dao.entity;


import netty.dao.annotion.TableField;
import netty.dao.annotion.TableId;
import netty.dao.annotion.TableName;
import netty.http.entity.Info;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 书籍表
 * </p>
 *
 * @author RAY
 * @since 2020-04-01
 */
@TableName("book")
public class Book {



    /**
     * 书籍id
     */
    @TableId("book_id")
    private Integer bookId;

    /**
     * 书籍编号
     */
    @TableField("book_no")
    private String bookNo;

    /**
     * 书籍姓名
     */
    @TableField("book_name")
    private String bookName;

    /**
     * 书的状态 1为已被借阅  0为未借阅
     */
    @TableField("book_state")
    private String bookState;

    public Book() {
    }

    public Book(Integer bookId) {
        this.bookId = bookId;
    }

    private LocalDateTime date;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookState() {
        return bookState;
    }

    public void setBookState(String bookState) {
        this.bookState = bookState;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Book{");
        sb.append("bookId=").append(bookId);
        sb.append(", bookNo='").append(bookNo).append('\'');
        sb.append(", bookName='").append(bookName).append('\'');
        sb.append(", bookState='").append(bookState).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
