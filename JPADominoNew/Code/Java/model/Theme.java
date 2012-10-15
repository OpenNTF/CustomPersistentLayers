package model;

import java.util.ArrayList;
import java.util.Set;

import persistence.annotation.DocumentReferences;
import persistence.annotation.DominoEntity;
import persistence.annotation.DominoProperty;
import persistence.annotation.resource.CascadeType;
import persistence.annotation.resource.FetchType;

import model.notes.ModelBase;

@DominoEntity(formName = "Theme", viewName = "Theme", intercept = true)
public class Theme extends ModelBase {

	/**
	 * @author weihang chen
	 */
	private static final long serialVersionUID = 2471594683085308276L;
	@DominoProperty(itemName = "ThemeName")
	private String themeName;

	@DominoProperty(itemName = "ThemeType")
	private int themeType;
	// eager, collection concrete class, with cascade
	@DocumentReferences(fetch = FetchType.LAZY, foreignKey = "unid", viewName = "CSS", cascade = { CascadeType.SAVE_UPDATE })
	private ArrayList<CSS> CSSList2;
	// lazy, collection interface, without cascade
	@DocumentReferences(fetch = FetchType.EAGER, foreignKey = "unid", viewName = "CSS")
	private Set<CSS> CSSList1;
	

	public Set<CSS> getCSSList1() {
		return CSSList1;
	}

	public void setCSSList1(Set<CSS> list1) {
		CSSList1 = list1;
	}

	public Theme() {
		System.out.println("am i being invoked? empty constructor for class theme");
	}

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public Theme(Object doc) {
		super(doc);
	}

	public int getThemeType() {
		return themeType;
	}

	public void setThemeType(int themeType) {
		this.themeType = themeType;
	}

	public ArrayList<CSS> getCSSList2() {
		return CSSList2;
	}

	public void setCSSList2(ArrayList<CSS> list2) {
		CSSList2 = list2;
	}
	
}
