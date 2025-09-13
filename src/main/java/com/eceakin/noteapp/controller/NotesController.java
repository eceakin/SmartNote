

package com.eceakin.noteapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eceakin.noteapp.application.dto.CreateNoteDto;
import com.eceakin.noteapp.application.dto.NoteDto;
import com.eceakin.noteapp.application.dto.SummaryResponse;
import com.eceakin.noteapp.application.dto.UpdateNoteDto;
import com.eceakin.noteapp.application.service.NoteOwnershipService;
import com.eceakin.noteapp.application.service.NoteService;
import com.eceakin.noteapp.model.Priority;
import com.eceakin.noteapp.security.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NotesController {
	private final NoteOwnershipService noteOwnershipService;
	private final NoteService noteService;

	@PostMapping
	public ResponseEntity<NoteDto> createNote(@Valid @RequestBody CreateNoteDto createNoteDto) {
		createNoteDto.setUserId(SecurityUtils.getCurrentUserId());
		NoteDto noteDto = noteService.createNote(createNoteDto);
		return new ResponseEntity<>(noteDto, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<NoteDto> getNoteById(@PathVariable Long id) {
		if (!noteOwnershipService.isOwnerOfNote(id)) {
			return ResponseEntity.notFound().build();
		}
		return noteService.getNoteById(id).map(note -> ResponseEntity.ok(note))
				.orElse(ResponseEntity.notFound().build());
	}

	/*@GetMapping
	public ResponseEntity<List<NoteDto>> getMyNotes(@RequestParam(required = false) String sortBy) {
		Long currentUserId = SecurityUtils.getCurrentUserId();
		List<NoteDto> notes;

		// Frontend'de sıralama işi halledildiği için, backend'den sadece tarihe göre sıralanmış olarak alıyoruz.
		// Frontend'de "priority" seçeneği seçilirse, React kendi sıralamasını yapacak.
		notes = this.noteService.getNotesByUserId(currentUserId); // Varsayılan olarak tarihe göre sıralı getir
		
		return ResponseEntity.ok(notes);
	}
 */ 
	@GetMapping
	public ResponseEntity<List<NoteDto>> getMyNotes(
	        @RequestParam(required = false, defaultValue = "date") String sortBy,
	        @RequestParam(required = false) String priority
	) {
	    Long currentUserId = SecurityUtils.getCurrentUserId();
	    List<NoteDto> notes;

	    // Eğer priority parametresi geldiyse filtre uygula
	    if (priority != null && !priority.isEmpty()) {
	        Priority priorityEnum = Priority.fromString(priority);
	        notes = noteService.getNotesByUserIdAndPriority(currentUserId, priorityEnum);
	    } else {
	        // Priority verilmediyse sadece sıralamayı uygula
	        if ("priority".equalsIgnoreCase(sortBy)) {
	            // Backend priority'ye göre sıralama yapmak istersen:
	            notes = noteService.getNotesByUserIdOrderByPriority(currentUserId);
	        } else {
	            // Default: createdAt’e göre
	            notes = noteService.getNotesByUserId(currentUserId);
	        }
	    }

	    return ResponseEntity.ok(notes);
	}

	// Belirli bir öncelikteki notları getiren metot
	@GetMapping("/by-priority")
	public ResponseEntity<List<NoteDto>> getNotesByPriority(@RequestParam String priority) {
		Long currentUserId = SecurityUtils.getCurrentUserId();
		
		// Priority.fromString metodunu kullanarak güvenli dönüşüm yap
		Priority priorityEnum = Priority.fromString(priority);
		
		List<NoteDto> notes = noteService.getNotesByUserIdAndPriority(currentUserId, priorityEnum);
		return ResponseEntity.ok(notes);
	}

	@PutMapping("/{id}")
	public ResponseEntity<NoteDto> updateNote(@PathVariable Long id, @Valid @RequestBody UpdateNoteDto updateNoteDto) {
		if (!noteOwnershipService.isOwnerOfNote(id)) {
			return ResponseEntity.notFound().build();
		}
		NoteDto noteDto = this.noteService.updateNote(id, updateNoteDto);
		return ResponseEntity.ok(noteDto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
		if (!noteOwnershipService.isOwnerOfNote(id)) {
			return ResponseEntity.notFound().build();
		}
		this.noteService.deleteNote(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/summarize")
	public ResponseEntity<SummaryResponse> summarizeNote(@PathVariable Long id) {
		if (!noteOwnershipService.isOwnerOfNote(id)) {
			return ResponseEntity.notFound().build();
		}
		try {
			String summary = noteService.summarizeNote(id);
			SummaryResponse response = new SummaryResponse(summary);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			SummaryResponse errorResponse = new SummaryResponse("Error generating summary: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping("/search")
	public ResponseEntity<List<NoteDto>> searchMyNotes(@RequestParam String query) {
		Long currentUserId = SecurityUtils.getCurrentUserId();
		List<NoteDto> notes = this.noteService.searchNotes(currentUserId, query);
		return ResponseEntity.ok(notes);
	}
}

/* 
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eceakin.noteapp.application.dto.CreateNoteDto;
import com.eceakin.noteapp.application.dto.NoteDto;
import com.eceakin.noteapp.application.dto.SummaryResponse;
import com.eceakin.noteapp.application.dto.UpdateNoteDto;
import com.eceakin.noteapp.application.service.NoteOwnershipService;
import com.eceakin.noteapp.application.service.NoteService;
import com.eceakin.noteapp.model.Priority;
import com.eceakin.noteapp.security.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NotesController {
	private final NoteOwnershipService noteOwnershipService;
	private final NoteService noteService;

	@PostMapping
	public ResponseEntity<NoteDto> createNote(@Valid @RequestBody CreateNoteDto createNoteDto) {
		// security
		createNoteDto.setUserId(SecurityUtils.getCurrentUserId());
		NoteDto noteDto = noteService.createNote(createNoteDto);
		return new ResponseEntity<>(noteDto, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<NoteDto> getNoteById(@PathVariable Long id) {

		if (!noteOwnershipService.isOwnerOfNote(id)) {
			return ResponseEntity.notFound().build();

		}
		return noteService.getNoteById(id).map(note -> ResponseEntity.ok(note))
				.orElse(ResponseEntity.notFound().build());

	}

	 @GetMapping
	    public ResponseEntity<List<NoteDto>> getMyNotes(@RequestParam(required = false) String sortBy) {
	        Long currentUserId = SecurityUtils.getCurrentUserId();
	        List<NoteDto> notes;

	        // sortBy parametresiyle sıralama seçeneği sunuluyor
	        if ("priority".equalsIgnoreCase(sortBy)) {
	            notes = this.noteService.getNotesByUserIdSortedByPriority(currentUserId);
	        } else {
	            notes = this.noteService.getNotesByUserId(currentUserId);
	        }
	        return ResponseEntity.ok(notes);
	    }
	   // NEW: Get notes by specific priority
	   @GetMapping("/by-priority")
	    public ResponseEntity<List<NoteDto>> getNotesByPriority(@RequestParam String priority) {
	        Long currentUserId = SecurityUtils.getCurrentUserId();
	        
	        // Priority.fromString metodunu kullanarak güvenli dönüşüm yap
	        Priority priorityEnum = Priority.fromString(priority);
	        
	        List<NoteDto> notes = noteService.getNotesByUserIdAndPriority(currentUserId, priorityEnum);
	        return ResponseEntity.ok(notes);
	    }
	@PutMapping("/{id}")
	public ResponseEntity<NoteDto> updateNote(@PathVariable Long id, @Valid @RequestBody UpdateNoteDto updateNoteDto) {
		if (!noteOwnershipService.isOwnerOfNote(id)) {
			return ResponseEntity.notFound().build();
		}

		NoteDto noteDto = this.noteService.updateNote(id, updateNoteDto);
		return ResponseEntity.ok(noteDto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
		if (!noteOwnershipService.isOwnerOfNote(id)) {
			return ResponseEntity.notFound().build();
		}
		this.noteService.deleteNote(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/summarize")
	public ResponseEntity<SummaryResponse> summarizeNote(@PathVariable Long id) {
		if (!noteOwnershipService.isOwnerOfNote(id)) {
			return ResponseEntity.notFound().build();
		}

		try {
			String summary = noteService.summarizeNote(id);
			SummaryResponse response = new SummaryResponse(summary);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			SummaryResponse errorResponse = new SummaryResponse("Error generating summary: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping("/search")

	public ResponseEntity<List<NoteDto>> searchMyNotes(@RequestParam String query) {
		Long currentUserId = SecurityUtils.getCurrentUserId();
		List<NoteDto> notes = this.noteService.searchNotes(currentUserId, query);
		return ResponseEntity.ok(notes);
	}

} */ 
