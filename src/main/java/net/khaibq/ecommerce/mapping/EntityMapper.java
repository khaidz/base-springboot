package net.khaibq.ecommerce.mapping;

public interface EntityMapper<E, D> {
    D toDto(E entity);

    E toEntity(D dto);
}
