package fit.hutech.spring.controllers;

import fit.hutech.spring.daos.Item;
import fit.hutech.spring.services.BookService;
import fit.hutech.spring.services.CartService;
import fit.hutech.spring.services.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final CategoryService categoryService;
    private final CartService cartService;

    @GetMapping
    public String showAllBooks(@NotNull Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("books", bookService.getAllBooks(pageNo, pageSize, sortBy));
        model.addAttribute("currentPage", pageNo);
        // Avoid division by zero
        int totalBooks = bookService.getAllBooks(0, 1000, sortBy).size();
        model.addAttribute("totalPages", (totalBooks + pageSize - 1) / pageSize);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/list";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(HttpSession session,
            @RequestParam long id,
            @RequestParam String name,
            @RequestParam double price,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestParam(required = false) String imageUrl,
            Model model) {
        var book = bookService.getBookById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));
        if (book.getQuantity() < quantity) {
            model.addAttribute("errorMessage", "Trong kho không đủ sách vui lòng chọn sách khác.");
            model.addAttribute("book", book);
            return "book/detail";
        }

        var cart = cartService.getCart(session);
        cart.addItems(new Item(id, book.getTitle(), book.getPrice(), quantity, book.getImageUrl()));
        cartService.updateCart(session, cart);
        return "redirect:/cart";
    }

    @GetMapping("/add-to-cart")
    public String addToCartGet() {
        return "redirect:/books";
    }

    @GetMapping("/detail/{id}")
    public String showBookDetail(@NotNull Model model, @PathVariable long id) {
        var book = bookService.getBookById(id);
        model.addAttribute("book", book.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách")));
        return "book/detail";
    }

    @GetMapping("/search")
    public String searchBook(
            @NotNull Model model,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("books", bookService.searchBook(keyword));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/list";
    }
}
