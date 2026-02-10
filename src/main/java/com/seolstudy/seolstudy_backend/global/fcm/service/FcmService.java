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

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {
    private final FcmTokenRepository fcmTokenRepository;

    /** 현재 클라이언트의 토큰이 DB에 저장된 토큰과 다르면 갱신, 없으면 토큰값을 저장 */
    @Transactional
    public void saveOrUpdateToken(Long userId, String tokenValue) {
        // 1. 해당 토큰이 이미 DB에 있는지 확인
        fcmTokenRepository.findByToken(tokenValue)
                .ifPresentOrElse(
                        existingToken -> {
                            // 2-1. 토큰이 이미 있다면, 주인(userId)이 바뀌었는지 체크 (기기 공유 케이스)
                            if (!existingToken.getUserId().equals(userId)) {
                                // 다른 사람이 쓰던 기기라면 기존 연결 끊고 새 주인으로 변경
                                // (보통은 기존 토큰 삭제 후 새로 생성하거나 업데이트)
                                fcmTokenRepository.delete(existingToken);
                                fcmTokenRepository.save(new FcmToken(userId, tokenValue));
                            } else {
                                // 주인도 같다면 마지막 사용 시간만 갱신
                                existingToken.updateLastUsed();
                            }
                        },
                        () -> {
                            // 2-2. 아예 처음 보는 토큰이라면 신규 저장
                            fcmTokenRepository.save(new FcmToken(userId, tokenValue));
                        }
                );
    }

    /** 알림 전송 서비스 */
    @Transactional
    public void sendNotification(String token, String title, String body, Long taskId){
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle("[설스터디]" + title)
                        .setBody(body)
                        .build())
                .putData("taskId", String.valueOf(taskId))
                .setWebpushConfig(WebpushConfig.builder()
                        .putHeader("Urgency", "high") //  'Urgency' 헤더를 추가해서 기기를 강제로 깨웁니다
                        .setFcmOptions(WebpushFcmOptions.withLink("https://seolstudy.duckdns.org"))
                        .setNotification(WebpushNotification.builder() // 웹 전용 노티 설정 보강
                                .setTitle("[설스터디] " + title)
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
