package edu.csulb.android.workout.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import edu.csulb.android.workout.Constants;

public class WorkoutDatabase extends SQLiteOpenHelper {
	static final String TAG = WorkoutDatabase.class.getSimpleName();
	static final boolean DEBUG_MODE = Constants.DEBUG_MODE;

	private static final String DATABASE_NAME = "workout.db";
	private static final int DATABASE_VERSION = 2;

	interface Tables {
		String WORKOUTS = "workouts";
		String EXERCISE_TYPES = "exercise_types";
		String EXERCISES = "exercises";
	}

	public WorkoutDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (DEBUG_MODE) {
			Log.i(TAG, "onCreate database");
		}

		db.execSQL("CREATE TABLE `" + Tables.WORKOUTS + "` ("
				+ "`" + BaseColumns._ID + "` INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "`" + WorkoutContract.WorkoutsTable.NAME + "` TEXT, "
				+ "`" + WorkoutContract.WorkoutsTable.COLOR + "` TEXT, "
				+ "UNIQUE (`" + BaseColumns._ID + "`) ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE `" + Tables.EXERCISE_TYPES + "` ("
				+ "`" + BaseColumns._ID + "` INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "`" + WorkoutContract.ExerciseTypesTable.ICON + "` INTEGER, "
				+ "`" + WorkoutContract.ExerciseTypesTable.NAME + "` TEXT, "
				+ "UNIQUE (`" + BaseColumns._ID + "`) ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE `" + Tables.EXERCISES + "` ("
				+ "`" + BaseColumns._ID + "` INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "`" + WorkoutContract.ExercisesTable.TYPE_ID + "` INTEGER, "
				+ "`" + WorkoutContract.ExercisesTable.WORKOUT_ID + "` INTEGER, "
				+ "`" + WorkoutContract.ExercisesTable.ICON + "` INTEGER, "
				+ "`" + WorkoutContract.ExercisesTable.NAME + "` TEXT, "
				+ "`" + WorkoutContract.ExercisesTable.COUNT + "` INTEGER, "
				+ "UNIQUE (`" + BaseColumns._ID + "`) ON CONFLICT REPLACE)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (DEBUG_MODE) {
			Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
		}

		if (oldVersion != DATABASE_VERSION) {
			Log.w(TAG, "Destroying old data during upgrade");

			db.execSQL("DROP TABLE IF EXISTS " + Tables.WORKOUTS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.EXERCISE_TYPES);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.EXERCISES);

			onCreate(db);
		}
	}

}