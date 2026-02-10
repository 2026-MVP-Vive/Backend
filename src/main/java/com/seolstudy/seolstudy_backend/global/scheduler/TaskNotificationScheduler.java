package com.seolstudy.seolstudy_backend.global.scheduler;

import com.seolstudy.seolstudy_backend.global.fcm.domain.FcmToken;
import com.seolstudy.seolstudy_backend.global.fcm.repository.FcmTokenRepository;
import com.seolstudy.seolstudy_backend.global.fcm.service.FcmService;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 자정을 기점으로 완료되지 않은 과제가 있는 멘티들에게 알람을 전송하는 스케쥴러
 * */
@Component
@Slf4j
@RequiredArgsConstructor
public class TaskNotificationScheduler {

    private final TaskRepository taskRepository;
    private final FcmService fcmService;
    private final FcmTokenRepository fcmTokenRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    @Transactional(readOnly = true)
    public void sendReminderForUnconfirmedTasks() {
        log.info("자정 알림 스케줄러 시작: 미완료 과제 요약 체크");

        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 1. 어제 날짜의 미완료 과제를 모두 가져옵니다.
        List<Task> unconfirmedTasks = taskRepository.findAllByTaskDateAndIsMentorConfirmedFalse(yesterday);

        // 2. 멘티ID별로 리스트를 그룹핑합니다. (Map<Long, List<Task>>)
        Map<Long, List<Task>> tasksByMentee = unconfirmedTasks.stream()
                .collect(Collectors.groupingBy(Task::getMenteeId));

        // 3. 멘티별로 요약 알림 발송
        tasksByMentee.forEach((menteeId, tasks) -> {
            int taskCount = tasks.size();
            List<FcmToken> tokens = fcmTokenRepository.findAllByUserId(menteeId);

            for (FcmToken token : tokens) {
                try {
                    String title = "📌 확인하지 않은 과제가 있어요!";
                    String body = String.format("멘토님이 기다리고 계세요! 어제 미완료된 과제 %d건을 지금 바로 확인해 보세요. 🔥", taskCount);

                    //미제출 과제가 여러 건인 경우 대표 TaskId 하나만 보냄
                    Long representativeTaskId = tasks.get(0).getId();

                    fcmService.sendNotification(token.getToken(), title, body, representativeTaskId);
                    log.info("UserId {}에게 {}건의 요약 알림 전송 완료", menteeId, taskCount);
                } catch (Exception e) {
                    log.error("요약 알림 전송 실패 (멘티ID: {}): {}", menteeId, e.getMessage());
                }
            }
        });
        log.info("자정 알림 스케줄러 종료");
    }
}
