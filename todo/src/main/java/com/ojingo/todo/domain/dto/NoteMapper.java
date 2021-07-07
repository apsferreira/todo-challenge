package com.ojingo.todo.domain.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ojingo.todo.domain.entities.Note;

@Mapper(componentModel = "cdi")
public interface NoteMapper {
	Note convertToNote(NoteDTO noteDTO);
	
	@Mapping(target = "user.id", source = "userId")
	@Mapping(target = "todo.id", source = "todoId")
	Note convertToNote(CreateNoteDTO createNoteDTO);
	
	Note convertToNote(UpdateNoteDTO updateNoteDTO);

	NoteDTO convertToNoteDTO(Note note);
}
