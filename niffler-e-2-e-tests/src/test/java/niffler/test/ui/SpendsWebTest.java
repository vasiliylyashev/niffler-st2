package niffler.test.ui;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.AllureId;
import niffler.jupiter.annotation.GenerateSpend;
import niffler.data.User;
import niffler.jupiter.annotation.GenerateCategory;
import niffler.jupiter.annotation.WebTest;
import niffler.model.CurrencyValues;
import niffler.model.SpendJson;
import niffler.test.BaseWebTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
@WebTest
public class SpendsWebTest  extends BaseWebTest {

    public static final User user = new User("TEST_USER", "12345");

    static {
        Configuration.browserSize = "1920x1080";
    }

    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.getName());
        $("input[name='password']").setValue(user.getPassword());
        $("button[type='submit']").click();
    }

    @GenerateCategory(
        username = "TEST_USER",
        category = "Automation Testing Course QA.GURU"
    )
    @GenerateSpend(
        username = "TEST_USER",
        description = "QA GURU ADVANCED VOL 2",
        currency = CurrencyValues.RUB,
        amount = 54950.00,
        category = "Automation Testing Course QA.GURU"
    )
    @AllureId("101")
    @Test
    void spendShouldBeDeletedByActionInTable(SpendJson spend) {
        $(".spendings-table tbody").$$("tr")
            .find(text(spend.getDescription()))
            .$("td")
            .scrollTo()
            .click();

        $$(".button_type_small").find(text("Delete selected"))
            .click();

        $(".spendings-table tbody")
            .$$("tr")
            .shouldHave(CollectionCondition.size(0));
    }
}
