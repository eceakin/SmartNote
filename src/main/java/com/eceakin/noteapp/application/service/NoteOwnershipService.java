package com.eceakin.noteapp.application.service;

import org.springframework.stereotype.Service;

import com.eceakin.noteapp.repository.NoteRepository;
import com.eceakin.noteapp.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteOwnershipService {
	 private final NoteRepository noteRepository;
	    
	    public boolean isOwnerOfNote(Long noteId) {
	        try {
	            Long currentUserId = SecurityUtils.getCurrentUserId();
	            return noteRepository.findById(noteId)
	                    .map(note -> note.getUser().getId().equals(currentUserId))
	                    .orElse(false);
	        } catch (Exception e) {
	            return false;
	        }
	    }
}