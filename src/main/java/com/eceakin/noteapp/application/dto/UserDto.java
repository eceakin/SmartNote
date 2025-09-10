package com.eceakin.noteapp.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.eceakin.noteapp.model.Note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<NoteDto> notes;
}