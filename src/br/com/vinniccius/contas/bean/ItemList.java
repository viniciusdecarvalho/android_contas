package br.com.vinniccius.contas.bean;

public class ItemList {

	private final int icon;
	private final String title;
	private final long counter;

	public ItemList(int icon, String title, long counter) {
		this.icon = icon;
		this.title = title;
		this.counter = counter;
	}
	
	public ItemList(String title) {
		this(-1, title, -1);
	}

	public String getTitle() {
		return title;
	}
	
	public int getIcon() {
		return icon;
	}
	
	public Long getCounter() {
		return counter;
	}
}
