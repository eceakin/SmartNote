package com.eceakin.noteapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eceakin.noteapp.model.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
	List<Note> findByUserId(Long userId);

	List<Note> findByUserIdOrderByCreatedAtDesc(Long userId);
	 @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.title LIKE %:title%")
	    List<Note> findByUserIdAndTitleContaining(@Param("userId") Long userId, @Param("title") String title);
}
