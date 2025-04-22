package jp.co.metateam.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;

/**
 * 書籍関連クラス
 */
@Log4j2
@Controller
public class BookController{
    
    private final BookMstService bookMstService;

    @Autowired
    public BookController(BookMstService bookMstService){
        this.bookMstService = bookMstService;
    }

    @GetMapping("/book/index")
    public String index(Model model) {
        // 書籍を全件取得
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();
        
        model.addAttribute("bookMstList", bookMstList);

        return "book/index";
    }

    @GetMapping("/book/add")
    public String add(Model model) {
        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }

        return "book/add";
    }

    @PostMapping("/book/add")
    public String register(@ModelAttribute("bookMstDto") BookMstDto bookMstDto, BindingResult result, Model model){      
        boolean errTitleFlg = false;
        boolean errIsbnFlg = false;        
        String titleExist = bookMstDto.getTitle();
        String isbnExist = bookMstDto.getIsbn();

        if(bookMstDto.getTitle() == null || bookMstDto.getTitle().trim().isEmpty()){
            result.rejectValue("title", "error.value", "書籍名は必須です");
            errTitleFlg = true;
        }
        if(bookMstDto.getIsbn() == null || bookMstDto.getIsbn().trim().isEmpty()){
            result.rejectValue("isbn", "error.value", "ISBNは必須です");
            errIsbnFlg = true;
        }
        if (bookMstDto.getTitle() != null && bookMstDto.getTitle().length() > 50) {
            result.rejectValue("title", "error.value", "書籍名は50文字以下で入力してください");
            errTitleFlg = true;
        }
        if (bookMstDto.getIsbn() != null) {
            if (bookMstDto.getIsbn().length() != 13) {
                result.rejectValue("isbn", "error.value", "ISBNは13桁で入力してください");
                errIsbnFlg = true;
            } else if (!bookMstDto.getIsbn().matches("^[0-9]+$")) {
                result.rejectValue("isbn", "error.value", "ISBNは半角数字のみで入力してください");
                errIsbnFlg = true;
            } else if (bookMstService.selectByIsbn(bookMstDto.getIsbn()) != null) {
                result.rejectValue("isbn", "error.value", "登録済みのISBNです");
                errIsbnFlg = true;
            }
        }
        if (result.hasErrors()) {
            model.addAttribute("bookMstDto", bookMstDto);
            return "book/add";
        }
        try{ 
        bookMstService.save(bookMstDto);
        return"redirect:/book/index";
             }catch (Exception e) {
                log.error(e.getMessage());
    
                result.reject("global.error", "登録処理でエラーが発生しました");
                model.addAttribute("bookMstDto", bookMstDto);
                return "book/add";
            }
        }
}
 

