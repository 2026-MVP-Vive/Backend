package com.seolstudy.seolstudy_backend.mentee.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seolstudy.seolstudy_backend.global.file.domain.File;
import com.seolstudy.seolstudy_backend.global.file.service.FileService;
import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.domain.MentorMentee;
import com.seolstudy.seolstudy_backend.mentee.domain.Subject;
import com.seolstudy.seolstudy_backend.mentee.domain.Task;
import com.seolstudy.seolstudy_backend.mentee.domain.User;
import com.seolstudy.seolstudy_backend.mentee.domain.UserRole;
import com.seolstudy.seolstudy_backend.mentee.dto.CommentCreateRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.TaskRequest;
import com.seolstudy.seolstudy_backend.mentee.dto.UpdateStudyTimeRequest;
import com.seolstudy.seolstudy_backend.mentee.repository.MentorMenteeRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.TaskRepository;
import com.seolstudy.seolstudy_backend.mentee.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MenteeApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MentorMenteeRepository mentorMenteeRepository;

    @MockBean
    private SecurityUtil securityUtil;

    @MockBean
    private FileService fileService;

    private User mentee;

    @BeforeEach
    void setUp() throws Exception {
        // Create access user (mentee)
        mentee = new User("mentee01", "password", "Mentee User", UserRole.MENTEE);
        userRepository.save(mentee);

        // Create mentor user
        User mentor = new User("mentor01", "password", "Mentor User", UserRole.MENTOR);
        userRepository.save(mentor);

        // Create mentor-mentee relationship
        MentorMentee mentorMentee = new MentorMentee(mentor.getId(), mentee.getId());
        mentorMenteeRepository.save(mentorMentee);

        // Mock SecurityUtil to return the ID of the created user
        when(securityUtil.getCurrentUserId()).thenReturn(mentee.getId());

        // Mock FileService
        File mockFile = File.builder()
                .id(1L)
                .storedName("stored.jpg")
                .originalName("test.jpg")
                .filePath("http://mock-s3/test.jpg")
                .fileType("image/jpeg")
                .fileSize(1024L)
                .build();
        when(fileService.saveFile(any(), any(), any())).thenReturn(mockFile);
        when(fileService.getProfileImageUrl(anyLong())).thenReturn("http://mock-s3/profile.jpg");
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.1 할 일 목록 조회 (일일 플래너)")
    void getDailyTasks() throws Exception {
        String today = LocalDate.now().toString();

        mockMvc.perform(get("/api/v1/mentee/tasks")
                .param("date", today))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.date").value(today))
                .andExpect(jsonPath("$.data.tasks").isArray());
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.2 할 일 상세 조회")
    void getTaskDetail() throws Exception {
        // Create a task for the mentee
        Task task = new Task(mentee.getId(), "Test Task", LocalDate.now(), Subject.MATH, mentee.getId());
        taskRepository.save(task);

        mockMvc.perform(get("/api/v1/mentee/tasks/" + task.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(task.getId()));
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.3 멘티 할 일 추가")
    void addTask() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDate(LocalDate.now());
        request.setSubject(Subject.MATH);

        mockMvc.perform(post("/api/v1/mentee/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("New Task"));
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.4 공부 시간 기록")
    void updateStudyTime() throws Exception {
        Task task = new Task(mentee.getId(), "Study Task", LocalDate.now(), Subject.KOREAN, mentee.getId());
        taskRepository.save(task);

        UpdateStudyTimeRequest request = new UpdateStudyTimeRequest();
        request.setStudyTime(60);

        mockMvc.perform(patch("/api/v1/mentee/tasks/" + task.getId() + "/study-time")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studyTime").value(60));
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.5 과제 제출")
    void submitTask() throws Exception {
        Task task = new Task(mentee.getId(), "Submission Task", LocalDate.now(), Subject.ENGLISH, mentee.getId());
        taskRepository.save(task);

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg",
                "test image content".getBytes());

        mockMvc.perform(multipart("/api/v1/mentee/tasks/" + task.getId() + "/submission")
                .file(image))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.6 어제자 피드백 목록 조회")
    void getYesterdayFeedbacks() throws Exception {
        mockMvc.perform(get("/api/v1/mentee/feedbacks/yesterday"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.7 날짜별 피드백 상세 조회")
    void getDailyFeedbacks() throws Exception {
        String yesterday = LocalDate.now().minusDays(1).toString();
        mockMvc.perform(get("/api/v1/mentee/feedbacks")
                .param("date", yesterday))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.8 약점 맞춤 솔루션 목록 조회")
    void getSolutions() throws Exception {
        mockMvc.perform(get("/api/v1/mentee/solutions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.solutions").isArray());
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.9 월간 계획표 조회")
    void getMonthlyPlan() throws Exception {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        mockMvc.perform(get("/api/v1/mentee/monthly-plan")
                .param("year", String.valueOf(year))
                .param("month", String.valueOf(month)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.year").value(year));
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.10 서울대쌤 칼럼 목록 조회")
    void getColumns() throws Exception {
        mockMvc.perform(get("/api/v1/mentee/columns"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.12 마이페이지 프로필 조회")
    void getProfile() throws Exception {
        mockMvc.perform(get("/api/v1/mentee/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").exists());
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.13 과목별 달성률 조회")
    void getAchievement() throws Exception {
        mockMvc.perform(get("/api/v1/mentee/achievement"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.14 월간 학습리포트 목록 조회")
    void getMonthlyReports() throws Exception {
        mockMvc.perform(get("/api/v1/mentee/monthly-reports"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.16 코멘트/질문 등록")
    void createComment() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("Test Comment");
        request.setDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/mentee/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "mentee01", roles = "MENTEE")
    @DisplayName("3.17 플래너 마감/피드백 요청")
    void completePlanner() throws Exception {
        // Create tasks for today
        Task task1 = new Task(mentee.getId(), "Task 1", LocalDate.now(), Subject.MATH, mentee.getId());
        Task task2 = new Task(mentee.getId(), "Task 2", LocalDate.now(), Subject.ENGLISH, mentee.getId());
        taskRepository.save(task1);
        taskRepository.save(task2);

        // Submit tasks
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "content".getBytes());
        mockMvc.perform(multipart("/api/v1/mentee/tasks/" + task1.getId() + "/submission").file(image));
        mockMvc.perform(multipart("/api/v1/mentee/tasks/" + task2.getId() + "/submission").file(image));

        String today = LocalDate.now().toString();

        mockMvc.perform(post("/api/v1/mentee/planner/" + today + "/complete"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }
}
