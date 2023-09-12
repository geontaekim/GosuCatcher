package com.foo.gosucatcher.domain.review.domain;

import static java.lang.Boolean.FALSE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "replies")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE replies SET is_deleted = true WHERE id = ?")
public class Reply extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @OneToOne(fetch = LAZY)
	// @JoinColumn(name = "review_id")
	// private Review review;

	private String content;

	private boolean isDeleted = FALSE;

	@Builder
	public Reply(String content) {
		this.content = content;
	}

	public void update(Reply reply) {
		content = reply.getContent();
	}
}
