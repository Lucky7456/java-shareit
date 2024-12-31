package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.User;

import java.time.Instant;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String description;

    @OneToOne
    @JoinColumn(name = "requester_id")
    @ToString.Exclude
    private User requester;

    @Column
    private Instant created = Instant.now();
}
