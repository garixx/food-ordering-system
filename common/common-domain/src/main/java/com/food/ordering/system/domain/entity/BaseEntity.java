package com.food.ordering.system.domain.entity;

import java.util.Objects;

public abstract class BaseEntity<ID> {
    private ID id;

    public ID getId() {
        return id;
    }

    public BaseEntity<ID> setId(ID id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
