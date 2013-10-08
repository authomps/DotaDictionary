package edu.mines.broomthompsondotadictionary;

/**
 * Class: Hero
 * Description: A container for all the information our program stores about each hero.
 *
 */
public class Hero {
	private long id;
	private String name, focus, attack, use, role, picture;
	
	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture_url) {
		this.picture = picture_url;
	}

	public long getId() {
		return id;
	}
	
	public String getFocus() {
		return focus;
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	public String getAttack() {
		return attack;
	}

	public void setAttack(String attack) {
		this.attack = attack;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
