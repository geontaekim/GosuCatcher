package com.foo.gosucatcher.domain.bucket.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foo.gosucatcher.domain.bucket.application.BucketService;
import com.foo.gosucatcher.domain.bucket.dto.request.BucketRequest;
import com.foo.gosucatcher.domain.bucket.dto.response.BucketResponse;
import com.foo.gosucatcher.domain.bucket.dto.response.BucketsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/buckets")
public class BucketController {

	private final BucketService bucketService;

	@GetMapping
	public ResponseEntity<BucketsResponse> findAll() {
		BucketsResponse bucketsResponse = bucketService.findAll();

		return ResponseEntity.ok(bucketsResponse);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable Long id) {
		bucketService.deleteById(id);

		return ResponseEntity.noContent().build();
	}

	@PostMapping
	public ResponseEntity<BucketResponse> create(@RequestBody BucketRequest bucketRequest) {
		BucketResponse bucketResponse = bucketService.create(bucketRequest);

		return ResponseEntity.ok(bucketResponse);
	}

	@GetMapping("/{status}")
	public ResponseEntity<Boolean> checkStatus(@RequestParam Long expertId, @RequestParam Long memberId) {
		Boolean status = bucketService.checkStatus(expertId, memberId);

		return ResponseEntity.ok(status);
	}
}
