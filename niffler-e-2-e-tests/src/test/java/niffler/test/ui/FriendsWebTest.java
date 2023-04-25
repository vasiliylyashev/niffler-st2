package niffler.test.ui;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.*;
import static niffler.jupiter.annotation.User.UserType.INVITATION_SENT;
import static niffler.jupiter.annotation.User.UserType.WITH_FRIENDS;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import niffler.jupiter.annotation.User;
import niffler.jupiter.extension.UsersQueueExtension;
import niffler.model.UserJson;
import niffler.test.BaseWebTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UsersQueueExtension.class)
public class FriendsWebTest extends BaseWebTest {

  @AllureId("102")
  @Test
  void friendsShouldBeVisible0(@User(userType = WITH_FRIENDS) UserJson user,
                               @User(userType = INVITATION_SENT) UserJson user2) {
    Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
    $("a[href*='redirect']").click();
    System.out.println(user.getFirstname());
    System.out.println(user.getUsername());
    $("input[name='username']").setValue(user.getUsername());
    $("input[name='password']").setValue(user.getPassword());
    $("button[type='submit']").click();
    $("a[href*='people']").click();
    $$(".table tbody tr").shouldHave(sizeGreaterThan(0));
    $x("//button[contains(@class,'logout')]").click();
    $("a[href*='redirect']").click();

    //Login as user2
    $("input[name='username']").setValue(user2.getUsername());
    $("input[name='password']").setValue(user2.getPassword());
    $("button[type='submit']").click();
  }

  @AllureId("103")
  @Test
  void friendsShouldBeVisible1(@User(userType = INVITATION_SENT) UserJson user) {
    Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
    $("a[href*='redirect']").click();
    $("input[name='username']").setValue(user.getUsername());
    $("input[name='password']").setValue(user.getPassword());
    $("button[type='submit']").click();

    $("a[href*='people']").click();
    $$(".table tbody tr").find(Condition.text("Pending invitation"))
        .should(Condition.visible);
  }
}
