package com.eceakin.noteapp.application.manager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.eceakin.noteapp.application.dto.CreateNoteDto;
import com.eceakin.noteapp.application.dto.NoteDto;
import com.eceakin.noteapp.application.dto.UpdateNoteDto;
import com.eceakin.noteapp.application.rules.NoteBusinessRules;
import com.eceakin.noteapp.application.service.AiSummaryService;
import com.eceakin.noteapp.application.service.NoteService;
import com.eceakin.noteapp.model.Note;
import com.eceakin.noteapp.model.Priority;
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
	private final NoteBusinessRules businessRules;

	private final AiSummaryService aiSummaryService;

	@Override
	public NoteDto createNote(CreateNoteDto createNoteDto) {
		this.businessRules.checkIfUserExists(createNoteDto.getUserId());
		this.businessRules.checkIfTitleIsNotBlank(createNoteDto.getTitle());
		this.businessRules.checkIfDescriptionLengthValid(createNoteDto.getDescription());
		this.businessRules.checkIfTagsValid(createNoteDto.getTags());

		User user = findUserById(createNoteDto.getUserId());
		Note note = modelMapperService.forRequest().map(createNoteDto, Note.class);
		note.setId(null);
		note.setUser(user);

		if (createNoteDto.getPriority() != null) {
			note.setPriority(createNoteDto.getPriority());
		} else {
			note.setPriority(Priority.MEDIUM);
		}
		Note savedNote = noteRepository.save(note);
		return modelMapperService.forResponse().map(savedNote, NoteDto.class);
	}

	@Override
	public NoteDto updateNote(Long id, UpdateNoteDto updateNoteDto) {
		this.businessRules.checkIfNoteExists(id);
		this.businessRules.checkIfTitleIsNotBlank(updateNoteDto.getTitle());
		this.businessRules.checkIfDescriptionLengthValid(updateNoteDto.getDescription());
		this.businessRules.checkIfTagsValid(updateNoteDto.getTags());

		Note note = findNoteById(id);
		note.setTitle(updateNoteDto.getTitle());
		note.setDescription(updateNoteDto.getDescription());
		note.setTags(updateNoteDto.getTags()); // <-- Etiket güncellemesi eklendi
		note.setPriority(updateNoteDto.getPriority());
		Note savedNote = noteRepository.save(note);
		return modelMapperService.forResponse().map(savedNote, NoteDto.class);
	}

	@Override
	public Optional<NoteDto> getNoteById(Long id) {
		this.businessRules.checkIfNoteExists(id);
		return this.noteRepository.findById(id).map(note -> modelMapperService.forResponse().map(note, NoteDto.class));
	}

	@Override
	public List<NoteDto> getNotesByUserId(Long id) {
		this.businessRules.checkIfUserExists(id);

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
		this.businessRules.checkIfNoteExists(id);
		this.noteRepository.deleteById(id);
	}

	@Override
	public List<NoteDto> searchNotes(Long userId, String query) {
		this.businessRules.checkIfUserExists(userId);
		if (query == null || query.trim().isEmpty()) {
			return getNotesByUserId(userId); // Sorgu boşsa kullanıcının tüm notlarını getir
		}
		String trimmedQuery = query.trim();
		List<Note> foundNotes = noteRepository.findByUserIdAndTitleOrTagsContaining(userId, trimmedQuery);
		return foundNotes.stream().map(note -> modelMapperService.forResponse().map(note, NoteDto.class))
				.collect(Collectors.toList());
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
	}

	private Note findNoteById(Long id) {
		return noteRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + id));
	}

	@Override
	public String summarizeNote(Long id) {
		Note note = findNoteById(id);

		// Combine title and description for summarization
		StringBuilder content = new StringBuilder();
		if (note.getTitle() != null && !note.getTitle().trim().isEmpty()) {
			content.append(note.getTitle()).append(". ");
		}
		if (note.getDescription() != null && !note.getDescription().trim().isEmpty()) {
			content.append(note.getDescription());
		}

		String textToSummarize = content.toString().trim();

		if (textToSummarize.isEmpty()) {
			return "No content available to summarize.";
		}

		return aiSummaryService.summarizeText(textToSummarize);
	}

	@Override
	public List<NoteDto> getNotesByUserIdOrderByPriority(Long userId) {
		return noteRepository.findByUserIdOrderByPriorityDescCreatedAtDesc(userId).stream()
				.map(note -> modelMapperService.forResponse().map(note, NoteDto.class)).collect(Collectors.toList());
	}

	@Override
	public List<NoteDto> getNotesByUserIdAndPriority(Long userId, Priority priority) {
		return this.noteRepository.findByUserIdAndPriorityOrderByCreatedAtDesc(userId, priority).stream()
				.map(note -> modelMapperService.forResponse().map(note, NoteDto.class)).collect(Collectors.toList());
	}
}