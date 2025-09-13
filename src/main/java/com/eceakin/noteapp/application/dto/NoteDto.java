
package com.eceakin.noteapp.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.eceakin.noteapp.model.Priority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDto {
    private Long id;
    private String title;
    private String description;
	private List<String> tags; // <-- Yeni eklendi
    private Priority priority;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String username;
}