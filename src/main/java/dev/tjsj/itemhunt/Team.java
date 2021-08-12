package dev.tjsj.itemhunt;

import java.util.List;
import java.util.ArrayList;

public class Team {
	private String name;
	private int score = 0;
	private List<String> members = new ArrayList<>();

	// Constructor
	public Team(String name) { this.name = name; }

	// name set/get
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	// score set/get
	public int getScore() { return score; }
	public void addScore(int increase) { score += increase; }
	public void setScore(int score) { this.score = score; }

	// members set/get
	public List<String> getMembers() { return members; }
	public void addMember(String username) { members.add(username); }
	public void removeMember(String username) { members.remove(username); }
	public void setMembers(List<String> members) { this.members = members; }
}
