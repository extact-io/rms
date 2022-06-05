package io.extact.rms.client.console.ui.admin;

import static io.extact.rms.client.console.ui.ClientConstants.*;
import static io.extact.rms.client.console.ui.textio.TextIoUtils.*;

import io.extact.rms.client.api.RentalReservationClientApi;
import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto.ClientUserType;
import io.extact.rms.client.api.exception.BusinessFlowClientException;
import io.extact.rms.client.console.ui.ClientConstants;
import io.extact.rms.client.console.ui.TransitionMap.RmsScreen;
import io.extact.rms.client.console.ui.TransitionMap.Transition;
import io.extact.rms.client.console.ui.textio.RmsStringInputReader.PatternMessage;
import io.extact.rms.client.console.ui.textio.TextIoUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditUserScreen implements RmsScreen {

    private final RentalReservationClientApi clientApi;

    @Override
    public Transition play(UserAccountClientDto loginUser, boolean printHeader) {

        if (printHeader) {
            printScreenHeader(loginUser, "ユーザ情報編集画面");
        }

        // ユーザ一覧を表示
        var users = clientApi.getAllUserAccounts();
        println(EDIT_USER_INFORMATION);
        users.forEach(dto ->
            println(ClientConstants.USER_ACCOUNT_FORMAT.format(dto))
        );
        blankLine();

        // 編集するユーザを選択
        var selectNumber = newIntInputReader()
                .withSelectableValues(
                        users.stream()
                            .map(UserAccountClientDto::getId)
                            .toList(),
                        SCREEN_BREAK_VALUE)
                .read("ユーザ番号");
        if (TextIoUtils.isBreak(selectNumber)) {
            return Transition.ADMIN_MAIN;
        }
        blankLine();

        var targetUser = users.stream()
                .filter(user -> user.getId().equals(selectNumber))
                .findFirst()
                .get();

        // パスワードの入力
        var password = newStringInputReader()
                .withDefaultValue(targetUser.getPassword())
                .withMinLength(5)
                .withMaxLength(15)
                .read("パスワード");
        targetUser.setPassword(password);

        // ユーザ名の入力
        var userName = newStringInputReader()
                .withDefaultValue(targetUser.getUserName())
                .withMinLength(1)
                .read("ユーザ名");
        targetUser.setUserName(userName);

        // 電話番号の入力
        var phoneNumber = newStringInputReader()
                .withDefaultValue(targetUser.getPhoneNumber())
                .withMaxLength(14)
                .withPattern(PatternMessage.PHONE_NUMBER)
                .read("電話番号（省略可）");
        targetUser.setPhoneNumber(phoneNumber);

        // 連絡先の入力
        var contact = newStringInputReader()
                .withDefaultValue(targetUser.getPhoneNumber())
                .withMaxLength(15)
                .read("連絡先（省略可）");
        targetUser.setContact(contact);

        // 連絡先の入力
        var userType = newEnumInputReader(ClientUserType.class)
                .withDefaultValue(targetUser.getUserType())
                .read("権限");
        targetUser.setUserType(userType);

        // ユーザ情報の更新実行
        try {
            var updatedUser = clientApi.updateUserAccount(targetUser);
            printResultInformation(updatedUser);
            return Transition.ADMIN_MAIN;

        } catch (BusinessFlowClientException e) {
            printServerError(e);
            return play(loginUser, false); // start over!!

        }
    }

    private void printResultInformation(UserAccountClientDto updatedUserAccount) {
        blankLine();
        println("***** ユーザ登録結果 *****");
        printf("[%s]のユーザ情報を更新しました", updatedUserAccount.getId());
        blankLine();
        blankLine();
        waitPressEnter();
    }
}
