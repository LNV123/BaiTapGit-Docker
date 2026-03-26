package fit.hutech.spring.repositories;

import fit.hutech.spring.entities.Book;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookRepository extends JpaRepository<Book, Long> {
    default List<Book> findAllBooks(Integer pageNo, Integer pageSize, String sortBy) {
        return findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy))).getContent();
    }

    @Query("""
            SELECT b FROM Book b
            WHERE b.title LIKE %?1%
            OR b.author LIKE %?1%
            OR b.category.name LIKE %?1%
            """)
    List<Book> searchBook(String keyword);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Book b SET b.isDeleted = false WHERE b.isDeleted = true")
    void restoreAllDeletedBooks();
}
