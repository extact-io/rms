package io.extact.rms.client.api.adaptor.local.dto;

import java.util.function.Function;

public interface DtoConverter<Entity, Dto> extends Function<Entity, Dto> {

    @Override
    default Dto apply(Entity entity) {
        return this.entityToDto(entity);
    }

    Dto entityToDto(Entity entity);

    Entity dtoToEntity(Dto dto);
}
