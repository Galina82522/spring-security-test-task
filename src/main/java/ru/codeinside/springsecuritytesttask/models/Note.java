package ru.codeinside.springsecuritytesttask.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;


/**
 * Заметка
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotAudited
    private User user;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = false)
    private String lastModifiedBy;

    @CreatedDate
    @Column(name = "created", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "updated", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime updated;
}
