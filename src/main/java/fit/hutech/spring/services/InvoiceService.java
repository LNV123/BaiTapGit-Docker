package fit.hutech.spring.services;

import fit.hutech.spring.entities.Invoice;
import fit.hutech.spring.repositories.IInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {
    private final IInvoiceRepository invoiceRepository;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }
    
    public void updateInvoiceStatus(Long id, String status) {
        invoiceRepository.findById(id).ifPresent(invoice -> {
            invoice.setStatus(status);
            invoiceRepository.save(invoice);
        });
    }
}
