package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentForItemDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {

    public CommentResponseDto mapCommentToResponseDto(Comment comment) {
        CommentResponseDto commentResponse = new CommentResponseDto();
        commentResponse.setId(comment.getId());
        commentResponse.setText(comment.getText());
        commentResponse.setAuthorName(comment.getAuthor().getName());
        commentResponse.setCreated(comment.getCreated());
        return commentResponse;
    }

    public Comment mapCommentDtoToComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }

    public CommentForItemDto mapCommentToCommentForItem(Comment comment) {
        CommentForItemDto commentForItemDto = new CommentForItemDto();
        commentForItemDto.setId(comment.getId());
        commentForItemDto.setText(comment.getText());
        commentForItemDto.setAuthorName(comment.getAuthor().getName());
        commentForItemDto.setCreated(comment.getCreated());
        return commentForItemDto;
    }
}
