package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long userId);

    @Query("select i " +
            "from Item i " +
            "where i.available = true " +
            "  and lower(i.name) like concat('%', lower(?1), '%') " +
            "   or lower(i.description) like concat('%', lower(?1), '%')")
    List<Item> findAllByText(String text);

    List<ItemSummaryDto> findAllByRequestId(long requestId);
}
