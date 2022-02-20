package io.extact.rms.application.service;

import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.exception.BusinessFlowException;
import io.extact.rms.application.exception.BusinessFlowException.CauseType;
import io.extact.rms.application.persistence.GenericRepository;
import io.extact.rms.application.persistence.RentalItemRepository;

@ApplicationScoped
public class RentalItemService implements GenericService<RentalItem> {

    private RentalItemRepository repository;

    @Inject
    public RentalItemService(RentalItemRepository rentalItemRepository) {
        this.repository = rentalItemRepository;
    }

    public RentalItem findBySerialNo(String serialNo) {
        return repository.findBySerialNo(serialNo);
    }

    @Override
    public Consumer<RentalItem> getDuplicateChecker() {
        return (targetItem) -> {
            var foundItem = findBySerialNo(targetItem.getSerialNo());
            if (foundItem != null && (targetItem.getId() == null || !foundItem.isSameId(targetItem))) {
                throw new BusinessFlowException("The serialNo is already registered.", CauseType.DUPRICATE);
            }
        };
    }

    @Override
    public GenericRepository<RentalItem> getRepository() {
        return this.repository;
    }
}
