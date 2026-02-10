package com.seolstudy.seolstudy_backend.global.fcm.service;

import com.google.firebase.messaging.*;
import com.seolstudy.seolstudy_backend.global.error.BusinessException;
import com.seolstudy.seolstudy_backend.global.error.ErrorCode;
import com.seolstudy.seolstudy_backend.global.fcm.domain.FcmToken;
import com.seolstudy.seolstudy_backend.global.fcm.repository.FcmTokenRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {
    private final FcmTokenRepository fcmTokenRepository;

    /** 현재 클라이언트의 토큰이 DB에 저장된 토큰과 다르면 갱신, 없으면 토큰값을 저장 */
    @Transactional
    public void saveOrUpdateToken(Long userId, String tokenValue) {
        // 1) 이 유저의 기존 토큰 제거
        fcmTokenRepository.deleteByUserId(userId);

        // 2) 이 기기의 기존 연결 제거
        fcmTokenRepository.findByToken(tokenValue)
                .ifPresent(existing -> fcmTokenRepository.delete(existing));

        // ★ 중요: 위 삭제 쿼리들을 DB에 즉시 반영
        fcmTokenRepository.flush();

        // 3) 새 토큰 저장
        fcmTokenRepository.save(FcmToken.builder()
                .userId(userId)
                .token(tokenValue)
                .build());
    }


    /** 알림 전송 서비스 */
    @Transactional
    public void sendNotification(String token, String title, String body, Long taskId){
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(taskId != null ? Map.of("taskId", String.valueOf(taskId)) : Map.of())
                .setWebpushConfig(WebpushConfig.builder()
                        .putHeader("Urgency", "high") //  'Urgency' 헤더를 추가해서 기기를 강제로 깨웁니다
                        .setFcmOptions(WebpushFcmOptions.withLink("https://seolstudy.duckdns.org"))
                        .setNotification(WebpushNotification.builder() // 웹 전용 노티 설정 보강
                                .setTitle(title)
                                .setBody(body)
                                .setIcon("https://cdn-icons-png.flaticon.com/512/3119/3119338.png") // 아이콘이 없으면 알림이 안 뜰 때가 있음
                                .setVibrate(new int[]{200, 100, 200}) // 진동 설정 (안드로이드 웹앱용)
                                .build())
                        .build())
                .build();
        try{
            log.info("유저 토큰 발견: {}", token); // 이게 찍히는지 확인
            FirebaseMessaging.getInstance().send(message);
            System.out.println("구글 서버로 전송 성공!"); // 이 로그가 찍히는지 확인
        } catch(FirebaseMessagingException e){
            if (MessagingErrorCode.UNREGISTERED.equals(e.getMessagingErrorCode())) {
                log.warn("만료된 토큰을 발견하여 삭제합니다: {}", token);
                fcmTokenRepository.deleteByToken(token);
            } else {
                throw new BusinessException("메시지 전송 중 에러가 발생했습니다.", ErrorCode.INTERNAL_ERROR);
            }
        }
    }
}
