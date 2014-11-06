package com.poiseinteractive.smartexpire.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Card {

	public enum Purpose {
		FOR_WORK ("for work"),
		FOR_ME ("for me");
		
		private final String name;
		
		private Purpose (String s) {
			name = s;
		}
		
		public boolean equalsName(String otherName) {
			return (otherName == null)? false:name.equals(otherName);
		}

		public static Purpose fromString(String text) {
			if (text != null) {
				for (Purpose p : Purpose.values()) {
					if (p.equalsName(text)) {
						return p;
					}
				}
			}
			return null;
		}

		public String toString() {
			return name;
		}
	};

	public enum Category {
		CREDIT_CARD ("Credit Card"),
		DEBIT_CARD ("Debit Card"),
		ATM_CARD ("ATM Card"),
		CHARGE_CARD ("Charge Card"),
		FLEET_CARD ("Fleet Card"),
		NONE_OF_THE_ABOVE ("None of the above");

		private final String name;

		private Category (String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null)? false:name.equals(otherName);
		}

		public static Category fromString(String text) {
			if (text != null) {
				for (Category c : Category.values()) {
					if (c.equalsName(text)) {
						return c;
					}
				}
			}
			return null;
		}

		public String toString() {
			return name;
		}
	}

	public enum NotifPeriod {
		ONE_WEEK ("1 Week before"),
		TWO_WEEKS ("2 Weeks before"),
		ONE_MONTH ("1 Month before"),
		TWO_MONTHS ("2 Months before"),
		THREE_MONTHS ("3 Months before"),
		NO_NOTIFICATION ("No Notification");

		private final String name;

		private NotifPeriod (String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null)? false:name.equals(otherName);
		}
		
		public static NotifPeriod fromString(String text) {
			if (text != null) {
				for (NotifPeriod np : NotifPeriod.values()) {
					if (np.equalsName(text)) {
						return np;
					}
				}
			}
			return null;
		}

		public String toString() {
			return name;
		}
	}

	String id;
	String label;
	Purpose purpose;
	Category category;
	NotifPeriod notifPeriod;
	Date expiryDate;

	public Card(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Purpose getPurpose() {
		return purpose;
	}

	public void setPurpose(Purpose purpose) {
		this.purpose = purpose;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public NotifPeriod getNotifPeriod() {
		return notifPeriod;
	}

	public void setNotifPeriod(NotifPeriod notifPeriod) {
		this.notifPeriod = notifPeriod;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}
	
	public String getExpiryString() {
		DateFormat df = new SimpleDateFormat("MM/yy", Locale.getDefault());
		return df.format(expiryDate);
	}

	public void setExpiryDate(String expiryDate) {
		DateFormat df = new SimpleDateFormat("MM/yy", Locale.getDefault());
		Date date;
		try {
			date = df.parse(expiryDate);
			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.setTime(date);
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		    cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		    cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		    cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		    
		    this.expiryDate = cal.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

}
