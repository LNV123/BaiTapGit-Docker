package fit.hutech.spring.controllers; 
 
import fit.hutech.spring.entities.Book;
import fit.hutech.spring.services.BookService; 
import fit.hutech.spring.services.CategoryService; 
import fit.hutech.spring.viewmodels.BookGetVm; 
import fit.hutech.spring.viewmodels.BookPostVm;
import lombok.RequiredArgsConstructor; 
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*; 
 
import java.util.List; 
 
@RestController 
@RequestMapping("/api/v1") 
@CrossOrigin(origins = "*") 
@RequiredArgsConstructor 
public class ApiController { 
    private final BookService bookService; 
 
    private final CategoryService categoryService; 
 
 
    @GetMapping("/books") 
    public ResponseEntity<List<BookGetVm>> getAllBooks(Integer pageNo, Integer pageSize, String sortBy) { 
        return ResponseEntity.ok(bookService.getAllBooks( 
                pageNo == null ? 0 : pageNo, 
                pageSize == null ? 20 : pageSize, 
                sortBy == null ? "id" : sortBy) 
                .stream() 
                .map(BookGetVm::from) 
                .toList()); 
    } 
 
    @GetMapping("/books/id/{id}") 
    public ResponseEntity<BookGetVm> getBookById(@PathVariable Long id) { 
        return ResponseEntity.ok(bookService.getBookById(id) 
                .map(BookGetVm::from) 
                .orElse(null)); 
    } 
 
    @DeleteMapping("/books/{id}") 
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) { 
        bookService.deleteBookById(id); 
        return ResponseEntity.ok().build(); 
    } 
 
    @GetMapping("/books/search") 
    public ResponseEntity<List<BookGetVm>> searchBooks(String keyword) { 
        return ResponseEntity.ok(bookService.searchBook(keyword) 
                .stream() 
                .map(BookGetVm::from) 
                .toList()); 
    } 

    @PostMapping("/books")
    public ResponseEntity<BookGetVm> addBook(@RequestBody BookPostVm bookPostVm) {
        var book = new Book();
        book.setTitle(bookPostVm.title());
        book.setAuthor(bookPostVm.author());
        book.setPrice(bookPostVm.price());
        book.setCategory(categoryService.getCategoryById(bookPostVm.categoryId()).orElse(null));
        bookService.addBook(book);
        return ResponseEntity.ok(BookGetVm.from(book));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookGetVm> updateBook(@PathVariable Long id, @RequestBody BookPostVm bookPostVm) {
        var book = bookService.getBookById(id).orElse(null);
        if (book == null) return ResponseEntity.notFound().build();
        book.setTitle(bookPostVm.title());
        book.setAuthor(bookPostVm.author());
        book.setPrice(bookPostVm.price());
        book.setCategory(categoryService.getCategoryById(bookPostVm.categoryId()).orElse(null));
        bookService.updateBook(book);
        return ResponseEntity.ok(BookGetVm.from(book));
    }
} 
