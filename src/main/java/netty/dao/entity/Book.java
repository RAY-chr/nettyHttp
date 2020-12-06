package netty.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import netty.dao.annotion.TableField;
import netty.dao.annotion.TableId;
import netty.dao.annotion.TableName;
import netty.http.entity.Info;

import java.io.Serializable;

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
        return "Book{" +
                "bookId=" + bookId +
                ", bookNo=" + bookNo +
                ", bookName=" + bookName +
                ", bookState=" + bookState +
                "}";
    }
}
