package io.extact.rms.application.persistence;

import io.extact.rms.application.domain.UserAccount;

public interface UserAccountRepository extends GenericRepository<UserAccount> {

    /**
     * ログインIDとパスワードに一致するユーザを取得。
     *
     * @param loginId ログインID
     * @param password パスワード
     * @return 該当ユーザ。該当なしはnull
     */
    UserAccount findByLoginIdAndPasswod(String loginId, String password);

    //
    /**
     * ログインIDに一致するユーザを取得する。
     *
     * @param loginId ログインID
     * @return 該当ユーザ。該当なしはnull
     */
    UserAccount findByLoginId(String loginId);
}