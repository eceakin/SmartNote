package com.eceakin.noteapp.application.manager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.eceakin.noteapp.application.dto.CreateNoteDto;
import com.eceakin.noteapp.application.dto.NoteDto;
import com.eceakin.noteapp.application.dto.UpdateNoteDto;
import com.eceakin.noteapp.application.service.NoteService;
import com.eceakin.noteapp.model.Note;
import com.eceakin.noteapp.model.User;
import com.eceakin.noteapp.repository.NoteRepository;
import com.eceakin.noteapp.repository.UserRepository;
import com.eceakin.noteapp.shared.mapper.ModelMapperService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteManager implements NoteService {

	private final NoteRepository noteRepository;
	private final ModelMapperService modelMapperService;
	private final UserRepository userRepository;

	@Override
	public NoteDto createNote(CreateNoteDto createNoteDto) {
		User user = findUserById(createNoteDto.getUserId());
		Note note = modelMapperService.forRequest().map(createNoteDto, Note.class);
		note.setId(null);
		note.setUser(user);
		Note savedNote = noteRepository.save(note);
		return modelMapperService.forResponse().map(savedNote, NoteDto.class);
	}

	@Override
	public NoteDto updateNote(Long id, UpdateNoteDto updateNoteDto) {
		Note note = findNoteById(id);
		note.setTitle(updateNoteDto.getTitle());
		note.setDescription(updateNoteDto.getDescription());
		Note savedNote = noteRepository.save(note);
		return modelMapperService.forResponse().map(savedNote, NoteDto.class);
	}

	@Override
	public Optional<NoteDto> getNoteById(Long id) {
		return this.noteRepository.findById(id).map(note -> modelMapperService.forResponse().map(note, NoteDto.class));
	}

	@Override
	public List<NoteDto> getNotesByUserId(Long id) {
		return this.noteRepository.findByUserIdOrderByCreatedAtDesc(id).stream()
				.map(note -> modelMapperService.forResponse().map(note, NoteDto.class)).collect(Collectors.toList());
	}

	@Override
	public List<NoteDto> getAllNotes() {
		List<Note> notes = noteRepository.findAll();
		return notes.stream().map(note -> this.modelMapperService.forResponse().map(note, NoteDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public void deleteNote(Long id) {
		this.noteRepository.deleteById(id);
	}

	@Override
	public List<NoteDto> searchNotesByTitle(Long userId, String title) {
		return noteRepository.findByUserIdAndTitleContaining(userId, title).stream()
				.map(note -> modelMapperService.forResponse().map(note, NoteDto.class)).collect(Collectors.toList());
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
	}

	private Note findNoteById(Long id) {
		return noteRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + id));
	}
}