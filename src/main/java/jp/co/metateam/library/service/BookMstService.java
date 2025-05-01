package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.util.StringUtils;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;
import lombok.extern.log4j.Log4j2;

//MVCモデルのModelに当たる部分（本来ならここにバリデーションチェックをする）

@Service
@Log4j2
public class BookMstService {

    private final BookMstRepository bookMstRepository;
    
    @Autowired
    public BookMstService(BookMstRepository bookMstRepository){
        this.bookMstRepository = bookMstRepository;
    }
    
public BookMst selectByTitle(String title){
    return this.bookMstRepository.findByTitle(title).orElse(null);
}
public BookMst selectByIsbn(String isbn){
    return this.bookMstRepository.findByIsbn(isbn).orElse(null);
}

    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.findLimitedBook();
        List<BookMstDto> bookMstDtoList = new ArrayList<BookMstDto>();

        // 書籍の在庫数を取得
        // FIXME: 現状は書籍ID毎にDBに問い合わせている。一度のSQLで完了させたい。
        for (int i = 0; i < books.size(); i++) {
            BookMst book = books.get(i);
            BookMstDto bookMstDto = new BookMstDto();
            bookMstDto.setId(book.getId());
            bookMstDto.setIsbn(book.getIsbn());
            bookMstDto.setTitle(book.getTitle());
            bookMstDtoList.add(bookMstDto);
        }

        return bookMstDtoList;
    }
        @Transactional
    public void save(BookMstDto bookMstDto) {
        try {
            // BookMstDtoからBookMstへの変換
            BookMst BookMst = new BookMst();

            BookMst.setTitle(bookMstDto.getTitle());
            BookMst.setIsbn(bookMstDto.getIsbn());


            // データベースへの保存
            this.bookMstRepository.save(BookMst);
        } catch (Exception e) {
            log.error("Failed to save book: " + bookMstDto, e);
            throw new RuntimeException("Failed to save book", e);
        }
    }
}



