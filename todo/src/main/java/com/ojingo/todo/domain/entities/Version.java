package com.ojingo.todo.domain.entities;

import java.security.Timestamp;
import java.util.UUID;

public class Version {
	
	private UUID id;

	private Note noteParent;
	
	private Note newNote;
	
	private Timestamp version;
	

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Note getNoteParent() {
		return noteParent;
	}

	public void setNoteParent(Note noteParent) {
		this.noteParent = noteParent;
	}

	public Note getNewNote() {
		return newNote;
	}

	public void setNewNote(Note newNote) {
		this.newNote = newNote;
	}

	public Timestamp getVersion() {
		return version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
	public static Version of(UUID id, Note noteParent, Note newNote) {
		Version version = new Version();
		
		version.id = id;
		version.noteParent = noteParent;
		version.newNote = newNote;
		
		return version;
	} 
	
	public static Version of(Note noteParent, Note newNote) {
		Version version = new Version();		
		
		version.noteParent = noteParent;
		version.newNote = newNote;
		
		return version;
	} 
}
