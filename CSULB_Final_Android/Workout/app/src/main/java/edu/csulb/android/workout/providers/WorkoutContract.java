package edu.csulb.android.workout.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public class WorkoutContract {

	static final String CONTENT_AUTHORITY = "edu.csulb.android.workout.providers";
	static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	static final String PATH_WORKOUTS = "workouts";
	static final String PATH_EXERCISE_TYPES = "exercise_types";
	static final String PATH_EXERCISES = "exercises";

	public interface WorkoutsColumns {
		/* Default columns _ID */
		String NAME = "workouts_name";
		String COLOR = "workouts_color";
	}

	public interface ExerciseTypesColumns {
		/* Default columns _ID */
		String ICON = "exercise_type_icon";
		String NAME = "exercise_type_name";
	}

	public interface ExercisesColumns {
		String TYPE_ID = "exercises_type_id";
		String NAME = "exercises_name";
		String ICON = "exercises_icon";
		String WORKOUT_ID = "exercises_workout_id";
		String COUNT = "exercises_count";
	}

	public static class WorkoutsTable implements WorkoutsColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORKOUTS).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.csulb.android.workout.workouts";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.csulb.android.workout.workouts";
		public static Uri buildUri(String id) {
			return CONTENT_URI.buildUpon().appendPath(id).build();
		}
		public static String getId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
	}

	public static class ExerciseTypesTable implements ExerciseTypesColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXERCISE_TYPES).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.csulb.android.workout.exercise_types";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.csulb.android.workout.exercise_types";
		public static Uri buildUri(String id) {
			return CONTENT_URI.buildUpon().appendPath(id).build();
		}
		public static String getId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
	}

	public static class ExercisesTable implements ExercisesColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXERCISES).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.csulb.android.workout.exercises";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.csulb.android.workout.exercises";
		public static Uri buildUri(String id) {
			return CONTENT_URI.buildUpon().appendPath(id).build();
		}
		public static String getId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
	}
}