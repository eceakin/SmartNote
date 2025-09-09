package com.eceakin.noteapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eceakin.noteapp.application.dto.CreateNoteDto;
import com.eceakin.noteapp.application.dto.NoteDto;
import com.eceakin.noteapp.application.dto.UpdateNoteDto;
import com.eceakin.noteapp.application.service.NoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NotesController {
	private final NoteService noteService;
	
	@PostMapping
	public ResponseEntity<NoteDto> createNote(@Valid @RequestBody CreateNoteDto createNoteDto){
		// security
		NoteDto noteDto = noteService.createNote(createNoteDto);
		return new ResponseEntity<>(noteDto,HttpStatus.CREATED);
	}
	  @GetMapping("/{id}")
	  public ResponseEntity<NoteDto> getNoteById(@PathVariable Long id){
		  return noteService.getNoteById(id).map(
				  note->ResponseEntity.ok(note)
				  ).orElse(ResponseEntity.notFound().build());
				  
	  }
	 /*  
	  @GetMapping
	  public ResponseEntity<List<NoteDto>> getMyNotes(){
		
	  } */ 
	  
	  @PutMapping("/{id}")
	    public ResponseEntity<NoteDto> updateNote(@PathVariable Long id, 
	                                                    @Valid @RequestBody UpdateNoteDto updateNoteDto) {
	        // Check if user owns this todo list
	      
	        NoteDto noteDto = this.noteService.updateNote(id, updateNoteDto);
	        return ResponseEntity.ok(noteDto);
	    }
	  
	  @DeleteMapping("/{id}")
	  public ResponseEntity<Void> deleteNote(@PathVariable Long id){
		  //securty
		  this.noteService.deleteNote(id);
		  return ResponseEntity.notFound().build();
	  }
	  
	/*   @GetMapping("/search")
	  public ResponseEntity<List<NoteDto>> searchMyNotesByTitle(@RequestParam String title){
		  //security
		  return ResponseEntity.ok(no)
	  } */ 
	  
	
}
