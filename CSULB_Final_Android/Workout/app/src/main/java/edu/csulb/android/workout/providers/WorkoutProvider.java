package edu.csulb.android.workout.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;

import edu.csulb.android.workout.Constants;
import edu.csulb.android.workout.utils.SelectionBuilder;

public class WorkoutProvider extends ContentProvider {

	static final String TAG = WorkoutProvider.class.getSimpleName();
	static final boolean DEBUG_MODE = Constants.DEBUG_MODE;

	static final String MANY = "/*";

	private WorkoutDatabase mOpenHelper;

	private UriMatcher mUriMatcher;

	static final int WORKOUTS = 100;
	static final int WORKOUTS_ID = 101;

	static final int EXERCISE_TYPES = 200;
	static final int EXERCISE_TYPES_ID = 201;

	static final int EXERCISES = 300;
	static final int EXERCISES_ID = 301;

	private UriMatcher buildUriMatcher(String authority) {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

		matcher.addURI(authority, WorkoutContract.PATH_WORKOUTS, WORKOUTS);
		matcher.addURI(authority, WorkoutContract.PATH_WORKOUTS + MANY, WORKOUTS_ID);
		matcher.addURI(authority, WorkoutContract.PATH_EXERCISE_TYPES, EXERCISE_TYPES);
		matcher.addURI(authority, WorkoutContract.PATH_EXERCISE_TYPES + MANY, EXERCISE_TYPES_ID);
		matcher.addURI(authority, WorkoutContract.PATH_EXERCISES, EXERCISES);
		matcher.addURI(authority, WorkoutContract.PATH_EXERCISES + MANY, EXERCISES_ID);

		return matcher;
	}

	@Override
	public boolean onCreate() {
		final Context context = getContext();
		mOpenHelper = new WorkoutDatabase(context);
		mUriMatcher = buildUriMatcher(WorkoutContract.CONTENT_AUTHORITY);
		return true;
	}

	@Override
	public String getType(Uri uri) {
		final int match = mUriMatcher.match(uri);
		switch (match) {

			case WORKOUTS:
				return WorkoutContract.WorkoutsTable.CONTENT_TYPE;
			case WORKOUTS_ID:
				return WorkoutContract.WorkoutsTable.CONTENT_ITEM_TYPE;

			case EXERCISE_TYPES:
				return WorkoutContract.ExerciseTypesTable.CONTENT_TYPE;
			case EXERCISE_TYPES_ID:
				return WorkoutContract.ExerciseTypesTable.CONTENT_ITEM_TYPE;

			case EXERCISES:
				return WorkoutContract.ExercisesTable.CONTENT_TYPE;
			case EXERCISES_ID:
				return WorkoutContract.ExercisesTable.CONTENT_ITEM_TYPE;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		if (DEBUG_MODE) {
			Log.v(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ", sel=" + selection + ")");
		}
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final SelectionBuilder builder = buildSelection(uri);
		return builder.where(selection, selectionArgs).query(db, projection, sortOrder);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (DEBUG_MODE) {
			Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
		}
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = mUriMatcher.match(uri);
		long id;
		switch (match) {

			case WORKOUTS:
				id = db.insertOrThrow(WorkoutDatabase.Tables.WORKOUTS, null, values);
				return WorkoutContract.WorkoutsTable.buildUri(String.valueOf(id));

			case EXERCISE_TYPES:
				id = db.insertOrThrow(WorkoutDatabase.Tables.EXERCISE_TYPES, null, values);
				return WorkoutContract.ExerciseTypesTable.buildUri(String.valueOf(id));

			case EXERCISES:
				id = db.insertOrThrow(WorkoutDatabase.Tables.EXERCISES, null, values);
				return WorkoutContract.ExercisesTable.buildUri(String.valueOf(id));

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		if (DEBUG_MODE) {
			Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
		}
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSelection(uri);
		return builder.where(selection, selectionArgs).update(db, values);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (DEBUG_MODE) {
			Log.v(TAG, "delete(uri=" + uri + ")");
		}
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSelection(uri);
		return builder.where(selection, selectionArgs).delete(db);
	}

	private SelectionBuilder buildSelection(Uri uri) {
		final SelectionBuilder builder = new SelectionBuilder();
		final int match = mUriMatcher.match(uri);
		switch (match) {

			case WORKOUTS: {
				return builder.table(WorkoutDatabase.Tables.WORKOUTS);
			}
			case WORKOUTS_ID: {
				final String id = WorkoutContract.WorkoutsTable.getId(uri);
				return builder.table(WorkoutDatabase.Tables.WORKOUTS).where(WorkoutContract.WorkoutsTable._ID + "=?", id);
			}

			case EXERCISE_TYPES: {
				return builder.table(WorkoutDatabase.Tables.EXERCISE_TYPES);
			}
			case EXERCISE_TYPES_ID: {
				final String id = WorkoutContract.ExerciseTypesTable.getId(uri);
				return builder.table(WorkoutDatabase.Tables.EXERCISE_TYPES).where(WorkoutContract.ExerciseTypesTable._ID + "=?", id);
			}

			case EXERCISES: {
				return builder.table(WorkoutDatabase.Tables.EXERCISES);
			}
			case EXERCISES_ID: {
				final String id = WorkoutContract.ExercisesTable.getId(uri);
				return builder.table(WorkoutDatabase.Tables.EXERCISES).where(WorkoutContract.ExercisesTable._ID + "=?", id);
			}

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

}