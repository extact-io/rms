package io.extact.rms.application.domain;

import static javax.persistence.AccessType.*;

import javax.persistence.Access;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.extact.rms.application.domain.constraint.ItemName;
import io.extact.rms.application.domain.constraint.RmsId;
import io.extact.rms.application.domain.constraint.SerialNo;
import io.extact.rms.application.domain.constraint.ValidationGroups.Update;

@Access(FIELD)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class RentalItem implements Transformable, IdAccessable {

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RmsId(groups = Update.class)
    private Integer id;

    /** シリアル番号 */
    @SerialNo
    private String serialNo;

    /** 品名 */
    @ItemName
    private String itemName;


    public static RentalItem ofTransient(String serialNo, String itemName) {
        return of(null, serialNo, itemName);
    }

    // ----------------------------------------------------- override methods

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
