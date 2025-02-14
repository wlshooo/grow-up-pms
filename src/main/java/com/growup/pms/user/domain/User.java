package com.growup.pms.user.domain;

import com.growup.pms.common.BaseEntity;
import com.growup.pms.common.exception.code.ErrorCode;
import com.growup.pms.common.exception.exceptions.BusinessException;
import com.growup.pms.common.util.RandomPasswordGenerator;
import com.growup.pms.common.util.RandomPasswordGenerator.PasswordOptions;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    public static final int MAX_LINKS_PER_USER = 5;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 16;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Embedded
    private UserProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<UserLink> links = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime passwordChangeDate = LocalDateTime.now();

    @Column(nullable = false)
    private int passwordFailureCount = 0;

    @Builder
    public User(String username, String password, String email, Provider provider, UserProfile profile, List<UserLink> links) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.provider = provider;
        this.profile = profile;

        if (links != null) {
            this.links.addAll(links);
        }
    }

    public void increasePasswordFailureCount() {
        passwordFailureCount++;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void changePassword(PasswordEncoder passwordEncoder, String newPassword) {
        password = passwordEncoder.encode(newPassword);
        passwordChangeDate = LocalDateTime.now();
    }

    public String renewPassword(PasswordEncoder passwordEncoder) {
        String newPassword = RandomPasswordGenerator.generatePassword(MIN_PASSWORD_LENGTH,
                PasswordOptions.builder()
                        .includeLower(true)
                        .includeUpper(true)
                        .includeDigits(true)
                        .includeSpecial(true)
                        .build());
        changePassword(passwordEncoder, newPassword);
        return newPassword;
    }

    public void updateProfileImageName(String imageName) {
        this.profile.changeImageName(imageName);
    }

    public void deleteProfileImage() {
        this.profile.changeImageName(null);
    }

    public boolean isProfileImageEmpty() {
        return StringUtils.isEmpty(this.profile.getImageName());
    }
  
    private void addLink(String link) {
        if (links.size() >= MAX_LINKS_PER_USER) {
            throw new BusinessException(ErrorCode.EXCEEDED_LINKS);
        }
        links.add(new UserLink(this, link));
    }

    public void addLinks(List<String> links) {
        if (links != null) {
            links.forEach(this::addLink);
        }
    }

    private void resetLinks() {
        this.links.clear();
    }

    public void removeLink(String link) {
        links.removeIf(item -> item.getLink().equals(link));
    }

    public void editNickname(String nickname) {
        profile.changeNickname(nickname);
    }

    public void editBio(String bio) {
        profile.changeBio(bio);
    }

    public void editLinks(List<String> links) {
        resetLinks();
        addLinks(links);
    }
}
