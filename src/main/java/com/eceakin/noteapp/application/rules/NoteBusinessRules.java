package com.eceakin.noteapp.application.rules;

import org.springframework.stereotype.Service;

import com.eceakin.noteapp.repository.NoteRepository;
import com.eceakin.noteapp.repository.UserRepository;
import com.eceakin.noteapp.shared.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteBusinessRules {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    // Kullanıcı var mı?
    public void checkIfUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("User not found with id: " + userId);
        }
    }

    // Note var mı?
    public void checkIfNoteExists(Long noteId) {
        if (!noteRepository.existsById(noteId)) {
            throw new BusinessException("Note not found with id: " + noteId);
        }
    }

    // Başlık boş olamaz
    public void checkIfTitleIsNotBlank(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessException("Note title cannot be empty!");
        }
    }

    // Açıklama 5000 karakteri aşmasın
    public void checkIfDescriptionLengthValid(String description) {
        if (description != null && description.length() > 5000) {
            throw new BusinessException("Description cannot exceed 5000 characters!");
        }
    }

    // Tag sayısı çok fazla olmasın (örnek: max 10 tag)
    public void checkIfTagsValid(java.util.List<String> tags) {
        if (tags != null && tags.size() > 10) {
            throw new BusinessException("A note cannot have more than 10 tags!");
        }
    }
}