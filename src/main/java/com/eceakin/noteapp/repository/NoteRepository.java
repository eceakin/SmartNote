package com.eceakin.noteapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eceakin.noteapp.model.Note;
import com.eceakin.noteapp.model.Priority;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
	List<Note> findByUserId(Long userId);

	List<Note> findByUserIdOrderByCreatedAtDesc(Long userId);

   
	
	@Query("SELECT DISTINCT n FROM Note n LEFT JOIN n.tags t WHERE n.user.id = :userId AND " +
	           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
	           "LOWER(t) LIKE LOWER(CONCAT('%', :query, '%')))")
	    List<Note> findByUserIdAndTitleOrTagsContaining(@Param("userId") Long userId, @Param("query") String query);
	List<Note> findByUserIdAndPriorityOrderByCreatedAtDesc(Long userId, Priority priority);

	// Priority’ye göre sıralama + aynı priority içinde tarihe göre
	@Query("SELECT n FROM Note n WHERE n.user.id = :userId ORDER BY n.priority DESC, n.createdAt DESC")
	List<Note> findByUserIdOrderByPriorityDescCreatedAtDesc(@Param("userId") Long userId);

}
// NEW: Add priority-based sorting
	//@Query("SELECT n FROM Note n WHERE n.user.id = :userId ORDER BY n.priority DESC, n.createdAt DESC")
	//List<Note> findByUserIdOrderByPriorityDescCreatedAtDesc(@Param("userId") Long userId);   
   // NEW: Filter by priority

/*
@Query("SELECT n FROM Note n WHERE n.user.id = :userId ORDER BY n.priority.level DESC, n.createdAt DESC")
  List<Note> findByUserIdOrderByPriorityLevelDescCreatedAtDesc(@Param("userId") Long userId);
  */
