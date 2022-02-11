package io.extact.rms.application.persistence;

import io.extact.rms.application.domain.RentalItem;

/**
 * レンタル品の永続化インタフェース。
 */
public interface RentalItemRepository extends GenericRepository<RentalItem> {

    /**
     * シリアル番号を指定してレンタル品を取得。
     *
     * @param serialNo シリアル番号
     * @return 該当エンティティ。該当なしはnull
     */
    RentalItem findBySerialNo(String serialNo);
}