package com.Universal_bot_jobs.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bot_jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String company;
    private String location;

    @Column(length = 2000)
    private String jobUrl;

    @Column(length = 2000)
    private String applyLink;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String source;
}