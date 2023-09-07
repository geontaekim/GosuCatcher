package com.foo.gosucatcher.domain.estimate.presentation;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.domain.estimate.application.MemberEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

@WebMvcTest(MemberEstimateController.class)
class MemberEstimateControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private MemberEstimateService memberEstimateService;

	@DisplayName("회원 요청 견적서 등록 성공 테스트")
	@Test
	void create() throws Exception {
		//given
		Long memberId = 1L;
		Long subItemId = 1L;

		MemberEstimateRequest memberEstimateRequest = new MemberEstimateRequest(subItemId,
			"서울 강남구 개포1동", LocalDateTime.now().plusDays(3), "추가 내용");

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, memberId,
			subItemId, "서울 강남구 개포1동", LocalDateTime.now().plusDays(4), "추가 내용");

		when(memberEstimateService.create(memberId, memberEstimateRequest)).thenReturn(
			memberEstimateResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member-estimates/{memberEstimateId}", 1L)
				.content(objectMapper.writeValueAsString(memberEstimateRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.memberId").value(memberEstimateResponse.memberId()))
			.andExpect(jsonPath("$.subItemId").value(memberEstimateResponse.subItemId()))
			.andExpect(jsonPath("$.location").value(memberEstimateResponse.location()))
			.andExpect(jsonPath("$.detailedDescription").value(memberEstimateResponse.detailedDescription()));
	}

	@DisplayName("회원 요청 견적서 등록 실패 테스트")
	@Test
	void createFailed() throws Exception {
		//given
		Long memberId = 1L;
		Long subItemId = 1L;
		MemberEstimateRequest memberEstimateRequest = new MemberEstimateRequest(subItemId, " ",
			LocalDateTime.now().plusDays(3), "추가 내용");

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, memberId,
			subItemId, " ", LocalDateTime.now().plusDays(3), "추가 내용");

		when(memberEstimateService.create(memberId, memberEstimateRequest)).thenReturn(
			memberEstimateResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/member-estimates/{memberEstimateId}", 1L)
				.content(objectMapper.writeValueAsString(memberEstimateRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("C001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.errors[0].field").value("location"))
			.andExpect(jsonPath("$.errors[0].value").value(" "))
			.andExpect(jsonPath("$.errors[0].reason").value("지역을 등록해주세요."))
			.andExpect(jsonPath("$.message").value("잘못된 값을 입력하셨습니다."));
	}

	@DisplayName("회원 요청 견적서 전체 조회 성공 테스트")
	@Test
	void findAll() throws Exception {
		//given
		Member member = Member.builder()
			.name("성이름")
			.password("abcd11@@")
			.email("abcd123@abc.com")
			.phoneNumber("010-0000-0000")
			.build();

		MainItem mainItem = MainItem.builder().name("메인 서비스 이름").description("메인 서비스 설명").build();

		SubItem subItem = SubItem.builder().mainItem(mainItem).name("세부 서비스 이름").description("세부 서비스 설명").build();

		MemberEstimate memberEstimate = MemberEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포1동")
			.preferredStartDate(LocalDateTime.now().plusDays(3))
			.detailedDescription("추가 내용")
			.build();

		List<MemberEstimate> mockEstimates = List.of(memberEstimate);
		MemberEstimatesResponse mockResponse = MemberEstimatesResponse.from(mockEstimates);

		when(memberEstimateService.findAll()).thenReturn(mockResponse);

		//when
		//then
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/member-estimates").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberEstimates").isArray())
			.andExpect(jsonPath("$.memberEstimates[0].location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.memberEstimates[0].detailedDescription").value("추가 내용"));
	}

	@DisplayName("회원 별 전체 요청 견적서 조회 성공 테스트")
	@Test
	void findAllByMember() throws Exception {
		//given
		Long memberId = 1L;

		Member member = Member.builder()
			.name("성이름")
			.password("abcd11@@")
			.email("abcd123@abc.com")
			.phoneNumber("010-0000-0000")
			.build();

		MainItem mainItem = MainItem.builder().name("메인 서비스 이름").description("메인 서비스 설명").build();

		SubItem subItem = SubItem.builder().mainItem(mainItem).name("세부 서비스 이름").description("세부 서비스 설명").build();

		MemberEstimate memberEstimate = MemberEstimate.builder()
			.member(member)
			.subItem(subItem)
			.location("서울 강남구 개포1동")
			.preferredStartDate(LocalDateTime.now().plusDays(3))
			.detailedDescription("추가 내용")
			.build();

		List<MemberEstimate> mockEstimates = List.of(memberEstimate);
		MemberEstimatesResponse mockResponse = MemberEstimatesResponse.from(mockEstimates);

		when(memberEstimateService.findAllByMember(memberId)).thenReturn(mockResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member-estimates/members/{memberId}", memberId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberEstimates").isArray())
			.andExpect(jsonPath("$.memberEstimates[0].location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.memberEstimates[0].detailedDescription").value("추가 내용"));
	}

	@DisplayName("회원 요청 견적서 단건 조회 성공 테스트")
	@Test
	void findById() throws Exception {
		//given
		Long memberEstimateId = 1L;

		Long memberId = 1L;
		Long subItemId = 1L;

		MemberEstimateResponse memberEstimateResponse = new MemberEstimateResponse(1L, memberId,
			subItemId, "서울 강남구 개포1동", LocalDateTime.now().plusDays(3), "추가 내용");

		when(memberEstimateService.findById(memberEstimateId)).thenReturn(memberEstimateResponse);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member-estimates/{memberEstimateId}",
				memberEstimateId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(memberEstimateId))
			.andExpect(jsonPath("$.memberId").value(memberId))
			.andExpect(jsonPath("$.subItemId").value(subItemId))
			.andExpect(jsonPath("$.location").value("서울 강남구 개포1동"))
			.andExpect(jsonPath("$.detailedDescription").value("추가 내용"));
	}

	@DisplayName("회원 요청 견적서 단건 조회 실패 테스트")
	@Test
	void findByIdFailed() throws Exception {
		//given
		when(memberEstimateService.findById(any(Long.class))).thenThrow(
			new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_REQUEST_ESTIMATE));

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member-estimates/{memberEstimateId}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("MRE001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원 요청 견적서입니다."));
	}

	@DisplayName("회원 요청 견적서 삭제 성공 테스트")
	@Test
	void delete() throws Exception {
		//given
		Long memberEstimateId = 1L;

		doNothing().when(memberEstimateService).delete(memberEstimateId);

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/member-estimates/{id}", memberEstimateId)
			.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@DisplayName("회원 요청 견적서 삭제 실패 테스트")
	@Test
	void deleteFailed() throws Exception {
		//given
		doThrow(new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_REQUEST_ESTIMATE)).when(
			memberEstimateService).delete(any(Long.class));

		//when
		//then
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/member-estimates/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("MRE001"))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.message").value("존재하지 않는 회원 요청 견적서입니다."));
	}
}