package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateTimeToTimestampConverter;
import by.sviryd.engvoc.type.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@NamedEntityGraph(
        name = "post-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("pairs"),
        }
)

@ToString(of = {"id", "username", "email", "social", "active"})
@EqualsAndHashCode(of = {"username", "email", "social", "active"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(value = {"authorCards", "clientCards", "dictionaries"})
@Entity
@Table(name = "usr", uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Id.class)
    private Long id;

    @Length (max = 100)
    @NotBlank
    @NonNull
    @Column(length = 100)
    @JsonView(Views.Username.class)
    private String username;

    private String password;

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    @JsonView(Views.Active.class)
    private boolean active;

    @Column(length = 20)
    @JsonView(Views.Social.class)
    private String social;

    @Column(length = 255)
    @JsonView(Views.Sub.class)
    private String sub;

    @UpdateTimestamp
    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    @JsonView(Views.LastModifiedLDT.class)
    private LocalDateTime lastModifiedLDT;

    @Length(max = 320)
    @Column(length = 320)
    @JsonView(Views.Email.class)
    private String email;

    @Column(length = 255)
    @JsonView(Views.Token.class)
    private String token;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Card> authorCards = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Card> clientCards = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Dictionary> dictionaries = new ArrayList<>();


    @ElementCollection(targetClass = LangLocalePair.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_pair",
            joinColumns = @JoinColumn(name = "user_id"))
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @CollectionId(columns = {@Column(name = "pair_id")}, generator = "uuid2", type = @Type(type = "uuid-char"))
    @JsonView(Views.LangLocalePairs.class)
    private List<LangLocalePair> pairs = new ArrayList<>();

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

    public boolean addPair(LangLocalePair pair) {
        return pairs.add(pair);
    }
}
