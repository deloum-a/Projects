package edu.csulb.android.workout.activities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.csulb.android.workout.R;
import edu.csulb.android.workout.adapters.NewExerciseAdapter;
import edu.csulb.android.workout.models.Exercise;
import edu.csulb.android.workout.providers.WorkoutContract;

public class NewWorkoutActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

	public static final String EXTRA_WORKOUT_ID = "EXTRA_WORKOUT_ID";

	private static final int WORKOUT_LOADER_ID = 0;
	private static final int DEFAULT_EXERCISES_LOADER_ID = 1;
	private static final int EXERCISES_LOADER_ID = 2;

	private long mWorkoutId;
	private NewExerciseAdapter mAdapter;
	private List<Exercise> mExercises;

	private EditText mWorkoutName;
	private ListView mExercisesList;
	private EditText mNewExerciseName;
	private View mNewExerciseButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_workout);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mWorkoutName = (EditText) findViewById(R.id.new_workout_name);
		mExercisesList = (ListView) findViewById(R.id.new_workout_list);
		mNewExerciseName = (EditText) findViewById(R.id.new_exercise_new_text);
		mNewExerciseButton = findViewById(R.id.new_exercise_new_button);

		registerForContextMenu(mExercisesList);
		mNewExerciseButton.setOnClickListener(this);

		mExercises = new ArrayList<>();
		mWorkoutId = getIntent().getLongExtra(EXTRA_WORKOUT_ID, -1);
		final LoaderManager loaderManager = getSupportLoaderManager();

		if (mWorkoutId != -1) {
			loaderManager.restartLoader(WORKOUT_LOADER_ID, null, this);
			loaderManager.restartLoader(EXERCISES_LOADER_ID, null, this);
		}
		loaderManager.restartLoader(DEFAULT_EXERCISES_LOADER_ID, null, this);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.new_workout_list) {
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			if (mAdapter != null) {
				Cursor cursor = (Cursor) mAdapter.getItem(info.position);
				if (cursor != null) {
					menu.setHeaderTitle(cursor.getString(cursor.getColumnIndex(WorkoutContract.ExerciseTypesTable.NAME)));
				}
			}

			menu.add(Menu.NONE, 0, 0, "Delete");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case 0: // Delete

				final List<Exercise> exercises = mAdapter.getExercises();
				Exercise exercise = null;
				for (Exercise e : exercises) {
					if (e.id == mAdapter.getItemId(info.position)) {
						exercise = e;
					}
				}
				exercises.remove(exercise);
				mAdapter.setExercises(exercises);

				final ContentResolver contentResolver = getContentResolver();
				String where = WorkoutContract.ExerciseTypesTable._ID + "=?";
				String[] selectionArgs = {String.valueOf(mAdapter.getItemId(info.position))};
				contentResolver.delete(WorkoutContract.ExerciseTypesTable.CONTENT_URI, where, selectionArgs);

				where = WorkoutContract.ExercisesTable.TYPE_ID + "=?";
				contentResolver.delete(WorkoutContract.ExercisesTable.CONTENT_URI, where, selectionArgs);

				final LoaderManager loaderManager = getSupportLoaderManager();
				loaderManager.restartLoader(DEFAULT_EXERCISES_LOADER_ID, null, this);
				break;
		}
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
			case WORKOUT_LOADER_ID: {
				final String selection = WorkoutContract.ExercisesTable._ID + "=?";
				final String[] selectionArgs = {String.valueOf(mWorkoutId)};
				return new CursorLoader(this, WorkoutContract.WorkoutsTable.CONTENT_URI, null, selection, selectionArgs, null);
			}

			case DEFAULT_EXERCISES_LOADER_ID: {
				return new CursorLoader(this, WorkoutContract.ExerciseTypesTable.CONTENT_URI, null, null, null, null);
			}

			case EXERCISES_LOADER_ID: {
				final String selection = WorkoutContract.ExercisesTable.WORKOUT_ID + "=?";
				final String[] selectionArgs = {String.valueOf(mWorkoutId)};
				return new CursorLoader(this, WorkoutContract.ExercisesTable.CONTENT_URI, null, selection, selectionArgs, null);
			}
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (loader == null || cursor == null) {
			return;
		}

		switch (loader.getId()) {
			case WORKOUT_LOADER_ID:
				if (cursor.moveToFirst()) {
					String title = cursor.getString(cursor.getColumnIndex(WorkoutContract.WorkoutsTable.NAME));
					mWorkoutName.setText(title);
					setTitle(title);
				}
				break;

			case EXERCISES_LOADER_ID:
				if (cursor.moveToFirst()) {
					do {
						final Exercise exercise = new Exercise();
						exercise.id = cursor.getInt(cursor.getColumnIndex(WorkoutContract.ExercisesColumns.TYPE_ID));
						exercise.iconResId = cursor.getInt(cursor.getColumnIndex(WorkoutContract.ExercisesColumns.ICON));
						exercise.name = cursor.getString(cursor.getColumnIndex(WorkoutContract.ExercisesColumns.NAME));
						exercise.count = cursor.getInt(cursor.getColumnIndex(WorkoutContract.ExercisesColumns.COUNT));
						mExercises.add(exercise);
					} while (cursor.moveToNext());

					if (mAdapter != null) {
						mAdapter.setExercises(mExercises);
					}
				}
				break;

			case DEFAULT_EXERCISES_LOADER_ID:
				cursor.moveToFirst();
				if (mAdapter == null) {
					mAdapter = new NewExerciseAdapter(this, cursor);
					mExercisesList.setAdapter(mAdapter);
					mAdapter.setExercises(mExercises);
				} else {
					mAdapter.swapCursor(cursor);
				}
				break;

		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.new_exercise_new_button) {
			String exerciseName = mNewExerciseName.getText().toString();
			if (!exerciseName.isEmpty()) {
				final ContentResolver contentResolver = getContentResolver();
				final ContentValues contentValues = new ContentValues();
				contentValues.put(WorkoutContract.ExerciseTypesTable.NAME, exerciseName);
				final Uri uri = contentResolver.insert(WorkoutContract.ExerciseTypesTable.CONTENT_URI, contentValues);

				final LoaderManager loaderManager = getSupportLoaderManager();
				loaderManager.restartLoader(DEFAULT_EXERCISES_LOADER_ID, null, this);

				mNewExerciseName.setText("");

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mNewExerciseName.getWindowToken(), 0);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_new_workout, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;

			case R.id.action_save_workout:
				final String workoutName = mWorkoutName.getText().toString();

				if (workoutName.isEmpty()) {
					Toast.makeText(this, "Provide a name for your workout", Toast.LENGTH_SHORT).show();
					return false;
				} else if (mAdapter == null || mAdapter.getExercisesCount() == 0) {
					Toast.makeText(this, "Include at least one exercise in your workout", Toast.LENGTH_SHORT).show();
					return false;
				}

				final ContentResolver contentResolver = getContentResolver();
				ContentValues contentValues = new ContentValues();
				contentValues.put(WorkoutContract.WorkoutsTable.NAME, workoutName);

				if (mWorkoutId != -1) {
					String where = WorkoutContract.WorkoutsTable._ID + "=?";
					String[] selectionArgs = {String.valueOf(mWorkoutId)};
					contentResolver.update(WorkoutContract.WorkoutsTable.CONTENT_URI, contentValues, where, selectionArgs);
				} else {
					final Uri workoutUri = contentResolver.insert(WorkoutContract.WorkoutsTable.CONTENT_URI, contentValues);
					mWorkoutId = ContentUris.parseId(workoutUri);
				}

				final List<Exercise> exercises = mAdapter.getExercises();
				for (Exercise exercise : exercises) {
					contentValues.clear();
					contentValues.put(WorkoutContract.ExercisesTable.WORKOUT_ID, mWorkoutId);
					contentValues.put(WorkoutContract.ExercisesTable.NAME, exercise.name);
					contentValues.put(WorkoutContract.ExercisesTable.TYPE_ID, exercise.id);
					contentValues.put(WorkoutContract.ExercisesTable.COUNT, exercise.count);
					contentValues.put(WorkoutContract.ExercisesTable.ICON, exercise.iconResId);

					if (mWorkoutId != -1) {
						String where = WorkoutContract.ExercisesTable.WORKOUT_ID + "=?" + " AND " + WorkoutContract.ExercisesTable.TYPE_ID + "=?";
						String[] selectionArgs = {String.valueOf(mWorkoutId), String.valueOf(exercise.id)};

						if (exercise.count == 0) {
							contentResolver.delete(WorkoutContract.ExercisesTable.CONTENT_URI, where, selectionArgs);
						} else if (contentResolver.update(WorkoutContract.ExercisesTable.CONTENT_URI, contentValues, where, selectionArgs) < 1) {
							contentResolver.insert(WorkoutContract.ExercisesTable.CONTENT_URI, contentValues);
						}
					} else if (exercise.count > 0) {
						contentResolver.insert(WorkoutContract.ExercisesTable.CONTENT_URI, contentValues);
					}
				}
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
