package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateTimeToTimestampConverter;
import by.sviryd.engvoc.type.Role;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@ToString(of = {"id", "username", "password", "email", "active"})
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "usr")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails, Serializable {
    public static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, unique = true)
    private String username;

    private String password;

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    @JsonView(Views.Active.class)
    private boolean active;

    // oAoth2 (google)
    @Column(length = 255)
    private String sub;

    @UpdateTimestamp
    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    private LocalDateTime lastModifiedLDT;

    @Column(length = 320)
    @Length(max = 320)
    private String email;

    @Column(length = 255)
    private String token;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

//    @OneToMany(mappedBy = "authorCard", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private List<Card> cards = new ArrayList<>();
//
//    @OneToMany(mappedBy = "authorDictionary", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private List<Dictionary> dictionaries = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_subscribtions",
            joinColumns = {@JoinColumn(name = "channel_id")},
            inverseJoinColumns = {@JoinColumn(name = "subscriber_id")})
    private Set<User> subscribers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_subscribtions",
            joinColumns = {@JoinColumn(name = "subscriber_id")},
            inverseJoinColumns = {@JoinColumn(name = "channel_id")})
    private Set<User> subscriptions = new HashSet<>();

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
