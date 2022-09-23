package io.extact.rms.application.persistence.jpa;

import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.persistence.GenericRepository.ApiType;
import io.extact.rms.application.persistence.RentalItemRepository;
import io.extact.rms.platform.extension.EnabledIfRuntimeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.JPA)
public class RentalItemJpaRepository extends JpaCrudRepository<RentalItem> implements RentalItemRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public RentalItem findBySerialNo(String serialNo) {
        var jpql = "select r from RentalItem r where r.serialNo = ?1";
        try {
            return em.createQuery(jpql, RentalItem.class)
                        .setParameter(1, serialNo)
                        .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public EntityManager getEntityManage() {
        return this.em;
    }

    @Override
    public Class<RentalItem> getTargetClass() {
        return RentalItem.class;
    }
}
