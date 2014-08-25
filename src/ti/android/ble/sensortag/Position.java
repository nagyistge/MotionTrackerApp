package ti.android.ble.sensortag;

public class Position {
	public static int NULL = 0;
	public static int UPPER_LEFT_LEG = 1;
	public static int LOWER_LEFT_LEG = 2;
	public static int UPPER_RIGHT_LEG = 3;
	public static int LOWER_RIGHT_LEG = 4;
	public static int BODY = 5;
	private int value;

	private Position(int value) {
		this.value = value;
	}
	
	public static String getString(int value) {
		String string = "NULL";
		switch (value) {
		case 1:
			string = "Upper Left Leg";
			break;
		case 2:
			string = "Lower Left Leg";
			break;
		case 3:
			string = "Upper Right Leg";
			break;
		case 4:
			string = "Lower Right Leg";
			break;
		case 5:
			string = "Body";
			break;
		case 0:
		default:
			break;
		}
		return string;
	}
};
