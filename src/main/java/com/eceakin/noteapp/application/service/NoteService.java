package com.eceakin.noteapp.application.service;

import java.util.List;
import java.util.Optional;

import com.eceakin.noteapp.application.dto.CreateNoteDto;
import com.eceakin.noteapp.application.dto.NoteDto;
import com.eceakin.noteapp.application.dto.UpdateNoteDto;

public interface NoteService {

	NoteDto createNote(CreateNoteDto createNoteDto);
	List<NoteDto> getAllNotes();
	Optional<NoteDto> getNoteById(Long id);
	NoteDto updateNote(Long id,UpdateNoteDto updateNoteDto);
	void deleteNote(Long id);

	
	List<NoteDto> getNotesByUserId(Long id);
	
	
	List<NoteDto> searchNotesByTitle(Long userId,String title);
}
