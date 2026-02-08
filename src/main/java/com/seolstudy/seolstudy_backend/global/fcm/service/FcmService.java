package com.seolstudy.seolstudy_backend.global.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.seolstudy.seolstudy_backend.global.error.BusinessException;
import com.seolstudy.seolstudy_backend.global.error.ErrorCode;
import com.seolstudy.seolstudy_backend.global.fcm.domain.FcmToken;
import com.seolstudy.seolstudy_backend.global.fcm.repository.FcmTokenRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {
    private final FcmTokenRepository fcmTokenRepository;

    /** 현재 클라이언트의 토큰이 DB에 저장된 토큰과 다르면 갱신, 없으면 토큰값을 저장 */
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
    public void sendNotification(String token, String title, String body, Long taskId){
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("taskId", String.valueOf(taskId))
                .build();
        try{
            log.info("멘토 토큰 발견: {}", token); // 이게 찍히는지 확인
            FirebaseMessaging.getInstance().send(message);
            System.out.println("구글 서버로 전송 성공!"); // 이 로그가 찍히는지 확인
        } catch(FirebaseMessagingException e){
            throw new BusinessException("메시지 전송 중 에러가 발생했습니다.", ErrorCode.INTERNAL_ERROR);
        }
    }
}
