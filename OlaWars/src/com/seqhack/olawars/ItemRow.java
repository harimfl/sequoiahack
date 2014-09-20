package com.seqhack.olawars;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ItemRow {

	String userName;
	String olaPoints;
	String playerRank;
	Bitmap icon;
	String snuid;
	
	
	
	public ItemRow(String userName, String olaPoints, String snuid, String playerRank, Bitmap icon) {
		super();
		this.userName = userName;
		this.olaPoints = olaPoints;
		this.snuid = snuid;
		this.playerRank = playerRank;
		this.icon = icon;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String itemName) {
		this.userName = itemName;
	}
	public String getOlaPoints() {
		return olaPoints;
	}
	public void setOlaPoints(String olaPoints) {
		this.olaPoints = olaPoints;
	}
	public String getSnuid() {
		return snuid;
	}
	public void setSnUid(String snuid) {
		this.snuid = snuid;
	}
	public String getPlayerRank() {
		return playerRank;
	}
	public void setPlayerRank(String playerRank) {
		this.playerRank= playerRank;
	}
	public Bitmap getIcon() {
		return icon;
	}
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
	
}
