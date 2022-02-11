package io.extact.rms.application.service;

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
    public RentalItem add(RentalItem addRentalItem) {
        // シリアル番号の重複チェック
        if (findBySerialNo(addRentalItem.getSerialNo()) != null) {
            throw new BusinessFlowException("The serialNo is already registered.", CauseType.DUPRICATE);
        }
        // 登録
        repository.add(addRentalItem);
        return get(addRentalItem.getId());
    }

    @Override
    public GenericRepository<RentalItem> getRepository() {
        return this.repository;
    }
}
