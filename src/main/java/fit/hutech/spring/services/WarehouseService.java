package fit.hutech.spring.services;

import fit.hutech.spring.entities.Warehouse;
import fit.hutech.spring.repositories.IWarehouseRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})
public class WarehouseService {
    private final IWarehouseRepository warehouseRepository;

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public Optional<Warehouse> getWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }

    public void addWarehouse(Warehouse warehouse) {
        warehouseRepository.save(warehouse);
    }

    public void updateWarehouse(@NotNull Warehouse warehouse) {
        Warehouse existingWarehouse = warehouseRepository
                .findById(warehouse.getId())
                .orElse(null);
        Objects.requireNonNull(existingWarehouse)
                .setName(warehouse.getName());
        existingWarehouse.setAddress(warehouse.getAddress());

        warehouseRepository.save(existingWarehouse);
    }

    public void deleteWarehouseById(Long id) {
        warehouseRepository.deleteById(id);
    }
}
