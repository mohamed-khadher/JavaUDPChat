package model;

public class Message {
	private Etudiant sender;
	private Etudiant reciever;
	private ConversationGroup recieverGroup;
	private String content;
	
	
	public Message(Etudiant sender, ConversationGroup recieverGroup, String content) {
		super();
		this.sender = sender;
		this.recieverGroup = recieverGroup;
		this.content = content;
	}
	
	
	public Message(Etudiant sender, Etudiant reciever, String content) {
		super();
		this.sender = sender;
		this.reciever = reciever;
		this.content = content;
	}
	
	public Etudiant getSender() {
		return sender;
	}
	public void setSender(Etudiant sender) {
		this.sender = sender;
	}
	public Etudiant getReciever() {
		return reciever;
	}
	public void setReciever(Etudiant reciever) {
		this.reciever = reciever;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}


	public ConversationGroup getRecieverGroup() {
		return recieverGroup;
	}


	public void setRecieverGroup(ConversationGroup recieverGroup) {
		this.recieverGroup = recieverGroup;
	}
	
}
