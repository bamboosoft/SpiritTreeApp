/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.viewmodel.user;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;

import com.bamboosoft.spirittreeapp.R;
import com.bamboosoft.spirittreeapp.domain.user.User;
import com.bamboosoft.spirittreeapp.repository.UsersDao;
import com.bamboosoft.spirittreeapp.repository.UsersRepository;
import com.bamboosoft.spirittreeapp.ui.user.RegisterUserNavigator;


/**
 * ViewModel for the Add/Edit screen.
 * 添加/编辑屏幕的ViewModel。
 * <p>
 * This ViewModel only exposes {@link ObservableField}s, so it doesn't need to extend
 * 这个ViewModel只公开了{ @ link可见域}，所以它不需要扩展
 * {@link android.databinding.BaseObservable} and updates are notified automatically. See
 * {@link com.bamboosoft.spirittreeapp.viewmodel.user.StatisticsViewModel} for
 * how to deal with more complex scenarios.
 * { @link android.databinding。可以自动地通知可执行的事件和更新。
 * 看到{ @link com.example.android.architecture.blueprints.todoapp.statistics。
 * 关于如何处理更复杂的场景的统计视图模型。
 */
public class RegisterUserViewModel implements UsersDao.GetUserCallback {

    public final ObservableField<String> account = new ObservableField<>();

    public final ObservableField<String> password = new ObservableField<>();

    public final ObservableField<String> email = new ObservableField<>();

    public final ObservableField<Integer> mobile = new ObservableField<>();


    public final ObservableField<String> description = new ObservableField<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> snackbarText = new ObservableField<>();

    private final UsersRepository mUsersRepository;

	// To avoid leaks, this must be an Application Context.
	// 为了避免泄漏，这必须是应用程序上下文。
    private final Context mContext;  

    @Nullable
    private String mUserId;

    private boolean mIsNewUser;

    private boolean mIsDataLoaded = false;

    private RegisterUserNavigator mRegisterUserNavigator;

    public RegisterUserViewModel(Context context, UsersRepository usersRepository) {
        // Force use of Application Context.
		// 强制使用应用程序上下文
		mContext = context.getApplicationContext(); 
        mUsersRepository = usersRepository;
    }

    public void onActivityCreated(RegisterUserNavigator navigator) {
        mRegisterUserNavigator = navigator;
    }

    public void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
		// 明确的引用以避免潜在的内存泄漏。
        mRegisterUserNavigator = null;
    }

    public void start(String userId) {
        if (dataLoading.get()) {
            // Already loading, ignore.
			// 已经加载,忽视。
            return;
        }
        mUserId = userId;
        if (userId == null) {
            // No need to populate, it's a new user
			// 不需要填充，这是一个新任务
            mIsNewUser = true;
            return;
        }
        if (mIsDataLoaded) {
            // No need to populate, already have data.
			// 不需要填充，已经有数据了。
            return;
        }
        mIsNewUser = false;
        dataLoading.set(true);
        mUsersRepository.getUser(userId, this);
    }

    @Override
    public void onUserLoaded(User user) {
        account.set(user.getAccount());
        description.set(user.getDescription());
        dataLoading.set(false);
        mIsDataLoaded = true;

        // Note that there's no need to notify that the values changed because we're using
        // ObservableFields.
		// 请注意，没有必要通知因为我们使用了可见字段而改变了值。

    }

    @Override
    public void onDataNotAvailable() {
        dataLoading.set(false);
    }

    // Called when clicking on fab.
	// 当单击fab时调用。
    public void saveUser(String account, String password, int mobile, String email) {
        if (isNewUser()) {
            createUser(account,password,mobile,email);
        } else {
            updateUser(account,password,mobile,email);
        }
    }

    @Nullable
    public String getSnackbarText() {
        return snackbarText.get();
    }

    private boolean isNewUser() {
        return mIsNewUser;
    }

    private void createUser(String account, String password, int mobile, String email) {
        User newUser = new User(account,password,mobile,email);
        if (newUser.isEmpty()) {
            snackbarText.set(mContext.getString(R.string.empty_user_message));
        } else {
            mUsersRepository.saveUser(newUser);
            navigateOnUserSaved();
        }
    }

    private void updateUser(String account, String password, int mobile, String email) {
        if (isNewUser()) {
            throw new RuntimeException("updateUser() was called but user is new.");
        }
        mUsersRepository.saveUser(new User(account,password,mobile,email, mUserId));
        // After an edit, go back to the list.
		// 编辑后，返回到列表。
		navigateOnUserSaved(); 
    }

    private void navigateOnUserSaved() {
        if (mRegisterUserNavigator!= null) {
            mRegisterUserNavigator.onUserRegisted();
        }
    }
}
