package com.seolstudy.seolstudy_backend.mentee.service;

import com.seolstudy.seolstudy_backend.mentee.domain.*;
import com.seolstudy.seolstudy_backend.mentee.dto.DailyFeedbackResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.YesterdayFeedbackResponse;
import com.seolstudy.seolstudy_backend.mentee.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenteeFeedbackService {

    private final TaskRepository taskRepository;
    private final FeedbackRepository feedbackRepository;
    private final OverallFeedbackRepository overallFeedbackRepository;
    private final MentorMenteeRepository mentorMenteeRepository;
    private final UserRepository userRepository;

    public DailyFeedbackResponse getDailyFeedbacks(Long menteeId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);

        // 1. Get tasks for the day
        List<Task> tasks = taskRepository.findAllByMenteeIdAndTaskDate(menteeId, date);

        // 2. Get feedbacks for these tasks
        List<DailyFeedbackResponse.FeedbackItem> feedbackItems = new ArrayList<>();
        for (Task task : tasks) {
            Feedback feedback = feedbackRepository.findByTaskId(task.getId());
            if (feedback != null) {
                feedbackItems.add(DailyFeedbackResponse.FeedbackItem.builder()
                        .id(feedback.getId())
                        .taskId(task.getId())
                        .taskTitle(task.getTitle())
                        .subject(task.getSubject())
                        .subjectName(task.getSubject() != null ? task.getSubject().getDescription() : null)
                        .isImportant(feedback.isImportant())
                        .summary(feedback.getSummary())
                        .content(feedback.getContent())
                        .createdAt(feedback.getCreatedAt())
                        .build());
            }
        }

        // 3. Get overall feedback
        Optional<OverallFeedback> overallFeedback = overallFeedbackRepository.findByMenteeIdAndFeedbackDate(menteeId,
                date);
        String overallComment = overallFeedback.map(OverallFeedback::getContent).orElse(null);

        // 4. Get mentor name
        String mentorName = null;
        Optional<MentorMentee> mentorMentee = mentorMenteeRepository.findByMenteeId(menteeId);
        if (mentorMentee.isPresent()) {
            Optional<User> mentor = userRepository.findById(mentorMentee.get().getMentorId());
            if (mentor.isPresent()) {
                mentorName = mentor.get().getName();
            }
        }

        return DailyFeedbackResponse.builder()
                .date(date)
                .feedbacks(feedbackItems)
                .overallComment(overallComment)
                .mentorName(mentorName)
                .build();
    }

    public YesterdayFeedbackResponse getYesterdayFeedbacks(Long menteeId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 1. Get tasks for yesterday
        List<Task> tasks = taskRepository.findAllByMenteeIdAndTaskDate(menteeId, yesterday);

        // 2. Get feedbacks for these tasks
        List<YesterdayFeedbackResponse.FeedbackItem> feedbackItems = new ArrayList<>();
        for (Task task : tasks) {
            Feedback feedback = feedbackRepository.findByTaskId(task.getId());
            if (feedback != null) {
                feedbackItems.add(YesterdayFeedbackResponse.FeedbackItem.builder()
                        .id(feedback.getId())
                        .taskId(task.getId())
                        .taskTitle(task.getTitle())
                        .subject(task.getSubject())
                        .subjectName(task.getSubject() != null ? task.getSubject().getDescription() : null)
                        .isImportant(feedback.isImportant())
                        .summary(feedback.getSummary())
                        .createdAt(feedback.getCreatedAt())
                        .build());
            }
        }

        // 3. Get overall feedback
        Optional<OverallFeedback> overallFeedback = overallFeedbackRepository.findByMenteeIdAndFeedbackDate(menteeId,
                yesterday);
        String overallComment = overallFeedback.map(OverallFeedback::getContent).orElse(null);

        return YesterdayFeedbackResponse.builder()
                .date(yesterday)
                .feedbacks(feedbackItems)
                .overallComment(overallComment)
                .build();
    }
}
