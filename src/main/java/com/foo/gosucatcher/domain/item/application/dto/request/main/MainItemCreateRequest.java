package com.foo.gosucatcher.domain.item.application.dto.request.main;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.foo.gosucatcher.domain.item.domain.MainItem;

public record MainItemCreateRequest(
	@NotBlank(message = "서비스명은 필수 입력 입니다.")
	String name,

	@NotBlank(message = "해당 서비스의 부가 설명을 적어주세요.")
	@Size(min = 6, message = "부가 설명은 6자 이상 작성해 주세요.")
	String description
) {

	public static MainItem toMainItem(MainItemCreateRequest mainItemCreateRequest) {
		return MainItem.builder()
			.name(mainItemCreateRequest.name)
			.description(mainItemCreateRequest.description)
			.build();
	}
}
