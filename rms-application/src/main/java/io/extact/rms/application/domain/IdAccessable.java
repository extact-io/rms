package io.extact.rms.application.domain;

public interface IdAccessable {

    Integer getId();

    void setId(Integer id);

    default boolean isSameId(IdAccessable other) {
        if (other == null) {
            return false;
        }
        if (this.getId() == null) {
            return false;
        }
        return this.getId().equals(other.getId());
    }
}
