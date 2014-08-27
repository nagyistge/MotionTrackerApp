package ti.android.ble.sensortag;

public class Position {
	public static final int NULL = 0;
	public static final int UPPER_LEFT_LEG = 1;
	public static final int LOWER_LEFT_LEG = 2;
	public static final int UPPER_RIGHT_LEG = 3;
	public static final int LOWER_RIGHT_LEG = 4;
	public static final int BODY = 5;
	private int value;

	private Position(int value) {
		this.value = value;
	}
	
	public static String getString(int value) {
		String string = "NULL";
		switch (value) {
		case UPPER_LEFT_LEG:
			string = "Upper Left Leg";
			break;
		case LOWER_LEFT_LEG:
			string = "Lower Left Leg";
			break;
		case UPPER_RIGHT_LEG:
			string = "Upper Right Leg";
			break;
		case LOWER_RIGHT_LEG:
			string = "Lower Right Leg";
			break;
		case BODY:
			string = "Body";
			break;
		case 0:
		default:
			break;
		}
		return string;
	}
};
