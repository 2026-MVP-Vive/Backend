package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.Comment;
import com.seolstudy.seolstudy_backend.mentee.dto.CommentCreateRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.CommentCreateResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenteeCommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public CommentCreateResponse createComment(Long menteeId, CommentCreateRequest request) {
        Comment comment = Comment.builder()
                .menteeId(menteeId)
                .content(request.getContent())
                .commentDate(request.getDate())
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentCreateResponse.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent())
                .date(savedComment.getCommentDate())
                .createdAt(savedComment.getCreatedAt())
                .build();
    }
}