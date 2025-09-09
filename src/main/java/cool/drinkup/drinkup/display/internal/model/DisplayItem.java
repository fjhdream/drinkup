package cool.drinkup.drinkup.display.internal.model;

import cool.drinkup.drinkup.display.internal.enums.DisplayType;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "display_item")
@Getter
@Setter
public class DisplayItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private DisplayType type;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "priority")
    private Integer priority = 0;

    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;

    @Column(name = "start_time")
    private ZonedDateTime startTime;

    @Column(name = "end_time")
    private ZonedDateTime endTime;

    @Column(name = "created_time")
    private ZonedDateTime createdTime;

    @Column(name = "updated_time")
    private ZonedDateTime updatedTime;

    @PrePersist
    protected void onCreate() {
        createdTime = ZonedDateTime.now();
        updatedTime = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = ZonedDateTime.now();
    }
}
