package ijse.lk.AutoCareManagement.entity;

import ijse.lk.AutoCareManagement.enumeration.RecommendationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ai_recommendations")
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private long recommendationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "suggested_service", nullable = false, length = 150)
    private String suggestedService;

    @Column(name = "reason_prompt", columnDefinition = "TEXT")
    private String reasonPrompt;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private double confidenceScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_status", length = 20)
    private RecommendationStatus status;

    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RecommendationStatus.PENDING;
        }
    }
}
