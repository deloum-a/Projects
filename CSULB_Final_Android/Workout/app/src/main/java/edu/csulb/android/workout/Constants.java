package edu.csulb.android.workout;

public class Constants {

	public static final boolean DEBUG_MODE = true;

	public static final String[] defaultExercises = {
			"Push-ups",
			"Squats",
			"Sit-ups",
			"Bench press"
	};

	public interface Preferences {
		String IS_FIRST_TIME = "PREF_IS_FIRST_TIME";
	}
}
