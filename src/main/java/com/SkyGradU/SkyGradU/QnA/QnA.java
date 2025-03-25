package com.SkyGradU.SkyGradU.QnA;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class QnA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String QTitle;
    private String QWriter;
    private Boolean Anonymity;

    @Column(columnDefinition = "TEXT")
    private String QContent;

    private LocalDateTime QDate;

    private Boolean Answered = false;

    @Column(columnDefinition = "TEXT")
    private String AContent;

    private LocalDateTime ADate;
}
