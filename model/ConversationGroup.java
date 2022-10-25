package model;

import java.util.ArrayList;
import java.util.List;

public class ConversationGroup {
	private String titre;
	private List<Etudiant> students;
	private List<Message> messages;
	
	public ConversationGroup() {};
	
	public ConversationGroup(String titre) {
		this.titre = titre;
		this.students = new ArrayList<Etudiant>();
		this.messages = new ArrayList<Message>();
	}
	
	public ConversationGroup(String titre, List<Etudiant> students, List<Message> messages) {
		super();
		this.titre = titre;
		this.students = students;
		this.messages = messages;
	}
	
	
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public List<Etudiant> getStudents() {
		return students;
	}
	public void setStudents(List<Etudiant> students) {
		this.students = students;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	public void addStudent(Etudiant e) {
		this.students.add(e);
	}
	
}
