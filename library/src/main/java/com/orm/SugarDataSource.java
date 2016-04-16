package com.orm;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.orm.util.ThreadUtil.*;

/**
 * SugarDataSource provides basic crud operations and simplifies SugarRecord by using callbacks and
 * performing Asynchronous execution to run queries.
 *
 * @author jonatan.salas
 */
public final class SugarDataSource<T> {
    private final Class<T> sClass;

    /**
     *
     * @param tClass
     */
    private SugarDataSource(Class<T> tClass) {
        if (null == tClass) {
            throw new IllegalArgumentException("sClass shouldn't be null!");
        }

        this.sClass = tClass;
    }

    /**
     *
     * @param sClass
     * @param <T>
     * @return
     */
    public static <T> SugarDataSource<T> getInstance(Class<T> sClass) {
        return new SugarDataSource<>(sClass);
    }

    /**
     *
     * @param object
     * @param success
     * @param error
     */
    @SuppressWarnings("all")
    public void insert(final T object, final SuccessCallback<Long> success, final ErrorCallback error) {
        checkNotNull(success);
        checkNotNull(object);
        checkNotNull(error);

        final Callable<Long> call = () -> { return SugarRecord.save(object); };
        final Future<Long> future = doInBackground(call);

        Long id = null;

        try {
            if (future.isDone()) {
                id = future.get();
            }

            if (null == object) {
                error.onError(new Exception("Error when performing insert of " + object.toString()));
            } else {
                success.onSuccess(id);
            }

        } catch (Exception e) {
            error.onError(e);
        }
    }

    /**
     *
     * @param id
     * @param success
     * @param error
     */
    @SuppressWarnings("all")
    public void findById(final Long id, final SuccessCallback<T> success, final ErrorCallback error) {
        checkNotNull(success);
        checkNotNull(error);
        checkNotNull(id);

        final Callable<T> call = () -> { return SugarRecord.findById(getSugarClass(), id); };
        final Future<T> future = doInBackground(call);

        T object = null;

        try {
            if (future.isDone()) {
                object = future.get();
            }

            if (null == object) {
                error.onError(new Exception("The object with " + id.toString() + "doesn't exist in database"));
            } else {
                success.onSuccess(object);
            }

        } catch (Exception e) {
            error.onError(e);
        }
    }

    /**
     *
     * @param orderBy
     * @param success
     * @param error
     */
    @SuppressWarnings("all")
    public void listAll(final String orderBy, final SuccessCallback<List<T>> success, final ErrorCallback error) {
        checkNotNull(success);
        checkNotNull(error);

        final Callable<List<T>> call = () -> { return SugarRecord.listAll(getSugarClass(), orderBy); };
        final Future<List<T>> future = doInBackground(call);

        List<T> objects = null;

        try {
            if (future.isDone()) {
                objects = future.get();
            }

            if (null == objects || objects.isEmpty()) {
                error.onError(new Exception("There are no objects in the database"));
            } else {
                success.onSuccess(objects);
            }

        } catch (Exception e) {
            error.onError(e);
        }
    }


    /**
     *
     * @param object
     * @param success
     * @param error
     */
    @SuppressWarnings("all")
    public void update(final T object, final SuccessCallback<Long> success, final ErrorCallback error) {
        checkNotNull(success);
        checkNotNull(object);
        checkNotNull(error);

        final Callable<Long> call = () -> { return SugarRecord.update(object); };
        final Future<Long> future = doInBackground(call);

        Long id = null;

        try {
            if (future.isDone()) {
                id = future.get();
            }

            if (null == object) {
                error.onError(new Exception("Error when performing update of " + object.toString()));
            } else {
                success.onSuccess(id);
            }

        } catch (Exception e) {
            error.onError(e);
        }
    }

    /**
     *
     * @param object
     * @param success
     * @param error
     */
    @SuppressWarnings("all")
    public void delete(final T object, final SuccessCallback<Boolean> success, final ErrorCallback error) {
        checkNotNull(success);
        checkNotNull(object);
        checkNotNull(error);

        final Callable<Boolean> call = () -> { return SugarRecord.delete(object); };
        final Future<Boolean> future = doInBackground(call);

        Boolean deleted = null;

        try {
            if (future.isDone()) {
                deleted = future.get();
            }

            if (null == object) {
                error.onError(new Exception("Error when performing delete of " + object.toString()));
            } else {
                success.onSuccess(deleted);
            }

        } catch (Exception e) {
            error.onError(e);
        }
    }

    /**
     *
     * @param object
     */
    private void checkNotNull(Object object) {
        if (null == object) {
            throw new IllegalArgumentException("object shouldn't be null");
        }
    }

    public Class<T> getSugarClass() {
        return sClass;
    }

    /**
     * @author jonatan.salas
     * @param <S>
     */
    public interface SuccessCallback<S> {

        /**
         *
         * @param object
         */
        void onSuccess(final S object);
    }

    /**
     * @author jonatan.salas
     */
    public interface ErrorCallback {

        /**
         *
         * @param e
         */
        void onError(final Exception e);
    }
}
