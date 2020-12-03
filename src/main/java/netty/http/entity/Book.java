package netty.http.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 * 书籍表
 * </p>
 *
 * @author RAY
 * @since 2020-04-01
 */
public class Book extends Model<Book> {



    /**
     * 书籍id
     */
    @TableId(value = "book_id", type = IdType.AUTO)
    private Integer bookId;

    /**
     * 书籍编号
     */
    private String bookNo;

    /**
     * 书籍姓名
     */
    private String bookName;

    /**
     * 书的状态 1为已被借阅  0为未借阅
     */
    private String bookState;

    private Person person;

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
    protected Serializable pkVal() {
        return this.bookId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", bookNo=" + bookNo +
                ", bookName=" + bookName +
                ", bookState=" + bookState +
                "}";
    }
}
