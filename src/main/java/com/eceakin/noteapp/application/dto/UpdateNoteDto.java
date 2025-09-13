package com.eceakin.noteapp.application.dto;

import java.util.ArrayList;
import java.util.List;

import com.eceakin.noteapp.model.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNoteDto {
	@NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;
    
    @Size(max = 5000, message = "Description cannot exceed 500 characters")
    private String description;
    
    private List<String> tags = new ArrayList<>();
    
    private Priority priority = Priority.MEDIUM;

}
