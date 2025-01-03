package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;

public interface ItemSummaryDto {
    long getId();

    String getName();

    @Value("#{target.owner.getId()}")
    long getOwnerId();
}
