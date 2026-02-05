package com.seolstudy.seolstudy_backend.mentee.controller;

import com.seolstudy.seolstudy_backend.global.util.SecurityUtil;
import com.seolstudy.seolstudy_backend.mentee.dto.AttachmentDto;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnDetailResponse;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnDto;
import com.seolstudy.seolstudy_backend.mentee.dto.ColumnListResponse;
import com.seolstudy.seolstudy_backend.mentee.service.ColumnService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ColumnController.class)
@AutoConfigureMockMvc(addFilters = false)
class ColumnControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ColumnService columnService;

        // Although not used in Controller directly, Security configuration might need
        // it or global filters
        @MockBean
        private SecurityUtil securityUtil;

        @Test
        @DisplayName("칼럼 목록 조회 API")
        void getColumns() throws Exception {
                // given
                ColumnListResponse response = ColumnListResponse.builder()
                                .columns(List.of(
                                                ColumnDto.builder()
                                                                .id(1L)
                                                                .title("Column Title")
                                                                .createdAt(LocalDate.now())
                                                                .build()))
                                .pagination(ColumnListResponse.PaginationDto.builder()
                                                .page(0)
                                                .size(10)
                                                .totalElements(1)
                                                .totalPages(1)
                                                .build())
                                .build();

                given(columnService.getColumns(any())).willReturn(response);

                // when & then
                mockMvc.perform(get("/api/v1/mentee/columns")
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.columns[0].title").value("Column Title"))
                                .andExpect(jsonPath("$.data.pagination.totalElements").value(1));
        }

        @Test
        @DisplayName("칼럼 상세 조회 API")
        void getColumnDetail() throws Exception {
                // given
                Long columnId = 1L;
                ColumnDetailResponse response = ColumnDetailResponse.builder()
                                .id(columnId)
                                .title("Detail Title")
                                .content("Detail Content")
                                .author("Author")
                                .createdAt(LocalDate.now())
                                .attachments(List.of(
                                                AttachmentDto.builder()
                                                                .id(100L)
                                                                .fileName("test.pdf")
                                                                .downloadUrl("/api/v1/files/100/download")
                                                                .build()))
                                .build();

                given(columnService.getColumnDetail(columnId)).willReturn(response);

                // when & then
                mockMvc.perform(get("/api/v1/mentee/columns/{columnId}", columnId)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.title").value("Detail Title"))
                                .andExpect(jsonPath("$.data.attachments[0].fileName").value("test.pdf"));
        }
}
