package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment toComment(User author, Item item, CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                author,
                commentDto.getCreated()
        );
    }
}
