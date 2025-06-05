package cool.drinkup.drinkup.user.internal.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true)
    private String oauthId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private OAuthTypeEnum oauthType;

    @Column(nullable = true)
    private String avatar;
    
    @Column(nullable = true)
    private String password;
    
    @Column(nullable = true, unique = true)
    private String phone;

    private boolean enabled = true;
    
    @CreationTimestamp
    @Column(name = "create_date", updatable = false, columnDefinition = "DATETIME")
    private ZonedDateTime createDate = ZonedDateTime.now(ZoneOffset.UTC);
    
    @UpdateTimestamp
    @Column(name = "update_date", columnDefinition = "DATETIME")
    private ZonedDateTime updateDate = ZonedDateTime.now(ZoneOffset.UTC);
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;
} 